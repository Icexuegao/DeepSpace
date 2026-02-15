package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.bullet.SapBulletType
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.gen.Sounds

class Heterogeneous : IceUnitType("unit_heterogeneous") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "异种", "重型全地形多足机甲,配备了快速发射的汲取光束\n对远距离敌人发射不稳定的聚变能量弹,造成巨大的范围伤害并使范围内的原子发生衰变")
    }
    health = 127000f
    armor = 31f
    speed = 0.8f
    hitSize = 60f
    range = 720f
    rotateSpeed = 1.2f
    targetPriority = 2f
    outlineColor = Color.valueOf("4A4B53")
    groundLayer = 75f
    legCount = 8
    legBaseOffset = 24f
    legLengthScl = 0.98f
    legPairOffset = 3f
    legMoveSpace = 0.8f
    legExtension = -10f
    legLength = 142f
    legForwardScl = 0.9f
    legMaxLength = 1.5f
    legMinLength = 0f
    stepShake = 3f
    rippleScale = 7f
    legSplashDamage = 325f
    legSplashRange = 40f
    drownTimeMultiplier = 60f
    legContinuousMove = true
    allowLegStep = true
    lockLegBase = true
    hovering = true
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 1.5f
    })
    setWeapon("headWeapon") {
      x = 0f
      y = -32f
      reload = 720f
      mirror = false
      rotate = true
      shootY = 27f
      recoil = 5f
      shake = 8f
      rotateSpeed = 1.2f
      rotationLimit = 90f
      cooldownTime = 500f
      shootSound = Sounds.shootBeamPlasma
      bullet = BasicBulletType(0f, 6f).apply {
        sprite = "circle-bullet"
        lifetime = 96f
        height = 32f
        width = 32f
        shrinkY = 0f
        hittable = false
        scaleLife = true
        absorbable = false
        reflectable = false
        collidesAir = false
        collidesGround = false
        status = IStatus.衰变
        homingRange = 160f
        homingPower = 0.01f
        splashDamage = 2000f
        splashDamageRadius = 192f
        scaledSplashDamage = true
        frontColor = Color.white
        backColor = Color.valueOf("BF92F9")
        lightColor = Color.valueOf("BF92F9")
        trailColor = Color.valueOf("BF92F9")
        trailChance = 1f
        trailLength = 20
        trailWidth = 8f
        trailEffect = ParticleEffect().apply {
          lifetime = 25f
          particles = 7
          sizeFrom = 7f
          cone = 360f
          length = 9f
          baseLength = 9f
          interp = Interp.circleOut
          lightColor = Color.valueOf("BF92F9")
          colorFrom = Color.valueOf("FFF8E8")
          colorTo = Color.valueOf("BF92F9")
        }
        bulletInterval = 3f
        intervalBullets = 3
        intervalBullet = SapBulletType().apply {
          length = 128f
          sapStrength = 1.5f
          color = Color.valueOf("BF92F9")
          hitColor = Color.valueOf("BF92F9")
          knockback = -5f
          status = StatusEffects.sapped
          statusDuration = 30f
          shootEffect = ParticleEffect().apply {
            damage = 225f
            particles = 3
            lifetime = 20f
            line = true
            strokeFrom = 3f
            strokeTo = 0f
            lenFrom = 6f
            lenTo = 6f
            cone = 30f
            length = 45f
            interp = Interp.fastSlow
            sizeInterp = Interp.slowFast
            lightColor = Color.valueOf("BF92F9")
            colorFrom = Color.valueOf("BF92F9")
            colorTo = Color.valueOf("BF92F9")
          }
        }
        lightning = 16
        lightningLength = 12
        lightningDamage = 125f
        lightningLengthRand = 29
        lightningColor = Color.valueOf("BF92F9")
        hitSound = Sounds.explosionPlasmaSmall
        hitEffect = Fx.reactorExplosion
        despawnEffect = Fx.reactorExplosion
      }
    }

    val shoote = ParticleEffect().apply {
      line = true
      particles = 12
      offset = 55f
      lifetime = 13f
      length = 35f
      cone = -30f
      lenFrom = 10f
      lenTo = 0f
      colorFrom = Color.white
      colorTo = Color.valueOf("BF92F9")
    }
    val hite = ParticleEffect().apply {
      line = true
      particles = 5
      lifetime = 15f
      length = 35f
      cone = -360f
      lenFrom = 8f
      lenTo = 0f
      colorFrom = Color.white
      colorTo = Color.valueOf("BF92F9")
    }
    setWeapon("spiderCannon") {
      x = 30f
      y = -18f
      recoil = 5f
      reload = 30f
      shake = 3f
      shootY = 8f
      rotate = true
      rotateSpeed = 4f
      alternate = false
      rotationLimit = 120f
      cooldownTime = 45f
      ejectEffect = Fx.casing4
      heatColor = Color.valueOf("79F2EE")
      shootSound = Sounds.shootSpectre
      bullet = ShrapnelBulletType().apply {
        damage = 450f
        width = 25f
        length = 200f
        drag = 0f
        speed = 0f
        status = IStatus.衰变
        fromColor = Color.valueOf("BF92F9")
        toColor = Color.valueOf("665C9F")
        lifetime = 15f
        hitEffect = hite
        shootEffect = shoote
      }
    }.copyAdd {
      x = 12f
      y = -6f

      bullet = ShrapnelBulletType().apply {
        damage = 450f
        width = 25f
        length = 200f
        drag = 0f
        speed = 0f
        status = IStatus.衰变
        fromColor = Color.valueOf("BF92F9")
        toColor = Color.valueOf("665C9F")
        lifetime = 15f
        weaveScale = 8f
        weaveMag = 2f
        hitEffect = hite
        shootEffect = shoote
      }
    }

    setWeapon("laser") {
      x = 34f
      y = 8f
      reload = 6f
      shootY = 8f
      rotate = true
      rotateSpeed = 8f
      shootCone = 45f
      shootSound = Sounds.shootSap
      bullet = SapBulletType().apply {
        damage = 150f
        length = 128f
        sapStrength = 1.5f
        color = Color.valueOf("BF92F9")
        hitColor = Color.valueOf("BF92F9")
        knockback = -5f
        status = StatusEffects.sapped
        statusDuration = 90f
        shootEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 20f
          line = true
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 6f
          lenTo = 6f
          cone = 30f
          length = 45f
          interp = Interp.fastSlow
          sizeInterp = Interp.slowFast
          lightColor = Color.valueOf("BF92F9")
          colorFrom = Color.valueOf("BF92F9")
          colorTo = Color.valueOf("BF92F9")
        }
      }
    }
    setWeapon("laser") {
      x = 24f
      y = 9f
      reload = 7.5f
      shootY = 8f
      rotate = true
      rotateSpeed = 8f
      shootCone = 45f
      shootSound = Sounds.shootSap
      bullet = SapBulletType().apply {
        damage = 225f
        length = 128f
        sapStrength = 1.5f
        color = Color.valueOf("BF92F9")
        hitColor = Color.valueOf("BF92F9")
        knockback = -5f
        status = StatusEffects.sapped
        statusDuration = 90f
        shootEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 20f
          line = true
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 6f
          lenTo = 6f
          cone = 30f
          length = 45f
          interp = Interp.fastSlow
          sizeInterp = Interp.slowFast
          lightColor = Color.valueOf("BF92F9")
          colorFrom = Color.valueOf("BF92F9")
          colorTo = Color.valueOf("BF92F9")
        }
      }
    }
    setWeapon("laser") {
      x = 14f
      y = 12f
      reload = 9f
      shootY = 8f
      rotate = true
      rotateSpeed = 8f
      shootCone = 45f
      shootSound = Sounds.shootSap
      bullet = SapBulletType().apply {
        damage = 275f
        length = 128f
        sapStrength = 1.5f
        color = Color.valueOf("BF92F9")
        hitColor = Color.valueOf("BF92F9")
        knockback = -5f
        status = StatusEffects.sapped
        statusDuration = 90f
        shootEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 20f
          line = true
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 6f
          lenTo = 6f
          cone = 30f
          length = 45f
          interp = Interp.fastSlow
          sizeInterp = Interp.slowFast
          lightColor = Color.valueOf("BF92F9")
          colorFrom = Color.valueOf("BF92F9")
          colorTo = Color.valueOf("BF92F9")
        }
      }
    }
  }
}