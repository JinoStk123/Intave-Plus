package de.jpx3.intave.module.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.module.cloud.protocol.JsonPacket;
import de.jpx3.intave.module.cloud.protocol.Token;
import de.jpx3.intave.module.cloud.protocol.TokenStorage;
import de.jpx3.intave.module.cloud.protocol.listener.Clientbound;

import static de.jpx3.intave.module.cloud.protocol.Direction.CLIENTBOUND;

public final class ClientboundCloseConnectionPacket extends JsonPacket<Clientbound> {
  private Token token = TokenStorage.currentToken();
  private String reason;

  public ClientboundCloseConnectionPacket() {
    super(CLIENTBOUND, "CLOSE_CONNECTION", "1");
  }

  @Override
  public void serialize(JsonWriter writer) {
    try {
      writer.beginObject();
      token.serialize(writer);
      writer.name("reason").value(reason);
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
          case "token":
            token = Token.from(reader);
            break;
          case "reason":
            reason = reader.nextString();
            break;
        }
      }
      reader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String reason() {
    return reason;
  }
}
