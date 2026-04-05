package ice.world.content.unit.ability

import arc.math.Angles
import arc.math.Mathf
import ice.entities.bullet.jumpIn
import ice.library.util.MultipleAny
import mindustry.Vars
import mindustry.entities.abilities.Ability
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.type.UnitType

class FlashbackAbility(var percent: Float,var amount: Int,var offset: Float,var spawnUnit: UnitType,var clone: UnitType) :Ability () {
  override fun localized(): String? {
    return "闪回"
  }

  override fun update(unit: Unit) {
    if (unit.healthf() > percent)return
    val spawner = Vars.spawner.spawns
    if (spawner.size > 0) {
      unit.clearStatuses()
      Sounds.shockBullet.at(unit)
      val random = Mathf.random(0, spawner.size - 1)
      unit.x = spawner.get(random).x * 8f
      unit.y = spawner.get(random).y * 8f
      for (i in 0 until amount) {
        val (x: Float,y: Float) = MultipleAny(Angles.trnsx(360f / amount * i, offset),Angles.trnsy(360f / amount * i, offset))
        val ux = unit.x + x
        val uy = unit.y + y
        spawnUnit.spawn(unit.team, ux, uy).rotation = unit.rotation
        jumpIn(spawnUnit, ux, uy).at(ux, uy, unit.rotation - 90, unit.team.color)
      }
      unit.remove()
      clone.spawn(unit.team, unit.x, unit.y).rotation = unit.rotation
    }
  }
}
