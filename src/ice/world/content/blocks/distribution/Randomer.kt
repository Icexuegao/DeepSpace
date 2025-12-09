package ice.world.content.blocks.distribution

import arc.func.Prov
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.Vars
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.BuildVisibility

open class Randomer(name: String) : IceBlock(name) {
    init {
        size = 1
        sync = true
        solid = true
        update = true
        hasItems = true
        hasPower = true
        hasLiquids = true
        outputsPower = true
        itemCapacity = 4000
        consumesPower = false
        liquidCapacity = 1000f
        group = BlockGroup.power
        buildType = Prov(::ItemSourceBuild)
        buildVisibility = BuildVisibility.sandboxOnly
    }

    inner class ItemSourceBuild : IceBuild(), HeatBlock {
        override fun getPowerProduction(): Float {
            return 1000f
        }

        override fun updateTile() {
            for (l in Vars.content.liquids()) {
                liquids.set(l, liquidCapacity)
                dumpLiquid(l)
            }
            for (item in Vars.content.items()) {
                items.set(item, itemCapacity)
                dump(item)
            }
        }

        override fun heat(): Float {
            return 10000f
        }

        override fun heatFrac(): Float {
            return 1f
        }
    }
}