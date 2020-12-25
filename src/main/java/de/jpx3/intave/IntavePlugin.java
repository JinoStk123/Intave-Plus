package de.jpx3.intave;

import de.jpx3.intave.adapter.ViaVersionAdapter;
import de.jpx3.intave.detect.CheckService;
import de.jpx3.intave.event.service.RetributionService;
import de.jpx3.intave.event.EventService;
import de.jpx3.intave.event.bukkit.BukkitEventLinker;
import de.jpx3.intave.event.packet.PacketSubscriptionLinker;
import de.jpx3.intave.tools.client.SinusCache;
import de.jpx3.intave.tools.inventory.InventoryUseItemHelper;
import de.jpx3.intave.world.collision.CollisionEngine;
import org.bukkit.plugin.java.JavaPlugin;

public final class IntavePlugin extends JavaPlugin {
  private static IntavePlugin singletonInstance;

  private BukkitEventLinker eventLinker;
  private PacketSubscriptionLinker packetSubscriptionLinker;
  private EventService eventService;
  private RetributionService retributionService;
  private CheckService checkService;

  static {
    // stage 1


  }

  public IntavePlugin() {
    singletonInstance = this;
    // stage 2
  }

  @Override
  public void onLoad() {
    // stage 3
  }

  @Override
  public void onEnable() {
    // stage 4

    eventLinker = new BukkitEventLinker(this);
    packetSubscriptionLinker = new PacketSubscriptionLinker(this);

    checkService = new CheckService(this);

    // stage 5

    SinusCache.setup();
    ViaVersionAdapter.setup();
    InventoryUseItemHelper.setup();
    CollisionEngine.setup();


    // stage 6


    // stage 7

    checkService = new CheckService(this);
    retributionService = new RetributionService();
    eventService = new EventService(this);


    // stage 8

    checkService.setup();
    eventService.setup();

  }

  @Override
  public void onDisable() {
  }

  public CheckService checkService() {
    return checkService;
  }

  public EventService eventService() {
    return eventService;
  }

  public BukkitEventLinker eventLinker() {
    return eventLinker;
  }

  public PacketSubscriptionLinker packetSubscriptionLinker() {
    return packetSubscriptionLinker;
  }

  public RetributionService retributionService() {
    return this.retributionService;
  }

  public static IntavePlugin singletonInstance() {
    return singletonInstance;
  }
}