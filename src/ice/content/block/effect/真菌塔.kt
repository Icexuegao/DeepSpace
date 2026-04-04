package ice.content.block.effect

import ice.ui.bundle.bundle
import ice.world.content.blocks.effect.FungusCore
import mindustry.type.Category

class 真菌塔 :FungusCore("fungusTower") {
  init {
    bundle {
      desc(zh_CN, "真菌塔")
    }
    size = 2
    squareSprite = false
    category = Category.effect
  }
}