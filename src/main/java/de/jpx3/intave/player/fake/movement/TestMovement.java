package de.jpx3.intave.player.fake.movement;

import de.jpx3.intave.block.access.BlockAccess;
import de.jpx3.intave.block.physics.BlockProperties;
import de.jpx3.intave.math.SinusCache;
import de.jpx3.intave.shade.Motion;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.concurrent.ThreadLocalRandom;

public final class TestMovement extends Movement {
  @Override
  protected void move(Location parentLocation) {
    Location expectedLocation = PositionRotationLookup.lookup(parentLocation, this.botDistance);
    Motion bestMotion = null;

    sprinting = true;

    for (int forwardKey = 1; forwardKey >= -1; forwardKey--) {
      for (int strafeKey = 1; strafeKey >= -1; strafeKey--) {
        for (int jumpState = 0; jumpState <= 1; jumpState++) {
          boolean jump = jumpState == 0;

          if (jump && ThreadLocalRandom.current().nextInt(5) % 10 != 0) {
            continue;
          }

          float forward = forwardKey * 0.98f;
          float strafe = strafeKey * 0.98f;

          if (sneaking) {
            forward *= 0.2f;
            strafe *= 0.2f;
          }

          Motion motion = simulate(forward, strafe, jump);
          if (bestMotion == null || distanceTo(expectedLocation, motion) < distanceTo(expectedLocation, bestMotion)) {
            bestMotion = motion;
          }
        }
      }
    }

    motionX = bestMotion.motionX;
    motionY = bestMotion.motionY;
    motionZ = bestMotion.motionZ;
  }

  @Override
  public void endTick() {
    double slipperiness;
    if (lastOnGround) {
      Material blockBelow = BlockAccess.global().typeOf(location.clone().subtract(0.0, 0.500001, 0.0).getBlock());
      slipperiness = BlockProperties.of(blockBelow).slipperiness() * 0.91f;
    } else {
      slipperiness = 0.91f;
    }

    motionY -= 0.08;
    motionX *= slipperiness;
    motionY *= 0.98f;
    motionZ *= slipperiness;
  }

  private double distanceTo(Location location1, Motion motion) {
    Location location2 = location.clone().add(motion.motionX, motion.motionY, motion.motionZ);
    return location1.distance(location2);
  }

  private Motion simulate(float forward, float strafe, boolean jump) {
    Motion motion = new Motion(motionX, motionY, motionZ);
    float yawSine = SinusCache.sin(rotationYaw * (float) Math.PI / 180.0F, false);
    float yawCosine = SinusCache.cos(rotationYaw * (float) Math.PI / 180.0F, false);
    float friction = resolveFriction();
    if (lastOnGround && onGround && jump) {
      motion.motionY = 0.42f;
      if (sprinting) {
        motion.add(-yawSine * 0.2f, 0.0, yawCosine * 0.2f);
      }
    }
    float f = strafe * strafe + forward * forward;
    if (f >= 1.0E-4F) {
      f = (float) Math.sqrt(f);
      f = friction / Math.max(1.0f, f);
      strafe *= f;
      forward *= f;
      motion.add(
        strafe * yawCosine - forward * yawSine,
        0.0,
        forward * yawCosine + strafe * yawSine
      );
    }
    return motion;
  }

  private float resolveFriction() {
    float aiMoveSpeed = 0.1f;
    float jumpMovementFactor = 0.02f;
    if (sprinting) {
      jumpMovementFactor = (float) ((double) jumpMovementFactor + (double) 0.02f * 0.3d);
    }
    float speed;
    if (lastOnGround) {
      Location location = this.location.clone().subtract(0.0, 0.500001, 0.0);
      Material type = BlockAccess.global().typeOf(location.getBlock());
      float slipperiness = BlockProperties.of(type).slipperiness() * 0.91f;
      float var4 = 0.16277137F / (slipperiness * slipperiness * slipperiness);
      return aiMoveSpeed * var4;
    } else {
      speed = jumpMovementFactor;
    }
    return speed;
  }

  @Override
  public boolean collideHorizontally() {
    return true;
  }
}