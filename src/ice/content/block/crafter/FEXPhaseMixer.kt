package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.draw.DrawMulti
import mindustry.content.Items
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class FEXPhaseMixer:NormalCrafter("FEX_phase_mixer"){
  init{
  bundle {
    desc(zh_CN, "FEX相位混合器", "重建FEX的液态物质结构,使其中的能量活性化")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.强化合金, 40, IItems.钴钢, 90, IItems.絮凝剂, 85, IItems.单晶硅, 80
    )
  )
  size = 2
  hasLiquids = true
  liquidCapacity = 12f


  newConsume()
  consume!!.time(120f)
  consume!!.item(Items.phaseFabric, 1)
  consume!!.liquid(ILiquids.FEX流体, 0.2f)
  consume!!.power(1.9f)
  newProduce()
  produce!!.liquid(ILiquids.相位态FEX流体, 0.2f)

  draw = DrawMulti(
    DrawBottom(), DrawLiquidTile(ILiquids.FEX流体), object : DrawLiquidTile(ILiquids.相位态FEX流体) {
      init {
        drawLiquidLight = true
      }
    }, DrawDefault(), DrawRegion("_top")
  )
}
}