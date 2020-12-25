package de.jpx3.intave.tools;

public final class RotationMathHelper {
  public static double gcdExact(double a, double b) {
    double r;
    while ((r = a % b) > 0) {
      a = b;
      b = r;
    }
    return b;
  }

  public static double gcd(double a, double b) {
    double r;
    double min = 0;
    while ((r = a % b) > min) {
      a = b;
      b = r;
      min = Math.max(a, b) * 1e-3;
    }
    return b;
  }

  public static float resolveSensitivity(final float gcd) {
    return (float) ((Math.cbrt(gcd / 8.0f) - 0.2f) / 0.6f * 2f);
  }
}