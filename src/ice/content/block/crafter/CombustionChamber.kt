package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawCrucibleFlame
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class CombustionChamber : NormalCrafter("combustion_chamber") {
  init {
    bundle {
      desc(zh_CN, "燃烧室", "密闭耐高温的舱室,用于执行化学燃烧过程,为最大化利用燃烧释放的能量,燃烧会将在活塞室内进行以推动线圈产生电力")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 90, IItems.钴锭, 80, IItems.石英玻璃, 80, IItems.单晶硅, 75
      )
    )
    size = 3
    liquidCapacity = 40f
    itemCapacity = 25


    newConsume()
    consume!!.liquid(ILiquids.氢气, 0.8f)
    newProduce()
    produce!!.liquid(ILiquids.纯净水, 0.4f)
    produce!!.power(5f)

    newConsume()
    consume!!.time(120f)
    consume!!.item(Items.pyratite, 1)
    newProduce()
    produce!!.liquid(ILiquids.二氧化硫, 0.4f)
    produce!!.power(4.5f)

    newBooster(2.65f)
    consume!!.liquid(Liquids.ozone, 0.4f)

    draw = DrawMulti(
      DrawBottom(), DrawCrucibleFlame(), DrawDefault()
    )
  }
}