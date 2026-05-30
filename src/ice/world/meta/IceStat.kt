package ice.world.meta

import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat
import universecore.ui.bundle.Localizable

class IceStat(name: String, category: StatCat = StatCat.general) :Stat(name, category), Localizable {
  override var localizedName: String = ""
  override var description: String = ""
  override var details: String = ""

  override fun localized() = localizedName
  override fun toString() = localizedName
}