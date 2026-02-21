package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.draw.*
import singularity.world.blocks.product.NormalCrafter

class CobaltSteelCompressor : NormalCrafter("cobaltSteelCompressor") {
  init {
    bundle {
      desc(zh_CN, "钴钢压缩机")
    }
    size = 3
    hasLiquids = true
    squareSprite = false
    itemCapacity = 36
    liquidCapacity = 36f
    craftEffect = IceEffects.square(IItems.钴钢.color)
    updateEffect = Fx.plasticburn
    draw = DrawMulti(DrawRegion("-bottom"), DrawPistons().apply {
      sinMag = 2.75f
      sinScl = 3f
      sides = 8
      sideOffset = 1.5707964f
    }, DrawDefault(), DrawRegion("-mid"), DrawLiquidRegion(Liquids.oil), DrawFade(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 5.652f
      color = Color.valueOf("F0511D")
    })
    requirements(Category.crafting, IItems.高碳钢, 150, IItems.铬锭, 100, IItems.锌锭, 50)

    newConsume().apply {
      time(36f)
      items(IItems.钴锭, 4, IItems.铬锭, 2)
      liquid(ILiquids.异溶质, 20f / 60f)
      power(7.5f)
    }
    newProduce().apply {
      items(IItems.钴钢, 3)
    }
  }
}