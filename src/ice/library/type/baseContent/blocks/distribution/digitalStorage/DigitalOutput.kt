package ice.library.type.baseContent.blocks.distribution.digitalStorage

import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.type.baseContent.blocks.abstractBlocks.IceBlock
import ice.library.type.draw.DrawRegionColor
import ice.library.type.draw.IceDrawMulti
import ice.ui.ItemSelection
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.type.Item
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion

class DigitalOutput(name: String) : IceBlock(name) {

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
        override fun buildConfiguration(table: Table) {
            ItemSelection.buildTable(this.block, table, Vars.content.items(), ::sortItem, ::configure)
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