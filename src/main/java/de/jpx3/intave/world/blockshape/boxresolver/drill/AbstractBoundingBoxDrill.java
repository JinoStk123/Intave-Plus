package de.jpx3.intave.world.blockshape.boxresolver.drill;

import de.jpx3.intave.world.blockshape.boxresolver.ResolverPipeline;
import de.jpx3.intave.world.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.world.wrapper.link.WrapperLinkage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractBoundingBoxDrill implements ResolverPipeline {
  protected List<WrappedAxisAlignedBB> translate(List<?> bbs) {
    if (bbs.isEmpty()) {
      return Collections.emptyList();
    }
    List<WrappedAxisAlignedBB> list = new ArrayList<>();
    for (Object bb : bbs) {
      list.add(WrapperLinkage.boundingBoxOf(bb));
    }
    return list;
  }

  protected List<WrappedAxisAlignedBB> translateWithOffset(List<?> bbs, int posX, int posY, int posZ) {
    if (bbs.isEmpty()) {
      return Collections.emptyList();
    }
    List<WrappedAxisAlignedBB> list = new ArrayList<>();
    for (Object bb : bbs) {
      list.add(WrappedAxisAlignedBB.fromNative(bb).offset(posX, posY, posZ));
    }
    return list;
  }
}
