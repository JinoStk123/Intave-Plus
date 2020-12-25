package de.jpx3.intave.event.bukkit;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2020
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BukkitEventSubscription {
  EventPriority priority() default EventPriority.NORMAL;
  boolean ignoreCancelled() default false;
}
