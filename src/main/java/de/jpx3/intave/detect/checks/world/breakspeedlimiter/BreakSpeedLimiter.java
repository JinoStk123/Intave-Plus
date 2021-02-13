package de.jpx3.intave.detect.checks.world.breakspeedlimiter;

import com.google.common.collect.ImmutableList;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveCheck;
import de.jpx3.intave.detect.IntaveCheckPart;

import java.util.List;

public final class BreakSpeedLimiter extends IntaveCheck {
  private final List<IntaveCheckPart<?>> checkParts;

  public BreakSpeedLimiter(IntavePlugin plugin) {
    super("BreakSpeedLimiter", "breakspeedlimiter");
    this.checkParts = ImmutableList.of(
      new BreakSpeedFinishCheck(this),
      new BreakSpeedStartCheck(this)
    );
  }

  @Override
  public List<IntaveCheckPart<?>> checkParts() {
    return checkParts;
  }
}