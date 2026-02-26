package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class MineralCrusher : NormalCrafter("mineralCrusher") {
  init {
    BaseBundle.bundle {
      desc(zh_CN,"矿石粉碎机","将融合矿物质粉碎为小块,然后筛选分类")
    }
    health=240
    size=4
    squareSprite = false
    hasLiquids = true
    draw = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(), DrawRegion("-runner", 6f, true).apply {
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
      power(2f)
      time(30f)
    }
    newProduce().apply {
      items(IItems.铬铁矿,4, IItems.方铅矿,2, IItems.铝土矿,3).random()
    }

    newConsume().apply {
      items(IItems.黄玉髓, 1)
      power(2f)
      time(30f)
    }
    newProduce().apply {
      items(IItems.黄铜矿,3, IItems.方铅矿,2, IItems.闪锌矿,2).random()
    }

    requirements(Category.crafting, IItems.高碳钢, 50, IItems.黄铜锭, 20, IItems.铬锭, 60, IItems.单晶硅, 20)
  }
}