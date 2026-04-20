package ice.content.block.liquid

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.content.IItems
import ice.library.scene.ui.ItemSelection
import ice.ui.bundle.localization

import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Liquid
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.modules.LiquidModule
import singularity.world.blocks.liquid.LiquidUnloader
import singularity.world.blocks.product.NormalCrafter
import universecore.world.producers.ProduceType
import kotlin.math.min

class 流体分类阀 :LiquidUnloader("liquid_classifier") {

  init {
    localization {
      zh_CN {
        name = "流体分类阀"
        description = "允许卸载临近工厂配方的可输出流体"
      }
    }
    size = 1
    buildType = Prov(::流体分类阀Build)
    health = 40
    requirements(Category.liquid, IItems.高碳钢, 5, IItems.石英玻璃, 5, IItems.单晶硅, 10, IItems.黄铜锭, 10)
  }

  inner class 流体分类阀Build :LiquidUnloadedBuild() {
    var cliquids = Seq<Liquid?>()
    override fun draw() {
      Draw.rect("$name-bottom", x, y)
      current?.let {
        LiquidBlock.drawTiledFrames(size, x, y, 0f, it, 1f)
      }
      Draw.rect(region, x, y)
    }

    override fun updateTile() {
      val next = getNext("liquidsPeek") {e: Building? -> e!!.block.hasLiquids && e.canUnload()}
      if (next == null) return

      if (next.liquids != null) {
        val dmp = LiquidModule.LiquidConsumer { l: Liquid?, a: Float ->
          var dump = getNext("liquids") { e: Building? ->
            val dest = e!!.getLiquidDestination(this, l)
            dest.acceptLiquid(this, l) && dest !== next && dest.liquids.get(l) / dest.block.liquidCapacity < a / next.block.liquidCapacity
          }
          if (dump == null) return@LiquidConsumer

          dump = dump.getLiquidDestination(this, l)
          var move =
            (a * dump.block.liquidCapacity - dump.liquids.get(l) * next.block.liquidCapacity) / (dump.block.liquidCapacity + next.block.liquidCapacity)
          move = min(min(move, dump.block.liquidCapacity - dump.liquids.get(l)), a)

          next.liquids.remove(l, move)
          dump.handleLiquid(this, l, move)
        }

        if (current != null) {
          dmp.accept(current, next.liquids.get(current))
        }
      }
    }
    override fun buildConfiguration(table: Table) {
      cliquids.clear()
      proximity.forEach {
        if (it is NormalCrafter.NormalCrafterBuild) {
          (it.block as NormalCrafter).producers.forEach { producer ->
            producer.get(ProduceType.liquid)?.liquids?.let { stacks ->
              for(stack in stacks) {
                cliquids.addUnique(stack.liquid)
              }
            }
          }
        }
      }
      ItemSelection.buildTable(
        this@流体分类阀, table, cliquids,
        { current },
        ::configure, true
      )
    }
  }
}