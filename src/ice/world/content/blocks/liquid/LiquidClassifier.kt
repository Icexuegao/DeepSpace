package ice.world.content.blocks.liquid

import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.graphics.TextureRegionDelegate
import ice.library.scene.ui.ItemSelection
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.graphics.Drawf
import mindustry.type.Liquid
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawRegion

open class LiquidClassifier(name: String) : IceBlock(name) {
    val top2: TextureRegion by TextureRegionDelegate("${this.name}-top2")

    init {
        update = true
        saveConfig = true
        hasLiquids = true
        configurable = true
        destructible = true
        underBullets = true
        outputsLiquid = true
        clearOnDoubleTap = true
        buildType = Prov(::LiquidClassifierBuild)
        drawers = DrawMulti(DrawRegion("-bottom"), DrawBuild<LiquidClassifierBuild> {
            if (sortLiquid != null) {
                LiquidBlock.drawTiledFrames(size, x, y, 0f, sortLiquid, 1f)
            }
        }, DrawRegion("-top"))
        config(Liquid::class.java) { tile: LiquidClassifierBuild, liquid: Liquid? -> tile.sortLiquid = liquid }
        configClear { tile: LiquidClassifierBuild -> tile.sortLiquid = null }
    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan>) {
        val config = plan.config
        if (config is Liquid) {
            Drawf.liquid(top2, plan.drawx(), plan.drawy(), 0.6f, config.color)
        }
    }

    inner class LiquidClassifierBuild : IceBuild() {
        var sortLiquid: Liquid? = null
        override fun config(): Liquid? {
            return sortLiquid
        }

        override fun buildConfiguration(table: Table) {
            ItemSelection.buildTable(block, table, Vars.content.liquids(), { sortLiquid },
                { value: Liquid? -> this.configure(value) }, true)
        }

        override fun updateTile() {
            proximity.select { it is MultipleLiquidBlock.MultipleBlockBuild }.forEach { mub ->
                val multipleBlockBuild = mub as MultipleLiquidBlock.MultipleBlockBuild
                proximity.select { it !is MultipleLiquidBlock.MultipleBlockBuild }.forEach {
                    if (sortLiquid != null) {
                        multipleBlockBuild.transferLiquid(it, 10f, sortLiquid)
                    }
                }
            }
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val id: Int = read.i()
            sortLiquid = if (id == -1) null else Vars.content.liquid(id)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i((sortLiquid?.id ?: -1).toInt())
        }
    }
}
