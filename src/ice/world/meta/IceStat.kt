package ice.world.meta

import ice.ui.bundle.BaseBundle
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName

import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat

class IceStat(name: String, category: StatCat = StatCat.general) : Stat(name, category), BaseBundle.Bundle {
  override fun localized() = localizedName
}