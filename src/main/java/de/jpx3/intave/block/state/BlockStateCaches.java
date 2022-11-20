package de.jpx3.intave.block.state;

import de.jpx3.intave.block.shape.ShapeResolverPipeline;
import de.jpx3.intave.block.shape.resolve.ShapeResolver;
import org.bukkit.entity.Player;

public final class BlockStateCaches {
  public static ExtendedBlockStateCache cacheForPlayer(Player player) {
    return cacheForPlayerWithResolver(player, ShapeResolver.globalPipeline());
  }

  public static ExtendedBlockStateCache cacheForPlayerWithResolver(Player player, ShapeResolverPipeline resolver) {
    return new MultiChunkKeyExtendedBlockStateCache(player, resolver);
  }

  public static ExtendedBlockStateCache emptyCache() {
    return new EmptyExtendedBlockStateCache();
  }
}
