package ice.world.blocks.distribution.digitalStorage

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.IFiles
import mindustry.Vars
import mindustry.content.Items
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.ItemSelection

class DigitalOutput(name: String) : Block(name) {
    val top = IFiles.findPng("${name}-top")
    val bottom = IFiles.findPng("${name}-bottom")
    val regions=IFiles.findPng(name)

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
        requirements(Category.distribution, ItemStack.with(Items.copper, 2))
        configClear { build: DigitalUnloaderBuild -> build.sortItem = null }
        config(Item::class.java) { build: DigitalUnloaderBuild, item: Item ->
            build.sortItem = item
        }
    }

    override fun icons(): Array<TextureRegion> {
        return arrayOf(IFiles.load("${name}-bottom"), region)
    }

    override fun outputsItems(): Boolean {
        return true
    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan?>?) {
        drawPlanConfigCenter(plan, plan.config, "unloader-center")
    }

    inner class DigitalUnloaderBuild : Building() {
        var sortItem: Item? = null

        override fun draw() {
            Draw.rect(bottom, x, y)
            if (sortItem != null) {
                Draw.color(sortItem!!.color)
                Draw.rect(top, x, y)
                Draw.color()
            }
            Draw.rect(regions, x, y)
        }

        override fun buildConfiguration(table: Table?) {
            ItemSelection.buildTable(this.block, table, Vars.content.items(), ::sortItem, ::configure, 5, 4)
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