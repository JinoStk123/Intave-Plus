package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class MiningStrategyExecutor {
  private final User user;
  private final long added;

  public MiningStrategyExecutor(User user) {
    this.user = user;
    this.added = AccessHelper.now();
    this.registerExecutor();
    this.setup();
  }

  private void registerExecutor() {
    UserMetaAttackData attackData = user.meta().attackData();
    attackData.activeMiningStrategies.put(miningStrategy(), this);
  }

  public void setup() {}

  public void stopStrategy() {}

  public void receiveAttackOfPlayer(EntityDamageByEntityEvent event) {}

  public boolean expired() {
    int duration = miningStrategy().duration();
    return duration > 0 && AccessHelper.now() - added > duration;
  }

  public User user() {
    return user;
  }

  public abstract MiningStrategy miningStrategy();
}