package de.jpx3.intave.module.feedback;

import org.bukkit.entity.Player;

public final class FeedbackRequest<T> {
  private final FeedbackCallback<T> callback;
  private final FeedbackTracker tracker;
  private final T obj;
  private final short key;
  private final long time;
  private final long num;

  FeedbackRequest(FeedbackCallback<T> Callback, FeedbackTracker tracker, T obj, short key, long num) {
    this.callback = Callback;
    this.tracker = tracker;
    this.obj = obj;
    this.key = key;
    this.num = num;
    this.time = System.currentTimeMillis();
  }

  public void sent() {
    if (tracker != null) {
      tracker.sent(this);
    }
  }

  public void acknowledge(Player player) {
    callback.success(player, obj);
    if (tracker != null) {
      tracker.received(this);
    }
  }

  public long passedTime() {
    return System.currentTimeMillis() - this.time;
  }

  public short key() {
    return key;
  }

  public long num() {
    return num;
  }

  public long requested() {
    return time;
  }
}