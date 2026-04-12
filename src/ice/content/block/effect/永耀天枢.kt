package ice.content.block.effect

import ice.content.IItems
import ice.content.IUnitTypes
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.CoreBlock
import mindustry.type.Category

 class 永耀天枢 :CoreBlock("foreverShineCore") {
  init {
    bundle {
      desc(zh_CN, "永耀天枢","控制区块的基础.有最大的容量与重型装甲.一旦被摧毁,所在区块将重归于敌人控制")
    }
    armor=25f
    size = 5
    health = 8000
    powerProduct = 1200 / 60f
    unitType = IUnitTypes.路西法
    squareSprite = false
    itemCapacity = 22000
    thrusterLength = 40/4f
    unitCapModifier = 15
    buildCostMultiplier = 3f
    requirements(
      Category.effect, IItems.强化合金, 3000, IItems.高碳钢, 7500, IItems.钴锭, 5000, IItems.铬锭, 4000, IItems.金锭, 2000
    )
  }
}