package ice.content.block.power

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.power.ConsumeGenerator
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawWarmupRegion
import universecore.ui.bundle.localization

class 燃烧发电机 :ConsumeGenerator("combustionGenerator") {
  init {
    localization {
      zh_CN {
        localizedName = "燃烧发电机"
        description = "燃烧可燃物缓慢生产电力"
      }
      en {
        localizedName = "Combustion Generator"
        description = "Slowly generates power by burning flammable materials."
      }
    }
    powerProduction = 1f
    itemDuration = 120f
    ambientSound = Sounds.shootMerui
    ambientSoundVolume = 0.03f
    generateEffect = Fx.generatespark
    consume(ConsumeItemFlammable())
    drawer = DrawMulti(DrawDefault(), DrawWarmupRegion())
    requirements(Category.power, IItems.高碳钢, 20, IItems.锌锭, 20)
  }
}