package de.jpx3.intave.event;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.event.dispatch.MovementDispatcher;
import de.jpx3.intave.event.dispatch.PlayerAbilityEvaluator;
import de.jpx3.intave.event.dispatch.PlayerInventoryEvaluator;
import de.jpx3.intave.event.dispatch.PotionEffectEvaluator;
import de.jpx3.intave.event.service.MovementEmulationEngine;
import de.jpx3.intave.event.service.TransactionFeedbackService;
import de.jpx3.intave.user.UserRepositoryEventListener;

public final class EventService {
  private final IntavePlugin plugin;
  private final TransactionFeedbackService transactionFeedbackService;
  private final MovementEmulationEngine emulationEngine;

  private final UserRepositoryEventListener userRepositoryEventListener;

  public EventService(IntavePlugin plugin) {
    this.plugin = plugin;
    this.transactionFeedbackService = new TransactionFeedbackService(plugin);
    this.emulationEngine = new MovementEmulationEngine(plugin);
    this.userRepositoryEventListener = new UserRepositoryEventListener(plugin);
  }

  public void setup() {
    MovementDispatcher movementDispatcher = new MovementDispatcher(plugin);
    PotionEffectEvaluator potionEffectEvaluator = new PotionEffectEvaluator(plugin);
    PlayerAbilityEvaluator playerAbilityEvaluator = new PlayerAbilityEvaluator(plugin);
    PlayerInventoryEvaluator playerInventoryEvaluator = new PlayerInventoryEvaluator(plugin);

  }
//
//  private void registerListeners() {
//    registerListener(new CheckableEventListener());
//    registerListener(new UserRegistryEventListener(plugin));
//    registerListener(new BlockAccessor());
//  }


  public MovementEmulationEngine emulationEngine() {
    return emulationEngine;
  }

  public TransactionFeedbackService transactionFeedbackService() {
    return transactionFeedbackService;
  }
}