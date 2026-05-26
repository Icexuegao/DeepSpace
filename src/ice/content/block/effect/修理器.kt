package ice.content.block.effect

import mindustry.content.Items
import mindustry.type.Category
import mindustry.type.ItemStack
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
    requirements(Category.effect, ItemStack.with(Items.lead, 100, Items.titanium, 25, Items.silicon, 40, Items.copper, 50))
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