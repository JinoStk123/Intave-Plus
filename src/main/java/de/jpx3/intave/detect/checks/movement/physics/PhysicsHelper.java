package de.jpx3.intave.detect.checks.movement.physics;

public final class PhysicsHelper {
  public static String resolveKeysFromInput(int forward, int strafe) {
    String key = "";
    if (forward == 1) {
      key += "W";
    } else if (forward == -1) {
      key += "S";
    }
    if (strafe == 1) {
      key += "A";
    } else if (strafe == -1) {
      key += "D";
    }
    return key;
  }
}