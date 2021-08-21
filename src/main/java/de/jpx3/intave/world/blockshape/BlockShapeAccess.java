package de.jpx3.intave.world.blockshape;

import de.jpx3.intave.user.User;
import de.jpx3.intave.world.blockshape.boxresolver.BoundingBoxResolver;
import de.jpx3.intave.world.wrapper.WrappedAxisAlignedBB;
import org.bukkit.Material;

import java.util.List;

/**
 * A BlockShapeAccess serves as an auto-resolving cache for block types, block bounding boxes and block variants.
 *
 * @see User
 * @see CachedBlockShapeAccess
 * @see OverrideBlockShapeAccess
 * @see OCBlockShapeAccess
 * @see MultiChunkKeyOCBlockShapeAccess
 * @see BlankUserOCBlockShapeAccess
 * @see BoundingBoxResolver
 */
public interface BlockShapeAccess {
  /**
   * Resolve-if-not-cached and retrieve the bounding boxes of the specified block.
   * @param chunkX the chunk x coordinate
   * @param chunkZ the chunk z coordinate
   * @param posX the blocks x coordinate
   * @param posY the blocks y coordinate
   * @param posZ the blocks z coordinate
   * @return the blocks bounding boxes
   */
  List<WrappedAxisAlignedBB> resolveBoxes(int chunkX, int chunkZ, int posX, int posY, int posZ);

  /**
   * Resolve-if-not-cached and retrieve the type of the specified block.
   * @param chunkX the chunk x coordinate
   * @param chunkZ the chunk z coordinate
   * @param posX the blocks x coordinate
   * @param posY the blocks y coordinate
   * @param posZ the blocks z coordinate
   * @return the blocks type
   */
  Material resolveType(int chunkX, int chunkZ, int posX, int posY, int posZ);

  /**
   * Resolve-if-not-cached and retrieve the variant index of the specified block.
   * @param chunkX the chunk x coordinate
   * @param chunkZ the chunk z coordinate
   * @param posX the blocks x coordinate
   * @param posY the blocks y coordinate
   * @param posZ the blocks z coordinate
   * @return the blocks variant index
   */
  int resolveVariant(int chunkX, int chunkZ, int posX, int posY, int posZ);
}
