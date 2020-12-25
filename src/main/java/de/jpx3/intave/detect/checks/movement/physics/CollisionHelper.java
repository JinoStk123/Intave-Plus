package de.jpx3.intave.detect.checks.movement.physics;

import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.Location;

public final class CollisionHelper {
  private final static float PLAYER_HEIGHT = 1.8f;
  private final static double HALF_WIDTH = 0.3;

  public static WrappedAxisAlignedBB entityBoundingBoxOf(
    User user,
    double positionX, double positionY, double positionZ
  ) {
    UserMetaMovementData movementData = user.meta().movementData();
    double width = movementData.width / 2.0;
    float height = movementData.height;
    return new WrappedAxisAlignedBB(
      positionX - width, positionY, positionZ - width,
      positionX + width, positionY + height, positionZ + width
    );
  }

  public static WrappedAxisAlignedBB entityBoundingBoxOf(Location center) {
    return entityBoundingBoxOf(center.getX(), center.getY(), center.getZ());
  }

  public static WrappedAxisAlignedBB entityBoundingBoxOf(
    double positionX, double positionY, double positionZ
  ) {
    return new WrappedAxisAlignedBB(
      positionX - HALF_WIDTH, positionY, positionZ - HALF_WIDTH,
      positionX + HALF_WIDTH, positionY + PLAYER_HEIGHT, positionZ + HALF_WIDTH
    );
  }
}