package ice.content.block.crafter

import ice.content.IItems

import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter

class 萃取固化器 :NormalCrafter("concentrateSolidifier") {
  init {
    localization {
      zh_CN {
        this.localizedName = "萃取固化器"
        description = "从锆英石中萃取并熔炼为铪锭"
      }
      en {
        this.localizedName = "Extraction Solidifier"
        description = "Extracts and smelts hafnium ingot from zircon"
      }
    }
    size = 3
    health = 400
    itemCapacity = 20
    craftEffect = IceEffects.square(IItems.铪锭.color, length = 8f)
    drawers = (DrawDefault())
    requirements(Category.crafting, IItems.高碳钢, 100, IItems.铱板, 80, IItems.黄铜锭, 50, IItems.钴钢, 30, IItems.单晶硅,30)

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
