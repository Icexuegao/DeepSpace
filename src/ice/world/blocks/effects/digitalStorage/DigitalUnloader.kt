package ice.world.blocks.effects.digitalStorage

import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.content.Items
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.ItemSelection

class DigitalUnloader(name: String) : Block(name) {

    init {
        health = 50
        size = 1
        destructible = true
        unloadable = false
        hasPower = true
        hasItems = true
        acceptsItems = true
        requirements(Category.effect, ItemStack.with(Items.copper, 2))
        buildType = Prov(::DigitalUnloaderBuild)
        update = true
        configurable = true
        conductivePower = true
        saveConfig = true
        copyConfig = true
        clearOnDoubleTap = true
        itemCapacity = 1
        config(
            Item::class.java
        ) { tile: DigitalUnloaderBuild, item: Item ->
            tile.sortItem = item
        }
        configClear { tile: DigitalUnloaderBuild -> tile.sortItem = null }
    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan?>?) {
        drawPlanConfigCenter(plan, plan.config, "unloader-center")
    }

    class DigitalUnloaderBuild : Building() {
        var sortItem: Item? = null
        override fun acceptItem(source: Building?, item: Item?): Boolean {
            return item == sortItem && items.get(item) < getMaximumAccepted(item)
        }

        override fun update() {
            super.update()
            if (sortItem != null) dump(sortItem)
        }

        override fun buildConfiguration(table: Table?) {
            ItemSelection.buildTable(this.block,
                table,
                Vars.content.items(),
                { sortItem },
                { value: Item? -> configure(value) },
                5,
                4
            )
        }

        override fun config(): Item? {
            return sortItem
        }

        override fun write(write: Writes) {
            super.write(write)
            write.s((if (sortItem == null) -1 else sortItem!!.id).toInt())
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val id = (if (revision.toInt() == 1) read.s() else read.b()).toInt()
            sortItem = if (id == -1) null else Vars.content.item(id)
        }
    }
}