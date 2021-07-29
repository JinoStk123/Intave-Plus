package de.jpx3.intave.fakeplayer.movement;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public final class LocationUtils {
  public static double distanceBetweenLocations(Location location1, Location location2) {
    if (location1.getWorld() != location2.getWorld()) {
      return 0.0;
    }
    return location1.distance(location2);
  }

  public static Location locationBehind(Location location, double distance) {
    location = location.clone();
    Vector direction = CameraUtils.getDirection(location.getYaw(), 0.0f);
    location.add(direction.multiply(-distance));
    return location;
  }
}