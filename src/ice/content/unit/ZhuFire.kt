package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.ForceFieldAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class ZhuFire : IceUnitType("zhuFire") {
  init {
    bundle {
      desc(zh_CN, "逐火", "中型空中突击单位.交替发射机炮与离子激光攻击敌人,以自身为中心投射一片立场护盾.初级气动外壳足以应对一部分异常状态,开火时减少所受伤害", "在战争烈度逐渐升级当下,[逐火]攻击机应运而生,更强的火力及装甲使其足以担任小队护航或集群突袭等多种用途")
    }
    circleTarget = true
    lowAltitude = true
    flying = true
    health = 9700f
    hitSize = 42f
    armor = 14f
    range = 40f
    accel = 0.06f
    drag = 0.017f
    speed = 2f
    rotateSpeed = 3.6f
    engineSize = 4f
    engineOffset = 6f
    immunities.addAll(StatusEffects.wet, StatusEffects.burning, StatusEffects.sporeSlowed)
    engines.addAll(UnitEngine().apply {
      x = 9.75f
      y = -7f
      radius = 3f
      rotation = -90f
    }, UnitEngine().apply {
      x = -9.75f
      y = -7f
      radius = 3f
      rotation = -90f
    })
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.4f
    }, ForceFieldAbility(80f, 2f, 1200f, 120f, 4, 0f))
    setWeapon("weapon1") {
      x = 21.25f
      y = -3f
      shake = 1f
      recoil = 2f
      shootY = 2f
      reload = 35f
      shoot = ShootPattern().apply {
        shots = 3
        shotDelay = 3f
      }
      rotate = true
      rotateSpeed = 4f
      shootSound = Sounds.shoot
      bullet = BasicBulletType().apply {
        damage = 55f
        speed = 5f
        lifetime = 48f
        width = 6f
        height = 9f
        splashDamage = 65f
        splashDamageRadius = 64f
        shootEffect = Fx.shootBig
        ammoMultiplier = 4f
        status = StatusEffects.blasted
        statusDuration = 60f
        hitEffect = Fx.flakExplosion
      }
    }
    setWeapon("weapon2") {
      x = 11f
      y = 2.75f
      shake = 3f
      recoil = 3f
      shootY = 7f
      reload = 180f
      rotate = true
      rotateSpeed = 2f
      rotationLimit = 45f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(135f).apply {
        status = IStatus.熔融
        length = 200f
        lifetime = 15f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          interp = Interp.fastSlow
          lightColor = Color.valueOf("#FF5845")
          colorFrom = Color.valueOf("#FF8663")
          colorTo = Color.valueOf("#FF5845")
        }
        colors = arrayOf(
          Color.valueOf("#D75B6E"), Color.valueOf("#E78F92"), Color.valueOf("#FFF0F0")
        )
        statusDuration = 180f
        ammoMultiplier = 1f
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 10
          lifetime = 20f
          length = 75f
          cone = -360f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = Color.valueOf("#FF5845")
          colorFrom = Color.valueOf("#FF8663")
          colorTo = Color.valueOf("#FF5845")
        }
      }
    }
    setWeapon("weapon3") {
      x = 15.25f
      y = 3.75f
      shake = 2f
      recoil = 3f
      reload = 55f
      shootY = 7.25f
      shoot = ShootPattern().apply {
        shots = 2
        shotDelay = 3f
      }
      rotate = true
      rotateSpeed = 2f
      rotationLimit = 45f
      shootSound = Sounds.shootPulsar
      layerOffset = -0.001f
      bullet = BasicBulletType().apply {
        damage = 85f
        speed = 5f
        lifetime = 48f
        width = 9f
        height = 12f
        splashDamage = 35f
        splashDamageRadius = 32f
        shootEffect = Fx.shootBig
        ammoMultiplier = 4f
        status = StatusEffects.blasted
        statusDuration = 60f
        hitEffect = Fx.flakExplosionBig
      }
    }
  }
}