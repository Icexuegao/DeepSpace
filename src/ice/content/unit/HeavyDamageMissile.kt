package ice.content.unit

import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.ExplosionBulletType
import ice.entities.effect.MultiEffect
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Weapon
import mindustry.type.unit.MissileUnitType

class HeavyDamageMissile : MissileUnitType("unit_heavyDamageMissile") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "重创")
    }

    health = 3000f
    hitSize = 8f
    speed = 6.6f
    lifetime = 230f
    rotateSpeed = 1f
    missileAccelTime = 60f
    engineColor = "FEB380".toColor()
    trailColor = "FEB380".toColor()
    engineLayer = 110f
    engineOffset = 16f
    engineSize = 5.3f
    trailLength = 22
    lowAltitude = true

    weapons.add(Weapon().apply {
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.none
      bullet = ExplosionBulletType().apply {
        splashDamageRadius = 80f
        splashDamage = 2000f
        hitEffect = MultiEffect(
          WaveEffect().apply {
            lifetime = 15f
            sizeFrom = 0f
            sizeTo = 90f
            strokeFrom = 7.5f
            strokeTo = 0f
            colorFrom = "A9D8FF".toColor()
            colorTo = "66B1FF".toColor()
          },
          ParticleEffect().apply {
            offset = 30f
            particles = 10
            lifetime = 20f
            baseLength = 85f
            length = 120f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            cone = -360f
            line = true
            strokeFrom = 1.5f
            strokeTo = 1.5f
            lenFrom = 8f
            lenTo = 8f
            lightColor = "A9D8FF".toColor()
            colorFrom = "E1F2FF".toColor()
            colorTo = "A9D8FF".toColor()
          },
          ParticleEffect().apply {
            offset = 30f
            particles = 10
            lifetime = 25f
            baseLength = 85f
            length = 100f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            cone = -360f
            line = true
            strokeFrom = 1.5f
            strokeTo = 1.5f
            lenFrom = 8f
            lenTo = 8f
            lightColor = "A9D8FF".toColor()
            colorFrom = "E1F2FF".toColor()
            colorTo = "A9D8FF".toColor()
          }
        )
        fragBullets = 5
        fragRandomSpread = 0f
        fragSpread = 70f
        fragLifeMin = 1f
        fragBullet = ContinuousFlameBulletType().apply {
          colors = arrayOf(
            "66B1FF8C".toColor(),
            "66B1FFB2".toColor(),
            "66B1FFCC".toColor(),
            "A9D8FF".toColor(),
            "FFFFFFCC".toColor()
          )
          damage = 70f
          width = 14.5f
          drawFlare = false
          hitEffect = ParticleEffect().apply {
            line = true
            particles = 7
            lifetime = 15f
            length = 65f
            cone = -360f
            strokeFrom = 2.5f
            strokeTo = 0f
            lenFrom = 8f
            lenTo = 0f
            colorFrom = "A9D8FF".toColor()
            colorTo = "66B1FF".toColor()
          }
          length = 150f
          lifetime = 25f
          status = IStatus.电链
          statusDuration = 150f
        }
      }
    })

    abilities.add(MoveEffectAbility().apply {
      y = -17f
      interval = 3f
      rotation = 180f
      rotateEffect = true
      effect = ParticleEffect().apply {
        particles = 7
        sizeFrom = 6f
        sizeTo = 1f
        length = 50f
        lifetime = 60f
        lightOpacity = 0f
        interp = Interp.circleOut
        sizeInterp = Interp.pow5In
        colorFrom = "66B1FFAa".toColor()
        colorTo = "78787870".toColor()
        cone = 15f
      }
    })
  }
}