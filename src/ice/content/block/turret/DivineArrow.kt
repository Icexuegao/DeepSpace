package ice.content.block.turret

import arc.graphics.Color
import arc.math.Mathf
import arc.util.Time
import ice.content.IItems
import ice.entities.bullet.MissileBulletType
import ice.graphics.IceColor
import ice.world.draw.part.IcePartProgress
import ice.world.meta.IceEffects
import mindustry.content.StatusEffects
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootAlternate
import mindustry.gen.Sounds
import mindustry.type.Category
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret

class DivineArrow :SglTurret("divineArrow") {
  init {
    localization {
      zh_CN {
        localizedName = "神矢"
        description="双管交替发射小型制导导弹,弹体沿正弦轨迹蛇行前进,命中后造成范围伤害"
      }
    }
    size = 2
    health = 1000
    squareSprite = false
    requirements(Category.turret, IItems.铬铁矿, 10, IItems.低碳钢, 20)
    recoils = 2

    drawers = DrawSglTurret().apply {
      for(i in 0..1) {
        parts.add(object :RegionPart("-" + (if (i == 0) "l" else "r")) {
          init {
            progress = PartProgress.recoil
            recoilIndex = i
            moveY = -1.5f
            under = true
          }
        })
        parts.add(RegionPart())
      }
      parts.add(ShapePart().apply {
        hollow = true
        radius = 4f
        layer = 110f
        sides = 4
        y = -4f
        color = IceColor.b4
        rotateSpeed = 2f
        progress = DrawPart.PartProgress.recoil
      })
      parts.add(ShapePart().apply {
        hollow = true
        radius = 0f
        radiusTo = 4f
        layer = 110f
        sides = 4
        stroke = 0.5f
        rotateSpeed = 2f
        y = -4f
        color = IceColor.b4
        progress = IcePartProgress { p: DrawPart.PartParams ->
          DrawPart.PartProgress.warmup.get(p) * ((Time.time / 15) % 1)
        }
      })
    }
    shoot = object :ShootAlternate() {
      var scl: Float = 2f
      var mag: Float = 1.5f
      var offset: Float = Mathf.PI * 1.25f
      override fun shoot(totalShots: Int, handler: BulletHandler, barrelIncrementer: Runnable?) {
        for(i in 0..<shots) {
          for(sign in Mathf.signs) {
            val index = ((totalShots + i + barrelOffset) % barrels) - (barrels - 1) / 2f
            handler.shoot(index * spread * -Mathf.sign(mirror), 0f, 0f, firstShotDelay + shotDelay * i) { b ->
              b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * sign))
            }
          }
          barrelIncrementer?.run()
        }
      }
    }.apply {
      barrelOffset = 8
      spread = 5f
      shots = 2
      shotDelay = 15f
    }
    shootSound = Sounds.shootMissile
    shootY = 6f
  }

  override fun setAmmo() {
    newAmmo(getBulletType(15f, 60f, 2.5f * 8f, IItems.硫化合物.color).apply {
      status = StatusEffects.burning
      statusDuration = 10 * 60f
    }).setReloadAmount(2)
    consume!!.apply {
      time(30f)
      items(IItems.硫化合物, 1)
    }

    newAmmo(getBulletType(10f, 65f, 3.8f * 8f, IItems.爆炸化合物.color)).setReloadAmount(3)
    consume!!.apply {
      time(30f)
      items(IItems.爆炸化合物, 1)
    }

    newAmmo(getBulletType(40f, 50f, 2.5f * 8f, IItems.铈锭.color).apply {
      status = StatusEffects.burning
      statusDuration = 5 * 60f
    }).setReloadAmount(2)
    consume!!.apply {
      time(30f)
      items(IItems.铈锭, 1)
    }

    newAmmo(getBulletType(15f, 110f, 4.5f * 8f, IItems.铈凝块.color).apply {
      reloadMultiplier = 0.7f
      status = StatusEffects.burning
      statusDuration = 15 * 60f
    }).setReloadAmount(2)
    consume!!.apply {
      time(30f)
      items(IItems.铈凝块, 1)
    }
  }

  fun getBulletType(damage: Float, splashDamage: Float, splashDamageRadius: Float, color: Color = IceColor.b4) =
    MissileBulletType(6f, damage).apply {
      this.splashDamageRadius = splashDamageRadius
      this.splashDamage = splashDamage
      lifetime = 45f
      trailLength = 20
      trailWidth = 1.5f
      trailColor = color
      backColor = color
      hitColor = color
      frontColor = color
      shootEffect = IceEffects.squareAngle(range = 30f, color1 = color, color2 = Color.white)
      despawnEffect = IceEffects.blastExplosion(color)
      hitEffect = despawnEffect
    }
}