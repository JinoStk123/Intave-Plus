package de.jpx3.intave.detect.checks.movement.physics.water;

import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.tools.wrapper.WrappedMathHelper;
import de.jpx3.intave.tools.wrapper.WrappedVector;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaMovementData;

public abstract class AquaticWaterMovementBase {
  public void setup() throws Exception {}
  public abstract boolean fluidStateEmpty(User user, double x, double y, double z);

  public boolean handleFluidAcceleration(User user, WrappedAxisAlignedBB boundingBox) {
    UserMetaMovementData movementData = user.meta().movementData();
    Object world = movementData.nmsWorld();
    WrappedAxisAlignedBB wrappedAxisAlignedBB = boundingBox.shrink(0.001D);
    int minX = WrappedMathHelper.floor(wrappedAxisAlignedBB.minX);
    int minY = WrappedMathHelper.floor(wrappedAxisAlignedBB.minY);
    int minZ = WrappedMathHelper.floor(wrappedAxisAlignedBB.minZ);
    int maxX = WrappedMathHelper.ceil(wrappedAxisAlignedBB.maxX);
    int maxY = WrappedMathHelper.ceil(wrappedAxisAlignedBB.maxY);
    int maxZ = WrappedMathHelper.ceil(wrappedAxisAlignedBB.maxZ);
    boolean inWater = false;
    int countedWaterCollisions = 0;
    WrappedVector waterFlowTotal = WrappedVector.ZERO;
    double d0 = 0;

    for (int x = minX; x < maxX; ++x) {
      for (int y = minY; y < maxY; ++y) {
        for (int z = minZ; z < maxZ; ++z) {
          Object blockPosition = blockPositionOf(x, y, z);
          Object fluidState = fluidState(user, blockPosition);
          if (!fluidTaggedWithWater(fluidState)) {
            continue;
          }
          float fluidHeight = fluidHeight(fluidState);
          double d1 = (float) y + fluidHeight;
          if (d1 >= wrappedAxisAlignedBB.minY) {
            inWater = true;
            d0 = Math.max(d1 - wrappedAxisAlignedBB.minY, d0);
            WrappedVector flowVector = resolveFlowVector(fluidState, world, blockPosition);
            if (d0 < 0.4) {
              flowVector = flowVector.scale(d0);
            }
            waterFlowTotal = waterFlowTotal.add(flowVector);
            ++countedWaterCollisions;
          }
        }
      }
    }

    if (waterFlowTotal.length() > 0.0D) {
      if (countedWaterCollisions > 0) {
        waterFlowTotal = waterFlowTotal.scale(1.0D / (double) countedWaterCollisions);
      }
      waterFlowTotal = waterFlowTotal.normalize();
      double d2 = 0.014D;
      movementData.physicsLastMotionX += waterFlowTotal.xCoord * d2;
      movementData.physicsLastMotionY += waterFlowTotal.yCoord * d2;
      movementData.physicsLastMotionZ += waterFlowTotal.zCoord * d2;
      movementData.pastPushedByWaterFlow = 0;
    }

    return inWater;
  }

  public boolean areEyesInFluid(User user, double positionX, double positionY, double positionZ) {
    UserMetaMovementData movementData = user.meta().movementData();
    double eyeHeight = movementData.eyeHeight();
    positionY -= 1;
    double playerViewPositionY = positionY + eyeHeight;
    int blockPlayerViewPositionY = WrappedMathHelper.floor(playerViewPositionY);
    int blockX = WrappedMathHelper.floor(positionX);
    int blockY = WrappedMathHelper.floor(positionY);
    int blockZ = WrappedMathHelper.floor(positionZ);
    Object blockPosition = blockPositionOf(blockX, blockPlayerViewPositionY, blockZ);
    Object fluidState = fluidState(user, blockPosition);
    return fluidTaggedWithWater(fluidState)
//      && blockPlayerViewPositionY < blockY + fluidHeight(fluidState) + 0.11111111F
      ;
  }

  public abstract boolean fluidTaggedWithWater(Object fluidState);
  public abstract Object blockPositionOf(int x, int y, int z);
  public abstract Object fluidState(User user, Object blockPosition);
  public abstract float fluidHeight(Object fluidState);
  public abstract WrappedVector resolveFlowVector(Object fluidState, Object world, Object blockPosition);
}