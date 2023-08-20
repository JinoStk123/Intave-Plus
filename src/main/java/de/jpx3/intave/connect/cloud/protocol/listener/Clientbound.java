package de.jpx3.intave.connect.cloud.protocol.listener;

import de.jpx3.intave.connect.cloud.protocol.Packet;
import de.jpx3.intave.connect.cloud.protocol.packets.*;

public interface Clientbound extends PacketListener {

  @Override
  default void onAny(Packet<?> packet) {

  }

  default void onClientHello(ClientboundHelloPacket packet) {

  }

  default void onCloseConnection(ClientboundDisconnectPacket packet) {

  }

  default void onCombatModifier(ClientboundCombatModifierPacket packet) {

  }

  default void onDownloadStorage(ClientboundDownloadStoragePacket packet) {

  }

  default void onKeepAlive(ClientboundKeepAlivePacket packet) {

  }

  default void onSetTrustfactor(ClientboundSetTrustfactorPacket packet) {

  }

  default void onViolation(ClientboundViolationPacket packet) {

  }
}
