package de.jpx3.intave.world.permission;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.BlockPlacePermissionCheck;
import de.jpx3.intave.event.bukkit.BukkitEventSubscriber;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.patchy.annotate.PatchyTranslateParameters;
import de.jpx3.intave.reflect.ReflectionFailureException;
import de.jpx3.intave.reflect.ReflectiveAccess;
import de.jpx3.intave.user.UserRepository;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@PatchyAutoTranslation
public final class LegacyCBPlacePermissionResolver implements BlockPlacePermissionCheck, BukkitEventSubscriber {
  private ResolverMode resolverMode = ResolverMode.PREFETCH_DUMMY;

  @Override
  @PatchyAutoTranslation
  public boolean hasPermission(Player player, World world, boolean mainHand, int blockX, int blockY, int blockZ, int enumDirection, int typeId, byte data) {
    if(resolverMode == ResolverMode.PREFETCH_DUMMY) {
      if(world.isChunkLoaded(blockX >> 4, blockZ >> 4)) {
        ItemStack item = UserRepository.userOf(player).meta().inventoryData().heldItem();//player.getInventory().getItemInHand();
        CraftChunk chunk = (CraftChunk) world.getChunkAt(blockX >> 4, blockZ >> 4);
        CustomCraftBlock placedBlock = new CustomCraftBlock(chunk, blockX, blockY, blockZ, typeId, data);
        CraftBlockState placedBlockState = new CraftBlockState(placedBlock);
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        CraftWorld craftWorld = worldServer.getWorld();
        Block blockClicked = craftWorld.getBlockAt(blockX, blockY, blockZ);

        boolean canBuild = canBuildReflectiveCall(craftWorld, player, placedBlock.getX(), placedBlock.getZ());
        BlockPlaceEvent event = new PermissionCheckBlockPlaceEvent(placedBlock, placedBlockState, blockClicked, item, player, canBuild);

        try {
          IntavePlugin.singletonInstance().eventLinker().fireExternalEvent(event);
        } catch (Exception exception) {
          exception.printStackTrace();
          IntavePlugin.singletonInstance().logger().error("Failed to prefetch block-permission, switching to allow-all permission strategy");
          resolverMode = ResolverMode.PREALLOW_CNTX;
          return true;
        }
        return !event.isCancelled();
      }
      return false;
    } else {
      return resolverMode == ResolverMode.PREALLOW_CNTX;
    }
  }

  private Method canBuildMethod;

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private boolean canBuildReflectiveCall(CraftWorld world, Player player, int x, int z) {
    if(canBuildMethod == null) {
      try {
        canBuildMethod = ReflectiveAccess.lookupCraftBukkitClass("event.CraftEventFactory").getDeclaredMethod("canBuild", CraftWorld.class, Player.class, Integer.TYPE, Integer.TYPE);
        if(!canBuildMethod.isAccessible()) {
          canBuildMethod.setAccessible(true);
        }
      } catch (NoSuchMethodException exception) {
        throw new ReflectionFailureException(exception);
      }
    }
    try {
      return (boolean) canBuildMethod.invoke(null, world, player, x, z);
    } catch (IllegalAccessException | InvocationTargetException exception) {
      throw new ReflectionFailureException(exception);
    }
  }

  @Override
  public void open() {
    IntavePlugin.singletonInstance().eventLinker().registerEventsIn(this);
  }

  @Override
  public void close() {
    IntavePlugin.singletonInstance().eventLinker().unregisterEventsIn(this);
  }

  @BukkitEventSubscription(priority = EventPriority.LOWEST)
  public void onPre(BlockPlaceEvent place) {
    if(!(place instanceof PermissionCheckBlockPlaceEvent) && resolverMode == ResolverMode.PREFETCH_DUMMY) {
      place.setCancelled(true);
    }
  }

  @BukkitEventSubscription(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPost(BlockPlaceEvent place) {
    if(!(place instanceof PermissionCheckBlockPlaceEvent) && resolverMode == ResolverMode.PREFETCH_DUMMY) {
      place.setCancelled(false);
    }
  }

  public static class PermissionCheckBlockPlaceEvent extends BlockPlaceEvent {
    public PermissionCheckBlockPlaceEvent(Block placedBlock, BlockState replacedBlockState, Block placedAgainst, ItemStack itemInHand, Player thePlayer, boolean canBuild) {
      super(placedBlock, replacedBlockState, placedAgainst, itemInHand, thePlayer, canBuild);
    }
  }
}