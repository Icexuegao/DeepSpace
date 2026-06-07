package universecore.world.ability

import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.scene.ui.layout.Table
import arc.util.Time
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.game.EventType.UnitCreateEvent
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.UnitType
import universecore.struct.AttachedProperty
import universecore.ui.bundle.Localizable
import universecore.util.log
import universecore.util.toTrimmedString
import kotlin.math.roundToInt

open class UnitSpawnAbility(var u: UnitType, var size: Int = 1, var time: Float, var sX: Float = 0f, var sY: Float = 0f) :IceAbility() {
  companion object {
    private var Localizable.sizes by AttachedProperty { "" }
  }

  init {
    localization {
      zh_CN {
        localizedName = "建造单位"
        description = "建造时间"
        sizes = "数量"
      }
    }
  }

  var spawnEffect: Effect = Fx.spawn
  var parentizeEffects: Boolean = false
  var alpha = 1f
  var color: Color = Pal.accent
  var timer: Float = 0f

  override fun addStats(t: Table) {
    super.addStats(t)
    t.add("[stat]${(time / 60f).toTrimmedString(2)} 秒[lightgray] $description").row()
    t.add("[stat]$size[] [lightgray]$sizes")
    t.add("[stat]${u.localizedName}")
  }

  override fun update(unit: Unit) {
    data.log()
    if (data.roundToInt() >= size) return
    timer += Time.delta * Vars.state.rules.unitBuildSpeed(unit.team)

    if (timer >= time && Units.canCreate(unit.team, this.u)) {
      val x = unit.x + Angles.trnsx(unit.rotation, sY, -sX)
      val y = unit.y + Angles.trnsy(unit.rotation, sY, -sX)
      spawnEffect.at(x, y, 0f, if (parentizeEffects) unit else null)
      val u = this.u.create(unit.team)
      u.set(x, y)
      u.rotation = unit.rotation
      Events.fire(UnitCreateEvent(u, null, unit))
      if (!Vars.net.client()) {
        u.add()
        Units.notifyUnitSpawn(u)
      }
      data += 1
      timer = 0f
    }
  }

  override fun draw(unit: Unit) {
    if (Units.canCreate(unit.team, this.u)&&data< size) {
      Draw.draw(Draw.z()) {
        val x = unit.x + Angles.trnsx(unit.rotation, sY, -sX)
        val y = unit.y + Angles.trnsy(unit.rotation, sY, -sX)
        Drawf.construct(x, y, this.u.fullIcon, color, unit.rotation - 90, timer / time, alpha, timer)
      }
    }
  }

  override fun localized() = localizedName
}