package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids

import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.world.draw.DrawCrucibleFlame
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class VacuumVrucible : NormalCrafter("vacuum_crucible") {
  init {
    localization {
      zh_CN {
        this.localizedName = "真空坩埚"
        description = "将多种材料进行低压处理并生产特定产物,可配置"
        details = "在低压高温环境下进行特殊工序时使用的设备"
      }
    }
    requirements(
      Category.crafting, IItems.铬锭, 90, IItems.单晶硅, 80, IItems.钴钢, 60, IItems.石英玻璃, 75, IItems.钴锭, 80
    )
    size = 3
    squareSprite = false
    liquidCapacity = 120f
    itemCapacity = 30

    newFormula { consumers, producers ->
      consumers.apply {
        time(60f)
        liquids(ILiquids.氯化硅溶胶, 0.2f, ILiquids.氢气, 0.4f)
        item(IItems.金珀沙, 5)
        power(70f/60f)
      }
      producers.apply {
        item(IItems.单晶硅, 8)
      }

    }
    newFormula { consumers, producers ->
      consumers.apply {
        time(120f)
        liquid(ILiquids.氯化硅溶胶, 0.4f)
        item(IItems.石英玻璃, 5)
        power(1.8f)
      }
      producers.apply {
        item(IItems.气凝胶, 5)
      }
    }

    newFormula { consumers, producers ->
      consumers.apply {
        time(120f)
        item(IItems.绿藻块, 1)
        liquid(ILiquids.酸液, 0.2f)
        power(1.6f)
      }
      producers.apply {
        item(IItems.絮凝剂, 1)
      }
    }
    newFormula { consumers, producers ->
      consumers.apply {
        time(120f)
        item(IItems.生煤, 3)
        power(140f/60f)
      }
      producers.apply {
        item(IItems.焦炭, 2)
      }
    }

    drawers = DrawMulti(
      DrawBottom(), DrawCrucibleFlame(), DrawDefault()
    )
  }
}
