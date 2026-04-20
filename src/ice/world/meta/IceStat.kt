package ice.world.meta

import ice.ui.bundle.Localizable
import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat

class IceStat(name: String, category: StatCat = StatCat.general) :Stat(name, category), Localizable {
  override fun localized() = localizedName
  override fun toString() = localizedName
  @JvmField var localizedName = ""
  @JvmField var details = ""
  @JvmField var description = ""

  override fun setLocalizedName(localizedName: String) {
    this.localizedName = localizedName
  }

  override fun setDescription(description: String) {
    this.description = description
  }

  override fun setDetails(details: String) {
    this.details = details
  }
}