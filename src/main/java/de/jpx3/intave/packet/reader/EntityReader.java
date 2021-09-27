package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.entity.EntityLookup;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class EntityReader extends AbstractPacketReader {
  public @Nullable Entity readEntity(PacketEvent event) {
    World world = event.getPlayer().getWorld();
    int identifier = event.getPacket().getIntegers().read(0);
    return EntityLookup.findEntity(world, identifier);
  }
}
