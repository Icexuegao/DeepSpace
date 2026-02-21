package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawPistons
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class PressingForge : NormalCrafter("pressingForge") {
  init {
    bundle {
      desc(zh_CN, "冲压锻炉", "快速大批量地熔炼铱锇矿并将其锻压为铱板")
    }
    size = 5
    armor = 4f
    itemCapacity = 60
    liquidCapacity = 60f
    updateEffect = Fx.fuelburn
    craftEffect = Fx.pulverizeMedium
    ambientSoundVolume = 0.07f
    draw = DrawMulti(DrawRegion("-bottom"), DrawPistons().apply {
      sinMag = -2.6f
      sinScl = 3.5325f
      lenOffset = 0f
    }, DrawDefault(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 3.53429f
      color = Color.valueOf("F0511D")
    })
    requirements(Category.crafting, IItems.高碳钢, 450, IItems.锌锭, 180, IItems.钴锭, 135)


    newConsume().apply {
      time(45f)
      items(IItems.铱锇矿, 45)
      liquid(Liquids.water, 1f)
      power(27.5f)
    }
    newProduce().apply {
      items(IItems.铱板, 15)
      liquid(ILiquids.废水, 1f)
    }
  }
}