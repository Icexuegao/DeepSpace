package ice.world.content.unit.ability

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Mathf
import arc.util.Time
import ice.graphics.IceColor
import mindustry.entities.abilities.Ability
import mindustry.gen.Unit
import mindustry.graphics.Layer

class ShieldAbility(var amount: Float, var max: Float, var tier: Int = 1, var size: Float = 0f, var color: Color = IceColor.b4) : Ability() {
  override fun localized() = " 持续护盾\n[stat]${max}[lightgray]护盾\n[stat]${amount}[lightgray]/秒"
  override fun update(unit: Unit) {
    if (unit.shield < max) unit.shield += amount / 60
  }

  override fun draw(unit: Unit) {
    if (tier == 1) return
    Draw.color(color)
    Draw.z(Layer.shields)

    for (i in 1..tier) {
      var j = 0
      while (j < 360) {
        val xy = Pair(Angles.trnsx(j + 30f, size * i * 2f - (if (j % 60 != 0) size / 2.1547f * i else size / 2 / 2.1547f * i)), Angles.trnsy(j + 30f, size * i * 2f - (if (j % 60 != 0) size / 2.1547f * i else size / 2 / 2.1547f * i)))
        val ex = xy.first + unit.x
        val ey = xy.second + unit.y
        Fill.poly(ex, ey, 6, size * Mathf.absin(-Time.time / 2 + i * 10, 8f, 0.8f * unit.shield / max))
        j += 360 / (i * 6)
      }
    }
  }
}
