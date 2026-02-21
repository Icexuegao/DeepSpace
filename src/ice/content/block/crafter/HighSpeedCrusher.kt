package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class HighSpeedCrusher : NormalCrafter("highSpeedCrusher") {
  init {
    bundle {
      desc(zh_CN, "高速粉碎机")
    }
    size = 2
    itemCapacity = 24
    squareSprite = false
    craftEffect = Fx.pulverize
    updateEffect = Fx.pulverizeSmall
    ambientSound = Sounds.loopGrind
    ambientSoundVolume = 0.025f
    draw = DrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate").apply {
      spinSprite = true
      rotateSpeed = 15f
    }, DrawDefault())
    requirements(Category.crafting, IItems.高碳钢, 100, IItems.铜锭, 50, IItems.铅锭, 30, IItems.低碳钢, 20)

    newConsume().apply {
      time(10f)
      power(1f)
      items(IItems.黄玉髓, 2)
    }
    newProduce().apply {
      items(IItems.金珀沙, 4)
    }
  }
}