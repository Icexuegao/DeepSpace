package singularity.world.blocks.liquid

import arc.func.Boolf
import arc.graphics.g2d.Draw
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.util.Eachable
import arc.util.Nullable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.blocks.ItemSelection
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.modules.LiquidModule.LiquidConsumer
import universecore.components.blockcomp.Takeable
import kotlin.math.min

/**液体提取器，可从周围的方块中抽取液体并送向下一个方块
 * 类似物品装卸器
 * @see mindustry.world.blocks.storage.Unloader*/
open class LiquidUnloader(name: String) : Block(name) {

  init {
    update = true
    solid = true
    unloadable = false
    hasLiquids = true
    liquidCapacity = 10f
    configurable = true
    outputsLiquid = true
    saveConfig = true
    displayFlow = false
    group = BlockGroup.liquids
    config(Liquid::class.java) { tile: LiquidUnloadedBuild?, l: Liquid? -> tile!!.current = l }
    configClear { tile: LiquidUnloadedBuild? -> tile!!.current = null }

  }

  override fun setBars() {
    super.setBars()
    removeBar("liquid")
  }

  override fun setStats() {
    super.setStats()
    stats.remove(Stat.liquidCapacity)
  }

  override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>?) {
    drawPlanConfigCenter(req, req.config, "center", true)
  }

  inner class LiquidUnloadedBuild : Building(), Takeable {
    @Nullable
    var current: Liquid? = null

    override fun updateTile() {
      val next = getNext("liquidsPeek") { e: Building? -> e!!.block.hasLiquids && e.canUnload() }
      if (next == null) return

      if (next.liquids != null) {
        val dmp = LiquidConsumer { l: Liquid?, a: Float ->
          var dump = getNext("liquids") { e: Building? ->
            val dest = e!!.getLiquidDestination(this, l)
            dest.acceptLiquid(this, l) && dest !== next && dest.liquids.get(l) / dest.block.liquidCapacity < a / next.block.liquidCapacity
          }
          if (dump == null) return@LiquidConsumer

          dump = dump.getLiquidDestination(this, l)
          var move = (a * dump.block.liquidCapacity - dump.liquids.get(l) * next.block.liquidCapacity) / (dump.block.liquidCapacity + next.block.liquidCapacity)
          move = min(min(move, dump.block.liquidCapacity - dump.liquids.get(l)), a)

          next.liquids.remove(l, move)
          dump.handleLiquid(this, l, move)
        }

        if (current != null) {
          dmp.accept(current, next.liquids.get(current))
        } else {
          next.liquids.each(dmp)
        }
      }
    }

    override fun draw() {
      Draw.rect(region, x, y)

      if (current != null) {
        Draw.color(current!!.color)
        Draw.rect(name + "_top", x, y)
        Draw.color()
      }
    }

    override fun buildConfiguration(table: Table) {
      ItemSelection.buildTable(table, Vars.content.liquids(), { current }, { value: Liquid? -> this.configure(value) })
    }

    override fun onConfigureBuildTapped(other: Building?): Boolean {
      if (this === other) {
        deselect()
        configure(null)
        return false
      }
      return true
    }

    override fun config(): Liquid? {
      return current
    }

    override fun acceptLiquid(source: Building?, liquid: Liquid?): Boolean {
      return false
    }

    override fun acceptItem(source: Building?, item: Item?): Boolean {
      return false
    }

    override fun write(write: Writes) {
      super.write(write)
      write.i((if (current == null) -1 else current!!.id).toInt())
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      val id = read.i()
      current = if (id == -1) null else Vars.content.liquid(id)
    }

    override var heaps = ObjectMap<String, Takeable.Heaps<*>>()
  }
}