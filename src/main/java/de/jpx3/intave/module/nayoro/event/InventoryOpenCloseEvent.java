package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class InventoryOpenCloseEvent extends Event {
  private int windowId;
  private Action action = null;

  private InventoryOpenCloseEvent(Action action) {
    this.action = action;
  }

  @Override
  public void serialize(Environment environment, DataOutput out) throws IOException {
    out.writeInt(windowId);
    out.writeUTF(action.name());
  }

  @Override
  public void deserialize(Environment environment, DataInput in) throws IOException {
    windowId = in.readInt();
    action = Action.valueOf(in.readUTF());
  }

  @Override
  public void accept(EventSink sink) {
//    sink.visit(this);
  }

  public static InventoryOpenCloseEvent create(Action action) {
    return new InventoryOpenCloseEvent(action);
  }

  public enum Action {
    OPEN,
    INFER_OPEN,
    CLOSE
  }
}
