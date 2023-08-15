package de.jpx3.intave.packet.reader;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface WindowItemReader extends PacketReader {
  int windowId();
  Map<Integer, ItemStack> itemMap();
}
