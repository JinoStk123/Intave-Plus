package de.jpx3.intave.config;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class ConfigurationService {
  private final IntavePlugin plugin;
  private final ConfigurationLoader loader;
  private final String configurationKey;

  public ConfigurationService(IntavePlugin plugin) {
    this.plugin = plugin;
    this.configurationKey = resolveConfigurationKey();
    this.loader = new ConfigurationLoader(configurationKey);
  }

  private String resolveConfigurationKey() {
    File dataFolder = plugin.getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    File configFile = new File(dataFolder, "config.yml");
    if (!configFile.exists()) {
      plugin.saveResource("config.yml", false);
    }
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
    String configurationIdentifier = configuration.getString("config-identifier");
    if(configurationIdentifier == null) {
      throw new IntaveException("It seems like you are using an old/invalid configuration");
    }
    return configurationIdentifier;
  }

  public void setupConfiguration(String requiredHash) {
    String hash = loader.precomputeConfigurationHash();
    if(hash == null || /* we don't have a configuration */
      requiredHash == null ||  /* no connection to our servers */
      hash.equalsIgnoreCase(requiredHash) /* configuration is up to date */
    ) {
      loader.loadConfiguration();
    } else {
      loader.loadConfigurationUpdatedForcefully();
    }
  }

  public String configurationKey() {
    return configurationKey;
  }
}