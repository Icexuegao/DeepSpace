package ice.content.block.liquid

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import singularity.world.blocks.liquid.LiquidUnloader

class 流体装卸器 :LiquidUnloader("liquid_unloader") {
  init {
    bundle {
      desc(zh_CN, "流体装卸器", "从建筑中抽取流体,就像装卸器提取物品一样")
    }
    requirements(Category.liquid, IItems.单晶硅, 20, IItems.铝锭, 15, IItems.铬锭, 15)
    size = 1
  }
}