package ice.content.block.product

import ice.content.IItems
import mindustry.content.Liquids
import mindustry.type.Category
import singularity.world.blocks.drills.BaseDrill

class 蛮荒钻井 :BaseDrill("uncivilizedDrill") {
  init {
    localization {
      zh_CN {
        localizedName = "蛮荒钻井"
        description = "次级钻井,在纤汲钻井的基础上进行了迭代,钻芯材料改进,可用于开采更高级资源"
      }
    }
    newBooster(3.6f).apply {
      liquid(Liquids.water, 12f / 60f)
    }
    bitHardness = 4
    size = 3
    drillTime = 350f
    requirements(Category.production, IItems.铬锭, 25, IItems.低碳钢, 20, IItems.高碳钢, 30, IItems.黄铜锭, 10)
  }
}