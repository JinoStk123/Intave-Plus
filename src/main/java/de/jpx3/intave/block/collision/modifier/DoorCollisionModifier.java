package de.jpx3.intave.block.collision.modifier;

import de.jpx3.intave.block.collision.CollisionOrigin;
import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.shape.BlockShapes;
import de.jpx3.intave.share.BoundingBox;
import de.jpx3.intave.user.User;
import org.bukkit.Material;

public final class DoorCollisionModifier extends CollisionModifier {
  @Override
  public BlockShape modify(User user, BoundingBox userBox, int posX, int posY, int posZ, BlockShape shape, CollisionOrigin type) {
    return type == CollisionOrigin.INTERSECTION_CHECK ? BlockShapes.emptyShape() : shape;
  }

  @Override
  public boolean matches(Material material) {
    return material.name().endsWith("_DOOR") || material.name().endsWith("_TRAPDOOR");
  }
}
