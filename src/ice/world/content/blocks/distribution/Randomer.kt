package ice.world.content.blocks.distribution

import arc.func.Prov
import mindustry.Vars
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.BuildVisibility
import singularity.world.blocks.nuclear.EnergySource

open class Randomer(name: String) : EnergySource(name) {
  init {
    size = 1
    sync = true
    solid = true
    update = true
    hasItems = false
    hasPower = true
    hasLiquids = false
    outputsPower = true
    consumesPower = false
    group = BlockGroup.power
    buildType = Prov(::ItemSourceBuild)
    buildVisibility = BuildVisibility.sandboxOnly
  }

  inner class ItemSourceBuild : EnergySourceBuild(), HeatBlock {
    override fun getPowerProduction(): Float {
      return 1000f
    }

    override fun updateTile() {
      super.updateTile()
      for (liquid in Vars.content.liquids()) {
        proximity.forEach {
          if (it.acceptLiquid(this, liquid)) {
            it.handleLiquid(this, liquid,it.block.liquidCapacity-it.liquids.get(liquid))
          }
        }
      }
      Vars.content.items().forEach(::offload)
    }

    override fun handleItem(source: Building?, item: Item?) {
    }

    override fun heat(): Float {
      return 10000f
    }

    override fun heatFrac(): Float {
      return 1f
    }
  }
}