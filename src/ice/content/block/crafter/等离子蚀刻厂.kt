package ice.content.block.crafter

import ice.content.IItems

import universecore.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.entities.effect.MultiEffect
import mindustry.type.Category
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class 等离子蚀刻厂 : NormalCrafter("integratedFactory") {
  init {
    localization {
      zh_CN {
        this.localizedName = "等离子蚀刻厂"
        description = "将石墨烯通过石英玻璃蚀刻于单晶硅上,产出电子原件"
        details = "采用等离子蚀刻技术,在硅晶圆上雕刻出微米级电路,电子工业的基础设施"
      }
    }
    size = 3
    health = 200
    itemCapacity = 20
    newConsume().apply {
      time(160f)
      items(IItems.单晶硅, 1, IItems.石墨烯, 2, IItems.石英玻璃, 1, IItems.金锭,1)
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