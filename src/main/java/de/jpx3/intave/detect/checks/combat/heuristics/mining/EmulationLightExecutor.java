package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class EmulationLightExecutor extends MiningStrategyExecutor {
  private static final long MANIPULATED_DAMAGE_TICKS_DURATION = 50000;

  public EmulationLightExecutor(User user) {
    super(user);
  }

  @Override
  public void setup() {
    UserMetaAttackData attackData = user().meta().attackData();
    attackData.miningStartEmulationLight = AccessHelper.now();
  }

  @Override
  public void receiveAttackOfPlayer(EntityDamageByEntityEvent event) {
    EntityNoDamageTickChanger.applyHurtTimeChangeTo(user(), (int) (MANIPULATED_DAMAGE_TICKS_DURATION / 50L));
  }

  @Override
  public MiningStrategy miningStrategy() {
    return MiningStrategy.EMULATION_LIGHT;
  }
}