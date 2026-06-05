package ice.content.block.crafter

import ice.content.IItems
import ice.world.meta.IceEffects
import mindustry.entities.effect.MultiEffect
import mindustry.type.Category
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter
import universecore.world.draw.DrawMulti

class 等离子蚀刻厂 :NormalCrafter("integratedFactory") {
  init {
    localization {
      zh_CN {
        this.localizedName = "等离子蚀刻厂"
        description = "将石墨烯通过石英玻璃蚀刻于单晶硅上,产出电子原件"
        details = "采用等离子蚀刻技术,在硅晶圆上雕刻出微米级电路,电子工业的基础设施"
      }
      en {
        this.localizedName = "Plasma Etching Factory"
        description = "Etches graphene onto monocrystalline silicon through quartz glass to produce electronic components"
        details = "Adopts plasma etching technology to carve micron-level circuits on silicon wafers, infrastructure of the electronics industry"
      }
    }
    size = 3
    health = 200
    itemCapacity = 20
    newConsume().apply {
      time(160f)
      items(IItems.钴钢, 35, IItems.铬锭, 30, IItems.钍锭, 15, IItems.铱板, 20,IItems.导能回路, 10)
      power(220 / 60f)
    }
    newProduce().apply {
      items(IItems.电子元件, 1)
    }
    craftEffect = MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin, IceEffects.hitLaserBlast)
    drawers = DrawMulti(DrawRegion("-bottom"), DrawRegion("-top"))
    requirements(Category.crafting, IItems.铜锭, 50, IItems.铬锭, 60, IItems.单晶硅, 30)
  }
}