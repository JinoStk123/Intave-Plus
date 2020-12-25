package de.jpx3.intave.user;

import de.jpx3.intave.tools.inventory.PlayerEnchantmentHelper;
import de.jpx3.intave.tools.sync.Synchronizer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class UserMetaInventoryData {
  private final Player player;
  private ItemStack heldItem;
  private boolean handActive;
  private boolean interactedWithFood;

  private boolean inventoryOpen;
  public int handActiveTicks;
  public int pastItemUsageTransition;
  public int pastHotBarSlotChange;
  public int selectedHotBarSlot;

  public UserMetaInventoryData(Player player) {
    this.player = player;
    this.heldItem = resolveMaterialInHand();
  }

  public void resynchronizeHeldItem() {
    this.heldItem = resolveMaterialInHand();
  }

  public boolean interactedWithFood() {
    return interactedWithFood;
  }

  private ItemStack resolveMaterialInHand() {
    return player.getItemInHand();
  }

  public boolean handActive() {
    return handActive;
  }

  public ItemStack heldItem() {
    return heldItem;
  }

  public boolean inventoryOpen() {
    return inventoryOpen;
  }

  public void setHeldItem(ItemStack heldItem) {
    this.heldItem = heldItem;
  }

  public void deactivateHand() {
    User user = UserRepository.userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    if (heldItem != null && PlayerEnchantmentHelper.tridentRiptideEnchanted(heldItem)) {
      movementData.pastRiptideSpin = 0;
    }
    this.handActive = false;
    this.interactedWithFood = false;
    this.pastItemUsageTransition = 0;
    this.handActiveTicks = 0;
  }

  public void activateHand(boolean interactedWithFood) {
    this.handActive = true;
    this.interactedWithFood = interactedWithFood;
    this.pastItemUsageTransition = 0;
    this.handActiveTicks = 0;
  }

  public void applySlotSwitch() {
    int previousItemSlot = this.selectedHotBarSlot;
    int newItemSlot = this.selectedHotBarSlot + 1;
    if (newItemSlot > 8) {
      newItemSlot = 0;
    }
    setHeldItemSlot(newItemSlot);
    setHeldItemSlot(previousItemSlot);
  }

  private void setHeldItemSlot(int slot) {
    PlayerInventory inventory = player.getInventory();
    inventory.setHeldItemSlot(slot);
  }

  public void setInteractedWithFood(boolean interactedWithFood) {
    this.interactedWithFood = interactedWithFood;
  }

  public void setInventoryOpen(boolean inventoryOpen) {
    this.inventoryOpen = inventoryOpen;
  }
}