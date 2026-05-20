package ice.content.block.effect

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.FleshAndBloodCoreBlock
import mindustry.type.Category
import universecore.ui.bundle.localization

open class 血肉枢纽 :FleshAndBloodCoreBlock("fleshAndBloodhinge") {
  init {
    localization {
      zh_CN {
        localizedName = "血肉枢纽"
        description = "操控血肉生物的唯一途径..."
      }
      en {
        localizedName = "Flesh and Blood Hinge"
        description = "The only way to control flesh creatures..."
      }
    }
    health = -1
    size = 4
    itemCapacity = 6000
    squareSprite = false
    requirements(Category.effect, IItems.无名肉块, 2300, IItems.碎骨, 2000)

  }
}