package de.jpx3.intave.detect.checks.world;

import com.google.common.collect.ImmutableList;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.Check;
import de.jpx3.intave.detect.CheckPart;
import de.jpx3.intave.detect.checks.world.breakspeedlimiter.BreakSpeedFinishCheck;
import de.jpx3.intave.detect.checks.world.breakspeedlimiter.BreakSpeedStartCheck;

import java.util.List;

public final class BreakSpeedLimiter extends Check {
  private final List<CheckPart<?>> checkParts;

  public BreakSpeedLimiter(IntavePlugin plugin) {
    super("BreakSpeedLimiter", "breakspeedlimiter");
    this.checkParts = ImmutableList.of(
      new BreakSpeedFinishCheck(this),
      new BreakSpeedStartCheck(this)
    );
  }

  @Override
  public List<CheckPart<?>> checkParts() {
    return checkParts;
  }
}