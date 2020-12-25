package de.jpx3.intave.tools.inventory;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.Lists;
import de.jpx3.intave.adapter.ProtocolLibAdapter;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.jpx3.intave.tools.inventory.PlayerEnchantmentHelper.tridentRiptideEnchanted;

public final class InventoryUseItemHelper {
  public static final Material ITEM_TRIDENT = materialByName("TRIDENT");
  private static final List<Material> USE_ITEM_LIST = Lists.newArrayList();
  private static final List<Material> FOOD_LIST = Lists.newArrayList();
  private static final List<Material> POTION_LIST = Lists.newArrayList();

  public static void setup() {
    try {
      MinecraftVersion serverVersion = ProtocolLibAdapter.serverVersion();
      setupDefaults(serverVersion);
      setupFood();
      setupPotions();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static void setupDefaults(MinecraftVersion serverVersion) {
    if (serverVersion.isAtLeast(MinecraftVersion.AQUATIC_UPDATE)) {
      USE_ITEM_LIST.add(resolveTrident());
    }
    if (serverVersion.isAtLeast(MinecraftVersion.COMBAT_UPDATE)) {
      USE_ITEM_LIST.add(resolveShield());
    } else {
      USE_ITEM_LIST.addAll(resolveSwords());
    }
    USE_ITEM_LIST.add(resolveBow());
  }

  private static void setupFood() {
    List<Material> defaultItems = Lists.newArrayList(
      materialByName("apple"),
      materialByName("bread"),
      materialByName("porkchop"),
      materialByName("cooked_porkchop"),
      materialByName("cookie"),
      materialByName("melon"),
      materialByName("beef"),
      materialByName("cooked_beef"),
      materialByName("chicken"),
      materialByName("cooked_chicken"),
      materialByName("rotten_flesh"),
      materialByName("spider_eye"),
      materialByName("baked_potato"),
      materialByName("poisonous_potato"),
      materialByName("golden_carrot"),
      materialByName("pumpkin_pie"),
      materialByName("rabbit"),
      materialByName("cooked_rabbit"),
      materialByName("mutton"),
      materialByName("cooked_mutton"),
      materialByName("enchanted_golden_apple")
    );
    FOOD_LIST.removeIf(Objects::isNull);
    FOOD_LIST.addAll(defaultItems);
  }

  private static void setupPotions() {
    POTION_LIST.add(Material.POTION);
  }

  private static List<Material> resolveSwords() {
    return materialsByName("SWORD");
  }

  private static Material resolveTrident() {
    return materialByName("TRIDENT");
  }

  private static Material resolveShield() {
    return materialByName("SHIELD");
  }

  private static Material resolveBow() {
    return Material.BOW;
  }

  private static List<Material> materialsByName(String name) {
    return Arrays.stream(Material.values())
      .filter(material -> material.name().toLowerCase().contains(name.toLowerCase()))
      .collect(Collectors.toList());
  }

  private static Material materialByName(String name) {
    return Arrays.stream(Material.values())
      .filter(material -> material.name().equalsIgnoreCase(name))
      .findFirst()
      .orElse(null);
  }

  public static boolean isUseItem(
    Player player,
    @Nullable ItemStack itemStack
  ) {
    Material item = itemStack == null ? Material.AIR : itemStack.getType();
    int foodLevel = player.getFoodLevel();
    if (ITEM_TRIDENT != null && item == ITEM_TRIDENT) {
      return tridentUsable(UserRepository.userOf(player), itemStack);
    }
    boolean useItem = USE_ITEM_LIST.contains(item);
    boolean food = FOOD_LIST.contains(item) && foodLevel < 20;
    boolean potion = POTION_LIST.contains(item);
    return useItem || food || potion;
  }

  public static boolean isFoodUsable(
    Player player,
    @Nullable ItemStack itemStack
  ) {
    int foodLevel = player.getFoodLevel();
    Material item = itemStack == null ? Material.AIR : itemStack.getType();
    return foodLevel < 20 && FOOD_LIST.contains(item);
  }

  private static boolean tridentUsable(
    User user,
    ItemStack itemStack
  ) {
    Player player = user.bukkitPlayer();
    World world = player.getWorld();
    UserMetaMovementData movementData = user.meta().movementData();
    if (tridentRiptideEnchanted(itemStack)) {
      return movementData.inWater || (world.isThundering() || world.hasStorm());
    }
    return true;
  }
}