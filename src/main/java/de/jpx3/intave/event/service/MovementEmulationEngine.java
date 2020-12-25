package de.jpx3.intave.event.service;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.detect.checks.movement.physics.CollisionHelper;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.user.UserMetaViolationLevelData;
import de.jpx3.intave.world.collision.CollisionFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public final class MovementEmulationEngine {
  private final IntavePlugin plugin;
  private final static boolean DEBUG_EMULATION = false;

  public MovementEmulationEngine(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public void emulationSetBack(Player player, Vector motion, int ticks) {
    User user = UserRepository.userOf(player);
    UserMetaViolationLevelData violationLevelData = user.meta().violationLevelData();

    if(violationLevelData.isInActiveTeleportBundle) {
      return;
    }

    // starting conditions

    violationLevelData.isInActiveTeleportBundle = true;

    if (DEBUG_EMULATION) {
      player.sendMessage("[E+] " + motion + " (" + ticks + " ticks)");
    }

    proceedEmulationTick(player, motion, ticks);
  }

  public void proceedEmulationTick(Player player, Vector motion, int ticks) {
    if(!Bukkit.isPrimaryThread()) {
      Vector finalMotion1 = motion;
      Synchronizer.synchronizeDelayed(() -> proceedEmulationTick(player, finalMotion1, ticks), 0);
      return;
    }

    User user = UserRepository.userOf(player);
    User.UserMeta meta = user.meta();
    UserMetaMovementData movementData = meta.movementData();
    UserMetaViolationLevelData violationLevelData = meta.violationLevelData();

    // check motion status (velocity?)
    Location futurePosition = movementData.verifiedLocation;
    WrappedAxisAlignedBB boundingBox = CollisionHelper.entityBoundingBoxOf(futurePosition);
    motion = motionProceed(motion, player, boundingBox);

    futurePosition = futurePosition.clone().add(motion);
    futurePosition.setYaw(movementData.rotationYaw);
    futurePosition.setPitch(movementData.rotationPitch);

    if ((Math.abs(motion.getX()) < 0.05 && Math.abs(motion.getZ()) < 0.05 && motion.getY() == 0.0) || ticks <= 0) {
      violationLevelData.isInActiveTeleportBundle = false;
      if (DEBUG_EMULATION) {
        player.sendMessage("[E-] (" + ticks + " ticks remaining)");
      }
    } else {
      // teleport
      //player.teleport(futurePosition);
      teleport(player, futurePosition);

      if (DEBUG_EMULATION) {
        String s = "[E/] " + MathHelper.formatMotion(motion) + " at " + MathHelper.formatPosition(futurePosition) + " (" + ticks + " ticks remaining)";
        player.sendMessage(s);
      }
      //   s += " @" + movementData.entityBoundingBox();


      Vector finalMotion = motion;
      Synchronizer.synchronizeDelayed(() -> {
        proceedEmulationTick(player, finalMotion, ticks - 1);
      }, 1);
    }

    // velocity
    Vector futureMotion = motionProceed(motion, player, boundingBox);
    player.setVelocity(futureMotion);
  }

  private Vector motionProceed(Vector lastMotion, Player player, WrappedAxisAlignedBB boundingBox) {
    double motionY = (lastMotion.getY() - 0.08) * 0.98f;
    Vector collisionVector = resolveCollisionVector(player, boundingBox, lastMotion.getX(), motionY, lastMotion.getZ());
    boolean onGround = motionY != collisionVector.getY() && motionY < 0.0;
    motionY = collisionVector.getY();
    double multiplier = onGround ? 0.546f : 0.91f;
    double motionX = lastMotion.getX() * multiplier;
    double motionZ = lastMotion.getZ() * multiplier;
    collisionVector = resolveCollisionVector(player, boundingBox, motionX, motionY, motionZ);

    // webs, water

    return collisionVector;
  }

  private void teleport(Player player, Location teleportLocation) {
    User user = UserRepository.userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    WrappedAxisAlignedBB entityBoundingBox = CollisionHelper.entityBoundingBoxOf(
      teleportLocation.getX(), teleportLocation.getY(), teleportLocation.getZ()
    );
    movementData.setBoundingBox(entityBoundingBox);
    movementData.verifiedLocation = teleportLocation.clone();
    player.teleport(teleportLocation);
  }

  public static Vector resolveCollisionVector(
    Player player,
    WrappedAxisAlignedBB entityBoundingBox,
    double motionX, double motionY, double motionZ
  ) {
    List<WrappedAxisAlignedBB> collisionBoxes = CollisionFactory.getCollisionBoxes(player, entityBoundingBox.addCoord(motionX, motionY, motionZ));

    // motion y
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionY = collisionBox.calculateYOffset(entityBoundingBox, motionY);
    }
    entityBoundingBox = (entityBoundingBox.offset(0.0D, motionY, 0.0D));

    // motion x
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionX = collisionBox.calculateXOffset(entityBoundingBox, motionX);
    }
    entityBoundingBox = entityBoundingBox.offset(motionX, 0.0D, 0.0D);

    // motion z
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionZ = collisionBox.calculateZOffset(entityBoundingBox, motionZ);
    }

    return new Vector(motionX, motionY, motionZ);
  }
}