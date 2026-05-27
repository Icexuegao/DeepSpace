package ice.content.block.effect

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Items
import mindustry.type.Category
import mindustry.world.blocks.defense.MendProjector
import universecore.ui.bundle.localization

class 修理器 :MendProjector("mend-projector") {
  init {
    localization {
      zh_CN {
        localizedName = "修理器"
        description = "定期修复附近的建筑"
      }
    }
    requirements(Category.effect, IItems.铅锭, 100, IItems.高碳钢, 25, IItems.单晶硅, 40, IItems.铜锭, 50)
    consumePower(1.5f)
    size = 2
    reload = 250f
    range = 85f
    healPercent = 11f
    phaseBoost = 15f
    scaledHealth = 80f
    consumeItem(Items.phaseFabric).boost()
  }
}