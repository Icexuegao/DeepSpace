package ice.world.meta


import ice.ui.bundle.Bundle
import ice.ui.bundle.localizedName
import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat

class IceStat(name: String, category: StatCat = StatCat.general) : Stat(name, category), Bundle {
  override fun localized() = localizedName
  override fun toString() = localizedName
}