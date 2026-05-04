package universecore.world.ability

import arc.graphics.Color
import arc.scene.ui.layout.Table
import ice.world.meta.IceStats
import mindustry.entities.Units
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.type.StatusEffect
import universecore.util.applyColor
import universecore.util.toTrimmedString

class DeathGiftAbility(var range: Float, var status: StatusEffect, var duration: Float, var percent: Float, var amount: Float) :
  IceAbility() {
  init {
    localization {
      zh_CN {
        localizedName = "遗馈"
        description = "死亡为周围单位施加状态,并恢复生命"
      }
    }
  }

  override fun localized() = localizedName
  override fun addStats(table: Table) {
    table.add(description).wrap().width(descriptionWidth).row()
    table.add("${range.toTrimmedString(1).applyColor(Pal.accent)} ${IceStats.范围.localized().applyColor(Color.lightGray)}").row()
    table.add("${status.localizedName.applyColor(Pal.accent)} ${IceStats.状态.localized().applyColor(Color.lightGray)}").row()
    table.add("${(duration / 60f).toTrimmedString(1).applyColor(Pal.accent)} ${IceStats.秒.localized().applyColor(Color.lightGray)}").row()
    table.add("${(percent * 100).toTrimmedString(1).applyColor(Pal.accent)}% ${IceStats.修复.localized().applyColor(Color.lightGray)}")
      .row()
    table.add("${(amount).toTrimmedString(1).applyColor(Pal.accent)} ${IceStats.修复量.localized().applyColor(Color.lightGray)}").row()
  }

  override fun death(unit: Unit) {
    Units.nearby(unit.team, unit.x, unit.y, range) { u ->
      u.apply(status, duration)
      u.heal(u.maxHealth * percent + amount)
    }
  }
}