package de.jpx3.intave.user;

public final class UserMetaPotionData {
  private int potionEffectSpeedAmplifier = 0;
  public int potionEffectSpeedDuration = 0;

  private int potionEffectSlownessAmplifier = 0;
  public int potionEffectSlownessDuration = 0;

  private int potionEffectJumpAmplifier = 0;
  public int potionEffectJumpDuration = 0;

  public int potionEffectSpeedAmplifier() {
    return potionEffectSpeedAmplifier;
  }

  public int potionEffectSlownessAmplifier() {
    return potionEffectSlownessAmplifier;
  }

  public int potionEffectJumpAmplifier() {
    return potionEffectJumpAmplifier;
  }

  public void potionEffectSpeedAmplifier(int potionEffectSpeedAmplifier) {
    this.potionEffectSpeedAmplifier = potionEffectSpeedAmplifier;
  }

  public void potionEffectSlownessAmplifier(int potionEffectSlownessAmplifier) {
    this.potionEffectSlownessAmplifier = potionEffectSlownessAmplifier;
  }

  public void potionEffectJumpAmplifier(int potionEffectJumpAmplifier) {
    this.potionEffectJumpAmplifier = potionEffectJumpAmplifier;
  }
}