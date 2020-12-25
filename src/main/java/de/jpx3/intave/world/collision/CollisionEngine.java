package de.jpx3.intave.world.collision;

import com.comphenix.protocol.utility.MinecraftVersion;
import de.jpx3.intave.adapter.ProtocolLibAdapter;

import static de.jpx3.intave.adapter.ProtocolLibAdapter.AQUATIC_UPDATE;

public final class CollisionEngine {
  private static AbstractCollisionDefaultResolver collisionResolver;

  public static void setup() {
    MinecraftVersion version = ProtocolLibAdapter.serverVersion();
    System.out.println("[Intave] Generating block collisions");

    try {
      if (version.isAtLeast(MinecraftVersion.BEE_UPDATE)) {
        collisionResolver = new CollisionResolverBeeUpdate();
      } else if (version.isAtLeast(AQUATIC_UPDATE)) {
        collisionResolver = new CollisionResolverVoxelShapes();
      } else {
        collisionResolver = new CollisionResolverLegacy();
      }
      collisionResolver.setup();

      System.out.println("[Intave] Generated successfully");
    } catch (Exception e) {
      System.out.println("[Intave] An error occurred while resolving block collisions");
      e.printStackTrace();
    }
  }

  public static AbstractCollisionDefaultResolver collisionResolver() {
    return collisionResolver;
  }
}