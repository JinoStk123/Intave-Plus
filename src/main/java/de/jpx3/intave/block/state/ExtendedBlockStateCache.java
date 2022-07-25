package de.jpx3.intave.block.state;

import de.jpx3.intave.user.User;

/**
 * A block state access  merges
 * {@link InvalidatableBlockStateCache} featuring methods for type caching
 * {@link OverridableBlockStateCache}, featuring methods for type override and
 * {@link BlockStateCache} for basic lookup access.
 *
 * @see User
 * @see BlockState
 * @see BlockStateCache
 * @see OverridableBlockStateCache
 * @see InvalidatableBlockStateCache
 * @see MultiChunkKeyExtendedBlockStateCache
 */
public interface ExtendedBlockStateCache extends BlockStateCache, InvalidatableBlockStateCache, OverridableBlockStateCache {
}