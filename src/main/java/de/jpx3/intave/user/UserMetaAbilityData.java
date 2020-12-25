package de.jpx3.intave.user;

import org.bukkit.entity.Player;

public final class UserMetaAbilityData {
  private boolean flying;
  private boolean allowFlying;
  private float flySpeed = 0.05f;
  private float walkSpeed = 0.1f;

  public UserMetaAbilityData(Player player) {
    this.allowFlying = player.getAllowFlight();
    this.flying = player.isFlying();
  }

  public boolean flying() {
    return flying;
  }

  public boolean allowFlying() {
    return allowFlying;
  }

  public float walkSpeed() {
    return walkSpeed;
  }

  public float flySpeed() {
    return flySpeed;
  }

  public void flying(boolean flying) {
    this.flying = flying;
  }

  public void allowFlying(boolean allowFlying) {
    this.allowFlying = allowFlying;
  }

  public void walkSpeed(float walkSpeed) {
    this.walkSpeed = walkSpeed;
  }

  public void flySpeed(float flySpeed) {
    this.flySpeed = flySpeed;
  }
}