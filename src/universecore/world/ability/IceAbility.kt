package universecore.world.ability

import ice.ui.bundle.Localizable
import mindustry.entities.abilities.Ability

/** 实现本地化接口Ability
 * @see Localizable */
abstract class IceAbility :Ability(), Localizable {
  override var localizedName = ""
  override var description = ""
  override var details = ""
}