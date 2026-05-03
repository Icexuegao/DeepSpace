package ice.content.block.effect

import ice.content.IItems
import ice.content.IUnitTypes
import ice.ui.bundle.localization

import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.CoreBlock
import mindustry.type.Category

class 传颂核心 :CoreBlock("eulogyCore") {
  init {
    localization {
      zh_CN {
        localizedName = "传颂核心"
        description = "控制区块的基础.有更大的容量与中型装甲.一旦被摧毁,所在区块将重归于敌人控制"
      }
    }
    size = 4
    armor = 30f
    health = 5000
    powerProduct = 600 / 60f
    unitType = IUnitTypes.米迦勒
    thrusterLength = 34 / 4f
    squareSprite = false
    itemCapacity = 10000
    unitCapModifier = 10
    buildCostMultiplier = 2f
    requirements(Category.effect, IItems.高碳钢, 3500, IItems.钴锭, 3000, IItems.铬锭, 2000, IItems.金锭, 1000)
  }
}