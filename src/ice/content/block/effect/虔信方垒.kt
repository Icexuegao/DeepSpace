package ice.content.block.effect

import ice.content.IItems
import ice.content.IUnitTypes
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.CoreBlock
import mindustry.type.Category

class 虔信方垒 :CoreBlock("pietasCornerCore") {
  init {
    size = 3
    armor=20f
    health = 1000
    squareSprite = false
    unitType = IUnitTypes.加百列
    powerProduct = 200 / 60f
    isFirstTier = true
    itemCapacity = 4000
    unitCapModifier = 8
    alwaysUnlocked = true
    buildCostMultiplier = 2f
    requirements(Category.effect, IItems.高碳钢, 1000, IItems.低碳钢, 1200, IItems.锌锭, 400, IItems.铜锭, 200)
    bundle {
      desc(zh_CN, "虔信方垒")
    }
  }
}