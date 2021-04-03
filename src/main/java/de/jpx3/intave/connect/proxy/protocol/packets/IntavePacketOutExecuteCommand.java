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
public class IntavePacketOutExecuteCommand extends IntavePacket {

  private UUID playerId;
  private String command;

  public IntavePacketOutExecuteCommand() {
  }

  public IntavePacketOutExecuteCommand(UUID playerId, String command) {
    this.playerId = playerId;
    this.command = command;
  }

  @Override
  public void applyFrom(ByteArrayDataInput input) throws IllegalStateException, AssertionError {
    playerId = UUID.fromString(input.readUTF());
    command = input.readUTF();
  }

  @Override
  public void applyTo(ByteArrayDataOutput output) {
    output.writeUTF(playerId.toString());
    output.writeUTF(command);
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public String getCommand() {
    return command;
  }
}
