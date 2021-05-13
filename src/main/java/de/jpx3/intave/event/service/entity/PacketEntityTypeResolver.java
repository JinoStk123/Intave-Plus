package de.jpx3.intave.event.service.entity;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.adapter.ProtocolLibraryAdapter;
import de.jpx3.intave.logging.IntaveLogger;
import de.jpx3.intave.patchy.PatchyLoadingInjector;
import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.reflect.ReflectiveAccess;
import de.jpx3.intave.reflect.ReflectiveHandleAccess;
import de.jpx3.intave.reflect.hitbox.HitBoxBoundaries;
import de.jpx3.intave.reflect.hitbox.ReflectiveEntityHitBoxAccess;
import net.minecraft.server.v1_14_R1.EntitySize;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.IRegistry;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

public final class PacketEntityTypeResolver {
  private static final boolean DATA_WATCHER = !MinecraftVersions.VER1_15_0.atOrAbove();
  private static final boolean ENTITY_SIZE_ACCESS = MinecraftVersions.VER1_14_0.atOrAbove();
  private String dataWatcherEntityFieldName;

  public PacketEntityTypeResolver(IntavePlugin plugin) {
    if (DATA_WATCHER) {
      registerDataWatcherEntityFieldName();
    }
    if (ENTITY_SIZE_ACCESS) {
      loadEntityTypeResolver();
    }
  }

  private void registerDataWatcherEntityFieldName() {
    com.comphenix.protocol.utility.MinecraftVersion serverVersion = ProtocolLibraryAdapter.serverVersion();
    if (serverVersion.isAtLeast(MinecraftVersions.VER1_14_0)) {
      dataWatcherEntityFieldName = "entity";
    } else if (serverVersion.isAtLeast(MinecraftVersions.VER1_10_0)) {
      dataWatcherEntityFieldName = "c";
    } else if (serverVersion.isAtLeast(MinecraftVersions.VER1_9_0)) {
      dataWatcherEntityFieldName = "b";
    } else {
      dataWatcherEntityFieldName = "a";
    }

    // search field

    Class<?> entityClass = ReflectiveAccess.NMS_ENTITY_CLASS;
    Class<?> dataWatcherClass = ReflectiveAccess.lookupServerClass("DataWatcher");

    for (Field declaredField : dataWatcherClass.getDeclaredFields()) {
      if (declaredField.getType() == entityClass) {
        String fieldName = declaredField.getName();
        if (!dataWatcherEntityFieldName.equals(fieldName)) {
          IntaveLogger.logger().globalPrintLn("[Intave] Conflicting method name: \"" + dataWatcherEntityFieldName + "\" expected but found \"" + fieldName + "\" for entity-from-dw access");
        }
        dataWatcherEntityFieldName = fieldName;
        break;
      }
    }
  }

  private void loadEntityTypeResolver() {
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), "de.jpx3.intave.event.service.entity.PacketEntityTypeResolver$EntityTypeResolver");
  }

  public String entityNameByBukkitEntity(Entity entity) {
    return entityNameOf(ReflectiveHandleAccess.handleOf(entity));
  }

  public EntitySpawn spawnInformationOf(PacketContainer packet) {
    return DATA_WATCHER ? dataWatcherAccess(packet) : typeAccess(packet);
  }

  //
  // Type Access
  //

  private EntitySpawn typeAccess(PacketContainer packet) {
    Integer type = packet.getIntegers().read(1);
    return EntityTypeResolver.resolveFromId(type);
  }

  //
  // DataWatcher Access
  //

  private EntitySpawn dataWatcherAccess(PacketContainer packet) {
    Object entity = entityOfDataWatcher(packet.getDataWatcherModifier().read(0));
    HitBoxBoundaries hitBoxBoundaries = ReflectiveEntityHitBoxAccess.boundariesOf(entity);
    String name = entityNameOf(entity);
    return new EntitySpawn(name, hitBoxBoundaries);
  }

  private String entityNameOf(Object entity) {
    String entityName = entity.getClass().getSimpleName();
    if (entityName.startsWith("Entity")) {
      entityName = entityName.substring("Entity".length());
    }
    return entityName;
  }

  private Object entityOfDataWatcher(WrappedDataWatcher dataWatcher) {
    Object handle = dataWatcher.getHandle();
    Class<?> handleClass = handle.getClass();
    try {
      return entityByHandle(handle, handleClass.getDeclaredField(dataWatcherEntityFieldName));
    } catch (NoSuchFieldException e) {
      throw new IntaveInternalException(e);
    }
  }

  private Object entityByHandle(Object handle, Field entityField) {
    try {
      ReflectiveAccess.ensureAccessible(entityField);
      return entityField.get(handle);
    } catch (Exception e) {
      throw new IntaveInternalException(e);
    }
  }

  public static final class EntitySpawn {
    private final String entityName;
    private final HitBoxBoundaries hitBoxBoundaries;

    public EntitySpawn(String entityName, HitBoxBoundaries hitBoxBoundaries) {
      this.entityName = entityName;
      this.hitBoxBoundaries = hitBoxBoundaries;
    }

    public String entityName() {
      return entityName;
    }

    public HitBoxBoundaries hitBoxBoundaries() {
      return hitBoxBoundaries;
    }
  }

  @PatchyAutoTranslation
  public final static class EntityTypeResolver {
    private final static boolean ENTITY_SIZE = MinecraftVersions.VER1_14_0.atOrAbove();
    private static Field entitySizeField;

    static {
      if (ENTITY_SIZE) {
        lookupField();
      }
    }

    private static void lookupField() {
      try {
        Class<?> entityTypesClass = ReflectiveAccess.lookupServerClass("EntityTypes");
        Class<?> entitySizeClass = ReflectiveAccess.lookupServerClass("EntitySize");
        for (Field field : entityTypesClass.getDeclaredFields()) {
          if (field.getType() == entitySizeClass) {
            entitySizeField = field;
            break;
          }
        }
        if (entitySizeField == null) {
          throw new IntaveInternalException("EntitySize field does not exist in " + entityTypesClass);
        }
        ReflectiveAccess.ensureAccessible(entitySizeField);
      } catch (Exception e) {
        throw new IntaveInternalException(e);
      }
    }

    @PatchyAutoTranslation
    public static EntitySpawn resolveFromId(int type) {
      try {
        EntityTypes<?> entityTypes = IRegistry.ENTITY_TYPE.fromId(type);
        EntitySize entitySize = (EntitySize) entitySizeField.get(entityTypes);
        IChatBaseComponent component = entityTypes.g();
        HitBoxBoundaries hitBoxBoundaries = HitBoxBoundaries.of(entitySize.width, entitySize.height);
        return new EntitySpawn(component.getString(), hitBoxBoundaries);
      } catch (IllegalAccessException e) {
        throw new IntaveInternalException(e);
      }
    }

    @PatchyAutoTranslation
    public static HitBoxBoundaries resolveBoundariesOf(int type) {
      try {
        EntityTypes<?> entityTypes = IRegistry.ENTITY_TYPE.fromId(type);
        EntitySize entitySize = (EntitySize) entitySizeField.get(entityTypes);
        return HitBoxBoundaries.of(entitySize.width, entitySize.height);
      } catch (IllegalAccessException e) {
        throw new IntaveInternalException(e);
      }
    }
  }
}