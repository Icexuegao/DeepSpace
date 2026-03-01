package ice.content.block.crafter

import arc.Core
import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.draw.DrawCultivator
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute
import singularity.world.blocks.product.FloorCrafter
import singularity.world.draw.DrawBottom
import universecore.world.consumers.cons.SglConsumeFloor

class Incubator : FloorCrafter("incubator") {
  init {
    bundle {
      desc(zh_CN, "沼气池", "厌氧消化产甲烷耦合活化能供器\n人话:厕所")
    }
    requirements(Category.production, IItems.钴钢, 85, IItems.铬锭, 90, IItems.气凝胶, 80, IItems.铜锭, 90)
    size = 3
    liquidCapacity = 20f

    newConsume()
    consume!!.time(45f)
    consume!!.power(2.2f)
    consume!!.liquids(
      *LiquidStack.with(
        Liquids.water, 0.4f, ILiquids.藻泥, 0.1f
      )
    )
    newProduce()
    produce!!.liquids(ILiquids.沼气, 3)

    newConsume()
    consume!!.time(30f)
    consume!!.power(2.2f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.纯净水, 0.3f, ILiquids.藻泥, 0.1f
      )
    )
    newProduce()
    produce!!.liquids(ILiquids.沼气, 3)

    newBooster(1f)
    consume!!.add(
      SglConsumeFloor<FloorCrafterBuild>(
        checkDeep = true, checkLiquid = true, attributes = arrayOf(
          Attribute.heat, 0.22f, Attribute.spores, 0.1f
        )
      )
    )

    draw = DrawMulti(
      DrawBottom(), DrawDefault(), object : DrawCultivator() {
        init {
          plantColor = Color.valueOf("bb2912")
          plantColorLight = plantColor.cpy().mul(1.1f)
        }

        override fun load(block: Block) {
          middle = Core.atlas.find(block.name + "_middle")
        }
      }, DrawRegion("_top")
    )
  }
}