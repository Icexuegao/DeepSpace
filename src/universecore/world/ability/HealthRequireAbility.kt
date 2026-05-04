package universecore.world.ability

import mindustry.entities.abilities.Ability
import mindustry.type.StatusEffect

class HealthRequireAbility(
  private val percent: Float,
  private val status: StatusEffect,
  private val status2: StatusEffect? = null
) :Ability() {

  override fun localized(): String {
    return "状态切换\n在${percent * 100}%生命值时切换"
  }

  override fun update(unit: mindustry.gen.Unit) {
    if (unit.healthf() >= percent) {
      unit.apply(status, 60f)
    } else if (status2 != null) {
      unit.apply(status2, 60f)
    }
  }

  override fun copy(): Ability {
    return HealthRequireAbility(percent, status, status2)
  }
}