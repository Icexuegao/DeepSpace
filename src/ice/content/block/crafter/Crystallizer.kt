package ice.content.block.crafter

import arc.Core
import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawDefaultBottom
import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawCultivator
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter

class Crystallizer : NormalCrafter("crystallizer") {
  init {
    bundle {
      desc(zh_CN, "结晶器", "最早的FEX结晶技术,依赖电磁场波动,使FEX在载体金属上逐步形成结晶")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 35, IItems.单晶硅, 45, IItems.铜锭, 40, IItems.石英玻璃, 50
      )
    )
    size = 2
    liquidCapacity = 16f

    newConsume()
    consume!!.time(240f)
    consume!!.item(IItems.强化合金, 1)
    consume!!.liquid(ILiquids.FEX流体, 0.2f)
    consume!!.power(2.8f)
    newProduce()
    produce!!.item(IItems.FEX水晶, 2)

    draw = DrawMulti(
      DrawDefaultBottom(), object : DrawCultivator() {
        init {
          plantColor = Color.valueOf("#C73A3A")
          plantColorLight = Color.valueOf("#E57D7D")
        }

        override fun load(block: Block) {
          middle = Core.atlas.find(block.name + "_middle")
        }
      }, DrawDefault()
    )
  }
}