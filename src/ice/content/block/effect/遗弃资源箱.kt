package ice.content.block.effect

import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.effect.ResBox

class 遗弃资源箱:ResBox("resBox"){
  init{
    bundle {
      desc(zh_CN, "遗弃资源箱","被废弃的存储箱,内部可能会存在少量资源")
    }
    squareSprite = false
  }
}