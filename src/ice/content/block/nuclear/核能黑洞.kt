package ice.content.block.nuclear

import mindustry.type.ItemStack
import mindustry.world.meta.BuildVisibility
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergyVoid

class 核能黑洞 :EnergyVoid("nuclear_energy_void") {
  init {
    localization {
      zh_CN {
        localizedName = "核能黑洞"
        description = "吸收中子能量"
      }
      en {
        localizedName = "Nuclear Energy Void"
        description = "Absorbs neutron energy."
      }
    }
    squareSprite = false
    requirements(SglCategory.nuclear, BuildVisibility.sandboxOnly, ItemStack.empty)
  }
}