package de.jpx3.intave.check.movement.physics.eval;

import de.jpx3.intave.check.movement.physics.Simulation;
import de.jpx3.intave.user.meta.MovementMetadata;

public final class ShulkerEvaluator extends Evaluator {
  @Override
  void alterEvaluation(EvaluationContext evaluationContext, MovementMetadata movement, Simulation simulation) {
    if (movement.shulkerXToleranceRemaining > 0 || movement.shulkerZToleranceRemaining > 0) {
      evaluationContext.withHorizontalUncertaintyOf(0.3);
    }
    if (movement.shulkerYToleranceRemaining > 0) {
      evaluationContext.withHorizontalUncertaintyOf(0.1);
      if (Math.abs(movement.motionY()) < 0.09) {
        evaluationContext.withVerticalUncertaintyOf(0.2);
      }
    }
  }
}
