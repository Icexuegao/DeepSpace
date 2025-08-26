package ice.library.baseContent.blocks.distribution.digitalStorage

import arc.Events
import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.EventType
import ice.library.baseContent.blocks.abstractBlocks.IceBlock
import ice.library.draw.drawer.DrawRegionColor
import ice.library.draw.drawer.IceDrawMulti
import ice.ui.ItemSelection
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.type.Item
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion

class LogisticsOutput(name: String) : IceBlock(name) {

    init {
        size = 1
        solid = true
        health = 50
        update = true
        hasPower = true
        unloadable = false
        saveConfig = true
        copyConfig = true
        destructible = true
        acceptsItems = true
        configurable = true
        conductivePower = true
        clearOnDoubleTap = true
        buildType = Prov(::DigitalUnloaderBuild)
        configClear { build: DigitalUnloaderBuild -> build.sortItem = null }
        config(Item::class.java) { build: DigitalUnloaderBuild, item: Item ->
            build.sortItem = item
        }
        drawers = IceDrawMulti(DrawRegion("-bottom"), DrawRegionColor<DigitalUnloaderBuild>("-top") {
            it.sortItem?.color
        }, DrawDefault())
    }

    override fun outputsItems(): Boolean {
        return true
    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan?>?) {
        drawPlanConfigCenter(plan, plan.config, "$name-center")
    }

    inner class DigitalUnloaderBuild : IceBuild() {
        var sortItem: Item? = null
        var original: LogisticsHub.DigitalStorageBuild? = null
        override fun updateProximity() {
            super.updateProximity()
            Events.fire(EventType.LogisticsHubFire())
        }

        override fun updateTile() {
            original?.let { ori ->
                proximity.forEach {
                    if (sortItem != null && ori.items.get(sortItem) > 0 && it.acceptItem(this, sortItem)) {
                        it.handleItem(this, sortItem)
                        ori.items.remove(sortItem, 1)
                    }
                }
            }

        }

        override fun buildConfiguration(table: Table) {
            ItemSelection.buildTable(block, table, Vars.content.items(), ::sortItem, ::configure, true)
        }

        override fun config(): Item? {
            return sortItem
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