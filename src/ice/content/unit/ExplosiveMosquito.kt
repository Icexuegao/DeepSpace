package ice.content.unit

import arc.graphics.Color
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.effect.ExplosionEffect
import mindustry.gen.Sounds
import mindustry.world.meta.BlockFlag

class ExplosiveMosquito : IceUnitType("explosiveMosquito") {
  init {
    bundle {
      desc(zh_CN, "爆蚊", "飞行自爆兵种,能从意想不到的方位发起进攻")
    }
    lowAltitude = true
    flying = true
    health = 270f
    hitSize = 11f
    armor = 1f
    speed = 2.4f
    accel = 0.08f
    drag = 0.04f
    engineSize = 3f
    trailLength = 3
    engineOffset = 7f
    immunities.addAll(StatusEffects.burning, StatusEffects.wet, StatusEffects.sporeSlowed)
    deathExplosionEffect = Fx.none
    targetFlags = arrayOf(
      BlockFlag.reactor, BlockFlag.generator, BlockFlag.turret
    )
    abilities.add(MoveEffectAbility().apply {
      y = -10f
      interval = 4f
      teamColor = true
      effect = Fx.missileTrailShort
    })
    setWeapon {
      x = 0f
      reload = 600f
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.explosion
      bullet = BulletType().apply {
        collides = false
        hittable = false
        killShooter = true
        instantDisappear = true
        shootEffect = Fx.none
        despawnEffect = ExplosionEffect().apply {
          sparkColor = Color.valueOf("F6E096")
          lifetime = 30f
          smokes = 30
          smokeSize = 13f
          smokeSizeBase = 0.6f
          smokeRad = 32f
          waveLife = 30f
          waveStroke = 2f
          waveRad = 73f
          waveRadBase = 2f
          sparkRad = 64f
          sparkLen = 13f
          sparkStroke = 4f
          sparks = 40
        }
        status = IStatus.蚀骨
        statusDuration = 240f
        splashDamage = 210f
        splashDamageRadius = 80f
        hitEffect = Fx.pulverize
        despawnSound = Sounds.explosion
      }
    }
  }
}