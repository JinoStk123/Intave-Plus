package de.jpx3.intave.connect.cloud.protocol;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProtocolSpecification {
  private final Map<Direction, Set<String>> packetsNames = Maps.newEnumMap(Direction.class);
  private final Map<Direction, List<String>> packetsIds = Maps.newEnumMap(Direction.class);
  private final Map<Direction, Boolean> idsKnown = Maps.newEnumMap(Direction.class);

  public ProtocolSpecification() {
    packetsNames.put(Direction.CLIENTBOUND, Sets.newHashSet("HELLO", "DISCONNECT"));
    packetsNames.put(Direction.SERVERBOUND, Sets.newHashSet("HELLO"));
  }

  public Packet<?> packetFromName(Direction direction, String name) {
    return PacketRegistry.fromName(direction, name);
  }

  public Packet<?> packetFromId(Direction direction, int id) {
    return PacketRegistry.fromAssignedId(this, direction, id);
  }

  public void overrideAvailablePackets(Direction direction, Set<String> packetNames) {
    packetsNames.put(direction, packetNames);
  }

  public void overridePacketIds(Direction direction, List<String> packetClasses) {
    packetsIds.put(direction, packetClasses);
    idsKnown.put(direction, true);
  }

  public List<String> packetIdsOf(Direction direction) {
    return packetsIds.get(direction);
  }

  public boolean packetIdsKnownFor(Direction direction) {
    return idsKnown.containsKey(direction);
  }

  public boolean packetAvailable(Direction direction, String name) {
    return packetsNames.get(direction).contains(name);
  }

  public int packetId(Direction direction, Class<? extends Packet> packetClass) {
    return packetsIds.get(direction).indexOf(packetClass);
  }
}
