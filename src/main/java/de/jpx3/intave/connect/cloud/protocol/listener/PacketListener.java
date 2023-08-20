package de.jpx3.intave.connect.cloud.protocol.listener;

import de.jpx3.intave.connect.cloud.protocol.Packet;

public interface PacketListener {
  default void onAny(Packet<?> packet) {

  }
}
