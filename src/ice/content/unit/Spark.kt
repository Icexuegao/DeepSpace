package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.ui.bundle.BaseBundle
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.bullet.ExplosionBulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Weapon
import mindustry.type.unit.MissileUnitType

class Spark : MissileUnitType("unit_spark") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "火花")
    }
    health = 385f
    hitSize = 11f
    armor = 1f
    speed = 9.6f
    lifetime = 150f
    maxRange = 10f
    rotateSpeed = 1.2f
    missileAccelTime = 60f
    engineColor = Color.valueOf("FEB380")
    trailColor = Color.valueOf("FEB380")
    engineLayer = 110f
    engineOffset = 9f
    engineSize = 2.3f
    trailLength = 12
    lowAltitude = true

    weapons.add(Weapon().apply {
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.none
      bullet = ExplosionBulletType().apply {
        splashDamageRadius = 55f
        splashDamage = 100f
        hitEffect = MultiEffect(WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 30f
          strokeFrom = 7.5f
          strokeTo = 0f
          colorFrom = Color.valueOf("FFDCD8")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 10
          lifetime = 20f
          line = true
          strokeFrom = 1.5f
          strokeTo = 1.5f
          lenFrom = 5f
          lenTo = 5f
          cone = 360f
          length = 50f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FFDCD8")
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          particles = 10
          lifetime = 25f
          line = true
          strokeFrom = 1.5f
          strokeTo = 1.5f
          lenFrom = 7f
          lenTo = 7f
          cone = 360f
          length = 30f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FFDCD8")
          colorTo = Color.valueOf("FF5845")
        })
      }
    }
    )
    abilities.add(
      MoveEffectAbility().apply {
        y = -8f
        interval = 2f
        rotation = 180f
        rotateEffect = true
        effect = ParticleEffect().apply {
          particles = 4
          sizeFrom = 4f
          sizeTo = 1f
          length = 30f
          lifetime = 30f
          lightOpacity = 0f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FF5845AA")
          colorTo = Color.valueOf("78787870")
          cone = 9f
        }
      })
  }
}