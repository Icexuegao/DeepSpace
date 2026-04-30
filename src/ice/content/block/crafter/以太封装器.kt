package ice.content.block.crafter

import arc.func.Cons
import arc.math.Interp
import ice.content.IItems
import ice.content.ILiquids
import ice.entities.effect.MultiEffect
import ice.library.util.toColor

import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.entities.effect.WrapEffect
import mindustry.type.Category
import mindustry.world.draw.*
import singularity.world.blocks.product.NormalCrafter

class 以太封装器 :NormalCrafter("etherEncapsulator") {

  init {
    localization {
      zh_CN {
        this.localizedName = "以太封装器"
        description = "将游离的以太封装为便于运输的容器"
      }
    }
    size = 5
    itemCapacity = 120
    liquidCapacity = 300f
    craftTrigger = Cons {
      it!!.applyBoost(5f, 240f)
    }
    newFormula { consumers, producers ->
      consumers.apply {
        time(1440f)
        power(25.7f)
        items(IItems.铈锭, 12, IItems.导能回路, 8, IItems.铀238, 16)
        liquid(ILiquids.急冻液, 0.5f)
      }
      producers.apply {
        items(IItems.以太能, 24)
      }
    }
    craftEffect = MultiEffect(WrapEffect().apply {
      color = "E6C4EE".toColor()
      effect = Fx.dynamicSpikes
      rotation = 40f
    }, WrapEffect().apply {
      color = "E6C4EE".toColor()
      effect = Fx.mineImpactWave
      rotation = 60f
    })
    requirements(Category.crafting, IItems.铱板, 230, IItems.铈锭, 115, IItems.导能回路, 85, IItems.陶钢, 45)

    drawers = DrawMulti(DrawRegion("-bottom"), DrawCircles().apply {
      color = "FEB380".toColor()
      amount = 3
      sides = 16
      strokeMax = 2f
      timeScl = 240f
      radius = 11f
      strokeMin = 1f
      radiusOffset = 8f
      strokeInterp = Interp.pow3In
    }, DrawMultiWeave().apply {
      glowColor = "FEB380".toColor()
      fadeWeave = true
    }, DrawDefault(), DrawGlowRegion().apply {
      color = "FEB380".toColor()
      alpha = 0.8f
      glowIntensity = 1f
      glowScale = 18.84f
    }, DrawGlowRegion("-heat").apply {
      color = "F0511D".toColor()
      alpha = 0.8f
      glowIntensity = 1f
      glowScale = 4.71f
    })
  }
}
