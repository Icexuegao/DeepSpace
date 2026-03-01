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

class CeriumExtractor : CeriumExtractor("ceriumExtractor") {
  init {
    bundle {
      desc(zh_CN, "铈提取器", "在特制的高压反应釜内,通过液相沉淀的方式从钍中提取铈")
    }
    size = 3
    itemCapacity = 36
    liquidCapacity = 36f
    draw = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(Liquids.water), DrawCultivator().apply {
      plantColor = Color.valueOf("A24FAA")
      plantColorLight = Color.valueOf("F9A3C7")
      bottomColor = Color.valueOf("474747")
      bubbles = 12
      sides = 16
      strokeMin = 0f
      spread = 3f
      timeScl = 90f
      recurrence = 6f
      radius = 3f
    }, DrawDefault(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 6.28f
      color = Color.valueOf("F0511D")
    }, DrawRegion("-top"), DrawParticles().apply {
      color = Color.valueOf("F9A3C7")
      alpha = 0.6f
      particles = 15
      particleLife = 300f
      particleRad = 12f
      particleSize = 2f
      fadeMargin = 0f
      rotateScl = 360f
      reverse = true
    })
    requirements(
      Category.crafting, IItems.铱板, 55, IItems.高碳钢, 230, IItems.石英玻璃, 30, IItems.铬锭, 80, IItems.单晶硅, 80
    )

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
  }
}