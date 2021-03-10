package de.jpx3.intave.command.stages;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.command.CommandStage;
import de.jpx3.intave.command.SubCommand;
import de.jpx3.intave.detect.CheckStatistics;
import de.jpx3.intave.detect.IntaveCheck;
import de.jpx3.intave.diagnostics.timings.Timing;
import de.jpx3.intave.diagnostics.timings.Timings;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class IntaveDiagnosticsStage extends CommandStage {
  private static IntaveDiagnosticsStage singletonInstance;
  private final IntavePlugin plugin;

  protected IntaveDiagnosticsStage() {
    super(IntaveCommandStage.singletonInstance(), "diagnostics", 1);
    plugin = IntavePlugin.singletonInstance();
  }

  @SubCommand(
    selectors = "performance",
    usage = "",
    description = "Output performance data",
    permission = "intave.command.diagnostics.performance"
  )
  public void timingsCommand(User user) {
    Player player = user.player();
    player.sendMessage(IntavePlugin.prefix() + "Service status");
    if (plugin.sibylIntegrationService().authentication().isAuthenticated(player)) {
      List<Timing> timings = new ArrayList<>(Timings.timingPool());
      timings.sort(Timing::compareTo);

      timings.forEach(timing -> {
        boolean suspicious = timing.getAverageCallDurationInMillis() > 0.5d;
        boolean dumping = timing.getAverageCallDurationInMillis() > 1.5d;

        String type;
        if (suspicious) {
          type = ChatColor.GOLD + "SUSPICIOUS";
        } else if (dumping) {
          type = ChatColor.RED + "CRITICAL";
        } else {
          type = ChatColor.GREEN + "HEALTHY";
        }

        String message = type + " " + ChatColor.GRAY + timing.getTimingName();
        player.sendMessage(message);
      });
    }
  }

  @SubCommand(
    selectors = "statistics",
    usage = "",
    permission = "intave.command.diagnostics.statistics",
    description = "Output check statistics"
  )
  public void checkStatisticsCommand(User user) {
    Player player = user.player();
    player.sendMessage(IntavePlugin.prefix() + "Loading check statistics...");
    for (IntaveCheck check : plugin.checkService().checks()) {
      CheckStatistics statistics = check.statistics();
      double processed = statistics.totalProcessed();
      double violations = statistics.totalViolations();
      if (processed == 0 || !check.enabled()) {
        continue;
      }
      String violatedRate = MathHelper.formatDouble(violations / processed * 100, 5);
      String checkFormat = ChatColor.RED + check.name().toUpperCase(Locale.ROOT);
      String message = checkFormat + ChatColor.GRAY + ": " + (int) processed + " processed / " + violatedRate + "% violated";
      player.sendMessage(message);
    }
  }

  public static IntaveDiagnosticsStage singletonInstance() {
    if (singletonInstance == null) {
      singletonInstance = new IntaveDiagnosticsStage();
    }
    return singletonInstance;
  }
}