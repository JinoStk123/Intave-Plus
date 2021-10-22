package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.check.movement.Physics;
import de.jpx3.intave.shade.Motion;
import de.jpx3.intave.user.User;

public abstract class Simulator {
  private Physics physics;

  public final void enterLinkage(Physics physics) {
    this.physics = physics;
  }

  public abstract Simulation simulate(
    User user, Motion motion,
    SimulationEnvironment environment,
    MovementConfiguration configuration
  );

  public abstract void prepareNextTick(
    User user,
    double positionX, double positionY, double positionZ,
    double motionX, double motionY, double motionZ
  );

  public String debugName() {
    return "";
  }

  @Deprecated
  protected Physics physics() {
    return physics;
  }

  public float stepHeight() {
    return 0.6f;
  }

  public boolean affectedByMovementKeys() {
    return true;
  }
}