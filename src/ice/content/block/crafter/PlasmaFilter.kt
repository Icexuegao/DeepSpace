package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import universecore.util.toColor

import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.world.draw.DrawCultivator
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawParticles
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class PlasmaFilter : NormalCrafter("plasmaFilter") {
  init {
    localization {
      zh_CN {
        this.localizedName = "血浆过滤器"
        description = "冷却血肉赘生物并从中提取生物钢"
      }
    }
    size = 5
    hasLiquids = true
    squareSprite = false
    itemCapacity = 60
    liquidCapacity = 60f
    newFormula {consumers, producers ->
      consumers.apply {
        time(600f)
        power(28.6f)
        liquids(ILiquids.血肉赘生物, 7.5f, ILiquids.急冻液, 0.5f)
      }
      producers.items(IItems.生物钢, 1)
    }

    requirements(Category.crafting, IItems.钴锭, 60, IItems.铱板, 155, IItems.石英玻璃, 40, IItems.铈锭, 85, IItems.导能回路, 30, IItems.生物钢, 15)

    drawers = DrawMulti(
      DrawRegion("-bottom"), DrawLiquidRegion(ILiquids.血肉赘生物), DrawLiquidRegion(ILiquids.纯净水),

      DrawCultivator().apply {
        plantColor = "D75B6E".toColor()
        plantColorLight = "E78F92".toColor()
        bottomColor = "474747".toColor()
        bubbles = 12
        sides = 16
        strokeMin = 0f
        spread = 5f
        timeScl = 90f
        recurrence = 8f
        radius = 4f
      }, DrawDefault(), DrawParticles().apply {
        color = "E78F92".toColor()
        alpha = 0.6f
        particles = 15
        particleLife = 300f
        particleRad = 16f
        particleSize = 2f
        fadeMargin = 0f
        rotateScl = 360f
        reverse = true
      })
  }
}