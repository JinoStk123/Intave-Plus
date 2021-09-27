package de.jpx3.intave.klass.locate;

import com.comphenix.protocol.utility.MinecraftVersion;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ClassLocations {
  private final Iterable<ClassLocation> classLocations;

  public ClassLocations(Iterable<ClassLocation> classLocations) {
    this.classLocations = classLocations;
  }

  public ClassLocations filterByKey(String key) {
    return forward(classLocation -> classLocation.key().equals(key));
  }

  public ClassLocations reduceToCurrentVersion() {
    int currentMinecraftVersion = currentMinecraftVersion();
    return forward(classLocation -> classLocation.versionMatcher().matches(currentMinecraftVersion));
  }

  private int currentMinecraftVersion() {
    return MinecraftVersion.getCurrentVersion().getMinor();
  }

  public Optional<ClassLocation> findAny() {
    return stream().findAny();
  }

  public ClassLocation findAnyOrNull() {
    return findAny().orElse(null);
  }

  public ClassLocation findAnyOrDefault(Supplier<ClassLocation> supplier) {
    return findAny().orElseGet(supplier);
  }

  private Stream<ClassLocation> stream() {
    return StreamSupport.stream(this.classLocations.spliterator(), false);
  }

  public ClassLocations forward(Predicate<ClassLocation> predicate) {
    return new ClassLocations(
      stream().filter(predicate).collect(Collectors.toList())
    );
  }
}
