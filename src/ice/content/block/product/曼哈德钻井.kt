package ice.content.block.product

import ice.content.IItems
import mindustry.content.Liquids
import mindustry.type.Category
import singularity.world.blocks.drills.BaseDrill

class 曼哈德钻井 :BaseDrill("manhardDrill") {
  init {
    localization {
      zh_CN {
        localizedName = "曼哈德钻井"
        description = "高级钻井,不同于其他钻井,其完全舍弃了传统的钻探方案,选择应用曼哈德效应以实现较为高效的资源开采"
      }
    }
    bitHardness = 5
    size = 3
    drillTime = 250f
    squareSprite = false
    newConsume().apply {
      power(1f)
    }
    newBooster(4.7f).apply {
      liquid(Liquids.water, 12f / 60f)
    }
    requirements(Category.production, IItems.钴钢, 30, IItems.铪锭, 20, IItems.单晶硅, 25, IItems.电子元件, 10)

  }
}