package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Rect
import arc.struct.Seq
import arc.util.Tmp
import ice.audio.ISounds
import ice.entities.bullet.AngleBulletType
import ice.entities.bullet.ChainBulletType
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.TrailFadeBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.Mover
import mindustry.entities.part.DrawPart
import mindustry.entities.part.FlarePart
import mindustry.game.Team
import mindustry.gen.Bullet
import mindustry.gen.Entityc
import mindustry.gen.Sounds
import mindustry.gen.Teamc
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import singularity.world.SglFx
import kotlin.math.min

class BreakUp : IceUnitType("breakUp") {
  init {
    bundle {
      desc(zh_CN, "断业", "断业是神殿[净罪计划]的产物,其装甲内层熔铸了经神祝圣的暮光合金,主炮能对建筑与重甲单位造成毁灭性伤害,能撕裂红雾中的畸变体集群", "帝国腐朽的装甲部队节节败退,唯有枢机的神术能短暂驱散腐化,帝国残部讥讽其为伪神的铁棺材,但无人能否认——当它的履带碾过焦土时,连红雾都会为之退散")
    }
    speed = 0.48f
    armor = 26f
    health = 22000f
    hitSize = 75f
    crushDamage = 25f / 5f
    rotateSpeed = 0.8f
    treadPullOffset = 1
    squareShape = true
    omniMovement = false
    rotateMoveFirst = true
    outlineColor = Color.valueOf("24222B")
    treadRects = arrayOf(Rect(70f - (400 / 2), 53f - (500 / 2), 83f, 394f))
    setWeapon("weapon1") {
      x = 0f
      y = 11f
      shake = 4f
      shootY += 16f
      mirror = false
      rotate = true
      recoil = 4f
      reload = 240f
      shootCone = 0f
      rotateSpeed = 0.5f
      cooldownTime = 38f * 2
      shoot.firstShotDelay = 80f
      shootSound = ISounds.laser2
      parentizeEffects = true
      val laserBulletTypelength = 400f
      val bullet2 = object : ChainBulletType(12f) {
        override fun init(b: Bullet) {
          (1..3).forEach { _ ->
            super.init(b)
          }
        }
      }.apply {
        collidesGround = false
        length = laserBulletTypelength
        hitColor = IceColor.b4.cpy().a(0.4f).also { lightningColor = it }.also { lightColor = it }
      }
      val bullet1 = object : LaserBulletType(1200f) {
        override fun create(owner: Entityc?, shooter: Entityc?, team: Team?, x: Float, y: Float, angle: Float, damage: Float, velocityScl: Float, lifetimeScl: Float, data: Any?, mover: Mover?, aimX: Float, aimY: Float, target: Teamc?): Bullet? {
          bullet2.create(
            owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target
          )
          return super.create(
            owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target
          )
        }
      }.apply {
        colors = arrayOf(IceColor.b4.cpy().a(0.4f), IceColor.b4, Color.white)

        width = 45f
        lifetime = 30f
        sideAngle = 60f
        sideLength = 35f
        collidesAir = true
        collidesGround = true
        hitEffect = Fx.hitLancer
        length = laserBulletTypelength
        buildingDamageMultiplier = 1.25f
        chargeSound = ISounds.forceHoldingLaser2
        shootEffect = IceEffects.lancerLaserShoot
        chargeEffect = MultiEffect(
          Effect(38f * 2) { e ->
            IceEffects.unitMountSXY(e.data, this@setWeapon) { bulletX, bulletY ->
              Draw.color(IceColor.b4)
              Angles.randLenVectors(
                e.id.toLong(), 20, 1f + 40f * e.fout(), e.rotation, 120f
              ) { x: Float, y: Float ->
                Lines.lineAngle(bulletX + x, bulletY + y, Mathf.angle(x, y), e.fslope() * 3f + 1f)
              }
            }
          },
          Effect(45f * 2) { e: EffectContainer ->
            IceEffects.unitMountSXY(e.data, this@setWeapon) { bulletX, bulletY ->
              val margin = 1f - Mathf.curve(e.fin(), 0.9f)
              val fin = min(margin, e.fin())
              Draw.color(IceColor.b4)
              Fill.circle(bulletX, bulletY, fin * 6f)
              Draw.color()
              Fill.circle(bulletX, bulletY, fin * 4f)
            }
          },
        )
      }


      bullet = bullet1
    }
    setWeapon("weapon2") {
      shoot.apply {
        shots = 3
        shotDelay = 10f
      }
      x = 0f
      y = -30f
      shootY = 8f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      reload = 150f
      recoil = 4f
      shootSound = ISounds.highExplosiveShell
      bullet = AngleBulletType(4f, 2f, 180f, 2f).apply {
        width = 8f
        height = 8f
        knockback = 0.5f
        shootEffect = Effect(32f) { e: EffectContainer ->
          Draw.color(Color.white, IceColor.b4, e.fin())
          Fx.rand.setSeed(e.id.toLong())
          (0..8).forEach { i ->
            val rot = e.rotation + Fx.rand.range(26f)
            Fx.v.trns(rot, Fx.rand.random(e.finpow() * 30f))
            Fill.poly(e.x + Fx.v.x, e.y + Fx.v.y, 4, e.fout() * 4f + 0.2f, Fx.rand.random(360f))
          }
        }
        parts = Seq.with(FlarePart().apply {
          followRotation = true
          rotMove = 180f
          progress = DrawPart.PartProgress.life
          color1 = IceColor.b4
          stroke = 6f
          radius = 5f
          radiusTo = 30f
        })
      }
      bullet = BasicBulletType().apply {
        val rand = Rand()

        speed = 13f
        lifetime = 60 * 2f
        val layer1 = Effect(300f, 1600f) { e: EffectContainer ->
          val rad = 150f
          rand.setSeed(e.id.toLong())
          Draw.color(Color.white, e.color, e.fin() + 0.6f)
          val circleRad = e.fin(Interp.circleOut) * rad * 4f
          Lines.stroke(12 * e.fout())
          Lines.circle(e.x, e.y, circleRad)
          (0..23).forEach { i ->
            Tmp.v1.set(1f, 0f).setToRandomDirection(rand).scl(circleRad)
            IceEffects.drawFunc(
              e.x + Tmp.v1.x, e.y + Tmp.v1.y, rand.random(circleRad / 16, circleRad / 12) * e.fout(), rand.random(circleRad / 4, circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180
            )
          }

          Draw.blend(Blending.additive)
          Draw.z(Layer.effect + 0.1f)
          Fill.light(
            e.x, e.y, Lines.circleVertices(circleRad), circleRad, Color.clear, Tmp.c1.set(Draw.getColor()).a(e.fout(Interp.pow10Out))
          )
          Draw.blend()
          Draw.z(Layer.effect)
          Drawf.light(e.x, e.y, rad * e.fout(Interp.circleOut) * 4f, e.color, 0.7f)
        }
        despawnEffect = layer1
      }
    }
    setWeapon("weapon3") {
      x = 28f
      y = -25f
      rotate = true
      reload = 30f
      recoils = 3
      rotateSpeed = 3f
      shootSound = Sounds.shootBeamPlasma
      bullet = TrailFadeBulletType(19f, 200f).apply {
        lifetime = 15f
        trailLength = 10
        trailWidth = 1.6f

        trailColor = IceColor.b4
        trailLength = 20
        trailWidth = 2.4f
        trailEffect = SglFx.trailParticle
        trailChance = 0.16f

        tracerStroke -= 0.3f
        keepVelocity = true
        tracerSpacing = 10f
        tracerUpdateSpacing *= 1.25f
        lightningColor = IceColor.b4
        lightColor = lightningColor
        backColor = lightColor
        hitColor = backColor
        trailColor = IceColor.b4
        frontColor = IceColor.b4
        width = 9f
        height = 9f
        hitSound = Sounds.shootBeamPlasma
        hitShake = 5f
        despawnShake = hitShake
        pierceArmor = true
        pierceCap = 4

        splashDamage = 20f
        splashDamageRadius = 24f
        splashDamagePierce = true
        collidesAir = true
        collidesGround = true
        shootEffect = MultiEffect(
          IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b4), IceEffects.lightningShoot()
        )
        hitEffect = MultiEffect(IceEffects.square(IceColor.b4, length = 16f, size = 4f), Effect(15f) { e ->
          val rad = 5f
          e.color = IceColor.b4
          IceEffects.rand.setSeed(e.id.toLong())
          Draw.color(Color.white, e.color, e.fin() + 0.6f)
          val circleRad = e.fin(Interp.circleOut) * rad * 4f
          Lines.stroke(3 * e.fout())
          Lines.circle(e.x, e.y, circleRad)
        })
        despawnEffect = hitEffect
      }
      shootY += 3f
    }
    setWeapon("weapon4") {
      x = 25.5f
      y = 10f
    }
    setWeapon("weapon4") {
      x = 25.5f
      y = 32f
    }
  }
}