package de.jpx3.intave.packet.reader;

import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.annotate.KeepEnumInternalNames;
import de.jpx3.intave.klass.Lookup;
import org.bukkit.inventory.ItemStack;

public final class WindowClickReader extends AbstractPacketReader {
  private static final boolean MODERN_WINDOW_CLICK = MinecraftVersions.VER1_9_0.atOrAbove();
  private static final Class<?> clickTypeConversionClass = MODERN_WINDOW_CLICK ? Lookup.serverClass("InventoryClickType") : null;

  public int windowId() {
    return packet().getIntegers().read(0);
  }

  public int slot() {
    return packet().getIntegers().read(1);
  }

  public int shiftClick() {
    return packet().getIntegers().read(2);
  }

  public short actionNumber() {
    return packet().getShorts().read(0);
  }

  public InventoryClickType clickType() {
    if (MODERN_WINDOW_CLICK) {
      return packet().getEnumModifier(InventoryClickType.class, clickTypeConversionClass).read(0);
    } else {
      return InventoryClickType.values()[packet().getIntegers().read(3)];
    }
  }

  public ItemStack itemStack() {
    return packet().getItemModifier().read(0);
  }

  public boolean isDrop() {
    if (MODERN_WINDOW_CLICK) {
      return clickType() == InventoryClickType.THROW && slot() != -999;
    } else {
      return packet().getIntegers().read(3) == 4 && slot() != -999;
    }
  }

  @KeepEnumInternalNames
  public enum InventoryClickType {
    PICKUP,
    QUICK_MOVE,
    SWAP,
    CLONE,
    THROW,
    QUICK_CRAFT,
    PICKUP_ALL
  }
}
