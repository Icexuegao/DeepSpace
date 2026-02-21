package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.meta.IceEffects
import mindustry.type.Category
import singularity.world.blocks.product.NormalCrafter

class ExplosiveMixer : NormalCrafter("explosiveMixer") {
  init {
    bundle {
      desc(zh_CN, "爆炸物混合器", "将硫化合物,燃素水晶混合生成爆炸物")
    }
    size = 3
    itemCapacity = 36
    craftEffect = IceEffects.square(IItems.爆炸化合物.color)
    requirements(Category.crafting, IItems.高碳钢, 80, IItems.铬锭, 50, IItems.单晶硅, 30)

    newConsume().apply {
      time(45f)
      items(IItems.硫化合物, 3, IItems.燃素水晶, 1)
      power(2f)
    }
    newProduce().apply {
      items(IItems.爆炸化合物, 3)
    }
  }
}