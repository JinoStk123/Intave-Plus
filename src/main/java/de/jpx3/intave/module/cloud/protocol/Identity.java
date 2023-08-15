package de.jpx3.intave.module.cloud.protocol;

import de.jpx3.intave.annotate.Nullable;

import java.util.UUID;

public class Identity {
  @Nullable
  private final UUID id;
  @Nullable
  private final String name;

  public Identity(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public UUID id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String toString() {
    int type = 0;
    if (id != null) {
      type |= 1;
    }
    if (name != null) {
      type |= 2;
    }
    String str = "ID-" + type;
    if (id != null) {
      str += "-" + id.toString().replace("-", "");
    }
    if (name != null) {
      str += "-" + name;
    }
    return str;
  }

  public static Identity fromString(String str) {
    String[] parts = str.split("-");
    if (parts.length < 3) {
      throw new IllegalArgumentException("Invalid identity string: " + str);
    }
    if (!"ID".equals(parts[0])) {
      throw new IllegalArgumentException("Invalid identity string: " + str);
    }
    int type = Integer.parseInt(parts[1]);
    UUID id = null;
    String name = null;
    if ((type & 1) == 1) {
      id = UUID.fromString(parts[2].substring(0, 8) + "-" + parts[2].substring(8, 12) + "-" + parts[2].substring(12, 16) + "-" + parts[2].substring(16, 20) + "-" + parts[2].substring(20, 32));
    }
    if ((type & 2) == 2) {
      name = parts[(type & 1) == 1 ? 3 : 2];
    }
    return new Identity(id, name);
  }
}
