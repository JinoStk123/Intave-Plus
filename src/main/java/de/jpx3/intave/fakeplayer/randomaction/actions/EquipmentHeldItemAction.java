package de.jpx3.intave.fakeplayer.randomaction.actions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.fakeplayer.equipment.Equipment;
import de.jpx3.intave.fakeplayer.equipment.EquipmentContainer;
import de.jpx3.intave.fakeplayer.randomaction.ActionType;
import de.jpx3.intave.fakeplayer.randomaction.RandomAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public final class EquipmentHeldItemAction extends RandomAction {
  public EquipmentHeldItemAction(Player player, FakePlayer fakePlayer) {
    super(Probability.MEDIUM, ActionType.HELD_ITEM_CHANGE, player, fakePlayer);
  }

  @Override
  protected void performAction() {
    EquipmentContainer equipment = Equipment.createEquipment();
    Material optionalHeldItem = equipment.heldItem();
    if (optionalHeldItem != null) {
      updateHeldItem(optionalHeldItem);
    }
  }

  private void updateHeldItem(Material material) {
    PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
    packet.getIntegers().writeSafely(0, this.fakePlayer.fakePlayerEntityId());
    packet.getModifier().writeSafely(1, 0);
    packet.getItemModifier().writeSafely(0, new ItemStack(material));
    try {
      protocolManager.sendServerPacket(this.parentPlayer, packet);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}