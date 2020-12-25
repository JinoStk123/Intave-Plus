package de.jpx3.intave.tools.client;

import de.jpx3.intave.tools.wrapper.WrappedMathHelper;
import de.jpx3.intave.tools.wrapper.WrappedVector;
import org.bukkit.util.Vector;

public final class PlayerRotationHelper {
  public static Vector vectorForRotation(float pitch, float yaw) {
    float f = pitch * ((float)Math.PI / 180F);
    float f1 = -yaw * ((float)Math.PI / 180F);
    float f2 = WrappedMathHelper.cos(f1);
    float f3 = WrappedMathHelper.sin(f1);
    float f4 = WrappedMathHelper.cos(f);
    float f5 = WrappedMathHelper.sin(f);
    return new Vector(f3 * f4, -f5, (double)(f2 * f4));
  }

  public static WrappedVector wrappedVectorForRotation(float pitch, float yaw) {
    float f = pitch * ((float)Math.PI / 180F);
    float f1 = -yaw * ((float)Math.PI / 180F);
    float f2 = WrappedMathHelper.cos(f1);
    float f3 = WrappedMathHelper.sin(f1);
    float f4 = WrappedMathHelper.cos(f);
    float f5 = WrappedMathHelper.sin(f);
    return new WrappedVector(f3 * f4, -f5, f2 * f4);
  }
}