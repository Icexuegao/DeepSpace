package singularity.type

import ice.ui.bundle.Localizable
import mindustry.type.Planet

open class IcePlanet :Planet, Localizable {
  constructor(name: String, parent: Planet? = null, radius: Float) :super(name, parent, radius)
  constructor(name: String, parent: Planet?, radius: Float, sectorSize: Int) :super(name, parent, radius, sectorSize)

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