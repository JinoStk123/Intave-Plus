package de.jpx3.intave.module.cloud.protocol;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class Packet {
  private final String name;
  private final String version;
  private final TransferMode mode;

  public Packet(String name, String version, TransferMode mode) {
    this.name = name;
    this.version = version;
    this.mode = mode;
  }

  public abstract void serialize(DataOutput output);

  public abstract void deserialize(DataInput input);

  public String name() {
    return name;
  }

  public String version() {
    return version;
  }
}
