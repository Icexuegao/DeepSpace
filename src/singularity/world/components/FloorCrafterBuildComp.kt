package singularity.world.components

import arc.struct.ObjectIntMap
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import universecore.components.blockcomp.BuildCompBase

interface FloorCrafterBuildComp : BuildCompBase {

  companion object {
    fun getFloors(tile: Tile, block: Block): ObjectIntMap<Floor> {
      count.clear()
      tile.getLinkedTilesAs(block) { t: Tile? ->
        val f: Floor?
        if ((t!!.floor().also { f = it }) != null) {
          count.increment(f, 0, 1)
        }
      }
      return count
    }

    val count = ObjectIntMap<Floor>()
  }

  //@Annotations.BindField(value = "floorCount", initialize = "new arc.struct.ObjectIntMap<>()")
  var floorCount: ObjectIntMap<Floor>

  // @Annotations.MethodEntry(entryMethod = "onProximityUpdate")
  fun updateFloors() {
    floorCount.clear()
    for (floor in getFloors(tile!!, block)) {
      floorCount.put(floor.key, floor.value)
    }
  }
}