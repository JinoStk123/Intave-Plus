package de.jpx3.intave.connect.proxy.protocol;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Class generated using IntelliJ IDEA
 * Any distribution is strictly prohibited.
 * Copyright Richard Strunk 2019
 */

public final class IntavePacketSerializer {
  public byte[] serializeDataFrom(IntavePacket packet) {
    ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
    packet.applyTo(dataOutput);
    return dataOutput.toByteArray();
  }
}
