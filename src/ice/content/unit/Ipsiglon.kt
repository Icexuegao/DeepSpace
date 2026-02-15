package ice.content.unit

import arc.graphics.Color
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.bullet.RailBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class Ipsiglon : IceUnitType("unit_ipsiglon") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "伊普西龙")
    }
    coreUnitDock = true
    lowAltitude = true
    isEnemy = false
    flying = true
    health = 1080f
    armor = 8f
    hitSize = 20f
    speed = 4.8f
    drag = 0.15f
    rotateSpeed = 20f
    mineTier = 8
    mineSpeed = 16f
    buildSpeed = 6f
    engineOffset = 14f
    engineSize = 3.5f
    itemCapacity = 125

    setWeapon("伊普西龙机枪") {
      x = 8f
      y = -2f
      reload = 16f
      shoot = ShootPattern().apply {
        shots = 2
        shotDelay = 2f
      }
      ejectEffect = Fx.casing2
      shootSound = Sounds.shootLocus
      bullet = BasicBulletType().apply {
        damage = 25f
        lifetime = 40f
        speed = 8f
        height = 8f
        width = 3f
        shrinkY = 0f
        weaveMag = 1f
        weaveScale = 5f
        trailLength = 15
        trailWidth = 1.5f
        trailColor = Color.valueOf("FAAF87")
        keepVelocity = false
        status = StatusEffects.melting
        statusDuration = 30f
        homingRange = 80f
        homingPower = 0.06f
        splashDamage = 20f
        splashDamageRadius = 10f
        buildingDamageMultiplier = 0.2f
      }
    }

    setWeapon("伊普西龙激光") {
      x = 8f
      y = -2f
      reload = 120f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(50f).apply {
        length = 288f
        width = 12f
        colors = arrayOf(
          Color.valueOf("FAAF87"), Color.valueOf("EBBD86"), Color.valueOf("FFF0F0")
        )
        buildingDamageMultiplier = 0.05f
        hitEffect = Fx.hitLancer
      }
    }

    setWeapon("weapon") {
      x = 0f
      y = 0f
      reload = 360f
      shootY = 8f
      recoil = 0f
      shake = 2f
      mirror = false
      shootCone = 2.5f
      cooldownTime = 210f
      shootSound = Sounds.shootReign
      bullet = RailBulletType().apply {
        damage = 600f
        length = 364f
        recoil = 2f
        knockback = 8f
        status = StatusEffects.melting
        statusDuration = 300f
        pierceDamageFactor = 0.2f
        buildingDamageMultiplier = 0.05f
        shootEffect = Fx.railShoot
        hitEffect = Fx.railHit
        hitColor = Color.valueOf("D86E56")
        pointEffectSpace = 36f
        pointEffect = Fx.railTrail
        pierceEffect = ParticleEffect().apply {
          line = true
          particles = 120
          offset = 0f
          lifetime = 15f
          length = 40f
          cone = -7.5f
          lenFrom = 6f
          lenTo = 0f
          colorFrom = Color.valueOf("D86E56")
          colorTo = Color.white
        }
      }
    }

    abilities.add(
      ArmorPlateAbility().apply {
        healthMultiplier = 0.25f
      })
  }
}