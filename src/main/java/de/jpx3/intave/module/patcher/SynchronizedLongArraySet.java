package de.jpx3.intave.module.patcher;

import it.unimi.dsi.fastutil.longs.LongArraySet;

final class SynchronizedLongArraySet extends LongArraySet {
  @Override
  public synchronized boolean add(long l) {
    return super.add(l);
  }

  @Override
  public synchronized boolean add(Long aLong) {
    return super.add(aLong);
  }

  @Override
  public synchronized boolean remove(long l) {
    return super.remove(l);
  }
}
