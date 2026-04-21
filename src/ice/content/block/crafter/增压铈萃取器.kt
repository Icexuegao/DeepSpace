package ice.content.block.crafter

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus

import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.entities.Damage
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.world.draw.*
import singularity.world.blocks.product.NormalCrafter
import kotlin.math.min

open class 增压铈萃取器 : NormalCrafter("ceriumExtractorLarge") {
  init {
    localization {
      zh_CN {
        this.localizedName = "增压铈萃取器"
        description = "从铈硅石中萃取并锻压成铈锭,需要通入水,会产生废水.可配置"
        details = "在特制的超高压密封反应釜内,通过液相沉淀的方式萃取铈\n相较初代密封性更强,具有更高的压力,能够更迅速的萃取铈"
      }
    }
    size = 4
    itemCapacity = 48
    liquidCapacity = 48f

    requirementPairs(
      Category.crafting, IItems.铬锭 to 150,

      IItems.石英玻璃 to 45,

      IItems.铱板 to 120,

      IItems.导能回路 to 80,

      IItems.钴锭 to 55
    )
    drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(Liquids.water), DrawCultivator().apply {
      plantColor = Color.valueOf("A24FAA")
      plantColorLight = Color.valueOf("F9A3C7")
      bottomColor = Color.valueOf("474747")
      bubbles = 12
      sides = 16
      strokeMin = 0f
      spread = 4f
      timeScl = 90f
      recurrence = 6f
      radius = 5f
    }, DrawDefault(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 6.28f
      color = Color.valueOf("F0511D")
    }, DrawRegion("-top"), DrawParticles().apply {
      color = Color.valueOf("F9A3C7")
      alpha = 0.6f
      particles = 15
      particleLife = 300f
      particleRad = 16f
      particleSize = 3f
      fadeMargin = 0f
      rotateScl = 360f
      reverse = true
    })


    newConsume().apply {
      time(80f)
      items(IItems.铈硅石, 5)
      liquid(Liquids.water, 15f / 60f)
      power(7.6f)
    }
    newProduce().apply {
      items(IItems.铈锭, 2)
      liquid(ILiquids.废水, 0.2f)
    }

    newConsume().apply {
      time(35f)
      items(IItems.铈硅石, 7)
      liquid(Liquids.water, 36f / 60f)
      power(13.85f)
    }
    newProduce().apply {
      items(IItems.铈锭, 3)
      liquid(ILiquids.废水, 30f / 60f)
    }
  }
  init {
    buildType = Prov(::CeriumExtractorBuild)
  }

  inner class CeriumExtractorBuild : NormalCrafterBuild() {
    var size = 0f
    fun range(): Float {
      return block.size * 8 * 1.5f
    }

    override fun draw() {
      super.draw()
      Draw.z(Layer.shields)
      Draw.color(Color.valueOf("F9A3C7"))
      Draw.alpha(0.4f)
      Fill.poly(this.x, this.y, 16, this.range() * this.warmup * size)
      Draw.reset()
    }

    override fun updateTile() {
      super.updateTile()
      size = (if (timeScale > 1) min((timeScale - 1f) / 2f + 1f, 2.5f) else timeScale) + Mathf.absin(
        3.14f * 3f, 0.1f
      ) * this.efficiency
      Damage.status(null, this.x, this.y, range() * this.warmup * size, IStatus.辐射, 300f, true, true)
    }
  }
}
