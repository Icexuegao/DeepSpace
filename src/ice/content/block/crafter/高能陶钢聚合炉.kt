package ice.content.block.crafter

import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IItems

import universecore.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.RadialEffect
import mindustry.type.Category
import mindustry.world.draw.*
import singularity.world.blocks.product.NormalCrafter

class 高能陶钢聚合炉 : NormalCrafter("highEnergyCeramicSteelFurnace") {
  init {
    localization {
      zh_CN {
        localizedName = "高能陶钢聚合炉"
        description = "将石英玻璃,钴钢和铈锭高效地熔炼为陶钢,需通入水"
        details = "依靠高能激光持续熔融原料以快速熔炼陶钢\n相比普通熔炼炉,熔炼效率及产物质量都有显著提升"
      }
    }
    health = 2000
    size = 5
    dumpTime = 2
    itemCapacity = 120
    liquidCapacity = 300f
    newConsume().apply {
      power(17f)
      time(4*60f)
      items(IItems.钴钢, 12, IItems.铈锭, 12, IItems.石英玻璃, 12)
      liquid(Liquids.water, 30f / 60f)
    }
    newProduce().apply {
      item(IItems.陶钢, 20)
    }
    canOverdrive = false
    updateEffect = Fx.redgeneratespark
    craftEffect = MultiEffect(RadialEffect().apply {
      effect = Fx.surgeCruciSmoke
      rotationSpacing = 90f
      rotationOffset = 30f
      lengthOffset = 17f
      amount = 4
    }, RadialEffect().apply {
      effect = Fx.surgeCruciSmoke
      rotationSpacing = 90f
      rotationOffset = 60f
      lengthOffset = 17f
      amount = 4
    })
    requirements(Category.crafting, IItems.钴钢, 230, IItems.铱板, 115, IItems.导能回路, 85, IItems.陶钢, 45)
    drawers = DrawMulti(DrawRegion("-bottom"), DrawCircles().apply {
      color = Color.valueOf("FEB380")
      amount = 3
      sides = 16
      strokeMax = 2f
      strokeMin = 1f
      timeScl = 240f
      radius = 11f
      radiusOffset = 8f
      strokeInterp = Interp.pow3In
    }, DrawMultiWeave().apply {
      glowColor = Color.valueOf("FF6666CD")
      fadeWeave = true
    }, DrawDefault(), DrawGlowRegion().apply {
      color = Color.valueOf("FF664D")
      alpha = 0.8f
      glowIntensity = 1f
      glowScale = 18.84f
    }, DrawGlowRegion("-heat").apply {
      color = Color.valueOf("F0511D")
      alpha = 0.8f
      glowIntensity = 1f
      glowScale = 4.71f
    })
    ambientSound = ISounds.beamLoop
    ambientSoundVolume = 0.03f
    }
}
