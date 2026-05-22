package ice.content.block.crafter

import ice.content.IItems
import ice.graphics.IceColor
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter
import universecore.world.draw.DrawMulti

class 普适冶炼阵列 :NormalCrafter("universalSmelterArray") {
  init {
    localization {
      zh_CN {
        this.localizedName = "普适冶炼阵列"
        description = "冶炼原矿石出铜锭,锌锭和铅锭,可配置"
        details = "核心级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铜,锌,铅等多种金属原料,为后续生产提供稳定的金属供应"
      }
      en {
        this.localizedName = "Universal Smelter Array"
        description = "Smelts raw ores into copper ingots, zinc ingots and lead ingots, configurable"
        details = "Core-level metal processing facility, specialized in converting raw ores into high-purity metal ingots, efficiently processing copper, zinc, lead and other metal raw materials, providing stable metal supply for subsequent production"
      }
    }
    size = 3
    itemCapacity = 30
    craftEffect = IceEffects.square(IceColor.b4)
    newConsume().apply {
      time(60f)
      item(IItems.黄铜矿, 2)
      power(60 / 60f)
    }
    newProduce().apply {
      items(IItems.铜锭, 1, IItems.低碳钢, 1)
    }

    newConsume().apply {
      time(180f)
      item(IItems.方铅矿, 5)
      power(80 / 60f)
    }
    newProduce().apply {
      items(IItems.铅锭, 4)
    }

    newConsume().apply {
      time(120f)
      item(IItems.闪锌矿, 3)
      power(100 / 60f)
    }
    newProduce().apply {
      items(IItems.锌锭, 2)
    }

    drawers = DrawMulti(DrawDefault(), DrawFlame(IceColor.b4))
    requirements(Category.crafting, IItems.高碳钢, 50, IItems.低碳钢, 60)
  }
}