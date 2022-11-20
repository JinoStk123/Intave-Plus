package de.jpx3.intave.packet.reader;

import de.jpx3.intave.adapter.MinecraftVersions;

public class AbilityInReader extends AbstractPacketReader {
  private static final boolean BIT_FIELD = MinecraftVersions.VER1_16_0.atOrAbove();

  public boolean requestedFlying() {
    return packet().getBooleans().read(BIT_FIELD ? 0 : 1);
  }
}
