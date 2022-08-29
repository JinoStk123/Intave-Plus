package de.jpx3.intave.block.collision;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.share.BoundingBox;
import de.jpx3.intave.user.User;
import org.bukkit.Material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CollisionModifiers {
  private static final Map<Material, CollisionModifier> repository = new ConcurrentHashMap<>();

  public static void setup() {
    setup(ScaffoldingCollisionModifier.class);
    setup(CarpetCollisionModifier.class);
    setup(ShulkerCollisionModifier.class);
    setup(PowderSnowCollisionModifier.class);
  }

  private static void setup(Class<? extends CollisionModifier> modifierClass) {
    CollisionModifier modifier;
    try {
      modifier = modifierClass.newInstance();
    } catch (InstantiationException | IllegalAccessException | Error exception) {
      throw new IllegalStateException("Unable to load collision modifier " + modifierClass, exception);
    }
    for (Material value : Material.values()) {
      if (modifier.matches(value)) {
        repository.put(value, modifier);
      }
    }
  }

  public static BlockShape modified(Material type, User user, BoundingBox userBox, int posX, int posY, int posZ, BlockShape shape) {
    return repository.get(type).modify(user, userBox, posX, posY, posZ, shape);
  }

  public static boolean playerInImaginaryBlock(Material type, User user, int posX, int posY, int posZ, int data) {
    return repository.get(type).playerInImaginaryBlock(type, user, posX, posY, posZ, data);
  }

  public static boolean isModified(Material type) {
    return repository.containsKey(type);
  }
}
