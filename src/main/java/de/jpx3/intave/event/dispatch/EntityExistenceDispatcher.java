package de.jpx3.intave.event.dispatch;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.event.packet.*;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import de.jpx3.intave.user.UserMetaSynchronizeData;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

public final class EntityExistenceDispatcher implements PacketEventSubscriber {
  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packets = {
      @PacketDescriptor(sender = Sender.SERVER, packetName = "SPAWN_ENTITY_LIVING"),
      @PacketDescriptor(sender = Sender.SERVER, packetName = "SPAWN_ENTITY"),
      @PacketDescriptor(sender = Sender.SERVER, packetName = "NAMED_ENTITY_SPAWN")
    }
  )
  public void receiveEntitySpawn(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PacketContainer packet = event.getPacket();
    Integer entityID = packet.getIntegers().read(0);
    checkFakePlayerOverride(user, entityID);
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    synchronizeData.occupiedEntityIDs().add(entityID);
  }

  private void checkFakePlayerOverride(User user, int entityID) {
    UserMetaAttackData attackData = user.meta().attackData();
    FakePlayer fakePlayer = attackData.fakePlayer();
    if (fakePlayer != null) {
      if (fakePlayer.fakePlayerEntityId() == entityID) {
        fakePlayer.despawn();
      }
    }
  }

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.SERVER, packetName = "ENTITY_DESTROY")
    }
  )
  public void receiveEntityDestroy(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PacketContainer packet = event.getPacket();
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    int[] entityIDs = packet.getIntegerArrays().read(0);
    for (int entityID : entityIDs) {
      synchronizeData.occupiedEntityIDs().remove(entityID);
    }
  }
}