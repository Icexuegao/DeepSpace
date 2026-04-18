package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawPistons
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class 冲压锻炉 : NormalCrafter("pressingForge") {
  init {
    bundle {
      desc(zh_CN, "冲压锻炉", "将铱锇矿熔炼锻压为铱板,需要通入水,会产生废水")
    }
    size = 5
    armor = 4f
    itemCapacity = 100
    liquidCapacity = 100f
    updateEffect = Fx.fuelburn
    craftEffect = Fx.pulverizeMedium
    ambientSoundVolume = 0.07f
    drawers = DrawMulti(DrawRegion("-bottom"), DrawPistons().apply {
      sinMag = -2.6f
      sinScl = 3.5325f
      lenOffset = 0f
    }, DrawDefault(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 3.53429f
      color = Color.valueOf("F0511D")
    })
    requirements(Category.crafting, IItems.高碳钢, 350, IItems.锌锭, 180, IItems.钴锭, 135)

    newConsume().apply {
      time(60f)
      items(IItems.铱锇矿, 25)
      liquid(Liquids.water, 1f)
      power(2.5f)
    }
    newProduce().apply {
      items(IItems.铱板, 10)
      liquid(ILiquids.废水, 25f/60f)
    }

    newConsume().apply {
      time(72f)
      items(IItems.铱锇矿, 50)
      liquid(Liquids.water, 2f)
      power(3f)
    }
    newProduce().apply {
      items(IItems.铱板, 25)
      liquid(ILiquids.废水, 60f/60f)
    }
  }
}
