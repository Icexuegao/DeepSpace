package ice.content.status

import arc.Core
import arc.Events
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.world.content.status.IceStatusEffect
import mindustry.entities.units.StatusEntry
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.world.meta.SglStat
import universecore.math.reverseProgress

class 电子干扰 :IceStatusEffect("electric_disturb") {
  init {
    Events.on(ice.game.EventType.BulletInitEvent::class.java) { e ->
      if (e.bullet.owner is Unit) {
        val u = e.bullet.owner as Unit
        val bullet = e.bullet
        bullet.apply {
          if (u.hasEffect(this@电子干扰)) {
            val deflect = 16.4f * Mathf.clamp(u.getDuration(this@电子干扰) / 120)
            val rot = Mathf.random(-deflect, deflect)
            rotation(rotation() + rot)
            Tmp.v1.set(aimX - x, aimY - y).rotate(rot)
            aimX = Tmp.v1.x
            aimY = Tmp.v1.y
          }
        }
      }
    }
  }

  init {
    localization {
      zh_CN {
        localizedName = "电子干扰"
        description = "电子设备受到外部干扰,火控系统将无法正常工作"
      }
    }
    color = Pal.accent
  }

  override fun update(unit: Unit, entry: StatusEntry) {
    super.update(unit, entry)
    unit.shield -= 0.4f * (entry.time / 120) * Time.delta
    unit.damageContinuousPierce(0.2f * (entry.time / 120))
    val scl = Mathf.clamp(entry.time / 120)
    unit.speedMultiplier *= (0.6f + 0.4f * scl.reverseProgress)
    unit.damageMultiplier *= (0.8f + 0.2f * (1 - scl))
    unit.reloadMultiplier *= (0.75f + 0.25f * (1 - scl))
  }

  override fun setStats() {
    super.setStats()
    stats.addPercent(Stat.damageMultiplier, 0.8f)
    stats.addPercent(Stat.speedMultiplier, 0.6f)
    stats.addPercent(Stat.reloadMultiplier, 0.75f)
    stats.add(Stat.damage, 12f, StatUnit.perSecond)
    stats.add(SglStat.special) { t ->
      t.row()
      t.add(Core.bundle.format("data.bulletDeflectAngle", 12.4f.toString() + StatUnit.degrees.localized()))
      t.row()
      t.add("[lightgray]" + Core.bundle.get("infos.attenuationWithTime") + "[]").padLeft(15f)
    }
  }
}
