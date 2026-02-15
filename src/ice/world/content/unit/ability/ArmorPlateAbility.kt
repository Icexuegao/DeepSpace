package ice.world.content.unit.ability

import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.util.Strings
import ice.graphics.IceColor
import mindustry.entities.abilities.Ability
import mindustry.gen.Unit

class ArmorPlateAbility : Ability() {

  var healthMultiplier: Float = 0.2f
  var color = IceColor.r2
  private var warmup: Float = 0f

  override fun update(unit: Unit) {
    super.update(unit)

    warmup = Mathf.lerpDelta(warmup, if (unit.isShooting()) 1f else 0f, 0.01f)
    unit.healthMultiplier += warmup * healthMultiplier
  }

  override fun addStats(t: Table) {
    super.addStats(t)
    t.add(abilityStat("damagereduction", Strings.autoFixed(-healthMultiplier * 100f, 1)))
  }

  override fun draw(unit: Unit) {
    if (warmup > 0.001f) {
      Draw.color(color)
      Draw.alpha(0.3f * warmup)
      Draw.rect(unit.type.region, unit.x, unit.y, unit.rotation - 90f)
    }
  }
}