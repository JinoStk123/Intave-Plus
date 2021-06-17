package de.jpx3.intave.world.blockaccess;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import de.jpx3.intave.access.IntaveResourceCompilationException;
import org.bukkit.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class VERTRAFileTypeTranslator implements TypeTranslator {
  private final List<TypeTranslation> typeTranslations;

  public VERTRAFileTypeTranslator(List<TypeTranslation> typeTranslations) {
    this.typeTranslations = typeTranslations;
  }

  @Override
  public Map<Material, Material> translationsFor(MinecraftVersion serverVersion, MinecraftVersion clientVersion) {
    boolean behind = !clientVersion.isAtLeast(serverVersion);
    if (behind) {
      return typeTranslations.stream()
        .filter(typeTranslation -> appropriateTranslation(typeTranslation, serverVersion, clientVersion))
        .collect(Collectors.toMap(typeTranslation -> typeTranslation.typeFrom, typeTranslation -> typeTranslation.typeTo, (a, b) -> b));
    } else {
      return Maps.newHashMap();
    }
  }

  private static boolean appropriateTranslation(TypeTranslation typeTranslation, MinecraftVersion serverVersion, MinecraftVersion clientVersion) {
    return serverVersion.isAtLeast(typeTranslation.versionTo) && !clientVersion.isAtLeast(typeTranslation.versionFrom) && clientVersion.isAtLeast(typeTranslation.versionTo);
  }

  public static TypeTranslator fromFile(File file) throws FileNotFoundException {
    return fromStream(new FileInputStream(file));
  }

  public static TypeTranslator fromStream(InputStream inputStream) {
    return fromFileLines(linesFrom(inputStream));
  }

  private final static Pattern SELECTOR_REGEX_PATTERN = Pattern.compile("^from ([0-9]+(\\.[0-9]+)+) down to ([0-9]+(\\.[0-9]+)+) interpret$", Pattern.CASE_INSENSITIVE);

  public static TypeTranslator fromFileLines(List<String> lines) {
    lines.removeIf(String::isEmpty);
    lines.removeIf(line -> line.startsWith("#"));
    MinecraftVersion fromVersion = null;
    MinecraftVersion toVersion = null;
    List<TypeTranslation> translations = new ArrayList<>();
    for (String line : lines) {
      boolean mapping = line.startsWith("  ");
      try {
        if (mapping) {
          if (fromVersion == null) {
            throw new IntaveResourceCompilationException("Mapping entered without selector");
          }
          String[] split = line.trim().split(" as ");
          String fromTypeName = split[0], toTypeName = split[1];
          Material fromType = searchMaterial(fromTypeName);
          Material toType = searchMaterial(toTypeName);
          if(fromType != null && toType != null) {
            translations.add(new TypeTranslation(fromVersion, toVersion, fromType, toType));
          }
        } else {
          // selector
          if(!SELECTOR_REGEX_PATTERN.matcher(line).matches()) {
            throw new IntaveResourceCompilationException("Invalid selector patter");
          }
          int fromVersionStartIndex = afterIndex(line, "from ");
          int fromVersionEndIndex = line.indexOf(" ", fromVersionStartIndex);
          fromVersion = new MinecraftVersion(line.substring(fromVersionStartIndex, fromVersionEndIndex));
          int toVersionStartIndex = afterIndex(line, "to ");
          int toVersionEndIndex = line.indexOf(" ", toVersionStartIndex);
          toVersion = new MinecraftVersion(line.substring(toVersionStartIndex, toVersionEndIndex));
        }
      } catch (IntaveResourceCompilationException exception) {
        throw new IntaveResourceCompilationException("Failed to compile line " + line + ": " + exception.getMessage());
      }
    }
    return new VERTRAFileTypeTranslator(ImmutableList.copyOf(translations));
  }

  private static Material searchMaterial(String name) {
    Material search = Material.matchMaterial(name);
    if (search == null) {
      search = Material.getMaterial(name);
    }
    if (search == null) {
      search = Material.matchMaterial("LEGACY_" + name);
    }
    if (search == null) {
      search = Material.getMaterial("LEGACY_" + name);
    }
    return search;
  }

  private static int afterIndex(String str, String needle) {
    return str.indexOf(needle) + needle.length();
  }

  private static List<String> linesFrom(InputStream inputStream) {
    Scanner scanner = new Scanner(inputStream);
    List<String> strings = new ArrayList<>();
    while (scanner.hasNextLine()) {
      strings.add(scanner.nextLine());
    }
    return strings;
  }

  public static class TypeTranslation {
    private final MinecraftVersion versionFrom, versionTo;
    private final Material typeFrom, typeTo;

    public TypeTranslation(MinecraftVersion versionFrom, MinecraftVersion versionTo, Material typeFrom, Material typeTo) {
      this.versionFrom = versionFrom;
      this.versionTo = versionTo;
      this.typeFrom = typeFrom;
      this.typeTo = typeTo;
    }

    public MinecraftVersion versionFrom() {
      return versionFrom;
    }

    public MinecraftVersion versionTo() {
      return versionTo;
    }

    public Material typeFrom() {
      return typeFrom;
    }

    public Material typeTo() {
      return typeTo;
    }
  }
}
