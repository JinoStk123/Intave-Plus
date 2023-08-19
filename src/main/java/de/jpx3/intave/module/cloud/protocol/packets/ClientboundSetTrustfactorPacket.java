package de.jpx3.intave.module.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.access.player.trust.TrustFactor;
import de.jpx3.intave.module.cloud.protocol.*;
import de.jpx3.intave.module.cloud.protocol.listener.Clientbound;

import java.util.UUID;

public final class ClientboundSetTrustfactorPacket extends JsonPacket<Clientbound> {
  private Token token = TokenStorage.currentToken();
  private Identity id;
  private TrustFactor trustFactor;

  public ClientboundSetTrustfactorPacket() {
    super(Direction.CLIENTBOUND, "SET_TRUSTFACTOR", "1");
  }

  public ClientboundSetTrustfactorPacket(UUID id, TrustFactor trustFactor) {
    super(Direction.CLIENTBOUND, "SET_TRUSTFACTOR", "1");
    this.id = Identity.from(id);
    this.trustFactor = trustFactor;
  }

  @Override
  public void serialize(JsonWriter jsonWriter) {
    try {
      jsonWriter.beginObject();
      token.serialize(jsonWriter);
      jsonWriter.name("id");
      id.serialize(jsonWriter);
      jsonWriter.name("factor").value(trustFactor.name());
      jsonWriter.endObject();
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
          case "factor":
            trustFactor = TrustFactor.valueOf(jsonReader.nextString());
            break;
        }
      }
      jsonReader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
