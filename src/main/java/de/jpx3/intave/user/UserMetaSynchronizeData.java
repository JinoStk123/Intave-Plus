package de.jpx3.intave.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.jpx3.intave.event.service.entity.WrappedEntity;
import de.jpx3.intave.event.service.transaction.TransactionCallBackData;
import de.jpx3.intave.tools.annotate.Relocate;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Relocate
public final class UserMetaSynchronizeData {
  private final Player player;
  private final Map<Short, TransactionCallBackData<?>> transactionFeedBackMap = Maps.newConcurrentMap();
  private final Map<Integer, WrappedEntity> synchronizedEntityMap = Maps.newConcurrentMap();
  private final Map<Long, Long> remainingPingPacketTimestamps = Maps.newConcurrentMap();
  private final List<Long> latencyDifferenceBalance = Lists.newArrayList();
  private final List<Integer> occupiedEntityIDs = new CopyOnWriteArrayList<>();

  // Client Synchronization
  public int latency;
  public long lastKeepAliveDifference;
  public int latencyJitter;
  public short transactionCounter = Short.MIN_VALUE;
  public long transactionNumCounter = 0;
  public long lastReceivedTransactionNum = -1;

  public UserMetaSynchronizeData(Player player) {
    this.player = player;
  }

  public int resolveEntityID(int add) {
    User user = UserRepository.userOf(player);
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    List<Integer> entityIDs = synchronizeData.occupiedEntityIDs();
    Integer highestEntityID = entityIDs.stream().max(Integer::compareTo).orElse(0);
    return highestEntityID + add;
  }

  public Map<Short, TransactionCallBackData<?>> transactionFeedBackMap() {
    return transactionFeedBackMap;
  }

  public Map<Integer, WrappedEntity> synchronizedEntityMap() {
    return synchronizedEntityMap;
  }

  public Map<Long, Long> remainingPingPacketTimestamps() {
    return remainingPingPacketTimestamps;
  }

  public List<Long> latencyDifferenceBalance() {
    return latencyDifferenceBalance;
  }

  public List<Integer> occupiedEntityIDs() {
    return occupiedEntityIDs;
  }
}