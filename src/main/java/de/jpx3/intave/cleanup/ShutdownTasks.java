package de.jpx3.intave.cleanup;

import java.util.ArrayDeque;
import java.util.Queue;

public final class ShutdownTasks {
  private final static Queue<Runnable> tasks = new ArrayDeque<>();

  private ShutdownTasks() {
    throw new UnsupportedOperationException("Initialization of helper class");
  }

  public static void add(Runnable runnable) {
    if (runnable == null) {
      throw new NullPointerException("Null shutdown task");
    }
    tasks.offer(runnable);
  }

  public static void runAll() {
    for (Runnable task : tasks) {
      try {
        task.run();
      } catch (Exception exception) {
        System.out.println("[Intave] Shutdown task " +task + " failed to complete");
        exception.printStackTrace();
      }
    }
  }
}
