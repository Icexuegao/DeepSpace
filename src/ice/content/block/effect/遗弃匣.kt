package ice.content.block.effect

import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.effect.LostBox
import mindustry.type.Category
import mindustry.world.meta.Env

class 遗弃匣:LostBox("lostBox"){
  init{
    bundle {
      desc(zh_CN, "遗弃匣")
    }
    size = 2
    envEnabled = Env.any
    category = Category.effect

  }
}