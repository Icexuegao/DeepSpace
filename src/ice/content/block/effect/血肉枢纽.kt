package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.FleshAndBloodCoreBlock
import mindustry.type.Category

open class 血肉枢纽 :FleshAndBloodCoreBlock("fleshAndBloodhinge") {
  init {
    bundle {
      desc(zh_CN, "血肉枢纽","操控血肉生物的唯一途径...")
    }
    health = -1
    size = 4
    itemCapacity = 6000
    squareSprite = false
    requirements(Category.effect, IItems.无名肉块, 2300, IItems.碎骨, 2000)

  }
}