package ice.content.block.crafter

import arc.Core
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.draw.DrawCultivator
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute
import singularity.world.blocks.product.FloorCrafter
import singularity.world.consumers.SglConsumeFloor
import singularity.world.draw.DrawBottom

class Incubator : FloorCrafter("incubator") {
  init {
    bundle {
      desc(zh_CN, "育菌箱", "从最原始的孢子培养技术发展而来的更高效的生物质培育设备")
    }
    requirements(Category.production, IItems.钴钢, 85, IItems.铬锭, 90, IItems.气凝胶, 80, IItems.铜锭, 90)
    size = 3
    liquidCapacity = 20f

    newConsume()
    consume!!.time(45f)
    consume!!.power(2.2f)
    consume!!.liquids(
      *LiquidStack.with(
        Liquids.water, 0.4f, ILiquids.孢子云, 0.1f
      )
    )
    newProduce()
    produce!!.item(Items.sporePod, 3)

    newConsume()
    consume!!.time(30f)
    consume!!.power(2.2f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.纯净水, 0.3f, ILiquids.孢子云, 0.1f
      )
    )
    newProduce()
    produce!!.item(Items.sporePod, 3)

    newBooster(1f)
    consume!!.add(
      SglConsumeFloor<FloorCrafterBuild>(
        true, true, arrayOf<Any>(
          Attribute.heat, 0.22f, Attribute.spores, 0.36f
        )
      )
    )

    draw = DrawMulti(
      DrawBottom(), object : DrawCultivator() {
        override fun load(block: Block) {
          middle = Core.atlas.find(block.name + "_middle")
        }
      }, DrawDefault(), DrawRegion("_top")
    )
  }
}