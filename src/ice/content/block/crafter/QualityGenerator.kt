package ice.content.block.crafter

import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.world.blocks.product.MediumCrafter

class QualityGenerator:MediumCrafter("quality_generator") {
  init {
    bundle {
      desc(zh_CN, "质量生成器", "将能量无序逆向转换的设备,将大量中子能向物质质量进行转换,将产出除夸克类介质外的纯净无序介质")
    }
    requirements(Category.crafting, ItemStack.with())
    size = 4

    placeablePlayer = false

    energyCapacity = 16384f
    mediumCapacity = 32f

    newConsume()
    consume!!.energy(32f)
    newProduce()
    produce!!.medium(0.6f)
  }

}