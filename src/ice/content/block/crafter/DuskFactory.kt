package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawArcSmelt
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class DuskFactory : NormalCrafter("duskFactory") {
  init {
    bundle {
      desc(zh_CN, "暮白高炉", "将金属与信仰在苍白焰火中熔合,冶炼蕴含暮光之息的特殊合金")
    }
    size = 3
    itemCapacity = 20
    craftEffect = IceEffects.square(IceColor.b4, length = 6f)
    draw = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawArcSmelt().apply {
      y = 2f
      flameColor = IceColor.b4
      startAngle = 60f
      endAngle = 120f
    }, DrawArcSmelt().apply {
      y = -2f
      flameColor = IceColor.b4
      startAngle = 240f
      endAngle = 300f
    }, DrawArcSmelt().apply {
      x = 2f
      flameColor = IceColor.b4
      startAngle = -30f
      endAngle = 30f
    }, DrawArcSmelt().apply {
      x = -2f
      flameColor = IceColor.b4
      startAngle = 150f
      endAngle = 210f
    }, DrawDefault(), DrawFlame().apply {
      flameColor = IceColor.b4
    })
    requirements(Category.crafting, IItems.高碳钢, 200, IItems.铬锭, 50, IItems.钴锭, 30, IItems.铪锭, 10)

    newConsume().apply {
      time(120f)
      items(IItems.低碳钢, 5, IItems.铬锭, 1, IItems.钴锭, 3, IItems.铪锭, 1)
      liquid(ILiquids.暮光液, 0.3f)
    }
    newProduce().apply {
      items(IItems.暮光合金, 3)
    }
  }
}