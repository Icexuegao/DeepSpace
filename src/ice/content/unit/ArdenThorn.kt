package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.audio.ISounds
import ice.entities.bullet.BombBulletType
import ice.entities.bullet.RandomDamageBulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.graphics.TextureRegionDelegate
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.base.Entity
import ice.world.content.unit.weapon.ChargeWeapon
import ice.world.meta.IceEffects
import mindustry.entities.Effect
import mindustry.entities.pattern.ShootHelix
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Layer

class ArdenThorn : IceUnitType("ardenThorn", ArdenThornUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(
        zh_CN, "焚棘", "轻型侦察攻击机,配备独特的渐速式火力系统,机身尾部的两挺转管重机枪在开火时可持续提升射速,形成愈演愈烈的压制弹幕.头部则搭载了两门锁定式导弹发射器,用于精准打击轻型防御目标.虽定位为侦察单位,但其出色的滞空能力与双重火力配置,使其能在探查敌情的同時实施骚扰性攻击,成为战场上空难以驱离的刺眼存在"
      )
    }
    speed = 1.3f
    accel = 0.5f
    drag = 0.05f
    flying = true
    health = 2000f
    hitSize = 40f
    drawCell = false
    faceTarget = false
    rotateSpeed = 1.9f
    setWeapon("weapon1") {
      x = 11f
      y = 27f
      shootY += 1f
      reload = 30f
      recoil = 2f
      rotate = true
      layerOffset = -1f
      rotateSpeed = 4f
      shootSound = Sounds.shootMissile
      shoot = object : ShootHelix() {
        var bl = true
        override fun shoot(totalShots: Int, handler: BulletHandler, barrelIncrementer: Runnable?) {
          for (i in 0..<shots) {
            bl = !bl
            handler.shoot(0f, 0f, 0f, firstShotDelay + shotDelay * i) { b ->
              b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * if (bl) 1 else -1))
            }
          }
        }

        init {
          scl = 4f
          mag = 2f
        }
      }
      bullet = BombBulletType(80f, 8 * 3f).apply {
        sprite = "mine-bullet"
        width *= 2
        height *= 2
        drag = 0f
        collidesAir = true
        splashDamagePierce = true
        collidesTiles = true
        collides = true
        var sec = 30f
        speed = (sec * 8f) / 60f
        lifetime = (25f / sec) * 60f

        trailWidth = 2f
        trailLength = 13
        trailColor = IceColor.b4
        frontColor = IceColor.b5
        lightColor = IceColor.b5
        backColor = IceColor.b5
        hitEffect = MultiEffect(Effect(16f) { e ->
          IceEffects.rand.setSeed(e.id.toLong())
          val rad = splashDamageRadius
          Draw.color(Color.white, backColor, e.fin())
          val circleRad = e.fin(Interp.circleOut) * rad
          Lines.stroke(5 * e.fout())
          Lines.circle(e.x, e.y, circleRad)
          (0..3).forEach { i ->
            Tmp.v1.set(1f, 0f).setToRandomDirection(IceEffects.rand).scl(circleRad)
            IceEffects.drawFunc(
              e.x + Tmp.v1.x, e.y + Tmp.v1.y, IceEffects.rand.random(circleRad / 16, circleRad / 12) * e.fout(), IceEffects.rand.random(circleRad / 4, circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180
            )
          }

          Draw.blend(Blending.additive)
          Draw.z(Layer.effect + 0.1f)
          Fill.light(
            e.x, e.y, Lines.circleVertices(circleRad), circleRad, Color.clear, Tmp.c1.set(Draw.getColor()).a(e.fout())
          )
          Draw.blend()
          Draw.z(Layer.effect)
          Drawf.light(e.x, e.y, rad * e.fout(Interp.circleOut) * 4f, e.color, 0.7f)
        }, IceEffects.基础子弹击中特效)
        despawnEffect = hitEffect
        shootEffect = IceEffects.squareAngle(color1 = IceColor.b5, color2 = IceColor.b4)
      }
    }

    setWeaponT<ChargeWeapon>("weapon2") {
      x = -17f
      y = -8f
      rotate = true
      shootY += 3f
      reload = 4f
      recoil = 1f
      layerOffset = -2f
      rotateSpeed = 2.5f
      shootSound = ISounds.laserGun

      bullet = RandomDamageBulletType(20, 30, 7f).apply {
        pierceCap = 3
        frontColor = IceColor.b5
        lightColor = IceColor.b5
        backColor = IceColor.b5
        width = 4f
        height = 9f
        hitSize = 4f
        shootEffect = Effect(8f) { e ->
          Draw.color(IceColor.b5, IceColor.b4, e.fin())
          val w = 1f + 2 * e.fout()
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation + 30f)
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation + 15f)
          Drawf.tri(e.x, e.y, w, 4 * e.fout(), e.rotation)
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation - 15f)
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation - 30f)
        }
        hitEffect = IceEffects.基础子弹击中特效
        despawnEffect = hitEffect
      }
    }
  }

  class ArdenThornUnit : Entity() {
    companion object {
      val regions: TextureRegion by TextureRegionDelegate("ardenThorn-propeller".appendModName())
    }

    override fun drawBodyRegion(rotation: Float) {
      super.drawBodyRegion(rotation)
      drawRotorWing(13f, 10f, rotation)
    }

    override fun drawShadowRegion(x: Float, y: Float, rotation: Float) {
      super.drawShadowRegion(x, y, rotation)
      val rot = rotation
      val trnsx1 = Angles.trnsx(rot, 13f, 10f)
      val trnsy1 = Angles.trnsy(rot, 13f, 10f)
      val trnsx2 = Angles.trnsx(rot, -13f, 10f)
      val trnsy2 = Angles.trnsy(rot, -13f, 10f)
      val speed = Time.time * 5f * 6
      val ux = x + trnsx1
      val uy = y + trnsy1
      val nx = x + trnsx2
      val ny = y + trnsy2
      Draw.rect(regions, ux, uy, speed)
      Draw.rect(regions, nx, ny, -speed)
    }

    fun drawRotorWing(rx: Float, ry: Float, rotation: Float) {
      val rot = rotation
      val trnsx1 = Angles.trnsx(rot, rx, ry)
      val trnsy1 = Angles.trnsy(rot, rx, ry)
      val trnsx2 = Angles.trnsx(rot, -rx, ry)
      val trnsy2 = Angles.trnsy(rot, -rx, ry)
      val speed = Time.time * 5f * 6
      val ux = x + trnsx1
      val uy = y + trnsy1
      val nx = x + trnsx2
      val ny = y + trnsy2
      Draw.rect(regions, ux, uy, speed)
      Draw.rect(regions, nx, ny, -speed)
    }
  }
}