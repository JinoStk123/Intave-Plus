package de.jpx3.intave.block.physics;

import com.comphenix.protocol.utility.MinecraftVersion;
import de.jpx3.intave.block.type.MaterialSearch;
import de.jpx3.intave.share.Motion;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;

final class PowderSnowPhysics implements BlockPhysic {
  private Set<Material> materials;
  private boolean supported;

  @Override
  public void setupFor(MinecraftVersion serverVersion) {
    materials = MaterialSearch.materialsThatContain("POWDER_SNOW");
    supported = !materials.isEmpty();
  }

  @Override
  public Motion entityCollidedWithBlock(User user, Location location, Location from, double motionX, double motionY, double motionZ) {
    MovementMetadata movementData = user.meta().movement();
//    movementData.setMotionMultiplier(new Vector(0.9f, 1.5f, 0.9f));
//    System.out.println("PowderSnowPhysics.entityCollidedWithBlock");
    return new Motion(motionX * 0.9, motionY * 1.5, motionZ * 0.9);
  }

  @Override
  public boolean supportedOnServerVersion() {
    return supported;
  }

  @Override
  public Set<Material> applicableMaterials() {
    return materials;
  }
}
