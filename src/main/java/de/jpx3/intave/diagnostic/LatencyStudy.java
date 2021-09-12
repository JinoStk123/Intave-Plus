package de.jpx3.intave.diagnostic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class LatencyStudy {
  private final static Map<Short, AtomicLong> hitDelays = new ConcurrentHashMap<>();

  public static void enterHit(short tickLatency) {
    hitDelays.computeIfAbsent(tickLatency, x -> new AtomicLong(0L)).incrementAndGet();
  }

  public static double average() {
    AtomicLong score = new AtomicLong();
    AtomicLong count = new AtomicLong();
    hitDelays.forEach((aShort, atomicLong) -> {
      score.addAndGet(aShort * atomicLong.get());
      count.addAndGet(atomicLong.get());
    });
    return (double) score.get() / (double) count.get();
  }
}
