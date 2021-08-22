package de.jpx3.intave.diagnostic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class MemoryTraced {
  private final static Map<Class<?>, AtomicLong> objectsLoaded = new ConcurrentHashMap<>();
  
  public MemoryTraced() {
    objectsLoaded.computeIfAbsent(getClass(), aClass -> new AtomicLong()).incrementAndGet();
  }

  public static Map<Class<?>, AtomicLong> tracedClasses() {
    return objectsLoaded;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    objectsLoaded.computeIfAbsent(getClass(), aClass -> new AtomicLong()).decrementAndGet();
  }
}
