package ice.content.block.crafter

import ice.content.IItems

import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter

class 电弧炉 : NormalCrafter("arcFurnace") {
  init {
    localization {
      zh_CN {
        this.localizedName = "电弧炉"
        description = "将铅锭,石英和金珀沙熔炼为石英玻璃"
      }
    }
    size = 3
    itemCapacity = 36
    requirements(Category.crafting, IItems.高碳钢, 80, IItems.铅锭, 50, IItems.铜锭, 50, IItems.锌锭, 30)
    craftEffect = Fx.smeltsmoke
    drawers= DrawMulti(DrawDefault(), DrawFlame())
    newConsume().apply {
      time(4f * 60f)
      items(IItems.铅锭, 3, IItems.石英, 2, IItems.金珀沙, 2)
      power(2.75f)
    }
    newProduce().apply {
      items(IItems.石英玻璃, 4)
    }
  }
}