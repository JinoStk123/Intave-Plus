package de.jpx3.intave.block.variant.index;

import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

class AquaticIndexer implements Indexer {
  @Override
  @PatchyAutoTranslation
  public Map<Object, Integer> index(Material type) {
    org.bukkit.craftbukkit.v1_13_R2.block.data.CraftBlockData blockData = org.bukkit.craftbukkit.v1_13_R2.block.data.CraftBlockData.newData(type, null);
    net.minecraft.server.v1_13_R2.Block block = blockData.getState().getBlock();
    Map<Object, Integer> index = new HashMap<>();
    int id = 0;
    for (net.minecraft.server.v1_13_R2.IBlockData nativeState : block.getStates().a()) {
      index.put(nativeState, id);
      id++;
    }
    return index;
  }
}
