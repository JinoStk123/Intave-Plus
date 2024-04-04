package de.jpx3.intave.access.player.trust;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SelectiveTrustfactorResolver implements TrustFactorResolver {
  private final TrustFactorResolver[] trustFactorResolver;

  public SelectiveTrustfactorResolver(TrustFactorResolver... trustFactorResolver) {
    this.trustFactorResolver = trustFactorResolver;
  }

  @Override
  public synchronized void resolve(Player player, Consumer<TrustFactor> callback) {
    pendingCallbacks.put(player, callback);
    for (TrustFactorResolver resolver : trustFactorResolver) {
      resolver.resolve(player, trustFactor -> issueTrustFactor(player, trustFactor));
    }
    checkTimeouts();
  }

  private final Map<Player, Consumer<TrustFactor>> pendingCallbacks = new ConcurrentHashMap<>();
  private final Map<Player, List<TrustFactor>> issuedTrustFactors = new ConcurrentHashMap<>();
  private final Map<Player, Long> lastIssued = new ConcurrentHashMap<>();

  public void issueTrustFactor(Player player, TrustFactor trustFactor) {
    issuedTrustFactors.computeIfAbsent(player, p -> new ArrayList<>()).add(trustFactor);
    lastIssued.put(player, System.currentTimeMillis());

    checkTimeouts();
  }

  private synchronized void checkTimeouts() {
    long now = System.currentTimeMillis();
    for (Map.Entry<Player, Long> entry : lastIssued.entrySet()) {
      if (now - entry.getValue() > 1000 * 10 || trustFactorResolver.length == issuedTrustFactors.get(entry.getKey()).size()) {
        List<TrustFactor> trustFactors = issuedTrustFactors.get(entry.getKey());
        issuedTrustFactors.remove(entry.getKey());
        Consumer<TrustFactor> trustFactorConsumer = pendingCallbacks.remove(entry.getKey());
        TrustFactor trustFactor = highestOf(trustFactors);
        if (trustFactor == null) {
          continue;
        }
        trustFactorConsumer.accept(trustFactor);
      }
    }
    lastIssued.entrySet().removeIf(entry -> now - entry.getValue() > 1000 * 10);
  }

  private TrustFactor highestOf(List<TrustFactor> trustFactors) {
    if (trustFactors == null || trustFactors.isEmpty()) {
      return null;
    }
    TrustFactor highest = null;
    for (TrustFactor trustFactor : trustFactors) {
      if (highest == null) {
        highest = trustFactor;
      }
      if (trustFactor.factor() > highest.factor()) {
        highest = trustFactor;
      }
    }
    return highest;
  }

  @Override
  public String toString() {
    return "max{" + Arrays.toString(trustFactorResolver) + "}";
  }
}
