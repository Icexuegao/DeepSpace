package ice.content.status

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.util.Time
import ice.content.IStatus.rand
import ice.content.IStatus.冻结
import ice.content.IStatus.凛冻
import ice.world.content.status.IceStatusEffect
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.units.StatusEntry
import mindustry.gen.Unit
import mindustry.graphics.Pal
import singularity.graphic.SglDraw
import singularity.world.meta.SglStat
import kotlin.math.max

class 熔毁 :IceStatusEffect("meltdown") {
  init {
    localization {
      zh_CN {
        localizedName = "熔毁"
        description = ""
      }
    }
    damage = 2.2f
    effect = Fx.melting
  }

  override fun update(unit: Unit, entry: StatusEntry) {
    super.update(unit, entry)
    if (unit.shield > 0) {
      unit.shieldAlpha = 1f
      unit.shield -= Time.delta * entry.time / 6
    }
  }

  override fun init() {
    super.init()
    opposite(StatusEffects.freezing, StatusEffects.wet)
    affinity(StatusEffects.tarred) { unit: Unit, result: StatusEntry, time: Float ->
      unit.damagePierce(8f)
      Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f))
      result.set(this, 180 + result.time)
    }

    affinity(冻结) { e: Unit, s: StatusEntry, t: Float ->
      e.damage(t)
      s.time -= t
    }

    transs(凛冻) { e: Unit, s: StatusEntry, t: Float ->
      s.time -= t
      e.apply(StatusEffects.blasted)
      e.damage(max(e.getDuration(凛冻), t) / 2f)
    }
  }

  override fun setStats() {
    super.setStats()
    stats.add(SglStat.exShieldDamage, Core.bundle.get("infos.meltdownDamage"))
  }

  override fun draw(unit: Unit, time: Float) {
    super.draw(unit, time)

    SglDraw.drawBloomUponFlyUnit<Unit>(unit, SglDraw.DrawAcceptor { u: Unit ->
      val rate = Mathf.clamp(90 / (time / 30))
      Lines.stroke(2.2f * rate, Pal.lighterOrange)
      Draw.alpha(rate * 0.7f)
      Lines.circle(u.x, u.y, u.hitSize / 2 + rate * u.hitSize / 2)
      rand.setSeed(unit.id.toLong())
      for(i in 0..7) {
        SglDraw.drawTransform(
          u.x, u.y, u.hitSize / 2 + rate * u.hitSize / 2, 0f, Time.time + rand.random(360f)
        ) { x: Float, y: Float, r: Float ->
          val len = rand.random(u.hitSize / 4, u.hitSize / 1.5f)
          SglDraw.drawDiamond(x, y, len, len * 0.135f, r)
        }
      }
      Draw.reset()
    })
  }
}