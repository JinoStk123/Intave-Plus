package de.jpx3.intave.block.collision;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.user.User;
import org.bukkit.Material;

import java.util.Arrays;

public abstract class CollisionModifier {
  private Material[] materials;

  protected CollisionModifier() {
  }

  protected CollisionModifier(Material... materials) {
    this.materials = materials;
  }

  public abstract BlockShape modify(User user, BoundingBox userBox, int posX, int posY, int posZ, BlockShape shape);

  public boolean matches(Material material) {
    return Arrays.asList(materials).contains(material);
  }
}
