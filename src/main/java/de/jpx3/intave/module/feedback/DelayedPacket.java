package de.jpx3.intave.module.feedback;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public final class DelayedPacket implements Delayed {
  private final Object packet;
  private final long scheduledTime;

  public DelayedPacket(Object packet, long scheduledTime) {
    this.packet = packet;
    this.scheduledTime = scheduledTime;
  }

  public Object packet() {
    return packet;
  }

  @Override
  public long getDelay(TimeUnit unit) {
    return unit.convert(scheduledTime - System.nanoTime(), TimeUnit.NANOSECONDS);
  }

  @Override
  public int compareTo(@NotNull Delayed o) {
    return Long.compare(scheduledTime, ((DelayedPacket) o).scheduledTime);
  }
}
