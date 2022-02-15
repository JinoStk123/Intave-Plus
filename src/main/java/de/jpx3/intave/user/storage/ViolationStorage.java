package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.check.Check;
import de.jpx3.intave.module.violation.Violation;
import de.jpx3.intave.module.violation.ViolationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public final class ViolationStorage implements Storage {
  private final static long VIOLATION_CHECK_UPDATE_TIMEOUT = TimeUnit.MINUTES.toMillis(3);
  private final static long VIOLATION_INSERT_CHECK_COOLDOWN = TimeUnit.MINUTES.toMillis(30);
  private final static long VIOLATION_ALLOWED_LIFETIME = TimeUnit.DAYS.toMillis(7);
  private final static long VIOLATION_OVERALL_LIMIT = 256;

  private final List<ViolationEvent> interestingViolations = new CopyOnWriteArrayList<>();

  public void noteViolation(ViolationContext violationContext) {
    Violation violation = violationContext.violation();
    String checkName = violation.check().name();
    String details = violation.details();
    int violationLevelAfter = (int) violationContext.violationLevelAfter();
    if (susVL(violation.check().type(), details, violationLevelAfter)) {
      Optional<ViolationEvent> violationEventOptional = lastViolationOfCheck(checkName);
      long lastViolationOfCheck = violationEventOptional.map(event -> System.currentTimeMillis() - event.timestamp()).orElseGet(System::currentTimeMillis);
      if (lastViolationOfCheck > VIOLATION_INSERT_CHECK_COOLDOWN) {
        if (interestingViolations.size() < VIOLATION_OVERALL_LIMIT) {
          ViolationEvent event = new ViolationEvent(
            checkName.toLowerCase(Locale.ROOT),
            details.toLowerCase(Locale.ROOT),
            IntavePlugin.version().toLowerCase(Locale.ROOT),
            violationLevelAfter,
            System.currentTimeMillis()
          );
          addViolationEvent(event);
        }
      } else if (lastViolationOfCheck < VIOLATION_CHECK_UPDATE_TIMEOUT && violationEventOptional.isPresent()) {
        ViolationEvent violationEvent = violationEventOptional.get();
        if (violationLevelAfter > violationEvent.violationLevel()) {
          violationEvent.setViolationLevel(violationLevelAfter);
          violationEvent.setTimestamp(System.currentTimeMillis());
        }
      }
    }
  }

  public boolean susVL(Check check, String description, int vl) {
    switch (check) {
      case ATTACK_RAYTRACE:
        return vl > 100;
      case HEURISTICS:
        return description.contains("!");
      case PHYSICS:
      case PLACEMENT_ANALYSIS:
        return vl > 500;
      default:
        return false;
    }
  }

  private Optional<ViolationEvent> lastViolationOfCheck(String check) {
    String finalCheck = check.toLowerCase(Locale.ROOT);
    return interestingViolations.stream()
      .filter(event -> event.checkName().equals(finalCheck))
      .max(Comparator.comparingLong(ViolationEvent::timestamp));
  }

  private void addViolationEvent(ViolationEvent event) {
    interestingViolations.add(event);
  }

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    clearOldViolations();
    output.writeInt(interestingViolations.size());
    for (ViolationEvent violation : interestingViolations) {
      violation.writeTo(output);
    }
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    int violations = input.readInt();
    for (int i = 0; i < violations; i++) {
      ViolationEvent violation = new ViolationEvent();
      violation.readFrom(input);
      interestingViolations.add(violation);
    }
    clearOldViolations();
  }

  private void clearOldViolations() {
    interestingViolations.removeIf(event -> System.currentTimeMillis() - event.timestamp > VIOLATION_ALLOWED_LIFETIME);
  }

  public List<ViolationEvent> violations() {
    return interestingViolations;
  }

  public static class ViolationEvent implements Storage {
    private String checkName;
    private String details;
    private String intaveVersion;
    private int violationLevel;
    private long timestamp;

    public ViolationEvent() {
    }

    public ViolationEvent(
      String checkName,
      String details,
      String intaveVersion,
      int violationLevel,
      long timestamp
    ) {
      this.checkName = checkName;
      this.details = details;
      this.intaveVersion = intaveVersion;
      this.violationLevel = violationLevel;
      this.timestamp = timestamp;
    }

    @Override
    public void writeTo(ByteArrayDataOutput output) {
      output.writeUTF(checkName);
      output.writeUTF(details);
      output.writeUTF(intaveVersion);
      output.writeInt(violationLevel);
      output.writeLong(timestamp);
    }

    @Override
    public void readFrom(ByteArrayDataInput input) {
      checkName = input.readUTF();
      details = input.readUTF();
      intaveVersion = input.readUTF();
      violationLevel = input.readInt();
      timestamp = input.readLong();
    }

    public String checkName() {
      return checkName;
    }

    public String details() {
      return details;
    }

    public String intaveVersion() {
      return intaveVersion;
    }

    public int violationLevel() {
      return violationLevel;
    }

    public void setViolationLevel(int violationLevel) {
      this.violationLevel = violationLevel;
    }

    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }

    public long timestamp() {
      return timestamp;
    }
  }
}
