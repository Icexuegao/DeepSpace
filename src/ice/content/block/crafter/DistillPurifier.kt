package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class DistillPurifier : NormalCrafter("distill_purifier") {init {
  bundle {
    desc(zh_CN, "蒸馏净化器", "用原始的蒸馏方式分离水中的杂质")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.铜锭, 30, IItems.单晶硅, 24, IItems.石英玻璃, 30, IItems.钴锭, 20
    )
  )
  size = 2
  hasLiquids = true
  liquidCapacity = 30f
  squareSprite = false
  updateEffect = Fx.steam
  updateEffectChance = 0.035f

  newConsume()
  consume!!.time(120f)
  consume!!.liquid(Liquids.water, 0.5f)
  consume!!.power(1f)
  newProduce()
  produce!!.liquid(ILiquids.纯净水, 0.4f)
  produce!!.item(IItems.碱石, 1)

  draw = DrawMulti(DrawBottom(), DrawLiquidTile(Liquids.water, 3f), DrawDefault())
}
}