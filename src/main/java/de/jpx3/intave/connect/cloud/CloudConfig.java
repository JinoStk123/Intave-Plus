package de.jpx3.intave.connect.cloud;

import de.jpx3.intave.IntaveControl;
import org.bukkit.configuration.ConfigurationSection;

public final class CloudConfig {
  private boolean enabled;
  private CloudFeatures features;

  public boolean isEnabled() {
    return this.enabled;
  }

  public CloudFeatures features() {
    return this.features;
  }

  public static CloudConfig from(ConfigurationSection section) {
    // enabled by default
    boolean enabled = section == null ? !IntaveControl.GOMME_MODE : section.getBoolean("enabled", !IntaveControl.GOMME_MODE);
    ConfigurationSection featuresSection = section == null ? null : section.getConfigurationSection("features");
    boolean cloudStorage = featuresSection == null || featuresSection.getBoolean("cloud-storage", true);
    boolean cloudTrustFactor = featuresSection == null || featuresSection.getBoolean("cloud-trustfactor", true);
    boolean cloudHeuristics = featuresSection == null || featuresSection.getBoolean("cloud-heuristics", true);
    boolean cloudLogs = featuresSection == null || featuresSection.getBoolean("cloud-logs", true);
    CloudFeatures features = new CloudFeatures();
    features.cloudStorage = cloudStorage;
    features.cloudTrustFactor = cloudTrustFactor;
    features.cloudHeuristics = cloudHeuristics;
    features.cloudLogs = cloudLogs;
    CloudConfig config = new CloudConfig();
    config.enabled = enabled;
    config.features = features;
    return config;
  }

  public static class CloudFeatures {
    private boolean cloudStorage;
    private boolean cloudTrustFactor;
    private boolean cloudHeuristics;
    private boolean cloudLogs;

    public boolean cloudStorageEnabled() {
      return this.cloudStorage;
    }

    public boolean cloudTrustfactorEnabled() {
      return this.cloudTrustFactor;
    }

    public boolean sampleTransmission() {
      return this.cloudHeuristics;
    }

    public boolean isCloudLogs() {
      return this.cloudLogs;
    }
  }
}
