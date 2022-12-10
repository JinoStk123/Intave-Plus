package de.jpx3.intave.connect.sibyl;

import de.jpx3.intave.annotate.Native;
import org.bukkit.entity.Player;

public final class SibylMessageTransmitter {
  @Native
  public static void sendMessage(Player player, String encryptedFormat, String... args) {
    // for now, just send the message to the player
    player.sendMessage(String.format(encryptedFormat, (Object[]) args));
  }
}
