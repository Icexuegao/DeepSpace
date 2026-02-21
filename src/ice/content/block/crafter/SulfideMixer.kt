package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.meta.IceEffects
import mindustry.type.Category
import singularity.world.blocks.product.NormalCrafter

class SulfideMixer : NormalCrafter("sulfideMixer") {
  init {
    bundle {
      desc(zh_CN, "硫化物混合器", "将煤,铅,沙混合生成硫化合物")
    }
    size = 3
    itemCapacity = 36
    craftEffect = IceEffects.square(IItems.硫化合物.color)
    requirements(Category.crafting, IItems.高碳钢, 150, IItems.铜锭, 30, IItems.铬锭, 30)

    newConsume().apply {
      time(45f)
      items(IItems.生煤, 4, IItems.铅锭, 6, IItems.金珀沙, 6)
      power(1f)
    }
    newProduce().apply {
      items(IItems.硫化合物, 3)
    }
  }
}