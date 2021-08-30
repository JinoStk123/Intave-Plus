package de.jpx3.intave.world.blockaccess;

import de.jpx3.intave.reflect.patchy.PatchyLoadingInjector;
import de.jpx3.intave.reflect.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.reflect.patchy.annotate.PatchyTranslateParameters;
import de.jpx3.intave.user.User;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;

public final class BlockEmitter {
  public static void setup() {
    ClassLoader classLoader = BlockEmitter.class.getClassLoader();
    PatchyLoadingInjector.loadUnloadedClassPatched(classLoader, "de.jpx3.intave.world.blockaccess.BlockEmitter$InternalEmitter");
    PatchyLoadingInjector.loadUnloadedClassPatched(classLoader, "de.jpx3.intave.world.blockaccess.BlockEmitter$EmittedBlock");

  }

  public static Block emit(User user, Block input) {
    return InternalEmitter.emit(user, input.getChunk(), input.getX(), input.getY(), input.getZ());
  }

  @PatchyAutoTranslation
  public static class InternalEmitter {
    @PatchyAutoTranslation
    public static Block emit(User user, Chunk chunk, int x, int y, int z) {
      return new EmittedBlock(user, (CraftChunk) chunk, x, y, z);
    }
  }

  @PatchyAutoTranslation
  public static class EmittedBlock extends CraftBlock {
    private final User user;
    private final Location location;

    @PatchyAutoTranslation
    @PatchyTranslateParameters
    public EmittedBlock(User user, CraftChunk chunk, int x, int y, int z) {
      super(chunk, x, y, z);
      this.user = user;
    }

    {
      location = getLocation();
    }

    @Override
    public Material getType() {
      return BukkitBlockAccess.cacheAppliedTypeAccess(user, location);
    }
  }
}
