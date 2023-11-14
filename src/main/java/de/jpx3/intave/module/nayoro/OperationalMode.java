package de.jpx3.intave.module.nayoro;

public enum OperationalMode {
  DISABLE,
  LOCAL_STORAGE,
  CLOUD_TRANSMISSION,
  CLOUD_STORAGE;

  public boolean keepCopyOfSamples() {
    return (this == LOCAL_STORAGE);
  }
}
