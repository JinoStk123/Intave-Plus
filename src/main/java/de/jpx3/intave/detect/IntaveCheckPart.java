package de.jpx3.intave.detect;

public abstract class IntaveCheckPart implements EventProcessor {
  private final IntaveCheck parentCheck;

  public IntaveCheckPart(IntaveCheck parentCheck) {
    this.parentCheck = parentCheck;
  }

}