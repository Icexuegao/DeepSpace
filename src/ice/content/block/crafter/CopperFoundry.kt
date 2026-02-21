package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter

class CopperFoundry : NormalCrafter("copperFoundry") {
  init {
    bundle {
      desc(zh_CN, "铸铜厂")
    }
    size = 4
    health = 200
    draw = DrawMulti(DrawDefault(), DrawFlame())
    craftEffect = IceEffects.square(IItems.铜锭.color)
    requirements(Category.crafting, ItemStack.with(IItems.铜锭, 200, IItems.低碳钢, 150))

    newConsume().apply {
      time(90f)
      items(IItems.铜锭, 3, IItems.锌锭, 1)
      power(60 / 60f)
    }
    newProduce().apply {
      items(IItems.黄铜锭, 3)
    }
  }
}