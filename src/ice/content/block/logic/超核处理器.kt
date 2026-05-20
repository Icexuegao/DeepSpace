package ice.content.block.logic

import ice.content.IItems
import ice.content.ILiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.LogicBlock
import universecore.ui.bundle.localization

class 超核处理器 :LogicBlock("hyper-processor") {
  init {
    localization {
      zh_CN {
        localizedName = "超核处理器"
        description = "循环运行一系列逻辑指令,可用于控制单位和建筑物,比逻辑处理器更快"
      }
      en {
        localizedName = "Hyper Processor"
        description =
          "Runs a series of logic instructions in a loop. Can be used to control units and buildings. Faster than the logic processor."
      }
    }
    requirements(
      Category.logic,
      IItems.强化合金,
      150,
      IItems.暮光合金,
      150,
      IItems.电子元件,
      100,
      IItems.气凝胶,
      150,
      IItems.铱锭,
      50,
      IItems.铪锭,
      120
    )
    consumeLiquid(ILiquids.急冻液, 5f / 60f)
    hasLiquids = true
    health = 900
    instructionsPerTick = 75
    range = (8 * 90).toFloat()
    size = 3
  }
}
