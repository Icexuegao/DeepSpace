package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.draw.DrawCrucibleFlame
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class VacuumVrucible : NormalCrafter("vacuum_crucible") {
  init {
    bundle {
      desc(zh_CN, "真空坩埚", "在低压高温环境下进行特殊工序时使用的设备")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 90, IItems.单晶硅, 80, IItems.钴钢, 60, IItems.石英玻璃, 75, IItems.钴锭, 80
      )
    )
    size = 3
    squareSprite = false
    liquidCapacity = 45f
    itemCapacity = 30

    newConsume()
    consume!!.time(60f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.氯化硅溶胶, 0.2f, Liquids.hydrogen, 0.4f
      )
    )
    consume!!.item(IItems.金珀沙, 5)
    consume!!.power(2f)
    newProduce()
    produce!!.item(
      IItems.单晶硅, 8
    )

    newConsume()
    consume!!.time(120f)
    consume!!.liquid(ILiquids.氯化硅溶胶, 0.4f)
    consume!!.item(IItems.石英玻璃, 10)
    consume!!.power(1.8f)
    newProduce()
    produce!!.item(IItems.气凝胶, 5)

    newConsume()
    consume!!.time(120f)
    consume!!.item(IItems.绿藻块, 1)
    consume!!.liquid(ILiquids.酸液, 0.2f)
    consume!!.power(1.6f)
    newProduce()
    produce!!.item(IItems.絮凝剂, 1)

    newConsume()
    consume!!.time(120f)
    consume!!.item(Items.sporePod, 1)
    consume!!.liquid(ILiquids.碱液, 0.2f)
    consume!!.power(1.6f)
    newProduce()
    produce!!.item(IItems.絮凝剂, 1)

    draw = DrawMulti(
      DrawBottom(), DrawCrucibleFlame(), DrawDefault()
    )
  }
}