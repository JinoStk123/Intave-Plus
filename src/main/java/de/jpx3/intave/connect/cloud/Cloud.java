package de.jpx3.intave.connect.cloud;

import de.jpx3.intave.access.player.trust.TrustFactor;
import de.jpx3.intave.annotate.HighOrderService;
import de.jpx3.intave.cleanup.ShutdownTasks;
import de.jpx3.intave.connect.cloud.protocol.Identity;
import de.jpx3.intave.connect.cloud.protocol.Packet;
import de.jpx3.intave.connect.cloud.protocol.Shard;
import de.jpx3.intave.connect.cloud.protocol.Token;
import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundRequestStoragePacket;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundRequestTrustfactorPacket;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundUploadStoragePacket;
import de.jpx3.intave.connect.cloud.request.Request;
import de.jpx3.intave.resource.Resource;
import de.jpx3.intave.resource.Resources;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@HighOrderService
public final class Cloud {
  // later
  private static final Resource INITIAL_SHARED_KEY_RESOURCE = Resources.localServiceCacheResource("cloud-initial.dat", "cloud-initial", TimeUnit.DAYS.toMillis(30));
  private static final Resource SHARD_STORAGE_RESOURCE = Resources.fileCache("shardStorage");
  private static final ShardCache shardCache = SHARD_STORAGE_RESOURCE.collectLines(ShardCache.resourceCollector());

  private final List<Session> sessions = new ArrayList<>();
  private final Map<UUID, Request<TrustFactor>> trustfactorRequests = new HashMap<>();
  private final Map<UUID, Request<ByteBuffer>> storageRequests = new HashMap<>();

  public void init() {
  }

  public void connect() throws Exception {
    Session masterSession = new Session(shardCache.masterShard(), this);
    masterSession.init();
    sessions.add(masterSession);
    ShutdownTasks.add(this::disable);
  }

  private void disable() {
    sessions.forEach(Session::close);
  }

  public void setMasterShard(
    String host, int port, byte[] tokenBytes, long tokenValidUntil
  ) {
    shardCache.addShard(new Shard("master", host, port, new Token(tokenBytes, tokenValidUntil)));
  }

  public boolean knowsMasterShard() {
    return shardCache.hasMasterShard() &&
      shardCache.masterCloudToken().isStillValidIn(5, TimeUnit.MINUTES);
  }

  private void sendPacket(Packet<Serverbound> packet) {
    for (Session session : sessions) {
      if (session.canSend(packet)) {
        session.sendPacket(packet);
        break;
      }
    }
  }

  public void trustfactorRequest(UUID id, Consumer<TrustFactor> callback) {
    Request<TrustFactor> request = trustfactorRequests.get(id);
    if (request == null) {
      request = new Request<>();
      trustfactorRequests.put(id, request);
    }
    request.subscribe(callback);
    sendPacket(new ServerboundRequestTrustfactorPacket(Identity.from(id)));
  }

  public void storageRequest(UUID id, Consumer<ByteBuffer> callback) {
    Request<ByteBuffer> request = storageRequests.get(id);
    if (request == null) {
      request = new Request<>();
      storageRequests.put(id, request);
    }
    request.subscribe(callback);
    sendPacket(new ServerboundRequestStoragePacket(Identity.from(id)));
  }

  public void saveStorage(UUID id, ByteBuffer buffer) {
    sendPacket(new ServerboundUploadStoragePacket(Identity.from(id), buffer));
  }
}
