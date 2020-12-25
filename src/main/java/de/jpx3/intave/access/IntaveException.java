package de.jpx3.intave.access;

public final class IntaveException extends RuntimeException {
  public IntaveException() {
    super();
  }

  public IntaveException(String message) {
    super(message);
  }

  public IntaveException(String message, Throwable cause) {
    super(message, cause);
  }

  public IntaveException(Throwable cause) {
    super(cause);
  }

  protected IntaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
