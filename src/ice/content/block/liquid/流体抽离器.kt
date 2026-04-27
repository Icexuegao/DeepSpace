package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.LiquidClassifier
import mindustry.type.Category

class 流体抽离器:LiquidClassifier("liquidClassifier")  {
  init {
    localization {
      zh_CN {
        localizedName = "流体抽离器"
        description = "从流体枢纽中抽取流体"
      }
    }
    size = 1
    requirements(Category.liquid, IItems.铜锭, 20, IItems.黄铜锭, 10, IItems.铬锭, 10, IItems.石英玻璃, 10)
  }
}