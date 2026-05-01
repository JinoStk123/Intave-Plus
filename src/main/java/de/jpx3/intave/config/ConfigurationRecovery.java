package de.jpx3.intave.config;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.resource.Resource;
import de.jpx3.intave.resource.Resources;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class ConfigurationRecovery {

  private static final Object LOCK = new Object();

  private static final String BUGGED_CONFIG_FOLDER = "bugged config";

  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

  private static final Set<String> VERSION_WARNINGS = ConcurrentHashMap.newKeySet();

  private static final Map<String, ConfigurationType> CONFIGURATION_TYPES = Map.of(
      "config.yml", new ConfigurationType("config.yml", "prefix", "14.0.0"),
      "advanced.yml", new ConfigurationType("advanced.yml", "layout.prefix", "14.0.0")
  );

  private ConfigurationRecovery() {}

  // ---------------------------
  // ENTRY
  // ---------------------------

  static YamlConfiguration loadConfiguration(File file, String defaultResource) {
    ensureConfigurationExists(file, defaultResource);

    try {
      YamlConfiguration configuration = loadFromFile(file);

      VersionState state = validate(configuration, defaultResource);
      if (state != VersionState.OK) {
        handleVersionState(state, file, defaultResource);
      }

      return configuration;

    } catch (Exception e) {
      return recover(file, defaultResource, e);
    }
  }

  static void ensureConfigurationExists(File file, String defaultResource) {
    Resource resource = Resources.resourceFromFile(file);
    if (resource.available()) return;

    writeDefaultSafe(file, defaultResource);
  }

  // ---------------------------
  // RECOVERY (CRASH SAFE)
  // ---------------------------

  static YamlConfiguration recover(File file, String defaultResource, Exception exception) {
    synchronized (LOCK) {

      logFailure(file, exception);

      moveBuggedConfigurationBestEffort(file);

      byte[] data = writeDefaultSafe(file, defaultResource);

      try {
        return loadFromBytes(data);
      } catch (Exception e) {
        IntavePlugin.singletonInstance()
            .logger()
            .error("Recovery parse failed: " + file.getName());

        return new YamlConfiguration();
      }
    }
  }

  private static void logFailure(File file, Exception exception) {
    IntavePlugin.singletonInstance().logger().error(
        "Config recovery triggered",
        "file=" + file.getName(),
        "error=" + exception.getClass().getSimpleName(),
        "msg=" + exception.getMessage()
    );
  }

  // ---------------------------
  // SAFE BACKUP (NO CRASH)
  // ---------------------------

  private static void moveBuggedConfigurationBestEffort(File file) {
    if (!file.exists()) return;

    File folder = new File(file.getParentFile(), BUGGED_CONFIG_FOLDER);

    if (!folder.exists() && !folder.mkdirs()) {
      IntavePlugin.singletonInstance()
          .logger()
          .warn("Cannot create backup folder, skipping backup: " + file.getName());
      return;
    }

    String timestamp = DATE_FORMAT.format(LocalDateTime.now());
    File target = new File(folder, timestamp + "-" + file.getName());

    int i = 1;
    while (target.exists()) {
      target = new File(folder, timestamp + "-" + i++ + "-" + file.getName());
    }

    try {
      Files.move(file.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
    } catch (IOException e) {
      try {
        Files.move(file.toPath(), target.toPath());
      } catch (IOException ex) {
        IntavePlugin.singletonInstance()
            .logger()
            .warn("Backup move failed: " + file.getName());
      }
    }
  }

  // ---------------------------
  // SAFE WRITE (NO OVERWRITE)
  // ---------------------------

  private static byte[] writeDefaultSafe(File file, String defaultResource) {
    Resource jar = Resources.resourceFromJarOrBuild(defaultResource);
    Resource out = Resources.resourceFromFile(file);

    try {
      byte[] data = readFully(jar, defaultResource);

      // FIX: never overwrite existing user config
      if (!file.exists() || file.length() == 0) {
        out.write(data);
      }

      return data;

    } catch (IOException e) {
      throw new RuntimeException("Default write failed: " + defaultResource, e);
    }
  }

  // ---------------------------
  // VALIDATION
  // ---------------------------

  private static YamlConfiguration loadFromFile(File file)
      throws IOException, InvalidConfigurationException {

    YamlConfiguration cfg = new YamlConfiguration();
    cfg.load(file);
    return cfg;
  }

  private static VersionState validate(YamlConfiguration cfg, String resource) {

    ConfigurationType type =
        CONFIGURATION_TYPES.get(resource.toLowerCase(Locale.ROOT));

    if (type == null) {
      throw new IllegalStateException("No schema: " + resource);
    }

    validatePrefix(cfg, type.prefixPath);
    return validateVersion(cfg, type);
  }

  private static void validatePrefix(YamlConfiguration cfg, String path) {
    if (!cfg.isString(path)) {
      throw new IllegalArgumentException("Missing: " + path);
    }

    String v = cfg.getString(path);
    if (v == null || v.trim().isEmpty()) {
      throw new IllegalArgumentException("Empty: " + path);
    }
  }

  private static VersionState validateVersion(YamlConfiguration cfg, ConfigurationType type) {

    String v = cfg.getString("version");

    if (v == null || v.trim().isEmpty()) {
      warnOnce(type.resourceName, "Missing version: " + type.resourceName);
      return VersionState.MISSING;
    }

    int cmp = compareSchemaVersions(v, type.schemaVersion);

    if (cmp != 0) {
      warnOnce(type.resourceName, "Schema mismatch: " + v + " vs " + type.schemaVersion);
      return VersionState.MISMATCH;
    }

    return VersionState.OK;
  }

  private static void warnOnce(String key, String msg) {
    if (VERSION_WARNINGS.add(key)) {
      IntavePlugin.singletonInstance().logger().warn(msg);
    }
  }

  private static void handleVersionState(VersionState state, File file, String resource) {
    // reserved
  }

  // ---------------------------
  // IO
  // ---------------------------

  private static byte[] readFully(Resource resource, String name) throws IOException {
    try (InputStream in = resource.read()) {

      if (in == null) {
        throw new IOException("Missing: " + name);
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];

      int r;
      while ((r = in.read(buffer)) != -1) {
        out.write(buffer, 0, r);
      }

      return out.toByteArray();
    }
  }

  private static YamlConfiguration loadFromBytes(byte[] bytes)
      throws InvalidConfigurationException {

    YamlConfiguration cfg = new YamlConfiguration();
    cfg.loadFromString(new String(bytes, StandardCharsets.UTF_8));
    return cfg;
  }

  // ---------------------------
  // VERSION COMPARE
  // ---------------------------

  private static int compareSchemaVersions(String a, String b) {
    int[] av = parse(a);
    int[] bv = parse(b);

    int len = Math.max(av.length, bv.length);

    for (int i = 0; i < len; i++) {
      int x = i < av.length ? av[i] : 0;
      int y = i < bv.length ? bv[i] : 0;

      int cmp = Integer.compare(x, y);
      if (cmp != 0) return cmp;
    }

    return 0;
  }

  private static int[] parse(String v) {
    String[] parts = v.split("\\.");
    int[] out = new int[parts.length];

    for (int i = 0; i < parts.length; i++) {
      out[i] = parsePart(parts[i]);
    }

    return out;
  }

  private static int parsePart(String part) {
    int i = 0;

    while (i < part.length() && Character.isDigit(part.charAt(i))) {
      i++;
    }

    if (i == 0) return 0;

    try {
      return Integer.parseInt(part.substring(0, i));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  // ---------------------------
  // TYPES
  // ---------------------------

  private static final class ConfigurationType {
    final String resourceName;
    final String prefixPath;
    final String schemaVersion;

    ConfigurationType(String r, String p, String s) {
      this.resourceName = r;
      this.prefixPath = p;
      this.schemaVersion = s;
    }
  }

  private enum VersionState {
    OK,
    MISSING,
    MISMATCH
  }
}