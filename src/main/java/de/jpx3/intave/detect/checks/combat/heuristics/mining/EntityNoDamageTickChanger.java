package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.logging.IntaveLogger;
import de.jpx3.intave.reflect.ReflectiveHandleAccess;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public final class EntityNoDamageTickChanger {
  private static boolean hitDelayLinkageError = false;

  public static void applyHurtTimeChangeTo(User user, int durationTicks) {
    if (hitDelayLinkageError) {
      return;
    }

    Player player = user.player();
    UserMetaAttackData attackData = user.meta().attackData();

    // Already changed
    if (attackData.miningStartEntityDamageTicksBefore != -1) {
      return;
    }

    int noDamageTicksBefore = resolveNoDamageTicksOf(player);
    int newNoDamageTicks = calculateNewNoDamageTicks(noDamageTicksBefore);
    attackData.miningStartEntityDamageTicksBefore = noDamageTicksBefore;
    setNoDamageTicksOf(player, newNoDamageTicks);
    Synchronizer.synchronizeDelayed(new Runnable() {
      @Override
      public void run() {
        removeNoDamageTickChangeOf(user);
      }
    }, durationTicks);
  }

  private static int calculateNewNoDamageTicks(int noDamageTicks) {
    return Math.max(0, noDamageTicks - 2);
  }

  private static void removeNoDamageTickChangeOf(User user) {
    Player player = user.player();
    UserMetaAttackData attackData = user.meta().attackData();
    int noDamageTicksBefore = attackData.miningStartEntityDamageTicksBefore;
    int expectedPlayerNoDamageTicks = calculateNewNoDamageTicks(noDamageTicksBefore);
    if (expectedPlayerNoDamageTicks != resolveNoDamageTicksOf(player)) {
      // The server has changed the noDamageTicks field, do not override
      attackData.miningStartEntityDamageTicksBefore = -1;
      return;
    }
    setNoDamageTicksOf(player, noDamageTicksBefore);
    attackData.miningStartEntityDamageTicksBefore = -1;
  }

  private static int resolveNoDamageTicksOf(Player player) {
    try {
      Object handle = ReflectiveHandleAccess.handleOf(player);
      Field maxDamageTicks = handle.getClass().getField("maxNoDamageTicks");
      return (int) maxDamageTicks.get(handle);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      IntaveLogger.logger().error("Intave has problems accessing an entity field");
      hitDelayLinkageError = true;
    }
    return -1;
  }

  private static void setNoDamageTicksOf(Player player, int noDamageTicks) {
    try {
      Object handle = ReflectiveHandleAccess.handleOf(player);
      Field maxDamageTicks = handle.getClass().getField("maxNoDamageTicks");
      maxDamageTicks.set(handle, noDamageTicks);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      IntaveLogger.logger().error("Intave has problems accessing an entity field");
      hitDelayLinkageError = true;
    }
  }
}