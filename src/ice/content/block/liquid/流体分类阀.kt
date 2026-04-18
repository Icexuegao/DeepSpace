package ice.content.block.liquid

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.content.IItems
import ice.library.scene.ui.ItemSelection
import ice.ui.bundle.BaseBundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.type.Liquid
import mindustry.world.blocks.liquid.LiquidBlock
import singularity.world.blocks.liquid.LiquidUnloader
import singularity.world.blocks.product.NormalCrafter
import universecore.world.producers.ProduceType

class 流体分类阀 :LiquidUnloader("liquid_classifier") {

  init {
    desc(BaseBundle.zh_CN, "流体分类阀", "允许卸载临近工厂配方的可输出流体")
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