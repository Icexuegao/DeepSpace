package ice.content.block.crafter

import ice.audio.ISounds
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter

class CeramicSteelFurnace : NormalCrafter("ceramicSteelFurnace") {
  init {
    bundle {
      desc(zh_CN, "陶钢熔炼炉", "使用多种原料熔炼一种前所未见的多功能装甲材料-陶钢")
    }
    size = 3
    itemCapacity = 36
    canOverdrive = false
    updateEffect = Fx.melting
    ambientSoundVolume = 0.02f
    ambientSound = ISounds.beamLoop
    draw = DrawMulti(DrawDefault(), DrawFlame())
    requirements(Category.crafting, IItems.铬锭, 130, IItems.钴钢, 45, IItems.铱板, 55, IItems.导能回路, 45)

    newConsume().apply {
      time(60f)
      power(7.75f)
      items(IItems.石英玻璃, 1, IItems.钴钢, 1, IItems.铈锭, 1)

    }
    newProduce().apply {
      items(IItems.陶钢, 1)
    }
  }
}