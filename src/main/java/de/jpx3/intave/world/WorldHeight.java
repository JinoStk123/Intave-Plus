package de.jpx3.intave.world;

import de.jpx3.intave.adapter.MinecraftVersions;

public final class WorldHeight {
  public final static int UPPER_WORLD_LIMIT = MinecraftVersions.VER1_18_0.atOrAbove() ? 256 + 64 : 256;
  public final static int LOWER_WORLD_LIMIT = MinecraftVersions.VER1_18_0.atOrAbove() ?     - 64 : 0;
}
