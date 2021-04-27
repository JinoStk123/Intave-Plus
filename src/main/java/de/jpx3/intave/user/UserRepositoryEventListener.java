package de.jpx3.intave.user;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.event.bukkit.BukkitEventSubscriber;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.tools.sync.Synchronizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class UserRepositoryEventListener implements BukkitEventSubscriber {
  public UserRepositoryEventListener(IntavePlugin plugin) {
    plugin.eventLinker().registerEventsIn(this);
    synchronizePlayers();
  }

  private void synchronizePlayers() {
    Bukkit.getOnlinePlayers()
      .stream()
      .filter(player -> UserRepository.userOf(player) == null)
      .forEach(UserRepository::registerUser);
  }

  @BukkitEventSubscription
  public void receiveJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UserRepository.registerUser(player);
    Synchronizer.synchronize(() -> {
      UserMetaClientData clientData = UserRepository.userOf(player).meta().clientData();
      System.out.println("[Intave] " + player.getName() + " joined with " + clientData.versionString() + " ("+clientData.protocolVersion()+")");
    });
  }

  @BukkitEventSubscription
  public void receiveQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    UserRepository.unregisterUser(player);
  }
}