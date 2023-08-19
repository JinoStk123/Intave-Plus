package de.jpx3.intave.module.cloud.protocol;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.IntavePlugin;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.UUID;

public final class Token implements JsonSerializable {
  private UUID serverGivenPart;
  private UUID clientGivenPart = IntavePlugin.gameId();

  private long validUntil;

  private Token() {
  }

  public Token(UUID serverGivenPart, long validUntil) {
    this.serverGivenPart = serverGivenPart;
    this.validUntil = validUntil;
  }

  public UUID serverGivenPart() {
    return serverGivenPart;
  }

  public UUID clientGivenPart() {
    return clientGivenPart;
  }

  public boolean hasExpireInformation() {
    return validUntil != 0;
  }

  public boolean isExpired() {
    return validUntil < System.currentTimeMillis();
  }

  @Override
  public void serialize(JsonWriter writer) {
    try {
      writer.name("token");
      writer.beginObject();
      writer.name("s").value(serverGivenPart.toString());
      writer.name("c").value(clientGivenPart.toString());
      // client doesn't need to reply this
//      writer.name("expires").value(validUntil);
      writer.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(JsonReader reader) {
    try {
      reader.beginObject();
      while (reader.hasNext()) {
        switch (reader.nextName()) {
          case "s":
            serverGivenPart = UUID.fromString(reader.nextString());
            break;
          case "c":
            clientGivenPart = UUID.fromString(reader.nextString());
            break;
          case "expires":
            validUntil = reader.nextLong();
            break;
        }
      }
      if (serverGivenPart == null) {
        throw new IllegalStateException("serverGivenPart is null");
      } else if (clientGivenPart == null) {
        throw new IllegalStateException("clientGivenPart is null");
      }
//      if (validUntil < System.currentTimeMillis()) {
//        throw new IllegalStateException("token is expired");
//      }
      reader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void serialize(DataOutput buffer) {
    try {
      buffer.writeLong(this.serverGivenPart.getMostSignificantBits());
      buffer.writeLong(this.serverGivenPart.getLeastSignificantBits());
      buffer.writeLong(this.clientGivenPart.getMostSignificantBits());
      buffer.writeLong(this.clientGivenPart.getLeastSignificantBits());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(DataInput buffer) {
    try {
      this.serverGivenPart = new UUID(buffer.readLong(), buffer.readLong());
      this.clientGivenPart = new UUID(buffer.readLong(), buffer.readLong());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Token from(DataInput input) {
    Token token = new Token();
    token.deserialize(input);
    return token;
  }

  public static Token from(JsonReader reader) {
    Token token = new Token();
    token.deserialize(reader);
    return token;
  }
}
