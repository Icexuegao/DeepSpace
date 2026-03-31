package ice.content.block.nuclear

import ice.ui.bundle.bundle
import mindustry.type.ItemStack
import mindustry.world.meta.BuildVisibility
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergyVoid

class 核能黑洞: EnergyVoid("nuclear_energy_void"){
  init {
    bundle {
      desc(zh_CN, "核能黑洞", "吸收中子能量")
    }
    squareSprite = false
    requirements(SglCategory.nuclear, BuildVisibility.sandboxOnly, ItemStack.empty)
  }
}