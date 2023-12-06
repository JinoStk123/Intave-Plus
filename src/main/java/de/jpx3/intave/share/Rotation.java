package de.jpx3.intave.share;

import de.jpx3.intave.math.MathHelper;

import java.io.Serializable;

public final class Rotation implements Serializable {
  private final float yaw, pitch;

  public Rotation(float yaw, float pitch) {
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public float yaw() {
    return yaw;
  }

  public float pitch() {
    return pitch;
  }

  public float distanceTo(Rotation rotation) {
    float yawDistance = MathHelper.distanceInDegrees(yaw, rotation.yaw);
    float pitchDistance = MathHelper.distanceInDegrees(pitch, rotation.pitch);
    return yawDistance + pitchDistance;
  }

  @Override
  public String toString() {
    return "{" + yaw + ", " + pitch + "}";
  }

  private static final Rotation ZERO = new Rotation(0, 0);

  public static Rotation zero() {
    return ZERO;
  }

  public static Rotation of(float yaw, float pitch) {
    return new Rotation(yaw, pitch);
  }
}
