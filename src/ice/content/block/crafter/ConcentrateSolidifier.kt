package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter

class ConcentrateSolidifier : NormalCrafter("concentrateSolidifier") {
  init {
    bundle {
      desc(zh_CN, "萃取固化器")
    }
    size = 3
    health = 400
    itemCapacity = 20
    craftEffect = IceEffects.square(IItems.铪锭.color, length = 8f)
    draw = (DrawDefault())
    requirements(Category.crafting, IItems.高碳钢, 100, IItems.铬锭, 80, IItems.黄铜锭, 50, IItems.铜锭, 30)

    newConsume().apply {
      time(90f)
      items(IItems.锆英石, 3)
      power(230f / 60f)
    }
    newProduce().apply {
      items(IItems.铪锭, 1)
    }
  }
}