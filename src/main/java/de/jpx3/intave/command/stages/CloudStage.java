package de.jpx3.intave.command.stages;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.command.CommandStage;
import de.jpx3.intave.command.SubCommand;
import de.jpx3.intave.connect.cloud.Cloud;
import de.jpx3.intave.connect.cloud.protocol.Shard;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public final class CloudStage extends CommandStage {
  private static CloudStage singletonInstance;

  private CloudStage() {
    super(BaseStage.singletonInstance(), "cloud");
  }

  @SubCommand(
    selectors = "status",
    usage = "",
    description = "Show version info"
  )
  public void statusCommand(CommandSender commandSender) {
    Cloud cloud = IntavePlugin.singletonInstance().cloud();
    boolean enabled = cloud.isEnabled();

    if (!enabled) {
      commandSender.sendMessage(IntavePlugin.prefix() + ChatColor.RED + "Cloud connection is not enabled");
      return;
    }

    Map<Shard, Boolean> shardConnected = cloud.shardConnections();
    Map<Shard, Long> receivedBytes = cloud.receivedBytesPerShard();
    Map<Shard, Long> sentBytes = cloud.sentBytesPerShard();


    // connected to at least one
    boolean connectedToAtLeastOne = shardConnected.values().stream().anyMatch(b -> b);
    commandSender.sendMessage(IntavePlugin.prefix() + ChatColor.GRAY + "Cloud is " + (connectedToAtLeastOne ? ChatColor.GREEN + "connected" : ChatColor.RED + "disconnected"));

    for (Map.Entry<Shard, Boolean> entry : shardConnected.entrySet()) {
      Shard shard = entry.getKey();
      boolean connected = entry.getValue();
      commandSender.sendMessage(IntavePlugin.prefix() + ChatColor.GRAY + "Shard " + ChatColor.GREEN + shard.name() + ChatColor.GRAY + " is " + (connected ? ChatColor.GREEN + "CONNECTED" : ChatColor.RED + "DISCONNECTED") + ChatColor.GRAY + " (" + ChatColor.GREEN + formatBytes(receivedBytes.get(shard)) + ChatColor.GRAY + " received, " + ChatColor.GREEN + formatBytes(sentBytes.get(shard)) + ChatColor.GRAY + " sent)");
    }
  }

  private String formatBytes(long bytes) {
    if (bytes < 1024) {
      return bytes + "B";
    } else if (bytes < 1024 * 1024) {
      return bytes / 1024 + "KB";
    } else if (bytes < 1024 * 1024 * 1024) {
      return bytes / (1024 * 1024) + "MB";
    } else {
      return bytes / (1024 * 1024 * 1024) + "GB";
    }
  }

  public static CloudStage singletonInstance() {
    if (singletonInstance == null) {
      singletonInstance = new CloudStage();
    }
    return singletonInstance;
  }
}
