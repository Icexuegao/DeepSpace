package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.storage.StorageBlock

class 压缩存储器 :StorageBlock("compressorMemory") {
  init {
    size = 2
    armor = 4f
    itemCapacity = 1200
    requirements(Category.effect, IItems.钴锭, 250, IItems.铱板, 150, IItems.钴钢, 100)
    bundle {
      desc(zh_CN, "压缩存储器", "坚固耐用,新型空间压缩技术使其具有更大的空间")
    }
  }
}