package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.desc
import ice.ui.bundle.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.LightBlock

class 小型照明器:LightBlock("illuminatorSmall"){
  init {
    size = 1
    armor = 1f
    radius = 90f
    brightness = 1.6f
    consumePower(0.2f)
    requirements(Category.effect, IItems.铜锭, 10f, IItems.高碳钢, 10f)
    bundle {
      desc(zh_CN, "照明器", "为自身周围区域提供照明","高效的照明设备,功耗低照明范围广")
    }
  }
}