package de.jpx3.intave.detect;

import de.jpx3.intave.event.bukkit.BukkitEventSubscriber;
import de.jpx3.intave.event.packet.PacketEventSubscriber;

public interface EventProcessor extends BukkitEventSubscriber, PacketEventSubscriber {
}