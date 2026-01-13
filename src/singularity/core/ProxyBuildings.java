package singularity.core;

import arc.struct.ObjectMap;
import mindustry.gen.Building;
import mindustry.world.Block;

public class ProxyBuildings{
  protected ObjectMap<Block, Building> oldBuildType = new ObjectMap<>();
  
  public void setBuildType(Block block, Building buildType){
    if(block.buildType.get() != buildType){
      oldBuildType.put(block, block.buildType.get());
      block.buildType = () -> buildType;
    }
  }
}
