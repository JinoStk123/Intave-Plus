package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class WindowClickEvent extends Event {
  private int windowId;
  private int slot;
  private int button;
  private int actionNumber;
  private int mode;
  private String itemName;
  private int itemAmount;
  private long timestamp;

  public WindowClickEvent() {
  }

  public WindowClickEvent(
    int windowId,
    int slot,
    int button,
    int actionNumber,
    int mode,
    String itemName,
    int itemAmount,
    long timestamp
  ) {
    this.windowId = windowId;
    this.slot = slot;
    this.button = button;
    this.actionNumber = actionNumber;
    this.mode = mode;
    this.itemName = itemName;
    this.itemAmount = itemAmount;
    this.timestamp = timestamp;
  }

  @Override
  public void serialize(Environment environment, DataOutput out) throws IOException {
    out.writeInt(windowId);
    out.writeInt(slot);
    out.writeInt(button);
    out.writeInt(actionNumber);
    out.writeInt(mode);
    out.writeUTF(itemName);
    out.writeInt(itemAmount);
    out.writeLong(timestamp);
  }

  @Override
  public void deserialize(Environment environment, DataInput in) throws IOException {
    windowId = in.readInt();
    slot = in.readInt();
    button = in.readInt();
    actionNumber = in.readInt();
    mode = in.readInt();
    itemName = in.readUTF();
    itemAmount = in.readInt();
    timestamp = in.readLong();
  }

  @Override
  public void accept(EventSink sink) {
    sink.visit(this);
  }
}
