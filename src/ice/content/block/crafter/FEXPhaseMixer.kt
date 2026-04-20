package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class FEXPhaseMixer:NormalCrafter("FEX_phase_mixer"){
  init{
  localization {
    zh_CN {
      name = "相位混合器"
      description = "重建FEX的液态物质结构,使其中的能量活性化"
    }
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
  consume!!.item(IItems.钍锭, 2)
  consume!!.liquid(ILiquids.FEX流体, 0.2f)
  consume!!.power(1.9f)
  newProduce()
  produce!!.liquid(ILiquids.相位态FEX流体, 0.2f)

  drawers = DrawMulti(
    DrawBottom(), DrawLiquidTile(ILiquids.FEX流体), object : DrawLiquidTile(ILiquids.相位态FEX流体) {
      init {
        drawLiquidLight = true
      }
    }, DrawDefault(), DrawRegion("_top")
  )
}
}