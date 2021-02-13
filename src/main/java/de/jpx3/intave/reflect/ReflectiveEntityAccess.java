package de.jpx3.intave.reflect;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

@PatchyAutoTranslation
public final class ReflectiveEntityAccess {
  @PatchyAutoTranslation
  public static void setOnGround(Player player, boolean onGround) {
    Entity entity = ((CraftEntity) player).getHandle();
    entity.onGround = onGround;
  }

  @PatchyAutoTranslation
  public static boolean onGround(Player player) {
    Entity entity = ((CraftEntity) player).getHandle();
    return entity.onGround;
  }
}