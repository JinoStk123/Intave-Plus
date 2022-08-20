package de.jpx3.intave.check.movement.physics.eval;

import com.google.common.collect.Lists;
import de.jpx3.intave.check.movement.physics.Simulation;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;

import java.util.List;

public final class Evaluation {
  private final List<Evaluator> evaluators = Lists.newArrayList(
    new ShulkerEvaluator(),
    new FireworkRocketEvaluator()
  );

  public EvaluationContext evaluate(User user, Simulation simulation) {
    EvaluationContext evaluationContext = EvaluationContext.from(user);
    MovementMetadata movement = user.meta().movement();
    for (Evaluator evaluator : evaluators) {
      evaluator.alterEvaluation(evaluationContext, movement, simulation);
    }
    return evaluationContext;
  }
}
