package singularity.world.components;

import universecore.world.blocks.chains.ChainsContainer;

public interface StructCoreBuildComp extends StructBuildComp{
  default StructCoreComp getStructCore(){
    return getBlock(StructCoreComp.class);
  }

  @Override
  default void chainsAdded(ChainsContainer old){
  }
}
