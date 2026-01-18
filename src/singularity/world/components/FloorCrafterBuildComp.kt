package singularity.world.components;

import arc.struct.ObjectIntMap;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import universecore.components.blockcomp.BuildCompBase;

public interface FloorCrafterBuildComp extends BuildCompBase {
  ObjectIntMap<Floor> count = new ObjectIntMap<>();

  static ObjectIntMap<Floor> getFloors(Tile tile, Block block){
    count.clear();
    tile.getLinkedTilesAs(block, t -> {
      Floor f;
      if ((f = t.floor()) != null) {
        count.increment(f, 0, 1);
      }
    });
    return count;
  }

  //@Annotations.BindField(value = "floorCount", initialize = "new arc.struct.ObjectIntMap<>()")
  default ObjectIntMap<Floor> floorCount(){
    return null;
  }

 // @Annotations.MethodEntry(entryMethod = "onProximityUpdate")
  default void updateFloors(){
    floorCount().clear();
    for (ObjectIntMap.Entry<Floor> floor : getFloors(getTile(), getBlock())) {
      floorCount().put(floor.key, floor.value);
    }
  }
}
