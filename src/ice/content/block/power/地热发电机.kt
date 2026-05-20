package ice.content.block.power

import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.type.Category
import mindustry.world.blocks.power.ThermalGenerator
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute
import universecore.ui.bundle.localization
import universecore.world.draw.DrawFloorLiquid

class 地热发电机 :ThermalGenerator("geothermalGenerator") {
  init {
    localization {
      zh_CN {
        localizedName = "地热发电机"
        description = "利用地热能持续产生电力,需要建造在高热量区域以发挥最大效率,稳定供能"
      }
      en {
        localizedName = "Geothermal Generator"
        description =
          "Continuously generates power using geothermal energy. Must be built in high-heat areas for maximum efficiency, providing stable energy output."
      }
    }
    size = 3
    floating = true
    attribute = Attribute.heat
    liquidCapacity = 36f
    powerProduction = 5f
    requirements(Category.power, IItems.石英玻璃, 90, IItems.铱板, 105, IItems.单晶硅, 35)
    effectChance = 0.1f
    generateEffect = Fx.redgeneratespark
    drawer = DrawMulti(DrawRegion("-bottom"), DrawFloorLiquid(), DrawDefault(), DrawGlowRegion())
  }
}