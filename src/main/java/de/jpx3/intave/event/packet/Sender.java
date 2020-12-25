package de.jpx3.intave.event.packet;

import com.comphenix.protocol.events.ConnectionSide;

public enum Sender {
  CLIENT,
  SERVER;

  public ConnectionSide toSide() {
    return this == CLIENT ? ConnectionSide.CLIENT_SIDE : ConnectionSide.SERVER_SIDE;
  }
}
