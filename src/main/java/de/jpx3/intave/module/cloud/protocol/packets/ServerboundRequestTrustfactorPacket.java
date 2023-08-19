package de.jpx3.intave.module.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.module.cloud.protocol.Identity;
import de.jpx3.intave.module.cloud.protocol.JsonPacket;
import de.jpx3.intave.module.cloud.protocol.Token;
import de.jpx3.intave.module.cloud.protocol.TokenStorage;
import de.jpx3.intave.module.cloud.protocol.listener.Serverbound;

import static de.jpx3.intave.module.cloud.protocol.Direction.SERVERBOUND;

public final class ServerboundRequestTrustfactorPacket extends JsonPacket<Serverbound> {
  private Token token = TokenStorage.currentToken();
  private Identity id;

  public ServerboundRequestTrustfactorPacket() {
    super(SERVERBOUND, "REQUEST_TRUSTFACTOR", "1");
  }

  public ServerboundRequestTrustfactorPacket(Identity id) {
    super(SERVERBOUND, "REQUEST_TRUSTFACTOR", "1");
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
  public void deserialize(JsonReader jsonReader) {
    try {
      jsonReader.beginObject();
      while (jsonReader.hasNext()) {
        switch (jsonReader.nextName()) {
          case "token":
            token = Token.from(jsonReader);
            break;
          case "id":
            id = Identity.from(jsonReader);
            break;
        }
      }
      jsonReader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
