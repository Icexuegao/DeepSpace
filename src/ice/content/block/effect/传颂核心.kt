package ice.content.block.effect

import ice.content.IItems
import ice.content.IUnitTypes
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.CoreBlock
import mindustry.type.Category

class 传颂核心 :CoreBlock("eulogyCore") {
  init {
    bundle {
      desc(zh_CN, "传颂核心")
    }
    size = 4
    armor=30f
    health = 5000
    powerProduct = 600 / 60f
    unitType = IUnitTypes.路西法
     thrusterLength = 34/4f
    squareSprite = false
    itemCapacity = 10000
    unitCapModifier = 10
    buildCostMultiplier = 2f
    requirements(Category.effect, IItems.高碳钢, 3500, IItems.钴锭, 3000, IItems.铬锭, 2000, IItems.金锭, 1000)
  }
}