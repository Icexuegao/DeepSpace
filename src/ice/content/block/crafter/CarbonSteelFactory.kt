package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawArcSmelt
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.entities.effect.RadialEffect
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class CarbonSteelFactory : NormalCrafter("carbonSteelFactory") {
  init {
    bundle {
      desc(
        zh_CN, "碳控熔炉", "通过精确控制碳元素配比,在同一生产线灵活产出高碳钢和低碳钢,稳定的温度控制确保钢材质量始终达标"
      )
    }
    size = 3
    itemCapacity = 50
    alwaysUnlocked = true
    requirements(Category.crafting, IItems.铜锭, 10, IItems.低碳钢, 50)
    craftEffect = RadialEffect().apply {
      effect = Fx.surgeCruciSmoke
      rotationSpacing = 0f
      lengthOffset = 0f
      amount = 4
    }
    draw = DrawMulti(DrawRegion("-bottom"), DrawArcSmelt().apply {
      x += 8
      startAngle = 135f
      endAngle = 225f
    }, DrawArcSmelt().apply {
      x -= 8
      startAngle = -45f
      endAngle = 45f
    }, DrawArcSmelt().apply {
      y += 8
      startAngle = 180 + 45f
      endAngle = 360 - 45f
    }, DrawArcSmelt().apply {
      y -= 8
      startAngle = 0f + 45
      endAngle = 180f - 45f
    }, DrawDefault())

    newConsume().apply {
      time(45f)
      item(IItems.赤铁矿, 2)
      power(60 / 60f)
    }
    newProduce().apply {
      items(IItems.低碳钢, 1)
    }
    newConsume().apply {
      time(60f)
      items(IItems.赤铁矿, 2, IItems.生煤, 3)
      power(90 / 60f)
    }
    newProduce().apply {
      items(IItems.高碳钢, 1)
    }
  }
}