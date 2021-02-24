package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class EmulationHeavyExecutor extends MiningStrategyExecutor {
  public EmulationHeavyExecutor(User user) {
    super(user);
  }

  @Override
  public void setup() {
    UserMetaAttackData attackData = user().meta().attackData();
    attackData.miningStartEmulationHeavy = AccessHelper.now();
  }

  @Override
  public void receiveAttackOfPlayer(EntityDamageByEntityEvent event) {
    event.setCancelled(true);
  }

  @Override
  public MiningStrategy miningStrategy() {
    return MiningStrategy.EMULATION_HEAVY;
  }
}