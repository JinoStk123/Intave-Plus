package de.jpx3.intave.module.patcher;

import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHashSet;

final class SynchronizedBukkitLongHashSet extends LongHashSet {
  @PatchyAutoTranslation
  public SynchronizedBukkitLongHashSet() {
    super();
  }

  @Override
  @PatchyAutoTranslation
  public synchronized boolean add(long l) {
    return super.add(l);
  }

  @Override
  @PatchyAutoTranslation
  public synchronized boolean contains(long l) {
    return super.contains(l);
  }

  @Override
  @PatchyAutoTranslation
  public synchronized void clear() {
    super.clear();
  }

  @Override
  @PatchyAutoTranslation
  public synchronized boolean remove(long l) {
    return super.remove(l);
  }
}
