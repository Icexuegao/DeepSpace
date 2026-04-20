package singularity.type

import ice.ui.bundle.Localizable
import mindustry.ctype.UnlockableContent
import mindustry.type.Planet

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
open class IcePlanet :Planet, Localizable {
  override var localizedName: String by UnlockableContent::localizedName
  override var description: String by UnlockableContent::description
  override var details: String by UnlockableContent::details

  constructor(name: String, parent: Planet? = null, radius: Float) :super(name, parent, radius)
  constructor(name: String, parent: Planet?, radius: Float, sectorSize: Int) :super(name, parent, radius, sectorSize)

}