package de.jpx3.intave.diagnostic.timings;

public class TimingData implements Cloneable {
  private volatile long totalDuration;
  private volatile long calls;

  public void addDuration(long durationToAdd) {
    setTotalDuration(totalDuration() + durationToAdd);
  }

  public void increaseCallCount() {
    setCalls(calls() + 1);
  }

  public long totalDuration() {
    return totalDuration;
  }

  private void setTotalDuration(long totalDuration) {
    this.totalDuration = totalDuration;
  }

  public long calls() {
    return calls;
  }

  private void setCalls(long calls) {
    this.calls = calls;
  }

  @Override
  public TimingData clone() {
    try {
      return (TimingData) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error(e);
    }
  }
}
