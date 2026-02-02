package ice.world.content.unit.ability

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.Time
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.entities.abilities.Ability
import mindustry.game.EventType.UnitCreateEvent
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.UnitType

open class UnitSpawnAbility : Ability {
  var unit: UnitType? = null
  var spawnTime: Float = 60f
  var spawnX: Float = 0f
  var spawnY: Float = 0f
  var spawnEffect: Effect = Fx.spawn
  var parentizeEffects: Boolean = false
  var alpha = 1f
  var color: Color = Pal.accent

  protected var timer: Float = 0f

  constructor(unit: UnitType, spawnTime: Float, spawnX: Float=0f, spawnY: Float=0f) {
    this.unit = unit
    this.spawnTime = spawnTime
    this.spawnX = spawnX
    this.spawnY = spawnY
  }

  constructor()

  override fun addStats(t: Table) {
    super.addStats(t)
    t.add(abilityStat("buildtime", Strings.autoFixed(spawnTime / 60f, 2)))
    t.row()
    t.add((if (unit!!.hasEmoji()) unit!!.emoji() else "") + "[stat]" + unit!!.localizedName)
  }

  override fun update(unit: Unit) {
    timer += Time.delta * Vars.state.rules.unitBuildSpeed(unit.team)

    if (timer >= spawnTime && Units.canCreate(unit.team, this.unit)) {
      val x = unit.x + Angles.trnsx(unit.rotation, spawnY, -spawnX)
      val y = unit.y + Angles.trnsy(unit.rotation, spawnY, -spawnX)
      spawnEffect.at(x, y, 0f, if (parentizeEffects) unit else null)
      val u = this.unit!!.create(unit.team)
      u.set(x, y)
      u.rotation = unit.rotation
      Events.fire(UnitCreateEvent(u, null, unit))
      if (!Vars.net.client()) {
        u.add()
        Units.notifyUnitSpawn(u)
      }

      timer = 0f
    }
  }

  override fun draw(unit: Unit) {
    if (Units.canCreate(unit.team, this.unit)) {
      Draw.draw(Draw.z()) {
        val x = unit.x + Angles.trnsx(unit.rotation, spawnY, -spawnX)
        val y = unit.y + Angles.trnsy(unit.rotation, spawnY, -spawnX)
        Drawf.construct(x, y, this.unit!!.fullIcon, color, unit.rotation - 90, timer / spawnTime, alpha, timer)
      }
    }
  }

  override fun localized(): String? {
    return Core.bundle.format("ability.unitspawn", unit!!.localizedName)
  }
}