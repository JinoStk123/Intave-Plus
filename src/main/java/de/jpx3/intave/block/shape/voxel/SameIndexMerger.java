package de.jpx3.intave.block.shape.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class SameIndexMerger implements IndexMerger {
  private final double[] coords;

  public SameIndexMerger(double[] coords) {
    this.coords = coords;
  }

  @Override
  public IndexMerger compileMerge() {
    return this;
  }

  @Override
  public boolean forMergedIndices(IndexConsumer indexConsumer) {
    int i = this.coords.length - 1;
    for (int index = 0; index < i; index++) {
      if (!indexConsumer.merge(index, index, index)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public DoubleList mergedIndexes() {
    return DoubleList.of(this.coords);
  }

  @Override
  public int size() {
    return this.coords.length;
  }
}
