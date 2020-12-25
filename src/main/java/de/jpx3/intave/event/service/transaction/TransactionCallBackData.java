package de.jpx3.intave.event.service.transaction;

public final class TransactionCallBackData<T> {
  private final TransactionFeedbackCallback<T> transactionFeedbackCallback;
  private final T obj;
  private final long time;

  public TransactionCallBackData(TransactionFeedbackCallback<T> transactionFeedbackCallback, T obj) {
    this.transactionFeedbackCallback = transactionFeedbackCallback;
    this.obj = obj;
    this.time = System.currentTimeMillis();
  }

  public TransactionFeedbackCallback<T> transactionFeedbackCallback() {
    return transactionFeedbackCallback;
  }

  public T obj() {
    return obj;
  }

  public long passedTime() {
    return System.currentTimeMillis() - this.time;
  }
}