package de.jpx3.intave.detect;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.detect.checks.movement.Physics;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class CheckService {
  private final IntavePlugin plugin;
  private Map<Class<?>, IntaveCheck> checkMap = new HashMap<>();

  public CheckService(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public void setup() {
    addCheck(Physics.class);

    bakeQuickAccess();
    linkBukkitEventSubscriptions();
    linkPacketEventSubscriptions();
  }

  public void reset() {
    checkMap.clear();

    resetQuickAccess();
    removeBukkitEventSubscriptions();
    removePacketEventSubscriptions();
  }

  public void addCheck(Class<? extends IntaveCheck> checkClass) {
    try {
      addCheck(checkClass.getConstructor(IntavePlugin.class).newInstance(plugin));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IntaveInternalException(e);
    }
  }

  public void addCheck(IntaveCheck check) {
    checkMap.put(check.getClass(), check);
  }

  public void bakeQuickAccess() {

  }

  public void resetQuickAccess() {

  }

  public void linkPacketEventSubscriptions() {

  }

  public void removePacketEventSubscriptions() {

  }

  public void linkBukkitEventSubscriptions() {

  }

  public void removeBukkitEventSubscriptions() {

  }

  public <T extends IntaveCheck> T searchCheck(Class<T> checkClass) {
    IntaveCheck check = checkMap.get(checkClass);
    if(check == null) {
      throw new IllegalStateException("Unable to find check " + checkClass);
    }
    return (T) check;
  }
}