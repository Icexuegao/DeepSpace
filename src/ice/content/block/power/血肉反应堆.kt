package ice.content.block.power

import ice.content.IItems
import ice.content.ILiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.power.ImpactReactor
import mindustry.world.draw.*
import universecore.ui.bundle.localization
import universecore.util.toColor

class 血肉反应堆:ImpactReactor("bloodImpactReactor"){
  init {
    size = 5
    armor = 8f
    lightColor = "EBFFFE".toColor()
    itemDuration = 120f
    warmupSpeed = 0.0006f
    powerProduction = 800f
    itemCapacity = 20
    liquidCapacity = 400f
    canOverdrive = false
    consumeItems(IItems.生物钢, 1)
    consumePower(100f)
    consumeLiquids(ILiquids.急冻液, 1.5f)
    explosionShake = 8f
    explosionRadius = 40
    explosionDamage = 4800
    explodeSound = Sounds.explosion
    requirements(
      Category.power,
      IItems.导能回路,
      475,
      IItems.石英玻璃,
      325,
      IItems.生物钢,
      255,
      IItems.陶钢,
      375,
      IItems.铱板,
      645,
      IItems.铪锭,
      125,
      IItems.铬锭,
      815
    )
    drawer = DrawMulti(DrawRegion("-bottom"), DrawSoftParticles().apply {
      particles = 27
      particleLife = 120f
      particleSize = 9f
      particleRad = 12f
      color = "FFDCD8".toColor()
      color2 = "FF5845".toColor()
      alpha = 0.35f
    }, DrawRegion("-mid"), DrawPlasma().apply {
      plasma1 = "FF5845".toColor()
      plasma2 = "FFDCD8".toColor()
    }, DrawGlowRegion(), DrawDefault(), DrawGlowRegion("-glow"))
    ambientSound = Sounds.loopPulse
    ambientSoundVolume = 0.08f
    localization {
      zh_CN {
        this.localizedName = "血肉反应堆"
        description = "刺激生物钢产生大量电力,运行时会产生强烈波动"
      }
    }
  }
}