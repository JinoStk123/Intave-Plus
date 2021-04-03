package de.jpx3.intave.connect.proxy.protocol.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import de.jpx3.intave.connect.proxy.protocol.IntavePacket;

import java.util.UUID;

/**
 * Class generated using IntelliJ IDEA
 * Any distribution is strictly prohibited.
 * Copyright Richard Strunk 2019
 */

public class IntavePacketOutKicked extends IntavePacket {

  private UUID playerId;
  private String checkName;
  private String finalFlagMessage;
  private double finalTotalViolationLevel;

  public IntavePacketOutKicked() {
  }

  public IntavePacketOutKicked(UUID playerId, String checkName, String finalFlagMessage, double finalTotalViolationLevel) {
    this.playerId = playerId;
    this.checkName = checkName;
    this.finalFlagMessage = finalFlagMessage;
    this.finalTotalViolationLevel = finalTotalViolationLevel;
  }

  @Override
  public void applyFrom(ByteArrayDataInput input) throws IllegalStateException, AssertionError {
    playerId = UUID.fromString(input.readUTF());
    checkName = input.readUTF();
    finalFlagMessage = input.readUTF();
    finalTotalViolationLevel = input.readDouble();
  }

  @Override
  public void applyTo(ByteArrayDataOutput output) {
    output.writeUTF(playerId.toString());
    output.writeUTF(checkName);
    output.writeUTF(finalFlagMessage);
    output.writeDouble(finalTotalViolationLevel);
  }
}
