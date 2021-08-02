package de.jpx3.intave.event.dispatch;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.comphenix.protocol.wrappers.WrappedAttributeModifier;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.EventProcessor;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAbilityData;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

import java.util.List;

import static de.jpx3.intave.event.packet.PacketId.Server.UPDATE_ATTRIBUTES;

public final class AttributeDispatcher implements EventProcessor {
  private final IntavePlugin plugin;

  public AttributeDispatcher(IntavePlugin plugin) {
    this.plugin = plugin;
    this.plugin.packetSubscriptionLinker().linkSubscriptionsIn(this);
    this.plugin.eventLinker().registerEventsIn(this);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      UPDATE_ATTRIBUTES
    }
  )
  public void sentAttributes(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PacketContainer packet = event.getPacket();
    if (packet.getEntityModifier(event).read(0) == player) {
      List<WrappedAttribute> attributes = packet.getAttributeCollectionModifier().read(0);
      plugin.eventService().feedback().singleSynchronize(player, attributes, (player1, target) -> target.forEach(attribute -> receivedAttribute(user, attribute)));
    }
  }

  private void receivedAttribute(User user, WrappedAttribute attribute) {
    UserMetaAbilityData abilityData = user.meta().abilityData();
    List<WrappedAttributeModifier> modifiers = abilityData.modifiersOf(attribute);
    modifiers.clear();
    modifiers.addAll(attribute.getModifiers());
  }
}
