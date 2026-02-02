package ice.world.content.unit.ability

import arc.util.Time
import mindustry.entities.Units
import mindustry.entities.abilities.SuppressionFieldAbility
import mindustry.gen.Unit

class RepairFieldAbility(var range: Float, var amount: Float, var percentAmount: Float) : SuppressionFieldAbility() {
  override fun localized() = "回复场\n[stat]${range}[lightgray]射程(格)\n[stat]${amount}/秒[lightgray]生命回复速度\n[stat]${percentAmount * 100}%/秒[lightgray]生命回复速度"
  override fun update(unit: Unit) {
    if (color != unit.team.color || this.particleColor != unit.team.color) {
      color = unit.team.color
      particleColor = unit.team.color
    }
    unit.heal((unit.maxHealth * percentAmount / 60 + amount / 60) * Time.delta)
    Units.nearby(unit.team, unit.x, unit.y, range) { other ->
      other.heal((unit.maxHealth * percentAmount / 60 + amount / 60) * Time.delta)
    }
  }
}
