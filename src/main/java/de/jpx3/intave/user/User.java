package de.jpx3.intave.user;

import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.reflect.Reflection;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public final class User {
  private final WeakReference<Player> playerRef;
  private final WeakReference<Object> nmsEntity;
  private final UserMeta userMeta;
  private final boolean hasPlayer;

  private User(Player player) {
    this.playerRef = new WeakReference<>(player);
    this.nmsEntity = new WeakReference<>(Reflection.resolveEntityNMSHandle(player));
    this.hasPlayer = player != null;
    this.userMeta = new UserMeta(player, this);
  }

  public UserMeta meta() {
    return this.userMeta;
  }

  public Object playerHandle() {
    return nmsEntity.get();
  }

  public Player bukkitPlayer() {
    Player player = playerRef.get();
    if(player == null) {
      throw new IntaveInternalException("Unable to reference player through service repo: Fallback user lacks reference");
    }
    return player;
  }

  public static User empty() {
    return new User(null);
  }

  public static User userFor(Player player) {
    return new User(player);
  }

  public static final class UserMeta {
    private final UserMetaViolationLevelData violationLevelData;
    private final UserMetaMovementData movementData;
    private final UserMetaAbilityData abilityData;
    private final UserMetaPotionData potionData;
    private final UserMetaClientData clientData;
    private final UserMetaInventoryData inventoryData;

    public UserMeta(Player player, User user) {
      this.violationLevelData = new UserMetaViolationLevelData();
      this.clientData = new UserMetaClientData(player);
      this.abilityData = new UserMetaAbilityData(player);
      this.potionData = new UserMetaPotionData();
      this.inventoryData = new UserMetaInventoryData(player);
      this.movementData = new UserMetaMovementData(player, user);
    }

    public UserMetaViolationLevelData violationLevelData() {
      return violationLevelData;
    }

    public UserMetaMovementData movementData() {
      return movementData;
    }

    public UserMetaInventoryData inventoryData() {
      return inventoryData;
    }

    public UserMetaAbilityData abilityData() {
      return abilityData;
    }

    public UserMetaPotionData potionData() {
      return potionData;
    }

    public UserMetaClientData clientData() {
      return clientData;
    }
  }
}