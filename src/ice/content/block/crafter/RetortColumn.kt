package ice.content.block.crafter

import arc.Core
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter
import kotlin.math.max

class RetortColumn : NormalCrafter("retort_column") {
  init {
    bundle {
      desc(zh_CN, "干馏塔", "将生煤干馏为硫化合物和焦炭", "通过隔绝空气的高温分离煤炭中的物质,以制造焦炭")
    }
    requirements(Category.crafting, IItems.铬锭, 70, IItems.钴锭, 75, IItems.铜锭, 90, IItems.石英玻璃, 90, IItems.钴钢, 50)
    size = 3
    itemCapacity = 12
    liquidCapacity = 20f
    craftEffect = Fx.smeltsmoke
    drawers = DrawMulti(
      DrawDefault(), object : DrawFlame() {
        override fun load(block: Block) {
          top = Core.atlas.find(block.name + "_top")
          block.clipSize = max(block.clipSize, (lightRadius + lightSinMag) * 2f * block.size)
        }
      })
    newFormula {consumers, producers ->
      consumers.apply {
        time(90f)
        power(2f)
        item(IItems.生煤, 3)
      }
      producers.apply {
        items(IItems.硫化合物, 1, IItems.焦炭, 1)
      }
    }

    newFormula {consumers, producers ->
      consumers.apply {
        time(120f)
        power(1f)
        liquid(ILiquids.藻泥, 36f / 60f)
      }
      producers.apply {
        items(IItems.生煤, 3, IItems.碱石, 1)
      }
    }
    newFormula {consumers, producers ->
      consumers.apply {
        time(120f)
        power(70f/60f)
        item(IItems.绿藻块,1)
      }
      producers.apply {
        items(IItems.焦炭,1)
      }
    }
  }
}