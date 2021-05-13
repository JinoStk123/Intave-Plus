package de.jpx3.intave.reflect.hitbox.typeaccess;

import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.event.service.entity.PacketEntityTypeResolver;
import de.jpx3.intave.reflect.hitbox.HitBoxBoundaries;

import java.util.HashMap;
import java.util.Map;

public final class EntityTypeAccess {
  private final static Map<Integer, HitBoxBoundaries> entityHitBoxMap = new HashMap<>();
  private final static boolean NATIVE_RESOLVE = MinecraftVersions.VER1_14_0.atOrAbove();

  static final int ENTITY_ID_LOOKUP = 200;

  public static void setup() {
    if (NATIVE_RESOLVE) {
      for (int id = 0; id < ENTITY_ID_LOOKUP; id++) {
        HitBoxBoundaries hitBoxBoundaries = PacketEntityTypeResolver.EntityTypeResolver.resolveBoundariesOf(id);
        entityHitBoxMap.put(id, hitBoxBoundaries);
      }
    } else {
      LegacyEntityBoundariesResolver.pollTo(entityHitBoxMap);
    }
  }

  public static HitBoxBoundaries boundariesFromId(int id) {
    return entityHitBoxMap.get(id);
  }
}