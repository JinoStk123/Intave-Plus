package de.jpx3.intave.block.shape.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class OverlappingGridMerger implements IndexMerger {
  private final double[] firstValues;
  private final double[] secondValues;
  private final boolean ignoreFirstStart, ignoreSecondStart;
  private final int firstSize, secondSize, totalSize;

  private final double[] result, firstIndices, secondIndices;
  private int resultLength;

  public OverlappingGridMerger(double[] firstValues, double[] secondValues, boolean ignoreFirstStart, boolean ignoreSecondStart) {
    this.firstValues = firstValues;
    this.secondValues = secondValues;
    this.ignoreFirstStart = ignoreFirstStart;
    this.ignoreSecondStart = ignoreSecondStart;
    this.firstSize = firstValues.length;
    this.secondSize = secondValues.length;
    this.totalSize = firstSize + secondSize;
    this.result = new double[totalSize];
    this.firstIndices = new double[totalSize];
    this.secondIndices = new double[totalSize];
  }

  private static final double EPSILON = 1e-7;

  @Override
  public IndexMerger compileMerge() {
    boolean skipFirst = !ignoreFirstStart;
    boolean skipSecond = !ignoreSecondStart;
    int resultIndex = 0;
    int firstIndex = 0;
    int secondIndex = 0;
    double lastValue = Double.NaN;
    while (true) {
      boolean firstListExhausted = firstIndex >= firstSize;
      boolean secondListExhausted = secondIndex >= secondSize;
      if (firstListExhausted && secondListExhausted) {
        this.resultLength = Math.max(1, resultIndex);
        return this;
      }
      boolean takeFromFirst = !firstListExhausted && (secondListExhausted || firstValues[firstIndex] < secondValues[secondIndex] + EPSILON);
      if (takeFromFirst) {
        firstIndex++;
        if (skipFirst && (secondIndex == 0 || secondListExhausted)) {
          continue;
        }
      } else {
        secondIndex++;
        if (skipSecond && (firstIndex == 0 || firstListExhausted)) {
          continue;
        }
      }
      int adjustedFirstIndex = firstIndex - 1;
      int adjustedSecondIndex = secondIndex - 1;
      double currentValue = takeFromFirst ? firstValues[adjustedFirstIndex] : secondValues[adjustedSecondIndex];
      if (!(currentValue - EPSILON <= lastValue)) {
        firstIndices[resultIndex] = adjustedFirstIndex;
        secondIndices[resultIndex] = adjustedSecondIndex;
        result[resultIndex++] = currentValue;
        lastValue = currentValue;
      } else {
        firstIndices[resultIndex - 1] = adjustedFirstIndex;
        secondIndices[resultIndex - 1] = adjustedSecondIndex;
      }
    }
  }

  @Override
  public boolean forMergedIndices(IndexConsumer indexConsumer) {
    int lastIndex = resultLength - 1;
    for (int i = 0; i < lastIndex; i++) {
      if (!indexConsumer.merge((int) firstIndices[i], (int) secondIndices[i], i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public DoubleList mergedIndexes() {
    if (resultLength < 1) {
      return DoubleArrayList.wrap(new double[0]);
    } else {
      return DoubleArrayList.wrap(result, resultLength);
    }
  }

  @Override
  public int size() {
    return resultLength;
  }
}
