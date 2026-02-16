package ice.content.unit

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.BombBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import singularity.world.blocks.turrets.LightningBulletType

class Missionary : IceUnitType("missionary") {
  init {
    BaseBundle.bundle {
      desc(
        zh_CN, "传教者", "重型空中火力平台,枢机教廷[净化之翼]军团,搭载4门防空拦截的磁轨速射炮,2门圣裁等离子爆裂炮,以及2门对地穿甲的粒子冲击炮形成全方位立体火力网", "枢机的例行祷告"
      )
    }
    speed = 0.9f
    flying = true
    hitSize = 90f
    health = 40000f
    targetAir = true
    faceTarget = true
    lowAltitude = true
    rotateSpeed = 0.6f
    targetGround = true
    forceMultiTarget = true
    engines.add(IUnitEngine(30f, -65f, 8f, -90f, 6f))
    engines.add(IUnitEngine(0f, -80f, 8f, -90f))
    engines.add(IUnitEngine(-30f, -65f, 8f, -90f, 6f))
    setWeapon("weapon1") {
      x = -34.75f
      y = -20.75f
      top = true
      rotate = true
      mirror = true
      shootY = 8f
      reload = 30f
      rotateSpeed = 10f
      shootSound = Sounds.shootSmite
      shoot = ShootPattern().apply {
        shotDelay = 30f
        shots = 3
        shotDelay = 10f
      }
      bullet = BasicBulletType(7f, 250f, "large-orb").apply {
        width = 17f
        height = 21f
        hitSize = 8f
        shootEffect = MultiEffect(Fx.shootTitan, Fx.colorSparkBig, object : WaveEffect() {
          init {
            colorTo = Pal.accent
            colorFrom = colorTo
            lifetime = 12f
            sizeTo = 20f
            strokeFrom = 3f
            strokeTo = 0.3f
          }
        })
        smokeEffect = Fx.shootSmokeSmite
        ammoMultiplier = 1f
        pierceCap = 4
        pierce = true
        pierceBuilding = true
        trailColor = Pal.accent
        backColor = trailColor
        hitColor = backColor
        frontColor = Color.white
        trailWidth = 2.8f
        trailLength = 9
        hitEffect = Fx.hitBulletColor
        buildingDamageMultiplier = 0.3f
        despawnEffect = MultiEffect(Fx.hitBulletColor, object : WaveEffect() {
          init {
            sizeTo = 30f
            colorTo = Pal.accent
            colorFrom = colorTo
            lifetime = 12f
          }
        })
        trailRotation = true
        trailEffect = Fx.disperseTrail
        trailInterval = 3f
        bulletInterval = 3f
        intervalBullet = object : LightningBulletType() {
          init {
            damage = 30f
            collidesAir = false
            lightningColor = Pal.accent
            lightningLength = 5
            lightningLengthRand = 10
            buildingDamageMultiplier = 0.5f
            lightningType = object : BulletType(0.0001f, 0f) {
              init {
                lifetime = Fx.lightning.lifetime
                hitEffect = Fx.hitLancer
                despawnEffect = Fx.none
                status = StatusEffects.shocked
                statusDuration = 10f
                hittable = false
                lightColor = Color.white
                buildingDamageMultiplier = 0.25f
              }
            }
          }
        }
      }
    }
    setWeapon("weapon2") {
      x = -20.5f
      y = 4f
      rotate = true
      reload = 60f
      shootSound = Sounds.shootMalign
      bullet = object : BombBulletType(500f, 64f) {
        var i = 0f
        override fun update(b: Bullet) {
          super.update(b)
          i += b.time()
        }

        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.color(Pal.accent)
          Lines.stroke(1 - Interp.pow3Out.apply(b.fin()) * 3)
          Lines.poly(b.x, b.y, 3, Interp.pow3Out.apply(1 - b.fin()) * 24, i)
        }
      }.apply {
        width = 10f
        height = 10f
        speed = 12f
        drag = 0.05f
        lifetime = 120f
        despawnEffect = WaveEffect().apply {
          colorTo = Pal.accent
          colorFrom = Pal.accent
          sizeFrom = 0f
          sizeTo = 8 * 5f
          lifetime = 30f
        }
        shootEffect = MultiEffect(Fx.shootTitan, ParticleEffect().apply {
          lifetime = 20f
          colorFrom = Pal.accent
          sizeFrom = 2f
          sizeTo = 8f
          length = 30f
          cone = 40f
          line = true
        })
        intervalSpread = 300f
        bulletInterval = 10f
        intervalBullet = BasicBulletType(2f, 30f, "mine-bullet").apply {
          pierce = true
          pierceBuilding = true
          status = IStatus.破甲II
          trailChance = 0.2f
          trailEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 24f
            sides = 3
            colorFrom = Pal.accent
          }
        }
      }
    }
    setWeapon("weapon3") {
      x = -16f
      y = 29f
      top = true
      rotate = true
      mirror = true
      reload = 6f
      shoot.apply {
        shotDelay = 15f
      }
      bullet = BasicBulletType(8f, 23f).apply {
        trailChance = 0.25f
        trailLength = 12
        trailWidth = 3.2f
        trailColor = Pal.accent
        hitEffect = WaveEffect().apply {
          lifetime = 15f
          sizeTo = 30f
          strokeFrom = 4f
          colorFrom = Pal.accent
          colorTo = Pal.accent
        }
        despawnEffect = hitEffect
        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 360f
          length = 23f
          sizeInterp = Interp.pow10In
          colorFrom = Pal.accent
          colorTo = Pal.accent
        }
      }
    }.copyAdd {
      x = -13f
      y = 45f
    }
  }
}
