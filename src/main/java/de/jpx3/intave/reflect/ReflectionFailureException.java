package de.jpx3.intave.reflect;

public final class ReflectionFailureException extends RuntimeException {
  private final StackTraceElement[] stackTraceElements;

  /**
   * Constructs a new runtime exception with {@code null} as its detail message.  The cause is not initialized, and may
   * subsequently be initialized by a call to {@link #initCause}.
   */
  public ReflectionFailureException(Throwable throwable) {
    super(throwable);
    this.stackTraceElements = throwable.getStackTrace();;
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return stackTraceElements;
  }
}