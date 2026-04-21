package ice.content.block.nuclear


import mindustry.type.ItemStack
import mindustry.world.meta.BuildVisibility
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergySource

class 核能源:EnergySource("nuclear_energy_source"){
 init {
    localization {
      zh_CN {
        this.localizedName = "核能源"
        description = "释放中子能量"
      }
    }
    squareSprite = false
    requirements(SglCategory.nuclear, BuildVisibility.sandboxOnly, ItemStack.empty)
  }
}