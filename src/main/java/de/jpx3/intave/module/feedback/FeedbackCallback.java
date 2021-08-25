package de.jpx3.intave.module.feedback;

import org.bukkit.entity.Player;

public interface FeedbackCallback<T> {
  void success(Player player, T target);
}