package ice.world.content.blocks.distribution.droneNetwork

import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IItems
import ice.library.scene.ui.ItemSelection
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.draw.DrawMulti
import ice.world.draw.DrawRegionColor
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.draw.DrawRegion

class DroneReceivingRnd(name: String) : IceBlock(name) {

    init {
        size = 1
        health = 300
        update = true
        hasItems = true
        saveConfig = true
        itemCapacity = 200
        configurable = true
        clearOnDoubleTap=true
        buildType = Prov(::DroneReceivingRndBuild)
        requirements(Category.distribution, ItemStack.with(IItems.铬铁矿, 10))
        configClear { build: DroneReceivingRndBuild -> build.sortItem = null }
        config(Item::class.java) { build: DroneReceivingRndBuild, item: Item ->
            build.sortItem = item
        }
        drawers = DrawMulti(DrawRegion("-bottom"), DrawRegionColor<DroneReceivingRndBuild>("-center") {
            it.sortItem?.color
        }, DrawRegion("-top"))

    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan?>?) {
        drawPlanConfigCenter(plan, plan.config, "$name-center")
    }

    inner class DroneReceivingRndBuild : IceBuild() {
        var sortItem: Item? = null
        var building: DroneDeliveryTerminal.DroneDeliveryTerminalBuild? = null
        override fun acceptItem(source: Building, item: Item?): Boolean {
            return items.get(item) < getMaximumAccepted(item)
        }

        override fun config(): Item? {
            return sortItem
        }

        override fun updateTile() {
            dump()
        }

        override fun buildConfiguration(table: Table) {
            ItemSelection.buildTable(block, table, Vars.content.items(), ::sortItem, ::configure,true)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val i = read.i()
            sortItem = if (i == -1) null else Vars.content.item(i)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(if (sortItem == null) -1 else sortItem!!.id.toInt())
        }
    }
}