package de.jpx3.intave.event.service.transaction;

public final class TransactionCallBackData<T> {
  private final TransactionFeedbackCallback<T> transactionFeedbackCallback;
  private final T obj;
  private final long time;
  private final long num;

  public TransactionCallBackData(TransactionFeedbackCallback<T> transactionFeedbackCallback, T obj, long num) {
    this.transactionFeedbackCallback = transactionFeedbackCallback;
    this.obj = obj;
    this.num = num;
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

  public long num() {
    return num;
  }

  public long requested() {
    return time;
  }
}