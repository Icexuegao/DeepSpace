package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.localization

import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock

class 晶格数据矩阵 :StorageBlock("effect_latticeDataMatrix"){
  init{
    localization {
      zh_CN {
        this.localizedName = "晶格数据矩阵"
        description = "巨量存储各种类型的物品,物品将被解构为数据进行存储.可以用装卸器卸载物品"
      }
    }
    size = 4
    health = 16000
    armor = 60f
    absorbLasers = true
    itemCapacity = 40000
    requirements(Category.effect, IItems.铱板, 1440, IItems.导能回路, 1200, IItems.陶钢, 1080, IItems.生物钢, 960, IItems.肃正协议, 1)
  }
}