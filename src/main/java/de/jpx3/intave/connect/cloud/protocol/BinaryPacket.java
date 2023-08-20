package de.jpx3.intave.connect.cloud.protocol;

import de.jpx3.intave.connect.cloud.protocol.listener.PacketListener;

public abstract class BinaryPacket<LISTENER extends PacketListener> extends Packet<LISTENER> {
  public BinaryPacket(Direction direction, String name, String version) {
    super(direction, name, version, TransferMode.BINARY);
  }
}
