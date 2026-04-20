package ice.world.meta

import ice.ui.bundle.Localizable
import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat

class IceStat(name: String, category: StatCat = StatCat.general) :Stat(name, category), Localizable {
  override fun localized() = localizedName
  override fun toString() = localizedName

  override var localizedName: String = ""

  override var description: String = ""

  override var details: String = ""
}