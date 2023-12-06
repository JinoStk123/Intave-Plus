package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class WindowItemsEvent extends Event {
  private int windowId;
  private int count;

  private String[] types;
  private int[] amounts;

  public WindowItemsEvent() {
  }

  public WindowItemsEvent(int windowId, int count, String[] types, int[] amounts) {
    this.windowId = windowId;
    this.count = count;
    this.types = types;
    this.amounts = amounts;
  }

  @Override
  public void serialize(Environment environment, DataOutput out) throws IOException {
    out.writeInt(windowId);
    out.writeInt(count);
    for (int i = 0; i < count; i++) {
      out.writeUTF(types[i]);
      out.writeInt(amounts[i]);
    }
  }

  @Override
  public void deserialize(Environment environment, DataInput in) throws IOException {
    windowId = in.readInt();
    count = in.readInt();
    if (count > 1024) {
      throw new IOException("Too many items: " + count);
    }
    types = new String[count];
    amounts = new int[count];
    for (int i = 0; i < count; i++) {
      types[i] = in.readUTF();
      amounts[i] = in.readInt();
    }
  }

  @Override
  public void accept(EventSink sink) {
    sink.visit(this);
  }
}
