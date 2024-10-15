package de.jpx3.intave.block.shape;

import de.jpx3.intave.share.BoundingBox;
import de.jpx3.intave.share.Direction;
import de.jpx3.intave.share.Position;
import it.unimi.dsi.fastutil.doubles.DoubleSet;

import java.util.Collections;
import java.util.List;

final class EmptyBlockShape implements BlockShape {
  EmptyBlockShape() {}

  @Override
  public BlockShape contextualized(int posX, int posY, int posZ) {
    return this;
  }

  @Override
  public BlockShape normalized(int posX, int posY, int posZ) {
    return this;
  }

  @Override
  public void appendUnsortedCoordsTo(Direction.Axis axis, DoubleSet appendTo) {

  }

  @Override
  public BlockRaytrace raytrace(Position origin, Position target) {
    return BlockRaytrace.none();
  }

  @Override
  public BoundingBox outline() {
    return BoundingBox.empty();
  }

  @Override
  public List<BoundingBox> boundingBoxes() {
    return Collections.emptyList();
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean isCubic() {
    return false;
  }

  @Override
  public double allowedOffset(Direction.Axis axis, BoundingBox entity, double offset) {
    return offset;
  }

  @Override
  public double min(Direction.Axis axis) {
    return 0;
  }

  @Override
  public double max(Direction.Axis axis) {
    return 0;
  }

  @Override
  public boolean intersectsWith(BoundingBox boundingBox) {
    return false;
  }

  @Override
  public String toString() {
    return "Empty";
  }
}
