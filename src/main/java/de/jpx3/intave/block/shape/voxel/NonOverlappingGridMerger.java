package de.jpx3.intave.block.shape.voxel;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class NonOverlappingGridMerger extends AbstractDoubleList implements IndexMerger {
  private final double[] lower;
  private final double[] upper;
  private final boolean swap;

  public NonOverlappingGridMerger(double[] lower, double[] upper, boolean swap) {
    this.lower = lower;
    this.upper = upper;
    this.swap = swap;
  }

  @Override
  public IndexMerger compileMerge() {
    return this;
  }

  @Override
  public boolean forMergedIndices(IndexConsumer indexConsumer) {
    return swap ? forNormalMergedIndexes((firstIndex, secondIndex, index) -> indexConsumer.merge(secondIndex, firstIndex, index)) : forNormalMergedIndexes(indexConsumer);
  }

  public boolean forNormalMergedIndexes(IndexConsumer indexConsumer) {
    int lowerSize = lower.length;
    for (int lowerIndex = 0; lowerIndex < lowerSize; lowerIndex++) {
      if (!indexConsumer.merge(lowerIndex, -1, lowerIndex)) {
        return false;
      }
    }
    int upperSize = upper.length - 1;
    for (int upperIndex = 0; upperIndex < upperSize; upperIndex++) {
      if (!indexConsumer.merge(lowerSize - 1, upperIndex, lowerSize + upperIndex)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public DoubleList mergedIndexes() {
    return this;
  }

  @Override
  public int size() {
    return lower.length + upper.length;
  }

  @Override
  public double getDouble(int index) {
    return index < lower.length ? lower[index] : upper[index - lower.length];
  }
}
