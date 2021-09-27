package de.jpx3.intave.klass;

import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.klass.locate.Locate;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public final class Lookup {
  private final static String VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
  private final static String CRAFT_BUKKIT_PREFIX = "org.bukkit.craftbukkit." + VERSION;

  public static Field serverField(String serverClassName, String fieldName) {
    return Locate.fieldByKey(serverClassName, fieldName);
  }

  public static Class<?> serverClass(String key) {
    return Locate.classByKey(key);
  }

  public static Class<?> craftBukkitClass(String className) {
    return classByName(appendCraftBukkitPrefixToClass(className));
  }

  private static <T> Class<T> classByName(String className) {
    try {
      //noinspection unchecked
      return (Class<T>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IntaveInternalException(e);
    }
  }

  private static String appendCraftBukkitPrefixToClass(String className) {
    return CRAFT_BUKKIT_PREFIX + "." + className;
  }

  public static Field declaredFieldIn(Class<?> clazz, String name) {
    try {
      return clazz.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      throw new IntaveInternalException(e);
    }
  }

  public static String version() {
    return VERSION;
  }
}
