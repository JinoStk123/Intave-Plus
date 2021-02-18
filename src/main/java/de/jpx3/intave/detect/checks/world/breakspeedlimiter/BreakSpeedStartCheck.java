package de.jpx3.intave.detect.checks.world.breakspeedlimiter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.world.BreakSpeedLimiter;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.event.service.ViolationService;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaClientData;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

public final class BreakSpeedStartCheck extends IntaveMetaCheckPart<BreakSpeedLimiter, BreakSpeedStartCheck.BreakSpeedStartMeta> {
  public BreakSpeedStartCheck(BreakSpeedLimiter parentCheck) {
    super(parentCheck, BreakSpeedStartCheck.BreakSpeedStartMeta.class);
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION_LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "FLYING"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "VEHICLE_MOVE")
    }
  )
  public void tickUpdate(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    BreakSpeedStartMeta meta = metaOf(user);
    meta.ticks++;
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_DIG")
    }
  )
  public void receiveBlockAction(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    BreakSpeedStartCheck.BreakSpeedStartMeta meta = metaOf(user);
    UserMetaClientData clientData = user.meta().clientData();

    PacketContainer packet = event.getPacket();
    EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().read(0);

    switch (digType) {
      case START_DESTROY_BLOCK: {
        if (clientData.flyingPacketStream()) {
          int ticksBetween = meta.ticks - meta.blockBreakTick;
          if (ticksBetween < 5) {
            ViolationService violationService = IntavePlugin.singletonInstance().violationProcessor();
            String message = "started block-break too quickly";
            String details = ticksBetween + " " + (ticksBetween == 1 ? "tick" : "ticks") + " between";
            if (violationService.processViolation(player, 5, "BreakSpeedLimiter", message, details)) {
              event.setCancelled(true);
            }
          }
        } else {
          long milliseconds = AccessHelper.now() - meta.blockBreakTimestamp;
          if (milliseconds < 200) {
            if (meta.blockBreakStartVL++ > 5) {
              ViolationService violationService = IntavePlugin.singletonInstance().violationProcessor();
              String message = "started block-break too quickly";
              String details = milliseconds + "ms between";
              if (violationService.processViolation(player, 1, "BreakSpeedLimiter", message, details)) {
                event.setCancelled(true);
              }
              meta.blockBreakStartVL--;
            }
          } else if (meta.blockBreakStartVL > 0) {
            meta.blockBreakStartVL -= 0.2;
          }
        }
        break;
      }
      case STOP_DESTROY_BLOCK: {
        meta.blockBreakTick = meta.ticks;
        meta.blockBreakTimestamp = AccessHelper.now();
        break;
      }
    }
  }

  public static final class BreakSpeedStartMeta extends UserCustomCheckMeta {
    private int ticks;
    private int blockBreakTick;
    private long blockBreakTimestamp;
    private double blockBreakStartVL;
  }
}