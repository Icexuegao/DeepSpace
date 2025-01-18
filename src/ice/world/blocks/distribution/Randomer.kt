package ice.world.blocks.distribution

import arc.func.Prov
import mindustry.Vars
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.BuildVisibility


open class Randomer(name: String) : Block(name) {
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

    inner class ItemSourceBuild : Building(), HeatBlock {
        override fun getPowerProduction(): Float {
            return 1000f
        }

        override fun updateTile() {
            for (l in Vars.content.liquids()) {
                if (liquids[l] < liquidCapacity) {
                    liquids.add(l, 10000f)
                }
                dumpLiquid(l)
            }
            for (item in Vars.content.items()) {
                if (items[item] < itemCapacity) {
                    items.add(item, 10000)
                }
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