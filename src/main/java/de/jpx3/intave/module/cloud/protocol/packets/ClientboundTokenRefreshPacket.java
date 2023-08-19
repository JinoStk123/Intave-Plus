package de.jpx3.intave.module.cloud.protocol.packets;

import de.jpx3.intave.module.cloud.protocol.BinaryPacket;
import de.jpx3.intave.module.cloud.protocol.Direction;
import de.jpx3.intave.module.cloud.protocol.Token;
import de.jpx3.intave.module.cloud.protocol.listener.Clientbound;

import java.io.DataInput;
import java.io.DataOutput;

public final class ClientboundTokenRefreshPacket extends BinaryPacket<Clientbound> {
  private Token oldToken;
  private Token newToken;

  public ClientboundTokenRefreshPacket() {
    super(Direction.CLIENTBOUND, "REFRESH_TOKEN", "1");
  }

  public ClientboundTokenRefreshPacket(Token newToken) {
    super(Direction.CLIENTBOUND, "REFRESH_TOKEN", "1");
    this.newToken = newToken;
  }

  @Override
  public void serialize(DataOutput buffer) {
    try {
      oldToken.serialize(buffer);
      newToken.serialize(buffer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(DataInput buffer) {
    try {
      oldToken = Token.from(buffer);
      newToken = Token.from(buffer);
      if (!oldToken.clientGivenPart().equals(newToken.clientGivenPart())) {
        throw new RuntimeException("Different client given part");
      }
      if (oldToken.serverGivenPart().equals(newToken.serverGivenPart())) {
        throw new RuntimeException("Same token?");
      }
      if (!newToken.hasExpireInformation()) {
        throw new RuntimeException("No expire information");
      }
      if (newToken.isExpired()) {
        throw new RuntimeException("Already expired");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
