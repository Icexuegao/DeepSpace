package ice.content.block.nuclear

import ice.ui.bundle.bundle
import mindustry.type.ItemStack
import mindustry.world.meta.BuildVisibility
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergySource

class 核能源:EnergySource("nuclear_energy_source"){
 init {
    bundle {
      desc(zh_CN, "核能源", "释放中子能量")
    }
    squareSprite = false
    requirements(SglCategory.nuclear, BuildVisibility.sandboxOnly, ItemStack.empty)
  }
}