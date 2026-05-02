package ice.world.content.unit.ability

import arc.scene.ui.layout.Table
import universecore.util.toStringi
import ice.world.meta.IceStats
import mindustry.entities.Units
import mindustry.entities.abilities.Ability
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.type.StatusEffect

class DeathGiftAbility(var range: Float, var status: StatusEffect, var duration: Float, var percent: Float, var amount: Float) :Ability() {
  override fun localized() = "遗馈"
  override fun addStats(table: Table) {
    table.add("死亡为周围单位施加状态,并恢复生命").wrap().width(descriptionWidth).row()
    table.add("[#${Pal.accent}]${range.toStringi(1)}[] ${IceStats.范围.localized()}").row()
    table.add("[#${Pal.accent}]${status.localizedName}[] ${IceStats.状态.localized()}").row()
    table.add("[#${Pal.accent}]${(duration / 60f).toStringi(1)}[] ${IceStats.秒.localized()}").row()
    table.add("[#${Pal.accent}]${(percent * 100).toStringi(1)}%[] ${IceStats.修复.localized()}").row()
    table.add("[#${Pal.accent}]${(amount).toStringi(1)}[] ${IceStats.修复量.localized()}").row()
  }

  override fun death(unit: Unit) {
    Units.nearby(unit.team, unit.x, unit.y, range) { u ->
      u.apply(status, duration)
      u.heal(u.maxHealth * percent + amount)
    }
  }
}