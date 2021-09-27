package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static com.comphenix.protocol.PacketType.Play.Client;
import static com.comphenix.protocol.PacketType.Play.Server;

public final class PacketReaders {
  private final static Map<PacketType, ThreadLocal<? extends PacketReader>> readerLocals = new ConcurrentHashMap<>();

  public static void setup() {
    setup(Server.BLOCK_CHANGE, SingleBlockChangeReader::new);
    setup(Server.BLOCK_BREAK, SingleBlockChangeReader::new);
    setup(Server.MULTI_BLOCK_CHANGE, MultiBlockChangeReader::new);
    setup(Server.MAP_CHUNK, MapChunkReader::new);
    setup(Server.MAP_CHUNK_BULK, MapChunkBulkReader::new);
    setup(Server.ENTITY_METADATA, EntityReader::new);
    setup(Server.ENTITY_VELOCITY, EntityReader::new);
    setup(Server.SPAWN_ENTITY_LIVING, EntityReader::new);
    setup(Server.SPAWN_ENTITY, EntityReader::new);
    setup(Server.ENTITY_EFFECT, EntityReader::new);
    setup(Server.REMOVE_ENTITY_EFFECT, EntityReader::new);
    setup(Server.NAMED_ENTITY_SPAWN, EntityReader::new);
    setup(Server.UPDATE_ATTRIBUTES, EntityReader::new);
    setup(Server.BLOCK_BREAK_ANIMATION, EntityReader::new);

    setup(Client.CUSTOM_PAYLOAD, PayloadInReader::new);
    setup(Client.BLOCK_PLACE, BlockInteractionReader::new);
    setup(Client.USE_ITEM, BlockInteractionReader::new);
    setup(Client.BLOCK_DIG, BlockPositionReader::new);
  }

  private static void setup(PacketType type, Supplier<? extends PacketReader> supplier) {
    readerLocals.put(type, ThreadLocal.withInitial(supplier));
  }

  public static <T extends PacketReader> T readerOf(PacketContainer container) {
    PacketType type = container.getType();
    ThreadLocal<? extends PacketReader> threadLocal = readerLocals.get(type);
    if (threadLocal == null) {
      throw new IllegalStateException("No interpreter available for " + type);
    }
    PacketReader interpreter = threadLocal.get();
    interpreter.flush(container);
    if (interpreter instanceof CompiledPacketReader) {
      ((CompiledPacketReader) interpreter).compile();
    }
    //noinspection unchecked
    return (T) interpreter;
  }
}
