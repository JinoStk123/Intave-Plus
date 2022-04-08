package de.jpx3.intave.block.shape.pipe.patch;

import de.jpx3.intave.block.type.BlockTypeAccess;
import de.jpx3.intave.block.variant.BlockVariantAccess;
import de.jpx3.intave.shade.BoundingBox;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public final class CarpetPatch extends BoundingBoxPatch {
  @Override
  public List<BoundingBox> patch(World world, Player player, Block block, List<BoundingBox> bbs) {
    return patch(world, player, block.getX(), block.getY(), block.getZ(), BlockTypeAccess.typeAccess(block, player), BlockVariantAccess.variantAccess(block), bbs);
  }

  @Override
  protected List<BoundingBox> patch(World world, Player player, int posX, int posY, int posZ, Material type, int blockState, List<BoundingBox> bbs) {
//    System.out.println("CarpetPatch.patch at " + posX + " " + posY + " " + posZ + " with state " + blockState);
//    User user = UserRepository.userOf(player);
    return /*user.meta().protocol().protocolVersion() <= 5 ?
      Collections.emptyList() : */bbs;
  }

  @Override
  public boolean appliesTo(Material material) {
    return material.name().contains("CARPET");
  }
}
