package de.jpx3.intave.module;

import com.comphenix.protocol.ProtocolLibrary;

public final class ModuleSettings {
  private final BootSegment bootSegment;
  private final Requirement requirement;
  private final boolean linkSubscriptions;

  private ModuleSettings(
    BootSegment bootSegment,
    Requirement requirement,
    boolean linkSubscriptions
  ) {
    this.bootSegment = bootSegment;
    this.requirement = requirement;
    this.linkSubscriptions = linkSubscriptions;
  }

  public BootSegment bootSegment() {
    return bootSegment;
  }

  public boolean requirementsFulfilled() {
    return requirement.fulfilled();
  }

  public boolean shouldLinkSubscriptions() {
    return linkSubscriptions;
  }

  public static ModuleSettings of() {
    return builder().build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private BootSegment bootSegment;
    private Requirement requirement;
    private boolean linkSubscriptions = true;

    public Builder bootBeforeIntave() {
      return bootAt(BootSegment.STAGE_3);
    }

    public Builder bootUsually() {
      return bootAt(BootSegment.STAGE_8);
    }

    public Builder bootAfterIntave() {
      return bootAt(BootSegment.STAGE_10);
    }

    public Builder requiresProtocolLib() {
      return requires(Requirements.protocolLib().and(() -> ProtocolLibrary.getProtocolManager() != null));
    }

    public Builder requires(Requirement requirement) {
      this.requirement = requirement;
      return this;
    }

    public Builder andMustFulfill(Requirement requirement) {
      if (this.requirement == null) {
        this.requirement = Requirements.none();
      }
      this.requirement = Requirements.mergeAnd(this.requirement, requirement);
      return this;
    }

    public Builder orCanFulfill(Requirement requirement) {
      if (this.requirement == null) {
        throw new IllegalStateException("Can not have or operation on empty requirement");
      }
      this.requirement = Requirements.mergeOr(this.requirement, requirement);
      return this;
    }

    public Builder bootAt(BootSegment bootSegment) {
      this.bootSegment = bootSegment;
      return this;
    }

    public Builder doNotLinkSubscriptions() {
      this.linkSubscriptions = false;
      return this;
    }

    public ModuleSettings build() {
      if (bootSegment == null) {
        bootUsually();
      }
      if (requirement == null) {
        requirement = Requirements.none();
      }
      return new ModuleSettings(bootSegment, requirement, linkSubscriptions);
    }
  }
}
