package de.jpx3.intave.world.collision;

import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import org.bukkit.entity.Player;

import java.util.List;

public final class CollisionFactory {
  public static List<WrappedAxisAlignedBB> getCollisionBoxes(
    Player player,
    WrappedAxisAlignedBB boundingBox
  ) {
    AbstractCollisionDefaultResolver collisionResolver = CollisionEngine.collisionResolver();
    return collisionResolver.getCollisionBoxes(player, boundingBox);
  }
}