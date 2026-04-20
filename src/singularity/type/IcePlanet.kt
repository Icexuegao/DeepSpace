package singularity.type

import ice.ui.bundle.Localizable
import mindustry.type.Planet

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
open class IcePlanet :Planet, Localizable {
  constructor(name: String, parent: Planet? = null, radius: Float) :super(name, parent, radius)
  constructor(name: String, parent: Planet?, radius: Float, sectorSize: Int) :super(name, parent, radius, sectorSize)

  override var localizedName: String
    get() = super.localizedName
    set(value) {
      super.localizedName = value
    }

  override var description: String
    get() = super.description
    set(value) {
      super.description = value
    }
  override var details: String
    get() = super.details
    set(value) {
      super.details = value
    }
}