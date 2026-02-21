package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.crafting.CeriumExtractor
import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.draw.*

class CeriumExtractorLarge : CeriumExtractor("ceriumExtractorLarge") {
  init {
    size = 4
    itemCapacity = 48
    liquidCapacity = 48f
    newConsume().apply {
      time(35f)
      items(IItems.铈硅石, 7)
      liquid(ILiquids.异溶质, 36f / 60f)
      power(13.85f)
    }
    newProduce().apply {
      items(IItems.铈锭, 3)
      liquid(ILiquids.废水, 30f / 60f)
    }
    requirements(
      Category.crafting, IItems.铬锭, 185, IItems.石英玻璃, 45, IItems.铱板, 120, IItems.导能回路, 80, IItems.铈锭, 55
    )
    draw = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(Liquids.water), DrawCultivator().apply {
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
    bundle {
      desc(
        zh_CN, "增压铈萃取器", "在特制的超高压密封反应釜内,通过液相沉淀的方式萃取铈\n相较初代密封性更强,具有更高的压力,能够更迅速的萃取铈"
      )
    }
  }
}