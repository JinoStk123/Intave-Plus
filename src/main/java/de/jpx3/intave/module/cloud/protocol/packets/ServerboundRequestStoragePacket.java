package de.jpx3.intave.module.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.module.cloud.protocol.*;
import de.jpx3.intave.module.cloud.protocol.listener.Serverbound;

import static de.jpx3.intave.module.cloud.protocol.Direction.SERVERBOUND;

public final class ServerboundRequestStoragePacket extends JsonPacket<Serverbound> {
  private Token token = TokenStorage.currentToken();
  private Identity id;

  public ServerboundRequestStoragePacket() {
    super(SERVERBOUND, "REQUEST_STORAGE", "1");
  }

  public ServerboundRequestStoragePacket(Identity id) {
    super(SERVERBOUND, "REQUEST_STORAGE", "1");
    this.id = id;
  }

  @Override
  public void serialize(JsonWriter writer) {
    try {
      writer.beginObject();
      token.serialize(writer);
      writer.name("id");
      id.serialize(writer);
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
          case "id":
            id = Identity.from(reader);
            break;
        }
      }
      reader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
