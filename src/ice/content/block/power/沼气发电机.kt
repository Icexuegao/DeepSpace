package ice.content.block.power

import ice.content.IItems
import ice.content.ILiquids
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawMulti
import singularity.world.blocks.product.NormalCrafter

class 沼气发电机 :NormalCrafter("biogaGenerator") {
  init {
    localization {
      zh_CN {
        localizedName = "沼气发电机"
        description = "燃烧沼气缓慢生产电力"
      }
      en {
        localizedName = "Biogas Generator"
        description = "Slowly generates power by burning biogas."
      }
    }
    size = 2
    liquidCapacity = 60f
    health = 100
    updateEffect = IceEffects.square(ILiquids.沼气.color)
    newFormula { consumers, producers ->
      consumers.apply {
        liquid(ILiquids.沼气, 20f / 60f)
      }
      producers.apply {
        power(550f / 60f)
      }
    }


    drawers = DrawMulti(DrawDefault(), DrawGlowRegion())
    requirements(Category.power, IItems.高碳钢, 20, IItems.锌锭, 30, IItems.钴锭, 30)
  }
}