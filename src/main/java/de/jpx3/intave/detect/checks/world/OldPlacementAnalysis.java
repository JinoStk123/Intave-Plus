package de.jpx3.intave.detect.checks.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import de.jpx3.intave.IntaveControl;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheck;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.GarbageCollector;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.tools.RotationMathHelper;
import de.jpx3.intave.user.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class OldPlacementAnalysis extends IntaveMetaCheck<OldPlacementAnalysis.PlacementAnalysisMeta> {
  private final IntavePlugin plugin;
  private final static boolean DEBUG = false;
  /**
   * MALONS PLAYGROUND
   */

  private final Map<UUID, Block> lastBlock = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Block> penaltyBlock = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Long> lastWentDown = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Long> lastBlockPlaced = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Long> lastsneak = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Long> lastblockplace = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Long> lastinteract = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Long> lastclear = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Integer> clicks = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, Integer> lvl = GarbageCollector.watch(new ConcurrentHashMap<>());
  private final Map<UUID, List<Block>> lastblocks = GarbageCollector.watch(new ConcurrentHashMap<>());

  private final int VL_NORMAL;
  private final int VL_SAFEWALK;
  private final int VL_TIMING;

  public OldPlacementAnalysis(IntavePlugin plugin) {
    super("PlacementAnalysis", "placementanalysis", PlacementAnalysisMeta.class);
    this.plugin = plugin;

    VL_NORMAL = 3;
    VL_SAFEWALK = 5;
    VL_TIMING = 1;

//    VL_NORMAL = configuration().flagSuggestionFor("normal", 3);
//    VL_SAFEWALK = configuration().flagSuggestionFor("safewalk", 5);
//    VL_TIMING = configuration().flagSuggestionFor("timing", 1);
  }

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_PLACE"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "FLYING"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION_LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "LOOK"),
    }
  )
  public void preEventDetection(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PlacementAnalysisMeta meta = metaOf(user);
    PacketType packetType = event.getPacketType();

    if (packetType == PacketType.Play.Client.BLOCK_PLACE && IntaveControl.GOMME_MODE) {
      long noice = AccessHelper.now() - meta.lastFlyingPacket;
      if (noice < 5) {
        if (
          AccessHelper.now() - meta.lastBlockPlacementPermutation < 500 &&
            AccessHelper.now() - meta.lastTimeTeleported > 1000
        ) {
          if (meta.permutationVL < 8) {
            meta.permutationVL += 1;
          }
          if (meta.permutationVL > 4) {
            if (plugin.violationProcessor().processViolation(player, VL_TIMING, "PlacementAnalysis", "tried to permute placement packet order (" + noice + " ms -> flying)")) {
              meta.cancelBlockPlacement = true;
            }
          }
        }
        if (AccessHelper.now() - meta.lastBlockPlacementPermutation > 100) {
          meta.lastBlockPlacementPermutation = AccessHelper.now();
        }
      } else if (meta.permutationVL > 0) {
        meta.permutationVL--;
      }
    }
  }

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_PLACE")
    }
  )
  public void receivePlacementPacket(PacketEvent event) {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    User user = UserRepository.userOf(player);
    PlacementAnalysisMeta meta = metaOf(user);

    PacketContainer packet = event.getPacket();
    Integer c = packet.getIntegers().readSafely(0);

    meta.lastTimeBlockPlacedUnder = AccessHelper.now();
    meta.lastTimeBlockPlaced = AccessHelper.now();

    if (c == null) {
      c = 0;
    }

    if (c == 255) {
      meta.lastHardFaultClick = AccessHelper.now();
    }

    if (packet.getFloat().size() < 3) {
      meta.lastTimeValidBlockPlaced = AccessHelper.now();
      return;
    }

    List<Float> floatValues = packet.getFloat().getValues();
    float f1 = floatValues.get(0);
    float f2 = floatValues.get(1);
    float f3 = floatValues.get(2);

    meta.lastTimeBlockPacketArrived = AccessHelper.now();

    if (f1 == 0.0 && f2 == 0.0 && f3 == 0.0) {
      return;
    }

    Vector lastBlockFaceLook = meta.lastBlockFaceLook;
    lastBlockFaceLook.setX(f1);
    lastBlockFaceLook.setY(f2);
    lastBlockFaceLook.setZ(f3);
    meta.lastTimeValidBlockPlaced = AccessHelper.now();
  }

  @BukkitEventSubscription(priority = EventPriority.LOW)
  public void on3(BlockPlaceEvent e) {
    Player player = e.getPlayer();
    User user = UserRepository.userOf(player);
    PlacementAnalysisMeta meta = metaOf(user);

    long currentTimeMillis = AccessHelper.now();
    long diffToLastValidBlockPacket = (currentTimeMillis - meta.lastTimeValidBlockPlaced);
    boolean valid = currentTimeMillis - meta.lastTimeBlockPacketArrived < 2000;
    //boolean flag = ThreadLocalRandom.current().nextInt(1,20) < 5;
    boolean flag = true;

    if (currentTimeMillis - meta.lastMotionBlockFlag < 1000) {
      if (currentTimeMillis - meta.lastMLAJFlag < 5000) {
        if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [AJ] (probability 99%)")) {
          e.setCancelled(true);
        }
      }

      meta.lastMLAJFlag = currentTimeMillis;

      if (DEBUG) {
        player.sendMessage(ChatColor.DARK_PURPLE + "" + (currentTimeMillis - meta.lastMotionBlockFlag));
      }

      meta.lastMotionBlockFlag = 0;
      return;
    }

    if (player.getItemInHand().getType().equals(Material.WATER_LILY))
      return;

    if (diffToLastValidBlockPacket > 2000 && valid) {
      if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [TI] # 1 (probability 100%))")) {
        e.setCancelled(true);
      }
      return;
    }

    Vector lastBlockFaceLook = meta.lastBlockFaceLook;
    double f1 = lastBlockFaceLook.getX();
    double f2 = lastBlockFaceLook.getY();
    double f3 = lastBlockFaceLook.getZ();

    int vl = 0;

    Block blockPlaced = e.getBlockPlaced();
    Location location = player.getLocation();
    float expectedX = (float) ((int) location.getX() * 16) / 16 - blockPlaced.getX();
    float expectedY = (float) ((int) location.getY() * 16) / 16 - blockPlaced.getY();
    float expectedZ = (float) ((int) location.getZ() * 16) / 16 - blockPlaced.getZ();

    if (e.getBlock().getY() < e.getPlayer().getLocation().getY() && e.getBlock().getY() + 1.6 > e.getPlayer().getLocation().getY()) {
      long diff = currentTimeMillis - meta.lastTimeBlockPlacedBelow;

      if (diff < 2000 && Math.abs(MathHelper.diff(blockPlaced.getY() - 2, this.penaltyBlock.getOrDefault(player.getUniqueId(), blockPlaced).getY())) < 0.1) {
        double balance = addNumberAndGetBalance(meta.blockPlaceUnderDiff, diff, 10);

        if (balance < 380) {
          if (DEBUG)
            player.sendMessage("razan: " + balance + " / " + MathHelper.diff(blockPlaced.getY() - 2, this.penaltyBlock.getOrDefault(player.getUniqueId(), blockPlaced).getY()));

                    /*
                    if(plugin.getRetributionManager().markPlayer(player,1,"MachineBlock",CheatCategory.WORLD,"tried to place a block suspiciously."+(debug?" (pattern [RZAN]) (probability 100%)":"")))
                    {
                        e.setCancelled(true);
                    }
                     */

          //return;
        }
      }

      //player.sendMessage(ChatColor.YELLOW + "Block placement. Yaw: " + player.getLocation().getYaw() + " |  Pitch: " + player.getLocation().getPitch());

      meta.lastTimeBlockPlacedBelow = currentTimeMillis;
    }

    if (isSuspicious(f1)) {
      vl++;
    }
    if (isSuspicious(f2)) {
      vl++;
    }
    if (isSuspicious(f3)) {
      vl++;
    }

    if ((f1 < 0 || f2 < 0 || f3 < 0 || f1 > 1 || f2 > 1 || f3 > 1) && valid && flag) {
      if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [TI] # 2 (probability 100%)")) {
        meta.cancelBlockPlacement = true;
      }
      return;
    }

    if (!e.getBlock().getWorld().equals(e.getPlayer().getWorld())) {
      e.setCancelled(true);
      return;
    }

    double hsDist = e.getBlock().getRelative(BlockFace.UP).getLocation().distance(e.getPlayer().getLocation());

    if (hsDist < 1.3
      /*      && !BlockLogic.doesAffectMovementContext(e.getBlock().getRelative(BlockFace.DOWN).getLocation()) */
      && collides(e.getBlock()) < 2) {
      if (player.isSneaking()) {
        int ticksSneaking = (int) meta.ticksSneakingSync;

        if (meta.ticksSneakingSync >= 2) {
          meta.fastSneakVL = Math.max(0, meta.fastSneakVL - 5);
        } else {
          if (meta.fastSneakVL < 20) {
            meta.fastSneakVL += 1;
          }

                    /*
                    if (checkable.getMeta().getVioValues().fastSneakVL > 10) {
                        if (plugin.getRetributionManager().markPlayer(player, 1, "MachineBlock", CheatCategory.WORLD, "tried to place a block suspiciously. " + (debug ? " (pattern [NOVA]) (probability high)" : ""))) {
                            e.setCancelled(true);
                        }
                    }
                     */
        }
      }

      long timeSinceLastBackwarsMove = currentTimeMillis - meta.lastTimeMovedBackwards;
      if (timeSinceLastBackwarsMove > 2000 && meta.lastYMovement == 0.0) {
/*        long timeSinceLastFlag = currentTimeMillis - meta.lastTimeBlockPlacedUnderWithoutBackwardsMotion;

        if (DEBUG) {
          player.sendMessage((timeSinceLastFlag < 800 ? ChatColor.RED : ChatColor.GREEN) + " CRITICAL TIME: " + timeSinceLastFlag);
        }

        if (timeSinceLastFlag < 800) {
          if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "tried to place a block suspiciously " + (DEBUG ? " (pattern [RYT]) (probability high)" : ""))) {
            e.setCancelled(true);
          }
        }

        meta.lastTimeBlockPlacedUnderWithoutBackwardsMotion = currentTimeMillis;*/
      }

      if (meta.lastBlockPlacedUnder != null && player.getWorld().equals(meta.lastBlockPlacedUnder.getWorld())) {
        meta.lastBPULocationDist = location.distance(meta.lastBlockPlacedUnder);
      }

      meta.lastBlockPlacedUnder = location;

      if (currentTimeMillis - meta.lastBlockPlaceUnderBlockRequest < 2000) {
        e.setCancelled(true);
        return;
      }

      if (meta.cryptaPlacementBuffer) {
        if (currentTimeMillis - meta.lastCryptaBuffer < 1000) {
          if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [ISPX] (probability high)")) {
            e.setCancelled(true);
          }
        } else {
          meta.cryptaPlacementBuffer = false;
        }
      }

      if (currentTimeMillis - meta.lastTimeSuspiciousForItemSpoof <= 2000) {
        if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [ISPF] (probability high)")) {
          e.setCancelled(true);
        }
        meta.lastTimeSuspiciousForItemSpoof = 0;
        return;
      }

      if (currentTimeMillis - meta.lastTimeSuspiciousForSaveWalk <= 200) {
        meta.suspiciousSafeWalkBlockPlaces++;

        if (meta.suspiciousSafeWalkBlockPlaces > 1) {
          double prob = MathHelper.map(meta.suspiciousSafeWalkBlockPlaces, 2, 5, 90, 100);
          /*if (plugin.violationProcessor().processViolation(player, VL_SAFEWALK, "PlacementAnalysis", "safewalk; (pattern [LL])")) {
            e.setCancelled(true);
          }*/
          return;
        }
      } else if (meta.suspiciousSafeWalkBlockPlaces > 0) {
        meta.suspiciousSafeWalkBlockPlaces--;
      }

      if (vl > 2 && valid) {
        if (meta.scaffoldwalkVL * 1.1 < 100) {
          meta.scaffoldwalkVL *= 1.1;
        }
      } else if (valid) {
        if (meta.scaffoldwalkVL * 0.6 > 50) {
          meta.scaffoldwalkVL *= 0.6;
        } else {
          meta.scaffoldwalkVL = 50;
        }
      }

      //player.sendMessage("[Intave] Scaffold: "+MathHelper.roundFromDouble(checkable.getMeta().getHeuristicValues().scaffoldwalkVL,5) +"%");

      if (meta.scaffoldwalkVL > 80 && flag) {
        if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [II] (probability " + MathHelper.formatDouble(meta.scaffoldwalkVL, 5) + "%)")) {
          e.setCancelled(true);
        }
        return;
      }

      double f = player.getTargetBlock((Set<Material>) null, 3).getLocation().distance(blockPlaced.getLocation());

      if (f > 0 && f < 1 && hsDist < 1) {
        if (DEBUG) {
          player.sendMessage(hsDist + " - " + player.getTargetBlock((Set<Material>) null, 3).getLocation().distance(blockPlaced.getLocation()));
        }

        if (currentTimeMillis - meta.lastTimeSuspiciousForScaffoldWalk < 2000 && flag) {
          if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [IL] (probability 80%")) {
            e.setCancelled(true);
            meta.cancelBlockPlacement = true;
          }
          return;
        }

        meta.lastTimeSuspiciousForScaffoldWalk = currentTimeMillis;
      }

//      if (!e.isCancelled()) {
      int size = 8;
      List<Long> blockPlaceInCornerDiff = meta.blockPlaceInCornerDiff;

      if (blockPlaceInCornerDiff.size() >= size) {
        blockPlaceInCornerDiff.remove(0);
      }

      if (e.getBlockAgainst().getLocation().getY() == blockPlaced.getLocation().getY()) {
        blockPlaceInCornerDiff.add(currentTimeMillis - meta.lastTimeBlockPlacedCorner);
        meta.lastTimeBlockPlacedCorner = currentTimeMillis;
      } else {
        blockPlaceInCornerDiff.add(currentTimeMillis - meta.lastTimeBlockPlacedCorner + 1000);
      }

      if (blockPlaceInCornerDiff.size() >= size) {
        if (!lastblocks.containsKey(e.getPlayer().getUniqueId())) {
          return;
        }

        double balance = RotationMathHelper.averageOf(blockPlaceInCornerDiff);
        boolean directional = isDirectional(lastblocks.get(e.getPlayer().getUniqueId()));

        if (DEBUG) {
          player.sendMessage(balance + "ms avg place delay. Directional: " + directional + " | LF: " + (AccessHelper.now() - meta.lastHardFaultClick));
        }

        boolean noHardFault = AccessHelper.now() - meta.lastHardFaultClick > 8000;
        boolean noSneaking = AccessHelper.now() - meta.lastSneakingMove > 8000;
        boolean recentJump = AccessHelper.now() - meta.lastFullJump < 750;
        double minBalance = (directional ? ((recentJump ? 450 : noHardFault ? (noSneaking ? 500 : 300) : (noSneaking ? 300 : 200))) : 150);
//          player.sendMessage(balance + " " + minBalance);
        if (balance < minBalance) {
          if (plugin.violationProcessor().processViolation(player, VL_TIMING, "PlacementAnalysis", "tried to place a block too quickly (~" + ((int) balance) + "ms/block | " + ((int) minBalance) + "ms/block)")) {
            //checkable.getMeta().getVioValues().cancelBlockPlacement = true;
            e.setCancelled(true);
          }
        }
      }
    }
//    }

    /*
        Coded by malon c:
     */

    Block target = player.getTargetBlock((Set<Material>) null, 10);
    if (
      !target.getRelative(BlockFace.NORTH).equals(e.getBlock()) &&
        !target.getRelative(BlockFace.EAST).equals(e.getBlock()) &&
        !target.getRelative(BlockFace.SOUTH).equals(e.getBlock()) &&
        !target.getRelative(BlockFace.WEST).equals(e.getBlock()) &&
        !target.equals(e.getBlock())
    ) {
      if (currentTimeMillis - meta.lastTimeBlockPlacedUnder < 380 && player.getVelocity().getY() < 0) {
        if (
          player.isSprinting()
        ) {
          meta.scaffoldwalkVL = Math.min(meta.scaffoldwalkVL * 1.1, 100);
          //if (meta.scaffoldwalkVL > 90) {
          if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "pattern [LI] (probability 88%)")) {
            e.setCancelled(true);
          }
          //}
        }
      }
    }
  }

  private boolean isDirectional(List<Block> blocks) {
    int lastBlockX = 0, lastBlockY = 0, lastBlockZ = 0;

    boolean lockedOnX = false,
      lockedOnZ = false;

    boolean first = true;

    for (Block block : blocks) {
      if (!first) {
        if (lastBlockY != block.getY()) {
          return false;
        }

        if (lastBlockX == block.getX()) {
          lockedOnX = true;
        } else if (lockedOnX) {
          return false;
        }

        if (lastBlockZ == block.getZ()) {
          lockedOnZ = true;
        } else if (lockedOnZ) {
          return false;
        }
      }

      lastBlockX = block.getX();
      lastBlockY = block.getY();
      lastBlockZ = block.getZ();
      first = false;
    }

    return lockedOnX || lockedOnZ;
  }

  public double addNumberAndGetBalance(List<Long> doubles, long toAdd, int maxSize) {
    return RotationMathHelper.averageOf(addDynamic(doubles, toAdd, maxSize));
  }

  private <T> List<T> addDynamic(List<T> list, T toAdd, double maxSize) {
    list.add(toAdd);
    if (list.size() > maxSize)
      list.remove(0);
    return list;
  }

  private boolean isSuspicious(double f) {
    return (f == 0 || f == 0.5 || f >= 1);
  }

  private int collides(Block b) {
    int hs = 0;

    if (!b.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR))
      hs++;
    if (!b.getRelative(BlockFace.EAST).getType().equals(Material.AIR))
      hs++;
    if (!b.getRelative(BlockFace.NORTH).getType().equals(Material.AIR))
      hs++;
    if (!b.getRelative(BlockFace.WEST).getType().equals(Material.AIR))
      hs++;

    return hs;
  }

  @BukkitEventSubscription
  public void processCheckableMove(PlayerMoveEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();
    User user = UserRepository.userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    UserMetaAbilityData abilityData = user.meta().abilityData();

    PlacementAnalysisMeta meta = metaOf(user);

    double motionX = movementData.motionX();
    double motionZ = movementData.motionZ();
    double motionXZ = Math.hypot(motionX, motionZ);
    double lastMotionXZ = Math.hypot(movementData.physicsMotionX, movementData.physicsMotionZ);

    double motionY = movementData.motionY();

    if (!this.lastBlockPlaced.containsKey(uuid)) {
      this.lastBlockPlaced.put(uuid, System.currentTimeMillis());
    }
    if (!this.lastWentDown.containsKey(uuid)) {
      this.lastWentDown.put(uuid, System.currentTimeMillis());
    } else if (movementData.artificialFallDistance > 0 && e.getTo().getY() - e.getFrom().getY() < 0) {
      this.lastWentDown.replace(uuid, System.currentTimeMillis());
    }
    double xzMotionDiff = MathHelper.diff(motionXZ, lastMotionXZ);
    double xzConjToLastxz = motionXZ / lastMotionXZ;
    if (AccessHelper.now() - meta.lastTimeBlockPlacedUnder < 500 && motionXZ > 0 && !abilityData.flying()) {
      boolean unsneak = !movementData.sneaking && AccessHelper.now() - meta.lastSneakToggled < 100;
      boolean bad = (xzConjToLastxz < 0.40 /*|| (MathHelper.diff(xzConjToLastxz, 1) < 0.0001 || false) && xzConjToLastxz != 1*/ && !movementData.sneaking && motionY == 0);
      if (bad && !movementData.lastSneaking) {
        if (DEBUG) {
          player.sendMessage((bad ? ChatColor.RED : ChatColor.GREEN) + "" + xzConjToLastxz + " / " + motionXZ);
        }
        meta.lastMotionBlockFlag = AccessHelper.now();
      }
    }

    if (e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR) && !player.isSneaking() && motionY == 0.0) {
      if (AccessHelper.now() - meta.lastTimeBlockPlacedUnder < 500) {
        double blockDist = e.getFrom().getBlock().getRelative(BlockFace.DOWN).getLocation().clone().add(0.5, 0.5, 0.5).distance(e.getTo());

        if (xzMotionDiff > 0.09) {
          meta.lastTimeSuspiciousForSaveWalk = AccessHelper.now();
        }
      }
    }

    if (motionY > 0.4 && meta.lastYMovement <= 0 && movementData.lastOnGround) {
//      Bukkit.broadcastMessage("jump");
      meta.lastFullJump = AccessHelper.now();
    }

    if (!lastsneak.containsKey(uuid))
      lastsneak.put(uuid, AccessHelper.now());
    if (!clicks.containsKey(uuid))
      clicks.put(uuid, 0);
    if (!lvl.containsKey(uuid))
      lvl.put(uuid, 0);
    if (!lastinteract.containsKey(uuid))
      lastinteract.put(uuid, AccessHelper.now());
    if (!lastclear.containsKey(uuid))
      lastclear.put(uuid, AccessHelper.now());
    if (!lastblockplace.containsKey(uuid))
      lastblockplace.put(uuid, AccessHelper.now());
    //if (!lastmove.containsKey(uuid)) lastmove.put(uuid, IIUA.currentTimeMillis());
    //if (!mode.containsKey(uuid)) mode.put(uuid, 0);

    if (player.isSneaking())
      lastsneak.replace(uuid, AccessHelper.now());
  }

  @BukkitEventSubscription
  public void on(PlayerItemHeldEvent e) {
        /*
        Player player = e.getPlayer();
        Checkable checkable = plugin.catchCheckable(player.getUniqueId());
        boolean currentSlotIsBuildable = player.getInventory().getItem(e.getPreviousSlot()) != null && player.getInventory().getItem(e.getPreviousSlot()).getType().isBlock();
        long lastBlockPlace = IIUA.currentTimeMillis() - checkable.getMeta().getTimedValues().lastTimeBlockPlaced;

        if (lastBlockPlace < 2 && currentSlotIsBuildable) {
            int lastSlotDetected = checkable.getMeta().getVioValues().lastSusiciousMBCBlockSwitchSlot;
            double vlToAdd = lastSlotDetected == e.getPreviousSlot() ? 3 : 1;

            if (checkable.getMeta().getVioValues().itemSpoofVl < 8) {
                checkable.getMeta().getVioValues().itemSpoofVl += vlToAdd;
            }

            e.setCancelled(true);
            player.getInventory().setHeldItemSlot(e.getPreviousSlot());

            if (checkable.getMeta().getVioValues().itemSpoofVl > 5) {
                checkable.getMeta().getVioValues().lastTimeSuspiciousForItemSpoof = IIUA.currentTimeMillis();
            }

            checkable.getMeta().getVioValues().lastSusiciousMBCBlockSwitchSlot = e.getPreviousSlot();
        } else if (checkable.getMeta().getVioValues().itemSpoofVl > 0) {
            checkable.getMeta().getVioValues().itemSpoofVl--;
        }

         */
  }

  @BukkitEventSubscription
  public void on(PlayerToggleSneakEvent e) {
    Player player = e.getPlayer();
    UUID uniqueId = player.getUniqueId();
    User user = UserRepository.userOf(player);
    PlacementAnalysisMeta meta = metaOf(user);

    meta.lastSneakingMove = AccessHelper.now();

    if (e.isSneaking() && Math.abs(meta.lastYMovement) < 0.005 && AccessHelper.now() - meta.lastTimeBlockPlaced < 500) {
      meta.lastSneakWasSuspicious = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR);
    }

    if (!e.isSneaking()) {
      int tSneaking = Math.toIntExact(meta.ticksSneaking);

      boolean suspiciousEquality = tSneaking == meta.lastSneakDurationTicks;
      boolean suspiciousLow = tSneaking <= 1;

      if (meta.lastSneakWasSuspicious && (suspiciousEquality)) {
        int vl = meta.machineSneakVL + 1;

        if (vl > 8) {
          if (DEBUG) {
            player.sendMessage("Scaffold? (" + tSneaking + ") (vl: " + vl + ")");
          }
          meta.lastMotionBlockFlag = AccessHelper.now();
        } else {
          meta.machineSneakVL++;
        }

      } else if (meta.machineSneakVL > 1) {
        meta.machineSneakVL -= 2;
      }

      meta.lastSneakDurationTicks = tSneaking;
      meta.lastSneakWasSuspicious = false;
    }
  }

  @BukkitEventSubscription
  public void on(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PlacementAnalysisMeta meta = metaOf(user);

    if (player.isSneaking()) {
      meta.ticksSneakingSync++;
    } else {
      meta.ticksSneakingSync = 0;
    }
  }

  @BukkitEventSubscription
  public void on(PlayerQuitEvent event) throws IllegalAccessException {
    for (Field declaredField : getClass().getDeclaredFields()) {
      if (declaredField.getType().isAssignableFrom(Map.class)) {
        declaredField.setAccessible(true);
        ((Map<?, ?>) declaredField.get(this)).remove(event.getPlayer().getUniqueId());
      }
    }
  }

  @BukkitEventSubscription
  public void onPlace(BlockPlaceEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();
    User user = UserRepository.userOf(player);
    PlacementAnalysisMeta meta = metaOf(user);

    if (player.isSneaking())
      lastsneak.replace(uuid, AccessHelper.now());
    if (!lastsneak.containsKey(uuid))
      lastsneak.put(uuid, AccessHelper.now());
    if (!clicks.containsKey(uuid))
      clicks.put(uuid, 0);
    if (!lvl.containsKey(uuid))
      lvl.put(uuid, 0);
    if (!lastinteract.containsKey(uuid))
      lastinteract.put(uuid, AccessHelper.now());
    if (!lastclear.containsKey(uuid))
      lastclear.put(uuid, AccessHelper.now());
    if (!lastblockplace.containsKey(uuid))
      lastblockplace.put(uuid, AccessHelper.now());

    double lastcleared = AccessHelper.now() - lastclear.get(uuid);
    double blockplacedelay = AccessHelper.now() - lastblockplace.get(uuid);
    double lastsneakdelay = AccessHelper.now() - lastsneak.get(uuid);

    if (lvl.get(uuid) >= 1 && lastcleared > 1200) {
      lvl.replace(uuid, lvl.get(uuid) - 1);
      lastclear.replace(uuid, AccessHelper.now());
    }

    /**
     *
     *  Towr
     *
     */

    Block block = e.getBlock();
    if (!this.lastBlock.containsKey(uuid)) {
      this.lastBlock.put(uuid, block);
    }

    if (!this.lastBlockPlaced.containsKey(uuid)) {
      this.lastBlockPlaced.put(uuid, System.currentTimeMillis());
    }

    if (!this.lastWentDown.containsKey(uuid)) {
      this.lastWentDown.put(uuid, System.currentTimeMillis());
    }

    long lastBlockPlaced2 = System.currentTimeMillis() - this.lastBlockPlaced.get(uuid);
    long lastWentDown2 = System.currentTimeMillis() - this.lastWentDown.get(uuid);
    //Location setBackLoc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY()-0.419, player.getLocation().getZ());

    if (lastBlockPlaced2 < (lastWentDown2 - lastBlockPlaced2) && e.getPlayer().getLocation().getY() > e.getBlockPlaced().getY() && !player.hasPotionEffect(PotionEffectType.JUMP) && MathHelper.diff(block.getLocation().getY() - 2, this.penaltyBlock.getOrDefault(uuid, block).getLocation().getY()) < .0001 && block.getLocation().getY() - 1 == this.lastBlock.get(uuid).getLocation().getY()) {
      boolean legit = String.valueOf(e.getPlayer().getLocation().getY()).length() > 6;
      double lastTowerhopFlag = AccessHelper.now() - meta.lastTimeSuspiciousForTowerhop;

      if (lastTowerhopFlag < 4000 && !legit) {
        if (DEBUG)
          player.sendMessage("[Intave] Towerfix");

        if (plugin.violationProcessor().processViolation(player, VL_NORMAL, "PlacementAnalysis", "tower-check")) {
          e.setCancelled(true);
        }
      }

      meta.lastTimeSuspiciousForTowerhop = AccessHelper.now();
    }

    this.penaltyBlock.put(uuid, this.lastBlock.getOrDefault(uuid, block));
    this.lastBlock.put(uuid, block);

    if (block.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR
      || block.getLocation().add(-1, 0, -1).getBlock().getType() != Material.AIR
      || block.getLocation().add(1, 0, 1).getBlock().getType() != Material.AIR
      || block.getLocation().add(-1, 0, 1).getBlock().getType() != Material.AIR
      || block.getLocation().add(1, 0, -1).getBlock().getType() != Material.AIR
      || block.getLocation().getY() != player.getLocation().getY() - 1
    ) {
      return;
    }

    if (!lastblocks.containsKey(uuid)) {
      lastblocks.put(uuid, new ArrayList<>());
      //TODO Other list
    } else if (lastblocks.get(uuid).size() == 5) {
      lastblocks.get(uuid).remove(0);
    }
    lastblocks.get(uuid).add(block);

    /*
    Block block0m = lastblocks.get(uuid).get(0);
    Block block1m = lastblocks.get(uuid).get(1);

    if (block0m.getFace(block1m).equals(BlockFace.NORTH)) {
      lastblockplace.replace(uuid, IIUA.currentTimeMillis());
      if (block0m.getRelative(BlockFace.NORTH_EAST).equals(block)) {
        mode.replace(uuid, 1);
      } else if (block0m.getRelative(BlockFace.NORTH_WEST).equals(block)) {
        mode.replace(uuid, 1);
      } else {
        mode.replace(uuid, 0);
      }
    } else if (block0m.getFace(block1m).equals(BlockFace.EAST)) {
      lastblockplace.replace(uuid, IIUA.currentTimeMillis());
      if (block0m.getRelative(BlockFace.NORTH_EAST).equals(block)) {
        mode.replace(uuid, 1);
      } else if (block0m.getRelative(BlockFace.SOUTH_EAST).equals(block)) {
        mode.replace(uuid, 1);
      } else {
        mode.replace(uuid, 0);
      }
    } else if (block0m.getFace(block1m).equals(BlockFace.SOUTH)) {
      lastblockplace.replace(uuid, IIUA.currentTimeMillis());

      if (block0m.getRelative(BlockFace.SOUTH_EAST).equals(block)) {
        mode.replace(uuid, 1);
      } else if (block0m.getRelative(BlockFace.SOUTH_WEST).equals(block)) {
        mode.replace(uuid, 1);
      } else {
        mode.replace(uuid, 0);
      }
    } else if (block0m.getFace(block1m).equals(BlockFace.WEST)) {
      lastblockplace.replace(uuid, IIUA.currentTimeMillis());
      if (block0m.getRelative(BlockFace.SOUTH_WEST).equals(block)) {
        mode.replace(uuid, 1);
      } else if (block0m.getRelative(BlockFace.NORTH_WEST).equals(block)) {
        mode.replace(uuid, 1);
      } else {
        mode.replace(uuid, 0);
      }
    }

    lastblockplace.replace(uuid, IIUA.currentTimeMillis());
    if (lastsneakdelay > blockplacedelay && mode.get(uuid) == 0) {
      if (blockplacedelay < 600 && clicks.get(uuid) == 1) {
        lvl.replace(uuid, lvl.get(uuid) + 1);
        if (lvl.get(uuid) >= 25) {
                    /*
                    if(plugin.getRetributionManager().markPlayer(player, 1, "MachineBlock", CheatCategory.WORLD, "tried to place a block suspiciously" + (!debug ? "" : " (" + blockplacedelay + " | " + lvl.get(uuid) + ")")))
                    {
                        e.setCancelled(true);
                    }
                     *
        }
      }
    }*/

    //clicks.replace(uuid, 0);
  }

  @BukkitEventSubscription
  public void onInteract(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    UUID uuid = p.getUniqueId();

    if (!clicks.containsKey(uuid))
      clicks.put(uuid, 0);

    if (p.isSneaking())
      lastsneak.replace(uuid, AccessHelper.now());
    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
      clicks.replace(uuid, clicks.get(uuid) + 1);
  }

  public static final class PlacementAnalysisMeta extends UserCustomCheckMeta {
    private int lastFlyingPacket;
    private long lastBlockPlacementPermutation;
    private long lastTimeTeleported;
    private int permutationVL;
    private boolean cancelBlockPlacement;
    private long lastHardFaultClick;
    private long lastTimeValidBlockPlaced;
    private long lastTimeBlockPacketArrived;
    private final Vector lastBlockFaceLook = new Vector();
    private long lastMotionBlockFlag;
    private long lastMLAJFlag;
    private long lastTimeBlockPlacedBelow;
    private final List<Long> blockPlaceUnderDiff = Lists.newArrayList();
    private final List<Long> blockPlaceInCornerDiff = Lists.newArrayList();
    private double ticksSneakingSync;
    private int fastSneakVL;
    private long lastTimeMovedBackwards;
    private double lastYMovement;
    private long lastTimeBlockPlacedUnderWithoutBackwardsMotion;
    private Location lastBlockPlacedUnder;
    private double lastBPULocationDist;
    private long lastBlockPlaceUnderBlockRequest;
    private boolean cryptaPlacementBuffer;
    private long lastCryptaBuffer;
    private long lastTimeSuspiciousForItemSpoof;
    private long lastTimeSuspiciousForSaveWalk;
    private int suspiciousSafeWalkBlockPlaces;
    private double scaffoldwalkVL;
    private long lastTimeBlockPlacedCorner;
    private long lastTimeSuspiciousForScaffoldWalk;
    private long lastSneakingMove;
    private long lastFullJump;
    private long lastTimeBlockPlacedUnder;
    private double lastXZMovement;
    private long lastSneakToggled;
    private long lastTimeBlockPlaced;
    private boolean lastSneakWasSuspicious;
    private int ticksSneaking;
    private int machineSneakVL;
    private int lastSneakDurationTicks;
    private long lastTimeSuspiciousForTowerhop;
  }
}