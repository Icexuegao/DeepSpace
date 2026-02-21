package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import singularity.world.blocks.product.NormalCrafter

class ArcFurnace : NormalCrafter("arcFurnace") {
  init {
    bundle {
      desc(zh_CN, "电弧炉")
    }
    size = 3
    itemCapacity = 36
    requirements(Category.crafting, IItems.高碳钢, 80, IItems.铅锭, 50, IItems.铜锭, 50, IItems.锌锭, 30)

    newConsume().apply {
      time(4f * 60f)
      items(IItems.铅锭, 3, IItems.石英, 2, IItems.金珀沙, 2)
      power(2.75f)
    }
    newProduce().apply {
      items(IItems.石英玻璃, 4)
    }
  }
}