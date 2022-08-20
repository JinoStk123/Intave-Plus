package de.jpx3.intave.check.movement.physics.eval;

import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserLocal;

import java.util.Collections;
import java.util.List;

public final class EvaluationContext {
  private static final UserLocal<EvaluationContext> evalUserLocal = UserLocal.withInitial(EvaluationContext::new);

  private double verticalVL;
  private double horizontalVL;

  private double verticalUncertainty;
  private double horizontalUncertainty;

  private double finalVerticalMultiplier;
  private double finalHorizontalMultiplier;

  private List<Class<? extends Evaluator>> responsibleEvaluators = Collections.emptyList();

  private EvaluationContext() {
  }

  public void restore() {
    verticalVL = 0;
    horizontalVL = 0;
    verticalUncertainty = 0;
    horizontalUncertainty = 0;
    finalVerticalMultiplier = 1;
    finalHorizontalMultiplier = 1;
    responsibleEvaluators = Collections.emptyList();
  }

  void withVerticalUncertaintyOf(double uncertainty) {
    verticalUncertainty = Math.max(uncertainty, verticalUncertainty);
  }

  void withHorizontalUncertaintyOf(double uncertainty) {
    horizontalUncertainty = Math.max(uncertainty, horizontalUncertainty);
  }

  void withUncertaintyOf(double verticalUncertainty, double horizontalUncertainty) {
    this.verticalUncertainty = Math.max(verticalUncertainty, this.verticalUncertainty);
    this.horizontalUncertainty = Math.max(horizontalUncertainty, this.horizontalUncertainty);
  }

  double verticalUncertainty() {
    return verticalUncertainty;
  }

  double horizontalUncertainty() {
    return horizontalUncertainty;
  }

  public static EvaluationContext from(User user) {
    return evalUserLocal.get(user);
  }
}
