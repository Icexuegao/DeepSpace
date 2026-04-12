package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.desc
import ice.ui.bundle.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.power.LightBlock

class 大型照明器:LightBlock("illuminatorLarge"){
  init{
    size = 2
    armor = 4f
    radius = 270f
    brightness = 1.6f
    squareSprite = false
    consumePower(0.5f)
    requirements(Category.effect, IItems.铜锭, 30f, IItems.高碳钢, 20f, IItems.黄铜锭, 10f)
    bundle {
      desc(zh_CN, "大型照明器", "为自身周围大片区域提供照明","神说要有光,于是便有了光")
    }
  }
}