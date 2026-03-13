package ice.content.block.crafter

import ice.content.IItems
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawParticles
import singularity.world.blocks.product.NormalCrafter

class BiomassReformer: NormalCrafter("biomassReformer") {
  init {
    BaseBundle.bundle {
      desc(zh_CN,"生物钢重组器","将多种原料培育为生物钢")
    }
    size=3
    itemCapacity=36

    requirements(Category.crafting, IItems.铱板, 235, IItems.电子元件, 85, IItems.钴钢, 75, IItems.强化合金, 35)
    updateEffect= Fx.burning
    updateEffectChance=0.1f
    ambientSound= Sounds.loopCutter
    ambientSoundVolume=0.06f
    drawers = DrawMulti(DrawDefault(), DrawParticles().apply {
      color = "D86E56FF".toColor()
      alpha = 0.6f
      particles = 30
      particleLife = 60f
      particleRad = 8f
      particleSize = 2f
      fadeMargin = 0.5f
      rotateScl = 4f
    })
    newConsume().apply {
      time(120f)
      power(11f)
      items(IItems.血囊孢子, 5, IItems.钴钢, 1, IItems.强化合金, 1, IItems.铱板, 2, IItems.陶钢, 1)
    }
    newProduce().apply {
      items(IItems.生物钢, 2)
    }
  }
}