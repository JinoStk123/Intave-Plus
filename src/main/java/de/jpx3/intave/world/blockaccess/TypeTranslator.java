package de.jpx3.intave.world.blockaccess;

import com.comphenix.protocol.utility.MinecraftVersion;
import org.bukkit.Material;

import java.util.Map;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2021
 */

public interface TypeTranslator {
  Map<Material, Material> translationsFor(MinecraftVersion serverVersion, MinecraftVersion clientVersion);
}
