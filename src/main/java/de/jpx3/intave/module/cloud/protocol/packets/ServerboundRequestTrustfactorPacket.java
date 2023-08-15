package de.jpx3.intave.module.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.module.cloud.protocol.Identity;
import de.jpx3.intave.module.cloud.protocol.JsonPacket;

import java.util.UUID;

public final class ServerboundRequestTrustfactorPacket extends JsonPacket {
  private Identity id;

  public ServerboundRequestTrustfactorPacket(Identity id) {
    super("SB_REQ_TF", "1");
    this.id = id;
  }

  @Override
  public void serialize(JsonWriter writer) {
    try {
      writer.name("id").value(id.toString());
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
          case "id":
            id = Identity.fromString(jsonReader.nextString());
            break;
        }
      }
      jsonReader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
