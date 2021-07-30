package de.jpx3.intave.fakeplayer;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public abstract class FakePlayerIdentity {
  private final int identifier;
  private final WrappedGameProfile wrappedGameProfile;
  private final WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();

  protected FakePlayerIdentity(int identifier, WrappedGameProfile wrappedGameProfile) {
    this.identifier = identifier;
    this.wrappedGameProfile = wrappedGameProfile;
  }

  public int identifier() {
    return identifier;
  }

  public WrappedGameProfile gameprofile() {
    return wrappedGameProfile;
  }

  public WrappedDataWatcher datawatcher() {
    return wrappedDataWatcher;
  }
}
