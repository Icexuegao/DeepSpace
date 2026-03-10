package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class BloodExtractor : NormalCrafter("bloodExtractor") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "血肉分离机", "分离出血肉赘生物,一种同时具有高温和易燃两种特性的高危液体")
    }
    size = 2
    itemCapacity = 24
    liquidCapacity = 24f

    updateEffect = Fx.melting

    newFormula {consumers, producers ->
      consumers.apply {
        time(120f)
        power(6f)
        items(IItems.生物钢, 1)
      }
      producers.liquid(ILiquids.血肉赘生物, 0.2f)
    }
    requirements(Category.crafting, IItems.钴锭, 60, IItems.石英玻璃, 55, IItems.铱板, 75, IItems.电子元件, 60, IItems.生物钢, 45)
    draw = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(ILiquids.血肉赘生物), DrawDefault())
  }
}