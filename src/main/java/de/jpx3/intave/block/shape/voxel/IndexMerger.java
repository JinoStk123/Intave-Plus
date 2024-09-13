package de.jpx3.intave.block.shape.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public interface IndexMerger {
  IndexMerger compileMerge();

  boolean forMergedIndices(IndexConsumer indexConsumer);

  DoubleList mergedIndexes();

  int size();

  interface IndexConsumer {
    boolean merge(int firstIndex, int secondIndex, int index);
  }
}
