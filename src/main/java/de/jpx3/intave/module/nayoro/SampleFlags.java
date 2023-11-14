package de.jpx3.intave.module.nayoro;

public final class SampleFlags {
  public static final int MARKED_LEGIT = 0x0001;
  public static final int MARKED_CHEAT = 0b0010;
  public static final int MARKED_UNKNOWN = 0b0100;

  public static final int EVENT_ZERO_BYTE_APPEND = 0b1000;

  public static boolean isMarkedLegit(int flags) {
    return (flags & MARKED_LEGIT) != 0 && (flags & MARKED_CHEAT) == 0 && (flags & MARKED_UNKNOWN) == 0;
  }

  public static boolean isMarkedCheat(int flags) {
    return (flags & MARKED_CHEAT) != 0 && (flags & MARKED_LEGIT) == 0 && (flags & MARKED_UNKNOWN) == 0;
  }

  public static boolean isMarkedUnknown(int flags) {
    return (flags & MARKED_UNKNOWN) != 0 && (flags & MARKED_LEGIT) == 0 && (flags & MARKED_CHEAT) == 0;
  }

  public static boolean isEventZeroByteAppend(int flags) {
    return (flags & EVENT_ZERO_BYTE_APPEND) != 0;
  }
}

