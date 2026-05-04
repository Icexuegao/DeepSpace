package universecore.world.ability

import arc.graphics.Color
import arc.math.Angles
import arc.scene.ui.layout.Table
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.graphics.Pal
import universecore.util.applyColor

class InterceptAbilty(var damage: Float, var range: Float) :IceAbility() {
  init {
    localization {
      zh_CN {
        localizedName = "拦截护盾"
        description = "格挡一定伤害值的子弹,对伤害超出格挡上限的子弹无效"
      }
    }
  }

  override fun localized() = localizedName
  override fun addStats(table: Table) {
    table.add(description).wrap().width(descriptionWidth).row()
    table.add("${damage.toInt().toString().applyColor(Pal.accent)} " + IceStats.拦截伤害.localized().applyColor(Color.lightGray)).left()
      .padLeft(5f).row()
    table.add("${range.toInt().toString().applyColor(Pal.accent)} " + IceStats.拦截范围.localized().applyColor(Color.lightGray)).left()
      .padLeft(5f).row()
  }

  var shieldWave: mindustry.entities.Effect? = null

  override fun update(unit: Unit) {
    super.update(unit)
    if (shieldWave === null) shieldWave = IceEffects.shieldWave(unit, range = range)
    val intersect = Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2f, range * 2f)
    intersect.forEach {
      if (it.team == unit.team() || it.damage > damage) return
      val angle = Angles.angle(unit.x, unit.y, it.x, it.y)
      shieldWave!!.at(it.x, it.y, angle)
      it.type.hit(it, it.x, it.y)
      it.remove()
    }
  }
}