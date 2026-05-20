package ice.content.block.effect

import ice.world.content.blocks.effect.ResBox

class 遗弃资源箱 :ResBox("resBox") {
  init {
    localization {
      zh_CN {
        localizedName = "遗弃资源箱"
        description = "被废弃的存储箱,内部可能会存在少量资源"
      }
      en {
        localizedName = "Abandoned Resource Box"
        description = "An abandoned storage box. A small amount of resources may still remain inside."
      }
    }
    squareSprite = false
  }
}