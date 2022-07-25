package de.jpx3.intave.block.state;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.shape.resolve.ShapeResolver;
import de.jpx3.intave.user.User;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * A BlockShapeAccess serves as an auto-resolving cache for block types, block bounding boxes and block variants.
 *
 * @see User
 * @see InvalidatableBlockStateCache
 * @see OverridableBlockStateCache
 * @see ExtendedBlockStateCache
 * @see MultiChunkKeyExtendedBlockStateCache
 * @see EmptyExtendedBlockStateCache
 * @see ShapeResolver
 */
public interface BlockStateCache {
  /**
   * Resolve-if-not-cached and retrieve the outline shape of the specified block.
   *
   * @param posX the blocks x coordinate
   * @param posY the blocks y coordinate
   * @param posZ the blocks z coordinate
   * @return the blocks bounding boxes
   */
  @NotNull BlockShape outlineShapeAt(int posX, int posY, int posZ);

  /**
   * Resolve-if-not-cached and retrieve the collision shape of the specified block.
   *
   * @param posX the blocks x coordinate
   * @param posY the blocks y coordinate
   * @param posZ the blocks z coordinate
   * @return the blocks bounding boxes
   */
  @NotNull BlockShape collisionShapeAt(int posX, int posY, int posZ);

  /**
   * Resolve-if-not-cached and retrieve the type of the specified block.
   *
   * @param posX the blocks x coordinate
   * @param posY the blocks y coordinate
   * @param posZ the blocks z coordinate
   * @return the blocks type
   */
  @NotNull Material typeAt(int posX, int posY, int posZ);

  /**
   * Resolve-if-not-cached and retrieve the variant index of the specified block.
   *
   * @param posX the blocks x coordinate
   * @param posY the blocks y coordinate
   * @param posZ the blocks z coordinate
   * @return the blocks variant index
   */
  int variantIndexAt(int posX, int posY, int posZ);
}
