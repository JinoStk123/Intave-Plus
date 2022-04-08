package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.player.collider.Collider;
import de.jpx3.intave.player.collider.complex.ColliderSimulationResult;
import de.jpx3.intave.player.collider.simple.SimpleColliderSimulationResult;
import de.jpx3.intave.shade.Motion;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static de.jpx3.intave.shade.ClientMathHelper.cos;
import static de.jpx3.intave.shade.ClientMathHelper.sin;

final class ElytraSimulator extends BaseSimulator {
  @Override
  public Simulation simulate(
    User user, Motion motion,
    SimulationEnvironment environment,
    MovementConfiguration configuration
  ) {
    float rotationPitch = environment.rotationPitch();
    Vector lookVector = environment.lookVector();

    double positionX = environment.verifiedPositionX();
    double positionY = environment.verifiedPositionY();
    double positionZ = environment.verifiedPositionZ();

    float f = rotationPitch * 0.017453292F;
    double rotationVectorDistance = Math.sqrt(lookVector.getX() * lookVector.getX() + lookVector.getZ() * lookVector.getZ());
    double dist2 = Math.sqrt(motion.motionX * motion.motionX + motion.motionZ * motion.motionZ);
    double rotationVectorLength = Math.sqrt(lookVector.lengthSquared());
    float pitchCosine = cos(f);
    pitchCosine = (float) ((double) pitchCosine * (double) pitchCosine * Math.min(1.0D, rotationVectorLength / 0.4D));
    motion.motionY += environment.gravity() * (-1 + pitchCosine * 0.75);

    if (motion.motionY < 0.0D && rotationVectorDistance > 0.0D) {
      double d2 = motion.motionY * -0.1D * (double) pitchCosine;
      motion.motionY += d2;
      motion.motionX += lookVector.getX() * d2 / rotationVectorDistance;
      motion.motionZ += lookVector.getZ() * d2 / rotationVectorDistance;
    }

    if (f < 0.0F && rotationVectorDistance > 0.0D) {
      double d9 = dist2 * (double) (-sin(f)) * 0.04D;
      motion.motionY += d9 * 3.2D;
      motion.motionX += -lookVector.getX() * d9 / rotationVectorDistance;
      motion.motionZ += -lookVector.getZ() * d9 / rotationVectorDistance;
    }

    if (rotationVectorDistance > 0.0D) {
      motion.motionX += (lookVector.getX() / rotationVectorDistance * dist2 - motion.motionX) * 0.1D;
      motion.motionZ += (lookVector.getZ() / rotationVectorDistance * dist2 - motion.motionZ) * 0.1D;
    }

    motion.motionX *= 0.99f;
    motion.motionY *= 0.98f;
    motion.motionZ *= 0.99f;

    tryRelinkFlyingPosition(user, motion, environment);

    ColliderSimulationResult collisionResult = Collider.collision(
      user, motion, environment.inWeb(),
      positionX, positionY, positionZ
    );
    notePossibleFlyingPacket(user, collisionResult);
    return Simulation.of(user, configuration, collisionResult);
  }

  private void tryRelinkFlyingPosition(User user, Motion motion, SimulationEnvironment environment) {
    Player player = user.player();
    MovementMetadata movementData = user.meta().movement();
    float rotationPitch = environment.rotationPitch();
    Vector lookVector = environment.lookVector();

    double positionX = environment.verifiedPositionX();
    double positionY = environment.verifiedPositionY();
    double positionZ = environment.verifiedPositionZ();

    boolean onGround;
    double resetMotion = environment.resetMotion();
    double jumpUpwardsMotion = environment.jumpMotion();

    int interpolations = 0;
    double interpolateX = motion.motionX;
    double interpolateY = motion.motionY;
    double interpolateZ = motion.motionZ;

    for (; interpolations <= 2; interpolations++) {
      SimpleColliderSimulationResult colliderResult = Collider.simplifiedCollision(
        player, positionX, positionY, positionZ,
        interpolateX, interpolateY, interpolateZ
      );

      positionX += colliderResult.motionX();
      positionY += colliderResult.motionZ();
      positionZ += colliderResult.motionY();

      double diffX = positionX - environment.verifiedPositionX();
      double diffY = positionY - environment.verifiedPositionY();
      double diffZ = positionZ - environment.verifiedPositionZ();
      onGround = colliderResult.onGround();

      boolean jumpLessThanExpected = colliderResult.motionY() < jumpUpwardsMotion;
      boolean jump = onGround && Math.abs(((colliderResult.motionY()) + jumpUpwardsMotion) - environment.motionY()) < 1e-5 && jumpLessThanExpected;

      if (!flyingPacket(diffX, diffY, diffZ) && !jump) {
        break;
      }

      float f = rotationPitch * 0.017453292F;
      double rotationVectorDistance = Math.sqrt(lookVector.getX() * lookVector.getX() + lookVector.getZ() * lookVector.getZ());
      double dist2 = Math.sqrt(motion.motionX * motion.motionX + motion.motionZ * motion.motionZ);
      double rotationVectorLength = Math.sqrt(lookVector.lengthSquared());
      float pitchCosine = cos(f);
      pitchCosine = (float) ((double) pitchCosine * (double) pitchCosine * Math.min(1.0D, rotationVectorLength / 0.4D));
      motion.motionY += environment.gravity() * (-1 + pitchCosine * 0.75);

      if (motion.motionY < 0.0D && rotationVectorDistance > 0.0D) {
        double d2 = motion.motionY * -0.1D * (double) pitchCosine;
        motion.motionY += d2;
        motion.motionX += lookVector.getX() * d2 / rotationVectorDistance;
        motion.motionZ += lookVector.getZ() * d2 / rotationVectorDistance;
      }

      if (f < 0.0F && rotationVectorDistance > 0.0D) {
        double d9 = dist2 * (double) (-sin(f)) * 0.04D;
        motion.motionY += d9 * 3.2D;
        motion.motionX += -lookVector.getX() * d9 / rotationVectorDistance;
        motion.motionZ += -lookVector.getZ() * d9 / rotationVectorDistance;
      }

      if (rotationVectorDistance > 0.0D) {
        motion.motionX += (lookVector.getX() / rotationVectorDistance * dist2 - motion.motionX) * 0.1D;
        motion.motionZ += (lookVector.getZ() / rotationVectorDistance * dist2 - motion.motionZ) * 0.1D;
      }

      motion.motionX *= 0.99f;
      motion.motionY *= 0.98f;
      motion.motionZ *= 0.99f;

      if (Math.abs(interpolateX) < resetMotion) {
        interpolateX = 0;
      }
      if (Math.abs(interpolateY) < resetMotion) {
        interpolateY = 0;
      }
      if (Math.abs(interpolateZ) < resetMotion) {
        interpolateZ = 0;
      }
    }
    if (interpolations != 0) {
      movementData.resetFlyingPacketAccurate();
    }
  }

  @Override
  public String debugName() {
    return "ELYTRA";
  }

  @Override
  public boolean affectedByMovementKeys() {
    return false;
  }
}