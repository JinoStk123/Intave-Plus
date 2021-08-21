package de.jpx3.intave.diagnostics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MemoryTraced {
  private final static Map<Class<?>, AtomicInteger> objectsLoaded = new ConcurrentHashMap<>();

  public MemoryTraced() {
    objectsLoaded.computeIfAbsent(getClass(), aClass -> new AtomicInteger()).incrementAndGet();
  }

  public static Map<Class<?>, AtomicInteger> tracedClasses() {
    return objectsLoaded;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    objectsLoaded.computeIfAbsent(getClass(), aClass -> new AtomicInteger()).decrementAndGet();
  }
}
