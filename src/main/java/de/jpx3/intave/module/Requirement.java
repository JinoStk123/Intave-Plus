package de.jpx3.intave.module;

public interface Requirement {
  boolean fulfilled();

  default Requirement and(Requirement requirement) {
    return Requirements.mergeAnd(this, requirement);
  }

  default Requirement or(Requirement requirement) {
    return Requirements.mergeOr(this, requirement);
  }
}
