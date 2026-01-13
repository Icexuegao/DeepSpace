package singularity.world.components;

import universecore.components.blockcomp.ChainsBlockComp;

public interface StructBlockComp extends ChainsBlockComp {
  @Override
  default boolean chainable(ChainsBlockComp other){
    return other instanceof StructBlockComp;
  }
}
