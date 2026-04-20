package ice.content.block.crafter

import arc.Core
import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.DrawCultivator
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute
import singularity.world.blocks.product.FloorCrafter
import singularity.world.draw.DrawBottom
import universecore.world.consumers.cons.ConsumeFloor

class Incubator : FloorCrafter("incubator") {
  init {
    localization {
      zh_CN {
        name = "沼气池"
        description = "厌氧消化产甲烷耦合活化能供器\n人话:厕所"
      }
    }
    requirements(Category.production, IItems.钴钢, 85, IItems.铬锭, 90, IItems.气凝胶, 40, IItems.铜锭, 90)
    size = 3
    liquidCapacity = 20f


    newFormula { consumers, producers ->
      consumers.apply {
        time(45f)
        power(2.2f)
        liquids(Liquids.water, 24f / 60f, ILiquids.藻泥, 6f / 60f)
      }
      producers.apply {
        liquids(ILiquids.沼气, 30f / 60f)
      }
    }
    newFormula { consumers, producers ->
      consumers.apply {
        time(30f)
        power(2.2f)
        liquids(ILiquids.纯净水, 0.3f, ILiquids.藻泥, 0.1f)
      }
      producers!!.liquids(ILiquids.沼气,  30f / 60f)
    }



    newBooster(1f)
    consume!!.add(
      ConsumeFloor<FloorCrafterBuild>(
        checkDeep = true, checkLiquid = true, attributes = arrayOf(
          Attribute.heat, 0.22f, Attribute.spores, 0.1f
        )
      )
    )

    drawers = DrawMulti(
      DrawBottom(), DrawDefault(), object : DrawCultivator() {
        init {
          plantColor = Color.valueOf("bb2912")
          plantColorLight = plantColor.cpy().mul(1.1f)
        }

        override fun load(block: Block) {
          middle = Core.atlas.find(block.name + "_middle")
        }
      }, DrawRegion("_top")
    )
  }
}