package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.ThreadLocalRandom;

public final class EmulationModerateExecutor extends MiningStrategyExecutor {
  private static final long MANIPULATED_DAMAGE_TICKS_DURATION = 100000;

  public EmulationModerateExecutor(User user) {
    super(user);
  }

  @Override
  public void setup() {
    UserMetaAttackData attackData = user().meta().attackData();
    attackData.miningStartEmulationModerate = AccessHelper.now();
  }

  @Override
  public void receiveAttackOfPlayer(EntityDamageByEntityEvent event) {
    UserMetaAttackData attackData = user().meta().attackData();
    EntityNoDamageTickChanger.applyHurtTimeChangeTo(user(), (int) (MANIPULATED_DAMAGE_TICKS_DURATION / 50L));
    int randomNumber = ThreadLocalRandom.current().nextInt(5, 10);
    if (attackData.attackCount % 5 == 0 || attackData.attackCount % randomNumber == 0) {
      event.setDamage(0);
    }

    if (attackData.attackCount % ThreadLocalRandom.current().nextInt(1, 10) == 0) {
      event.setCancelled(true);
    }
  }

  @Override
  public MiningStrategy miningStrategy() {
    return MiningStrategy.EMULATION_MODERATE;
  }
}