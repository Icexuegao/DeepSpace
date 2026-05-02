package ice.content.block.product

import ice.content.IItems
import universecore.util.toColor
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.type.Category
import singularity.world.blocks.drills.BaseDrill

class 热熔钻井 :BaseDrill("hotMeltDrill") {
  init {
    localization {
      zh_CN {
        localizedName = "热熔钻井"
        description = "高级钻井,通过加热多种合金制成的钻头融毁地层以实现高效的资源开采"
      }
    }
    size = 5
    bitHardness = 6
    itemCapacity = 60
    liquidCapacity = 60f
    drillTime = 200f
    newConsume().apply {
      power(4f)
    }
    newBooster(5f).apply {
      liquid(Liquids.water, 0.3f)
    }
    drillEffect = Fx.mine
    updateEffect = mindustry.entities.effect.WaveEffect().apply {
      lifetime = 60f
      sizeFrom = 0f
      sizeTo = 40f
      colorFrom = "FFD37F".toColor()
      colorTo = "FFD37F00".toColor()
    }
    rotator.rotateSpeed = 6f
    warmupSpeed = 0.06f
    requirements(Category.production, IItems.铱板, 125, IItems.导能回路, 85, IItems.陶钢, 55, IItems.强化合金, 30)
  }
}