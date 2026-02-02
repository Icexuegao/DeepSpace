package ice.world.content.unit.ability

import mindustry.entities.Units
import mindustry.entities.abilities.Ability
import mindustry.gen.Unit
import mindustry.type.StatusEffect

class DeathGiftAbility(var range: Float, var status: StatusEffect, var duration: Float, var percent: Float, var amount: Float) : Ability() {
  override fun localized() = "遗馈"
  override fun death(unit: Unit) {
    Units.nearby(unit.team, unit.x, unit.y, range) { u ->
      u.apply(status, duration)
      u.heal(u.maxHealth * percent + amount)
    }
  }
}