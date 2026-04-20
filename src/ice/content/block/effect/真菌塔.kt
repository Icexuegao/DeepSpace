package ice.content.block.effect


import ice.ui.bundle.localization
import ice.world.content.blocks.effect.FungusCore
import mindustry.type.Category

class 真菌塔 :FungusCore("fungusTower") {
  init {
    localization {
      zh_CN {
        name = "真菌塔"
        description = "真菌占据此区域的标志,一旦被摧毁,此区块所有真菌将失去营养供应而死"
        details = "失控的生物武器已经成为了本土生物的噩梦..."
      }
    }
    size = 2
    squareSprite = false
    category = Category.effect
  }
}