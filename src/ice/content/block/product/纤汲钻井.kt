package ice.content.block.product

import ice.content.IItems
import mindustry.content.Liquids
import mindustry.type.Category
import singularity.world.blocks.drills.BaseDrill

class 纤汲钻井 :BaseDrill("deriveDrill") {
  init {
    bitHardness = 3
    size = 2
    requirements(Category.production, IItems.高碳钢, 10, IItems.低碳钢, 5)
    drillTime = 400f
    localization {
      zh_CN {
        localizedName = "纤汲钻井"
        description = "基础钻井,配备了最基础的钻芯,可用于开采基础资源"
      }
    }
    newBooster(2.3f).apply {
      liquid(Liquids.water, 12f / 60f)
    }
  }
}