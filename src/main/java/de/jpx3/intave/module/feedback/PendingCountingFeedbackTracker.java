package de.jpx3.intave.module.feedback;

import java.util.concurrent.atomic.AtomicLong;

public final class PendingCountingFeedbackTracker implements FeedbackTracker {
  private final AtomicLong counter = new AtomicLong();

  @Override
  public void sent(FeedbackRequest<?> request) {
    counter.incrementAndGet();
  }

  @Override
  public void received(FeedbackRequest<?> request) {
    counter.decrementAndGet();
  }

  public long pending() {
    return counter.get();
  }
}
