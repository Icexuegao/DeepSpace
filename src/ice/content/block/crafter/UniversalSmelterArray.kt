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

class UniversalSmelterArray : NormalCrafter("universalSmelterArray") {
  init {
    bundle {
      desc(
        zh_CN, "普适冶炼阵列", "核心级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铜,锌,铅等多种金属原料,为后续生产提供稳定的金属供应"
      )
    }
    size = 3
    itemCapacity = 30
    craftEffect = IceEffects.square(IceColor.b4)
    newConsume().apply {
      time(80f)
      item(IItems.黄铜矿, 3)
      power(60 / 60f)
    }
    newProduce().apply {
      items(IItems.铜锭, 1, IItems.低碳钢, 1)
    }

    newConsume().apply {
      time(180f)
      item(IItems.方铅矿, 5)
      power(90 / 60f)
    }
    newProduce().apply {
      items(IItems.铅锭, 4)
    }

    newConsume().apply {
      time(120f)
      item(IItems.闪锌矿, 3)
      power(180 / 60f)
    }
    newProduce().apply {
      items(IItems.锌锭, 2)
    }

    draw = DrawMulti(DrawDefault(), DrawFlame(IceColor.b4))
    requirements(Category.crafting, IItems.高碳钢, 100, IItems.低碳钢, 70)
  }
}