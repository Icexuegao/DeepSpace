package ice.entities.bullet.base

import arc.Core
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.Time
import ice.world.meta.IceStatValues.sep
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Lightning
import mindustry.gen.Bullet
import mindustry.gen.Icon
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit

open class BulletType(speed: Float = 1f, damage: Float = 1f) : mindustry.entities.bullet.BulletType(speed, damage) {
  open fun setDamageStats(bt: Table) {
    if (damage > 0 && (collides || splashDamage <= 0)) {
      if (continuousDamage() > 0) {
        bt.add(Core.bundle.format("bullet.damage", continuousDamage()) + StatUnit.perSecond.localized())
      } else {
        bt.add(Core.bundle.format("bullet.damage", damage))
      }
    }
  }

  fun setStats(table: Table) {
    table.left().defaults().padRight(3f).left()
    setDamageStats(table)

    /*  if (this is HeatBulletType) {
          table.row()
          table.table(Cons { t: Table? ->
              t!!.left().defaults().padRight(3f).left()
              t.image(OtherContents.meltdown.uiIcon).size(25f).scaling(Scaling.fit)
              t.add(Core.bundle.format("infos.heatAmmo", Strings.autoFixed(bullet.meltDownTime / 60, 1),
                  Strings.autoFixed(bullet.melDamageScl * 60, 1),
                  if (bullet.maxExDamage > 0) bullet.maxExDamage else Math.max(bullet.damage, bullet.splashDamage)))
          })
      }*/

    if (buildingDamageMultiplier != 1f) {
      sep(table, Core.bundle.format("bullet.buildingdamage", (buildingDamageMultiplier * 100).toInt()))
    }

    if (rangeChange != 0f) {
      sep(
        table, Core.bundle.format(
          "bullet.range", (if (rangeChange > 0) "+" else "-") + Strings.autoFixed(rangeChange / Vars.tilesize, 1)
        )
      )
    }

    if (splashDamage > 0) {
      sep(
        table, Core.bundle.format(
          "bullet.splashdamage", splashDamage.toInt(), Strings.fixed(splashDamageRadius / Vars.tilesize, 1)
        )
      )
    }

    if (knockback > 0) {
      sep(table, Core.bundle.format("bullet.knockback", Strings.autoFixed(knockback, 2)))
    }

    if (healPercent > 0f) {
      sep(table, Core.bundle.format("bullet.healpercent", Strings.autoFixed(healPercent, 2)))
    }

    if (healAmount > 0f) {
      sep(table, Core.bundle.format("bullet.healamount", Strings.autoFixed(healAmount, 2)))
    }

    if (pierce || pierceCap != -1) {
      sep(
        table, if (pierceCap == -1) "@bullet.infinitepierce" else Core.bundle.format("bullet.pierce", pierceCap)
      )
    }

    if (incendAmount > 0) {
      sep(table, "@bullet.incendiary")
    }

    if (homingPower > 0.01f) {
      sep(table, "@bullet.homing")
    }

    if (lightning > 0) {
      sep(
        table, Core.bundle.format("bullet.lightning", lightning, if (lightningDamage < 0) damage else lightningDamage)
      )
    }

    if (pierceArmor) {
      sep(table, "@bullet.armorpierce")
    }

    if (status !== StatusEffects.none && status != null) {
      sep(
        table, (if (status.minfo.mod == null) status.emoji() else "") + "[stat]" + status.localizedName + "[lightgray] ~ " + "[stat]" + Strings.autoFixed(
          statusDuration / 60f, 1
        ) + "[lightgray] " + Core.bundle.get("unit.seconds")
      )
    }

    if (intervalBullet != null) {
      table.row()
      val ic = Table()
      if (intervalBullet is BulletType) {
        (intervalBullet as BulletType).setStats(ic)
      } else {
        table.add("警告 分裂子弹类型不正确${intervalBullet != null}")
      }
      val coll = Collapser(ic, true)
      coll.setDuration(0.1f)
      table.table {
        it.left().defaults().left()
        it.add(
          Core.bundle.format("bullet.interval", Strings.autoFixed(intervalBullets / bulletInterval * 60, 2))
        )
        it.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }.update { i ->
          i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen)
        }.size(8f).padLeft(16f).expandX()
      }
      table.row()
      table.add(coll).padLeft(16f)
    }
    if (fragBullet != null) {
      table.row()
      val ic = Table()
      if (fragBullet is BulletType) {
        (fragBullet as BulletType).setStats(ic)
      } else {
        table.add("警告 破片子弹类型不正确${fragBullet::class.simpleName}").row()
      }
      val coll = Collapser(ic, true)
      coll.setDuration(0.1f)
      table.table { ft ->
        ft.left().defaults().left()
        ft.add(Core.bundle.format("bullet.frags", fragBullets))
        ft.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }.update { i ->
          i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen)
        }.size(8f).padLeft(16f).expandX()
      }
      table.row()
      table.add(coll).padLeft(16f)
    }
    table.row()
  }

  override fun hit(b: Bullet?, x: Float, y: Float, createFrags: Boolean) {
    hitEffect.at(x, y, b!!.rotation(), hitColor)
    hitSound.at(x, y, hitSoundPitch, hitSoundVolume)

    Effect.shake(hitShake, hitShake, b)

    if (fragOnHit) {
      if (delayFrags && fragBullet != null && fragBullet.delayFrags) {
        Time.run(0f, Runnable { createFrags(b, x, y) })
      } else {
        createFrags(b, x, y)
      }
    }
    createPuddles(b, x, y)
    createIncend(b, x, y)
    createUnits(b, x, y)

    if (suppressionRange > 0) {
      //bullets are pooled, require separate Vec2 instance
      Damage.applySuppression(b.team, b.x, b.y, suppressionRange, suppressionDuration, 0f, suppressionEffectChance, Vec2(b.x, b.y), suppressColor)
    }

    createSplashDamage(b, x, y)

    for (i in 0..<lightning) {
      Lightning.create(b, lightningColor, if (lightningDamage < 0) damage else lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone / 2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand))
    }
  }
  override fun removed(b: Bullet) {
    if (trailLength > 0 && b.trail != null && b.trail.size() > 0) {
      Fx.trailFade.at(b.x, b.y, trailWidth, trailColor, b.trail.copy())
    }
  }

  override fun despawned(b: Bullet) {
    if (despawnHit) {
      hit(b)
    } else {
      createUnits(b, b.x, b.y)
    }

    if (!fragOnHit) {
      createFrags(b, b.x, b.y)
    }

    despawnEffect.at(b.x, b.y, b.rotation(), hitColor)
    despawnSound.at(b)

    Effect.shake(despawnShake, despawnShake, b)
  }
}
