package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Material;

public final class BlockActionReader extends AbstractPacketReader {
  public BlockPosition blockPosition() {
    return packet().getBlockPositionModifier().read(0);
  }

  public int action() {
    return packet().getIntegers().read(0);
  }

  public int data() {
    return packet().getIntegers().read(1);
  }

  public Material blockType() {
    return packet().getBlocks().read(0);
  }
}