package ice.content.block.crafter

import ice.content.IItems
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter

class SpecializedSmelterArray : NormalCrafter("specializedSmelterArray") {
  init {
    bundle {
      desc(
        zh_CN, "特化冶炼阵列", "进阶级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铬,金,钴等多种金属原料,为后续生产提供稳定的金属供应"
      )
    }
    size = 3
    itemCapacity = 35
    craftEffect = IceEffects.square(IceColor.b4)
    draw = DrawMulti(DrawDefault(), DrawFlame())
    requirements(Category.crafting, IItems.高碳钢, 150, IItems.铅锭, 40, IItems.铜锭, 30, IItems.锌锭, 30)

    newConsume().apply {
      time(240f)
      item(IItems.铬铁矿, 5)
      power(180f / 60f)
    }
    newProduce().apply {
      items(IItems.铬锭, 3, IItems.低碳钢, 1)
    }
    newConsume().apply {
      time(150f)
      item(IItems.硫钴矿, 3)
      power(90 / 60f)
    }
    newProduce().apply {
      items(IItems.钴锭, 1)
    }
    newConsume().apply {
      time(60f)
      item(IItems.金矿, 5)
      power(80 / 60f)
    }
    newProduce().apply {
      items(IItems.金锭, 1)
    }
  }
}