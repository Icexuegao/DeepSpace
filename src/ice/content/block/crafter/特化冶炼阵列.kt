package ice.content.block.crafter

import ice.content.IItems
import ice.graphics.IceColor

import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter

class 特化冶炼阵列 :NormalCrafter("specializedSmelterArray") {
  init {
    localization {
      zh_CN {
        this.localizedName = "特化冶炼阵列"
        description = "冶炼原矿石出铬锭,金锭,钴锭和铝锭,可配置"
        details = "进阶级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铬,金,钴等多种金属原料,为后续生产提供稳定的金属供应"
      }
    }
    size = 3
    itemCapacity = 35
    craftEffect = IceEffects.square(IceColor.b4)
    drawers = DrawMulti(DrawDefault(), DrawFlame())
    requirements(Category.crafting, IItems.高碳钢, 70, IItems.铅锭, 40, IItems.铜锭, 30, IItems.锌锭, 30)

    newConsume().apply {
      time(240f)
      items(IItems.铬铁矿, 5)
      power(100f / 60f)
    }
    newProduce().apply {
      items(IItems.铬锭, 3, IItems.低碳钢, 1)
    }
    newConsume().apply {
      time(150f)
      items(IItems.硫钴矿, 5)
      power(90 / 60f)
    }
    newProduce().apply {
      items(IItems.钴锭, 2)
    }
    newConsume().apply {
      time(60f)
      item(IItems.金矿, 4)
      power(80 / 60f)
    }
    newProduce().apply {
      items(IItems.金锭, 1)
    }
    newConsume().apply {
      time(30f)
      items(IItems.铝土矿, 3, IItems.生煤, 1)
      power(80 / 60f)
    }
    newProduce().apply {
      items(IItems.铝锭, 1)
    }
  }
}
