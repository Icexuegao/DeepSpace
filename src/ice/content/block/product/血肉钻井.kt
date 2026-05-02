package ice.content.block.product

import ice.audio.ISounds
import ice.content.IItems
import universecore.util.toColor
import ice.ui.bundle.localization
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Liquids
import mindustry.entities.effect.ParticleEffect
import mindustry.type.Category
import mindustry.world.blocks.production.BurstDrill

class 血肉钻井 :BurstDrill("fleshBloodDrill") {
  init {
    localization {
      zh_CN {
        localizedName = "血肉钻井"
        description = "高级钻井,能够自主驱动钻探.需要持续供给血肉赘生物,可以安置在水上"
      }
    }
    size = 5
    tier = 11
    drillTime = 41.66f
    itemCapacity = 600
    liquidCapacity = 60f
    consumePower(10f)
    consumeLiquids(Liquids.water, 0.5f)
    drillSound = ISounds.激射
    placeableLiquid = true
    drillEffect = ParticleEffect().apply {
      particles = 6
      lifetime = 90f
      sizeFrom = 2f
      sizeTo = 3f
      length = 15f
      baseLength = 30f
      colorFrom = "D75B6E".toColor()
      colorTo = "D75B6E00".toColor()
      cone = 360f
    }
    requirements(Category.production, IItems.铱板, 450, IItems.导能回路, 225, IItems.钴锭, 32, IItems.生物钢, 75, IItems.肃正协议, 1)

  }
}