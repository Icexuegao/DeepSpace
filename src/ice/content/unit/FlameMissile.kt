package ice.content.unit

import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.ExplosionBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Weapon
import mindustry.type.unit.MissileUnitType

class FlameMissile : MissileUnitType("unit_flameMissile") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "火苗")
    }

    health = 435f
    hitSize = 11f
    armor = 3f
    speed = 6.6f
    lifetime = 230f
    rotateSpeed = 1f
    missileAccelTime = 60f
    engineColor = "FEB380".toColor()
    trailColor = "FEB380".toColor()
    engineLayer = 110f
    engineOffset = 10f
    engineSize = 3f
    trailLength = 22
    lowAltitude = true

    weapons.add(Weapon().apply {
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.none
      bullet = ExplosionBulletType().apply {
        splashDamageRadius = 60f
        splashDamage = 600f
        hitEffect = MultiEffect(
          WaveEffect().apply {
            lifetime = 15f
            sizeFrom = 0f
            sizeTo = 90f
            strokeFrom = 7.5f
            strokeTo = 0f
            colorFrom = "FF8663".toColor()
            colorTo = "FF5845".toColor()
          },
          ParticleEffect().apply {
            particles = 10
            lifetime = 20f
            length = 85f
            sizeFrom = 6f
            sizeTo = 0f
            cone = 360f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            lightColor = "FF8663".toColor()
            colorFrom = "FF8663".toColor()
            colorTo = "FF5845".toColor()
          },
          ParticleEffect().apply {
            particles = 10
            lifetime = 25f
            line = true
            strokeFrom = 1.5f
            strokeTo = 0f
            lenFrom = 8f
            lenTo = 0f
            cone = 360f
            length = 100f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            lightColor = "FF8663".toColor()
            colorFrom = "FF8663".toColor()
            colorTo = "FF5845".toColor()
          }
        )
        fragBullets = 5
        fragVelocityMin = 0.5f
        fragBullet = BasicBulletType(4f,0f,"crystal").apply {
          lifetime = 60f
          drag = 0.025f
          width = 8f
          height = 12f
          shrinkY = 0f
          trailWidth = 2f
          trailLength = 8
          trailColor = "FF8663".toColor()
          frontColor = "FF8663".toColor()
          backColor = "FF5845".toColor()
          status = IStatus.湍能
          statusDuration = 150f
          homingRange = 60f
          homingPower = 0.08f
          splashDamage = 225f
          splashDamageRadius = 40f
          hitEffect = ParticleEffect().apply {
            particles = 7
            lifetime = 15f
            line = true
            strokeFrom = 2.5f
            strokeTo = 0f
            lenFrom = 8f
            lenTo = 0f
            cone = 360f
            length = 65f
            colorFrom = "FF8663".toColor()
            colorTo = "FF5845".toColor()
          }
          despawnEffect = ParticleEffect().apply {
            particles = 7
            lifetime = 15f
            line = true
            strokeFrom = 2.5f
            strokeTo = 0f
            lenFrom = 8f
            lenTo = 0f
            cone = 360f
            length = 65f
            colorFrom = "FF8663".toColor()
            colorTo = "FF5845".toColor()
          }
        }
      }
    })

    abilities.add(MoveEffectAbility().apply {
      y = -11f
      interval = 3f
      rotation = 180f
      rotateEffect = true
      effect = ParticleEffect().apply {
        lifetime = 60f
        particles = 7
        sizeFrom = 6f
        sizeTo = 1f
        cone = 15f
        length = 50f
        interp = Interp.circleOut
        sizeInterp = Interp.pow5In
        colorFrom = "FF5845AA".toColor()
        colorTo = "78787870".toColor()
      }
    })
  }
}