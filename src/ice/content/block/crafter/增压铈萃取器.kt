package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.crafting.CeriumExtractor
import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.draw.*

class 增压铈萃取器 : CeriumExtractor("ceriumExtractorLarge") {
  init {
    bundle {
      desc(
        zh_CN, "增压铈萃取器", "从铈硅石中萃取并锻压成铈锭,需要通入水,会产生废水.可配置","在特制的超高压密封反应釜内,通过液相沉淀的方式萃取铈\n相较初代密封性更强,具有更高的压力,能够更迅速的萃取铈"
      )
    }
    size = 4
    itemCapacity = 48
    liquidCapacity = 48f

    requirementPairs(
      Category.crafting, IItems.铬锭 to 185,

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
}