package ice.content.block.power

import ice.content.IItems
import mindustry.content.Fx
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawPistons
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class 铈基热电机组 :NormalCrafter("ceriumBasedThermal") {
  init {
    localization {
      zh_CN {
        localizedName = "铈基热电机组"
        description = "裂解铈进行热能转换,可以产生大量电力"
      }
      en {
        localizedName = "Cerium-Based Thermal Generator"
        description = "Cracks cerium for thermal conversion, capable of producing large amounts of power."
      }
    }
    size = 2
    health = 300
    craftEffect = Fx.flakExplosion
    drawers = universecore.world.draw.DrawMulti(DrawBottom(), DrawPistons().also {
      it.sinMag = 1.5f
      it.lenOffset = 3.5f
    }, DrawDefault())
    requirements(Category.power, IItems.高碳钢, 50, IItems.锌锭, 20, IItems.黄铜锭, 15)
    newFormula { consumers, producers ->
      consumers.items(IItems.铈锭, 1)
      consumers.time(120f)
      producers.power(400f / 60f)
    }
    newFormula { consumers, producers ->
      consumers.items(IItems.铈凝块, 1)
      consumers.time(120f)
      producers.power(1000f / 60f)
    }
  }
}