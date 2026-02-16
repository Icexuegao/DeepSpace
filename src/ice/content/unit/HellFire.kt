package ice.content.unit

import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.ExplosionBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.type.Weapon
import mindustry.type.unit.MissileUnitType

class HellFire : MissileUnitType("unit_hellFire") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "火狱")
    }

    health = 3600f
    hitSize = 17f
    armor = 8f
    speed = 6f
    lifetime = 400f
    maxRange = 6f
    rotateSpeed = 1f
    missileAccelTime = 60f
    engineColor = "FEB380".toColor()
    trailColor = "FEB380".toColor()
    outlineColor = "1F1F1F".toColor()
    engineLayer = 100f
    engineOffset = 12f
    engineSize = 3.1f
    trailLength = 18
    lowAltitude = true
    fogRadius = 6f

    parts.add(RegionPart().apply {
      suffix = "-heat"
      progress = DrawPart.PartProgress.life
      color = "F03B0E00".toColor()
      colorTo = "F03B0E".toColor()
    })
    abilities.add(MoveEffectAbility().apply {
      y = -12f
      interval = 4f
      rotation = 180f
      rotateEffect = true
      effect = ParticleEffect().apply {
        lifetime = 60f
        particles = 11
        sizeFrom = 8f
        sizeTo = 1f
        cone = 19f
        length = -50f
        interp = Interp.pow5Out
        sizeInterp = Interp.pow5In
        colorFrom = "FF5845AA".toColor()
        colorTo = "78787870".toColor()
      }
    })

    deathExplosionEffect = Fx.massiveExplosion

    weapons.add(Weapon().apply {
      shake = 8f
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.shootBeamPlasma
      bullet = ExplosionBulletType().apply {
        damage = 2160f
        status = IStatus.熔融
        statusDuration = 180f
        hitSound = Sounds.explosionPlasmaSmall
        hitEffect = MultiEffect().apply {
          effects = arrayOf(ParticleEffect().apply {
            lifetime = 180f
            particles = 10
            sizeFrom = 24f
            sizeTo = 10f
            length = 100f
            baseLength = 5f
            interp = Interp.circleOut
            colorFrom = "FF5845".toColor()
            colorTo = "FF584500".toColor()
          }, ParticleEffect().apply {
            lifetime = 180f
            particles = 10
            sizeFrom = 30f
            sizeTo = 23f
            length = 130f
            baseLength = 10f
            interp = Interp.circleOut
            colorFrom = "FF5845".toColor()
            colorTo = "FF584500".toColor()
          })
        }
        despawnEffect = MultiEffect().apply {
          effects = arrayOf(ParticleEffect().apply {
            particles = 20
            lifetime = 180f
            sizeFrom = 24f
            sizeTo = 10f
            length = 80f
            baseLength = 5f
            interp = Interp.circleOut
            colorFrom = "FF5845".toColor()
            colorTo = "FF584500".toColor()
          }, ParticleEffect().apply {
            particles = 20
            lifetime = 180f
            sizeFrom = 30f
            sizeTo = 23f
            length = 100f
            baseLength = 10f
            interp = Interp.circleOut
            colorFrom = "FF5845".toColor()
            colorTo = "FF584500".toColor()
          }, WaveEffect().apply {
            lifetime = 45f
            sizeFrom = 0f
            sizeTo = 70f
            strokeFrom = 6f
            strokeTo = 0f
            colorFrom = "FF6666".toColor()
            colorTo = "FF5845".toColor()
          })
        }
        splashDamageRadius = 60f
        splashDamage = 1080f
        fragBullets = 8
        fragLifeMin = 0.3f
        fragBullet = BasicBulletType().apply {
          sprite = "circle-bullet"
          damage = 475f
          lifetime = 20f
          speed = 6f
          shrinkY = 0f
          height = 12f
          width = 12f
          absorbable = false
          status = IStatus.熔融
          statusDuration = 60f
          homingRange = 150f
          homingPower = 0.06f
          splashDamage = 285f
          splashDamageRadius = 20f
          trailColor = "FF5845".toColor()
          trailLength = 6
          trailWidth = 4f
          trailEffect = Fx.none
          hitEffect = ParticleEffect().apply {
            particles = 10
            lifetime = 46f
            sizeFrom = 3f
            sizeTo = 0f
            cone = 360f
            length = 60f
            interp = Interp.circleOut
            colorFrom = "FF6666".toColor()
            colorTo = "FF5845".toColor()
          }
          despawnEffect = ParticleEffect().apply {
            particles = 10
            lifetime = 46f
            sizeFrom = 3f
            sizeTo = 0f
            cone = 360f
            length = 60f
            interp = Interp.circleOut
            colorFrom = "FF6666".toColor()
            colorTo = "FF5845".toColor()
          }
          frontColor = "FF6666".toColor()
          backColor = "FF5845".toColor()
          fragBullets = 1
          fragBullet = ShrapnelBulletType().apply {
            damage = 325f
            lifetime = 36f
            length = 122f
            width = 30f
            serrations = 0
            lightColor = "FF6666".toColor()
            fromColor = "FF6666".toColor()
            toColor = "FF5845".toColor()
          }
        }
      }
    })
  }
}