package ice.content.block.power

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.blocks.power.ThermalGenerator
import mindustry.world.draw.DrawBlurSpin
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.Attribute
import mindustry.world.meta.BlockGroup
import universecore.ui.bundle.localization

class 蒸汽冷凝机: ThermalGenerator("steamCondenser"){
  init {
    localization {
      zh_CN {
        this.localizedName = "蒸汽冷凝机"
        description = "建造在喷气口上时生产少量电力与水"
      }
    }
    squareSprite = false
    size = 3
    fogRadius = 3
    hasLiquids = true
    liquidCapacity = 20f
    attribute = Attribute.steam
    group = BlockGroup.liquids
    displayEfficiencyScale = 1f / 9f
    minEfficiency = 9f - 0.0001f
    powerProduction = 3f / 9f
    displayEfficiency = false
    generateEffect = Fx.turbinegenerate
    effectChance = 0.04f
    ambientSound = Sounds.loopHum
    ambientSoundVolume = 0.06f
    requirements(Category.power, IItems.高碳钢, 80)
    drawer = DrawMulti(DrawDefault(), DrawBlurSpin("-rotator", 0.6f * 9f).apply {
      blurThresh = 0.01f
    })
    outputLiquid = LiquidStack(Liquids.water, 5f / 60f / 9f)
    liquidCapacity = 20f
  }
}