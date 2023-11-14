package de.jpx3.intave.connect.cloud.protocol;

import de.jpx3.intave.connect.cloud.protocol.listener.PacketListener;

public abstract class JsonPacket<LISTENER extends PacketListener> extends Packet<LISTENER> implements JsonSerializable {
  public JsonPacket(Direction direction, String name, String version) {
    super(direction, name, version, TransferMode.JSON);
  }
}
