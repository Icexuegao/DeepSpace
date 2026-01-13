package singularity.world.components;

import universecore.components.blockcomp.ChainsBuildComp;

public interface StructBuildComp extends ChainsBuildComp {
//  @Annotations.BindField("structCore")
  default StructCoreBuildComp core(){
    return null;
  }
  
 // @Annotations.BindField("structCore")
  default void core(StructCoreBuildComp core){}
}
