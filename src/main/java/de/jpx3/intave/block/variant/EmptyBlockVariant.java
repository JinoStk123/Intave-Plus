package de.jpx3.intave.block.variant;

final class EmptyBlockVariant implements BlockVariant {
  @Override
  public <T> T propertyOf(String name) {
    return null;
  }

  @Override
  public <T extends Enum<T>> T enumProperty(Class<T> klass, String name) {
    return null;
  }

  @Override
  public void dumpStates() {
  }
}
