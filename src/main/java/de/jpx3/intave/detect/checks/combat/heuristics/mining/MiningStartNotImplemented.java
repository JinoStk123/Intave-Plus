package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.user.User;

public final class MiningStartNotImplemented extends MiningStrategyExecutor {
  public MiningStartNotImplemented(User user) {
    super(user);
  }

  @Override
  public MiningStrategy miningStrategy() {
    return MiningStrategy.RAYTRX;
  }
}