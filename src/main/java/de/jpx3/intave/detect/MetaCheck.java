package de.jpx3.intave.detect;

import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

public abstract class MetaCheck<M extends UserCustomCheckMeta> extends Check {
  private final Class<? extends UserCustomCheckMeta> metaClass;

  public MetaCheck(String checkName, String configurationName, Class<M> metaClass) {
    super(checkName, configurationName);
    this.metaClass = metaClass;
  }

  protected M metaOf(Player player) {
    return metaOf(UserRepository.userOf(player));
  }

  public M metaOf(User user) {
    //noinspection unchecked
    return (M) user.customMeta(metaClass);
  }
}
