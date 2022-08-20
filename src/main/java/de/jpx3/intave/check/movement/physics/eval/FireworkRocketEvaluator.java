package de.jpx3.intave.check.movement.physics.eval;

import de.jpx3.intave.check.movement.physics.Simulation;
import de.jpx3.intave.user.meta.MovementMetadata;

public final class FireworkRocketEvaluator extends Evaluator {
  @Override
  void alterEvaluation(EvaluationContext evaluationContext, MovementMetadata movement, Simulation simulation) {
    if (movement.fireworkRocketsTicks < 10) {
      evaluationContext.withVerticalUncertaintyOf(1);
      evaluationContext.withHorizontalUncertaintyOf(3);
    } else if (movement.fireworkRocketsTicks < 30) {
      evaluationContext.withVerticalUncertaintyOf(0.5);
      evaluationContext.withHorizontalUncertaintyOf(3);
    }
  }
}
