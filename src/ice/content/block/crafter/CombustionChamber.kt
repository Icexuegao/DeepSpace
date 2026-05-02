package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids

import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawCrucibleFlame
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class CombustionChamber : NormalCrafter("combustion_chamber") {
  init {
    localization {
      zh_CN {
        this.localizedName = "燃烧室"
        description = "将多种材料置入燃烧,生产特定产物并输出电力,可配置\n密闭耐高温的舱室,用于执行化学燃烧过程,为最大化利用燃烧释放的能量,燃烧会将在活塞室内进行以推动线圈产生电力"
      }
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 90, IItems.钴锭, 80, IItems.石英玻璃, 80, IItems.单晶硅, 75
      )
    )
    health = 420
    size = 3
    liquidCapacity = 120f
    itemCapacity = 25


    newFormula { consumers, producers ->
      consumers.apply {
        liquids(ILiquids.氢气, 0.8f, ILiquids.氧气,36f/60f)
      }
      producers.apply {
        liquid(ILiquids.纯净水, 0.4f)
        power(5f)
      }
    }
    newFormula { consumers, producers ->
      consumers.apply {
        item(IItems.硫化合物, 1)
        time(120f)
      }
      producers.apply {
        liquid(ILiquids.二氧化硫, 0.4f)
        power(4.5f)
      }
    }

    newFormula { consumers, producers ->
      consumers.apply {
        item(IItems.生煤, 3)
        liquid(Liquids.water,24f/60)
        time(120f)
        power(2f)
      }
      producers.apply {
        liquids(ILiquids.废水, 12f/60f, ILiquids.氢气,20f/60f)
      }
    }

    newFormula { consumers, producers ->
      consumers.apply {
        liquids(ILiquids.氢气,12f/60,ILiquids.氯气,24f/60)
        time(60f)
      }
      producers.apply {
        liquids(ILiquids.酸液,12f/60f)
        power(280f/60f)
      }
    }



    newBooster(1.65f)
    consume!!.liquid(ILiquids.沼气, 0.2f)

    drawers = DrawMulti(
      DrawBottom(), DrawCrucibleFlame(), DrawDefault()
    )
  }
}
