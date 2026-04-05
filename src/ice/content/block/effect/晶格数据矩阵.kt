package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock

class 晶格数据矩阵 :StorageBlock("effect_latticeDataMatrix"){
  init{
    bundle {
      desc(zh_CN, "晶格数据矩阵", "通过灵能解构技术,将物品转为数据存储在灵能晶格之中")
    }
    size = 4
    health = 16000
    armor = 60f
    absorbLasers = true
    itemCapacity = 40000
    requirements(Category.effect, IItems.铱板, 1440, IItems.导能回路, 1200, IItems.陶钢, 1080, IItems.生物钢, 960, IItems.肃正协议, 1)
  }
}