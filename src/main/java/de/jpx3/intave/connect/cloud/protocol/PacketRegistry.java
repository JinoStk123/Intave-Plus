package de.jpx3.intave.connect.cloud.protocol;

import com.google.common.collect.Maps;
import de.jpx3.intave.connect.cloud.protocol.packets.*;

import java.util.*;

public final class PacketRegistry {
  private static final Map<Direction, Map<String, Class<? extends Packet<?>>>> packetsByName = Maps.newEnumMap(Direction.class);
  private static final Map<Direction, Map<String, PacketSpecification>> specifications = Maps.newEnumMap(Direction.class);

  static {
    registerClientbound(ClientboundDisconnectPacket.class);
    registerClientbound(ClientboundCombatModifierPacket.class);
    registerClientbound(ClientboundDownloadStoragePacket.class);
    registerClientbound(ClientboundHelloPacket.class);
    registerClientbound(ClientboundSetTrustfactorPacket.class);
    registerClientbound(ClientboundViolationPacket.class);

    registerServerbound(ServerboundConfirmEncryptionPacket.class);
    registerServerbound(ServerboundHelloPacket.class);
    registerServerbound(ServerboundPassNayoroPacket.class);
    registerServerbound(ServerboundRequestStoragePacket.class);
    registerServerbound(ServerboundRequestTrustfactorPacket.class);
    registerServerbound(ServerboundUploadStoragePacket.class);
  }
  
  private static void registerClientbound(Class<? extends Packet<?>> packetClass) {
    register(Direction.CLIENTBOUND, packetClass);
  }

  private static void registerServerbound(Class<? extends Packet<?>> packetClass) {
    register(Direction.SERVERBOUND, packetClass);
  }

  private static void register(Direction direction, Class<? extends Packet<?>> packetClass) {
    try {
      Packet<?> packet = packetClass.newInstance();
      String packetName = packet.name();
      packetsByName.computeIfAbsent(direction, x -> new HashMap<>())
        .put(packetName, packetClass);
      specifications.computeIfAbsent(direction, x -> new HashMap<>())
        .put(packetName, PacketSpecification.from(packet));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Packet<?> fromName(Direction direction, String name) {
    try {
      return packetsByName.get(direction).get(name).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Set<String> packetNamesOf(Direction direction) {
    return new HashSet<>(packetsByName.get(direction).keySet());
  }

  public static Packet<?> fromAssignedId(ProtocolSpecification protocol, Direction direction, int id) {
    try {
      String packetName = protocol.packetIdsOf(direction).get(id);
      return fromName(direction, packetName);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  public static Map<String, PacketSpecification> packetSpecsFor(Direction direction) {
    return specifications.computeIfAbsent(direction, x -> new HashMap<>());
  }
}
