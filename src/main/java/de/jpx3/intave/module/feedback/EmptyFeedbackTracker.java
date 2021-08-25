package de.jpx3.intave.module.feedback;

public final class EmptyFeedbackTracker implements FeedbackTracker {
  @Override
  public void sent(FeedbackRequest<?> request) {

  }

  @Override
  public void received(FeedbackRequest<?> request) {

  }
}
