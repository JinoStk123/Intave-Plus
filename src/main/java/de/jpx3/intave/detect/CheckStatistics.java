package de.jpx3.intave.detect;

public final class CheckStatistics {
  private long totalViolations;

  private long totalProcessed;
  private long totalPassed;
  private long totalFails;

  public void increasePasses() {
    totalPassed++;
  }

  public void increaseFails() {
    totalFails++;
  }

  public void increaseViolations() {
    totalViolations++;
  }

  public void increaseTotal() {
    totalProcessed++;
  }

  public long totalViolations() {
    return totalViolations;
  }

  public long totalProcessed() {
    return totalProcessed;
  }

  public long totalPasses() {
    return totalPassed;
  }

  public long totalFails() {
    return totalFails;
  }
}
