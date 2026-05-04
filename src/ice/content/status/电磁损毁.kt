package ice.content.status

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.math.Mathf
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.content.IStatus
import ice.content.IStatus.BanedAbility
import ice.world.content.status.IceStatusEffect
import mindustry.entities.units.StatusEntry
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.world.meta.SglStat

class 电磁损毁 :IceStatusEffect("emp_damaged") {
  init {
    Events.on(ice.game.EventType.BulletInitEvent::class.java) { e ->
      if (e.bullet.owner is Unit) {
        val u = e.bullet.owner as Unit
        val bullet = e.bullet
        bullet.apply {
          if (u.hasEffect(IStatus.电磁损毁)) {
            val rot = Mathf.random(-45, 45).toFloat()
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
        localizedName = "电磁损毁"
        description = "单位的系统中枢及各周边电子设备严重损毁,火控核心几乎失效,所有功能设备完全失效,近乎废铁"
      }
    }
    color = Pal.accent
    speedMultiplier = 0.5f
    buildSpeedMultiplier = 0.1f
    reloadMultiplier = 0.6f
    damageMultiplier = 0.7f
  }

  override fun update(unit: Unit, entry: StatusEntry) {
    super.update(unit, entry)
    if (Sgl.empHealth.empDamaged(unit)) {
      if (unit.getDuration(this) <= 60) {
        unit.apply(this, 60f)
      } else {
        unit.speedMultiplier = 0.01f
        unit.reloadMultiplier = 0f
        unit.buildSpeedMultiplier = 0f
      }

      unit.shield = 0f
      unit.damageContinuousPierce((1 - Sgl.empHealth.healthPresent(unit)) * Sgl.empHealth.get(unit).model!!.empContinuousDamage)

      for(i in unit.abilities.indices) {
        if (unit.abilities[i] !is BanedAbility) {
          val baned = Pools.obtain(BanedAbility::class.java, ::BanedAbility)
          baned.index = i
          baned.masked = unit.abilities[i]
          unit.abilities[i] = baned
        }
      }
    } else {
      unit.unapply(this)
    }
  }

  override fun setStats() {
    super.setStats()
    stats.add(SglStat.effect) { t ->
      t.defaults().left().padLeft(5f)
      t.row()
      t.add(Core.bundle.format("data.bulletDeflectAngle", "45" + StatUnit.degrees.localized())).color(Color.lightGray)
      t.row()
      t.add(Core.bundle.get("infos.banedAbilities")).color(Color.lightGray)
      t.row()
      t.add(Core.bundle.get("infos.empDamagedInfo"))
    }
  }
}