package de.jpx3.intave.packet.reader;

import com.google.common.collect.Maps;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class WindowBulkItemReader extends AbstractPacketReader implements WindowItemReader {
  @Override
  public int windowId() {
    return packet().getIntegers().read(0);
  }

  @Override
  public Map<Integer, ItemStack> itemMap() {
    List<ItemStack> read = packet().getItemListModifier().readSafely(0);
    if (read == null) {
      return Maps.newHashMap();
    }
    // to map with indices
    Map<Integer, ItemStack> map = new java.util.HashMap<>();
    for (int i = 0; i < read.size(); i++) {
      map.put(i, read.get(i));
    }
    return map;
  }
}
