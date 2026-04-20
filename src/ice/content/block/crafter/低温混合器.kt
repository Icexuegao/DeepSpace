package ice.content.block.crafter

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import ice.content.IItems
import ice.content.ILiquids

import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import ice.world.meta.IAttribute
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.FloorCrafter
import universecore.world.consumers.cons.ConsumeFloor

class 低温混合器 : FloorCrafter("lowTemperatureMixer") {
  init {
    localization {
      zh_CN {
        name = "低温混合器"
        description = "在极端低温环境中混合纯净水生产低温化合物,在特定地形上生效"
      }
    }
    size = 2
    health = 150
    liquidCapacity=60f
    itemCapacity = 20
    craftEffect = Effect(60f){e->
      Angles.randLenVectors(e.id.toLong(), 6, e.fin()*24f){x,y->
        Draw.color(IItems.低温化合物.color)
        Draw.alpha(e.fout())
        Fill.rect(e.x+x,e.y+y,4f,4f,e.fin())
      }
    }
    drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(),DrawDefault())
    requirements(Category.crafting, IItems.铜锭, 50, IItems.铬锭, 60, IItems.单晶硅, 30)
    newConsume().apply {
      time(120f)
      liquid(Liquids.water, 50f / 60f)
      power(210 / 60f)
    }
    newProduce().apply {
      items(IItems.低温化合物, 1)
    }

    newConsume().apply {
      time(120f)
      liquid(ILiquids.纯净水, 40f / 60f)
      power(180 / 60f)
    }
    newProduce().apply {
      items(IItems.低温化合物, 3)
    }
    newBooster(1f).add(ConsumeFloor(IAttribute.寒冷, 0.1f))

  }
}
