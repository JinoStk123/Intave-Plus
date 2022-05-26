package de.jpx3.intave.block.collision;

import com.comphenix.protocol.wrappers.BlockPosition;
import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.tick.ShulkerBox;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.user.User;
import org.bukkit.Material;

public final class ShulkerCollisionModifier extends CollisionModifier {
  @Override
  public BlockShape modify(User user, BoundingBox userBox, int posX, int posY, int posZ, BlockShape shape) {
    BlockPosition blockPosition = new BlockPosition(posX, posY, posZ);
    ShulkerBox shulker = user.meta().movement().shulkerData.get(blockPosition);
    return shulker != null ? shulker.originShape().contextualized(posX, posY, posZ) : shape;
  }

  @Override
  public boolean matches(Material material) {
    return material.name().contains("SHULKER_BOX");
  }
}
