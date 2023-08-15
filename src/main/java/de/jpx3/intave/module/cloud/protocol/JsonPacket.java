package de.jpx3.intave.module.cloud.protocol;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;

public abstract class JsonPacket extends Packet {
  public JsonPacket(String name, String version) {
    super(name, version, TransferMode.JSON);
  }

  public abstract void serialize(JsonWriter writer);

  public abstract void deserialize(JsonReader reader);

  @Override
  public void serialize(DataOutput output) {
    StringWriter jsonString = new StringWriter();
    JsonWriter writer = new JsonWriter(new BufferedWriter(jsonString));
    serialize(writer);
    try {
      writer.close();
      output.writeUTF(jsonString.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(DataInput input) {
    try {
      deserialize(new JsonReader(new StringReader(input.readUTF())));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
