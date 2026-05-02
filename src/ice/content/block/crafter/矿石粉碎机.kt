package ice.content.block.crafter

import ice.content.IItems

import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class 矿石粉碎机 :NormalCrafter("mineralCrusher") {
  init {
    localization {
      zh_CN {
        this.localizedName = "矿石粉碎机"
        description = "将复杂矿石质粉碎并筛选分类为更易处理的矿石,可配置"
      }
    }
    health = 810
    itemCapacity = 30
    size = 4
    squareSprite = false
    hasLiquids = true
    drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(), DrawRegion("-runner", 6f, true).apply {
      x = 8.3f
      y = 8.3f
    }, DrawRegion("-runner", 6f, true).apply {
      x = -8.3f
      y = -8.3f
    }, DrawRegion("-runner", 6f, true).apply {
      x = 8.3f
      y = -8.3f
    }, DrawRegion("-runner", 6f, true).apply {
      x = -8.3f
      y = 8.3f
    })
    newConsume().apply {
      items(IItems.黑晶石, 1)
      power(1.5f)
      time(30f)
    }
    newProduce().apply {
      items(IItems.铬铁矿, 4, IItems.方铅矿, 2, IItems.铝土矿, 3).random()
    }

    newConsume().apply {
      items(IItems.黄玉髓, 1)
      power(1.5f)
      time(30f)
    }
    newProduce().apply {
      items(IItems.黄铜矿, 3, IItems.方铅矿, 2, IItems.闪锌矿, 2).random()
    }

    newFormula { consumers, producers ->
      consumers.apply {
        items(IItems.铱锇矿, 2)
        time(60f)
        power(1.5f)
      }
      producers.apply {
        items(IItems.铱金混合物, 1)
      }
    }

    requirements(Category.crafting, IItems.高碳钢, 50, IItems.黄铜锭, 20, IItems.铬锭, 60, IItems.单晶硅, 20)
  }
}
