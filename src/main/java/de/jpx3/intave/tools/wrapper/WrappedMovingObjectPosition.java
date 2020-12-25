package de.jpx3.intave.tools.wrapper;

import org.bukkit.entity.Entity;

public class WrappedMovingObjectPosition {
  private WrappedBlockPosition blockPos;

  /** What type of ray trace hit was this? 0 = block, 1 = entity */
  public WrappedMovingObjectPosition.MovingObjectType typeOfHit;
  public WrappedEnumDirection sideHit;

  /** The vector position of the hit */
  public WrappedVector hitVec;

  /** The hit entity */
  public Entity entityHit;

  public WrappedMovingObjectPosition(WrappedVector hitVecIn, WrappedEnumDirection facing, WrappedBlockPosition blockPosIn) {
    this(WrappedMovingObjectPosition.MovingObjectType.BLOCK, hitVecIn, facing, blockPosIn);
  }

  public WrappedMovingObjectPosition(WrappedVector p_i45552_1_, WrappedEnumDirection facing) {
    this(WrappedMovingObjectPosition.MovingObjectType.BLOCK, p_i45552_1_, facing, WrappedBlockPosition.ORIGIN);
  }

  public WrappedMovingObjectPosition(Entity entity) {
    this(entity, new WrappedVector(
      entity.getLocation().getX(),
      entity.getLocation().getY(),
      entity.getLocation().getZ())
    );
  }

  public WrappedMovingObjectPosition(
    WrappedMovingObjectPosition.MovingObjectType typeOfHitIn,
    WrappedVector hitVecIn, WrappedEnumDirection sideHitIn, WrappedBlockPosition blockPosIn
  ) {
    this.typeOfHit = typeOfHitIn;
    this.blockPos = blockPosIn;
    this.sideHit = sideHitIn;
    this.hitVec = new WrappedVector(hitVecIn.xCoord, hitVecIn.yCoord, hitVecIn.zCoord);
  }

  public WrappedMovingObjectPosition(Entity entityHitIn, WrappedVector hitVecIn) {
    this.typeOfHit = WrappedMovingObjectPosition.MovingObjectType.ENTITY;
    this.entityHit = entityHitIn;
    this.hitVec = hitVecIn;
  }

  public WrappedBlockPosition getBlockPos() {
    return this.blockPos;
  }

  public String toString() {
    return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
  }

  public enum MovingObjectType {
    MISS,
    BLOCK,
    ENTITY
  }
}
