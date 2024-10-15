package de.jpx3.intave.check.movement.timer;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.annotate.DispatchTarget;
import de.jpx3.intave.check.MetaCheckPart;
import de.jpx3.intave.check.movement.Timer;
import de.jpx3.intave.math.Histogram;
import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.module.Modules;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.module.tracker.entity.Entity;
import de.jpx3.intave.module.violation.Violation;
import de.jpx3.intave.packet.reader.EntityUseReader;
import de.jpx3.intave.share.Position;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import de.jpx3.intave.user.meta.ConnectionMetadata;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.event.Cancellable;

import static com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction.ATTACK;
import static de.jpx3.intave.module.linker.packet.PacketId.Client.USE_ENTITY;
import static de.jpx3.intave.module.mitigate.AttackNerfStrategy.CANCEL;
import static de.jpx3.intave.module.mitigate.AttackNerfStrategy.SHORT_CANCEL;

public class MicroBlink extends MetaCheckPart<Timer, MicroBlink.MicroBlinkMeta> {
  public MicroBlink(Timer parentCheck) {
    super(parentCheck, MicroBlinkMeta.class);
  }

  @PacketSubscription(
    packetsIn = USE_ENTITY
  )
  public void receiveUseEntity(
    User user, EntityUseReader reader, Cancellable cancellable
  ) {
    if (reader.useAction() == ATTACK) {
      MicroBlinkMeta meta = metaOf(user);
      meta.lastAttack = System.currentTimeMillis();
    }
  }

  @DispatchTarget
  public void receiveMovement(PacketEvent event) {
    User user = userOf(event.getPlayer());
    MicroBlinkMeta meta = metaOf(user);
    MovementMetadata movement = user.meta().movement();
    double horizontalDistance = movement.motion().horizontalLength();

    Histogram timeHistogram = meta.timeHistogram;

    if (horizontalDistance > 0.125 && meta.lastHorizontalDistance > 0.125) {
      long timeDifference = System.currentTimeMillis() - meta.lastMovement;
      timeHistogram.add(timeDifference);
      double probability = timeHistogram.normalProbability(timeDifference);
      // restriction to 1.8 temporary
      if (timeDifference > 70 && probability < 0.000001 && !user.meta().protocol().combatUpdate()) {
//        System.out.println(timeDifference + "ms: " + probability + ", aligned: " + lagEntityAligned(user));
        if (lagEntityAligned(user)) {
          meta.violationLevel += 1.5;
//          System.out.println("VL: " + meta.violationLevel);
          if (meta.violationLevel > 5) {
            Violation violation = Violation.builderFor(Timer.class)
              .forPlayer(user.player())
              .withCustomThreshold("microblink")
              .withMessage("seems be micro-lagging entity-aligned")
              .withDetails(MathHelper.formatDouble(probability * 100, 6) + "% likelihood of " + timeDifference + "ms")
              .withVL(meta.violationLevel - 5)
              .build();
            Modules.violationProcessor().processViolation(violation);
            meta.mitigationLevel += 2;
            if (meta.violationLevel > 10) {
              meta.violationLevel = 10;
              if (meta.mitigationLevel > 8) {
                user.nerf(CANCEL, "TBX2");
              }
            }
            if (meta.mitigationLevel > 8) {
              user.nerf(SHORT_CANCEL, "TBX2");
            }
          }
        }
      }

      long pastAttack = System.currentTimeMillis() - meta.lastAttack;
      if (probability < 0.000001 && timeDifference > 150 && timeDifference < 400 && pastAttack < 1250 && movement.lastTeleport > 5) {
        if (++meta.violationLevel > 5) {
          Violation violation = Violation.builderFor(Timer.class)
            .forPlayer(user.player())
            .withCustomThreshold("microblink")
            .withMessage("seems to be micro-lagging in combat")
            .withDetails(MathHelper.formatDouble(probability * 100, 6) + "% likelihood of " + timeDifference + "ms")
            .withVL(meta.violationLevel - 5)
            .build();
          meta.mitigationLevel++;
          Modules.violationProcessor().processViolation(violation);
          if (meta.violationLevel > 10) {
            meta.violationLevel = 10;
          }

          if (meta.mitigationLevel > 15) {
            user.nerf(SHORT_CANCEL, "TBX1");
          }
        }
      } else {
        meta.violationLevel = Math.max(0, meta.violationLevel - 0.007);
      }
    }

    meta.lastMovement = System.currentTimeMillis();
    meta.lastHorizontalDistance = horizontalDistance;
  }

  private boolean lagEntityAligned(User user) {
    MovementMetadata movement = user.meta().movement();
    ConnectionMetadata connection = user.meta().connection();
    Position lastPosition = movement.lastPosition();
    Position position = movement.position();
    for (Entity tracedEntity : connection.tracedEntities()) {
      Position entityPosition = tracedEntity.position.toPosition();
      // 1. position must be closer than last position
      double distanceToCurrentPos = position.distance(entityPosition);
      double distanceToLastPos = lastPosition.distance(entityPosition);
      if (distanceToCurrentPos >= distanceToLastPos) {
        continue;
      }
//      System.out.println("Distance to last " + distanceToLastPos);
      // 2. last position must be > 3 blocks away from entity, but not farther than 4 blocks
      if (distanceToLastPos < 2.7 || distanceToLastPos > 3.8) {
        continue;
      }
      return true;
    }
    return false;
  }

  public static class MicroBlinkMeta extends CheckCustomMetadata {
    private long lastMovement = 0L;
    private double lastHorizontalDistance = 0.0;
    private final Histogram timeHistogram = new Histogram(0, 500, 10, 20 * 60 * 2);
    private double violationLevel = 0.0;
    private double mitigationLevel = 0.0;

    private long lastAttack = 0L;
  }
}
