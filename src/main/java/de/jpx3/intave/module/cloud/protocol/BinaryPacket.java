package de.jpx3.intave.module.cloud.protocol;

public abstract class BinaryPacket extends Packet {
  public BinaryPacket(String name, String version) {
    super(name, version, TransferMode.BINARY);
  }
}
