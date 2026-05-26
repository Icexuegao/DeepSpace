package ice.content.block.effect

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Items
import mindustry.type.Category
import mindustry.world.blocks.defense.MendProjector
import universecore.ui.bundle.localization

class 小型修理器 :MendProjector("mender") {
  init {
    localization {
      zh_CN {
        localizedName = "小型修理器"
        description = "定期修复附近的建筑"
      }
    }
    requirements(Category.effect, IItems.铜锭, 30, IItems.低碳钢, 25)
    consumePower(0.3f)
    size = 1
    reload = 200f
    range = 40f
    healPercent = 4f
    phaseBoost = 4f
    phaseRangeBoost = 20f
    health = 80
    consumeItem(Items.silicon).boost()
  }
}