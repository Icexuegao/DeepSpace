package ice.world.content.blocks.liquid

import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.scene.ui.ItemSelection
import ice.library.struct.texture.LazyTextureSingleDelegate
import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Liquid
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import singularity.world.blocks.SglBlock

open class LiquidClassifier(name: String) : SglBlock(name) {
  val top2: TextureRegion by LazyTextureSingleDelegate("${this.name}-top2")

  init {
    update = true
    saveConfig = true
    hasLiquids = true
    configurable = true
    underBullets = true
    group = BlockGroup.liquids
    outputsLiquid = true
    clearOnDoubleTap = true
    buildType = Prov(::LiquidClassifierBuild)
    drawers = DrawMulti(DrawRegion("-bottom"), DrawBuild<LiquidClassifierBuild> {
      if (sortLiquid != null) {
        LiquidBlock.drawTiledFrames(size, x, y, 0f, sortLiquid, 1f)
      }
    }, DrawRegion("-top"))
    config(Liquid::class.java) { tile: LiquidClassifierBuild, liquid: Liquid? ->
      tile.sortLiquid = liquid
    }
    configClear { tile: LiquidClassifierBuild -> tile.sortLiquid = null }
  }

  override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan>) {
    val config = plan.config
    if (config is Liquid) {
      Drawf.liquid(top2, plan.drawx(), plan.drawy(), 0.6f, config.color)
    }
  }

  override fun setStats() {
    super.setStats()
    stats.remove(Stat.liquidCapacity)
  }

  inner class LiquidClassifierBuild : SglBuilding() {

    var sortLiquid: Liquid? = null
    override fun config(): Liquid? {
      return sortLiquid
    }

    override fun acceptLiquid(source: Building, liquid: Liquid)=false

    override fun buildConfiguration(table: Table) {
      ItemSelection.buildTable(block, table, Vars.content.liquids(), ::sortLiquid, this::configure, true)
    }

    override fun updateTile() {
      sortLiquid ?: return
      proximity.forEach {
        if (it is MultipleLiquidBlock.MultipleBlockBuild) {
          proximity.forEach { building ->

            it.moveLiquid(building, sortLiquid)

            /*if (building.acceptLiquid(this, sortLiquid)) {
              val amount: Float = min(it.liquids.get(sortLiquid), building.block.liquidCapacity - building.liquids.get(sortLiquid))
              building.handleLiquid(this, sortLiquid, amount)
              it.liquids.remove(sortLiquid, amount)
            }*/
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
