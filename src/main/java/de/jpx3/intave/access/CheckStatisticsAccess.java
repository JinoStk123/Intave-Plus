package de.jpx3.intave.access;

public interface CheckStatisticsAccess {
  long totalProcesses();
  long totalPasses();
  long totalViolations();
  long totalFails();
}
