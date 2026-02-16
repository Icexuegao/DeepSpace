package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ArmorPlateAbility
import mindustry.content.Fx
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.RegionPart
import mindustry.gen.MechUnit
import mindustry.gen.Sounds
import mindustry.type.ammo.ItemAmmoType

class DeathOath : IceUnitType("unit_deathOath", MechUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "死誓", "发射穿透能量弹攻击远距离敌人,对近距离敌人则发射高热激光")
    }
    health = 29000f
    armor = 25f
    hitSize = 30f
    speed = 0.36f
    range = 388f
    rotateSpeed = 1.8f
    riseSpeed = 0.05f
    canBoost = true
    lowAltitude = true
    boostMultiplier = 2f
    mechLandShake = 4f
    stepShake = 0.75f
    mechSideSway = 0.6f
    mechFrontSway = 1.9f
    drownTimeMultiplier = 6f
    mechStepParticles = true
    ammoType = ItemAmmoType(IItems.钍锭)
    ammoCapacity = 240
    engineSize = 6f
    engineOffset = 15f

    engines.add(UnitEngine().apply {
      x = 15f
      y = -18f
      radius = 4f
      rotation = -90f
    }, UnitEngine().apply {
      x = -15f
      y = -18f
      radius = 4f
      rotation = -90f
    })

    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.5f
    }, RegenAbility().apply {
      percentAmount = 0.0125f
    })

    setWeapon("weapon1") {
      top = false
      x = 23f
      y = 1f
      recoil = 3f
      shake = 3f
      reload = 120f
      shootY = 24f
      shootCone = 5f
      alternate = false
      cooldownTime = 150f
      ejectEffect = Fx.casing3
      shootSound = Sounds.shootSpectre
      bullet = BasicBulletType(12f, 585f).apply {
        lifetime = 33f
        width = 16f
        height = 25f
        pierce = true
        pierceArmor = true
        absorbable = false
        reflectable = false
        status = IStatus.湍能
        statusDuration = 60f
        splashDamage = 195f
        splashDamageRadius = 80f
        lightColor = Color.valueOf("FF8663")
        frontColor = Color.white
        backColor = Color.valueOf("FF8663")
        hitColor = Color.valueOf("FF8663")
        trailChance = 1f
        trailInterval = 24f
        trailEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 25f
          baseLength = 9f
          length = 9f
          sizeFrom = 7f
          sizeTo = 0f
          cone = 360f
          interp = Interp.circleOut
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF8663")
        }
        shootEffect = Fx.shootSmokeTitan
        despawnSound = Sounds.explosionPlasmaSmall
        despawnEffect = Fx.bigShockwave
        hitEffect = MultiEffect(
          ExplosionEffect().apply {
            lifetime = 20f
            waveStroke = 2f
            waveColor = Color.valueOf("FF8663")
            sparkColor = Color.valueOf("FF8663")
            waveRad = 12f
            smokeSize = 0f
            smokeSizeBase = 0f
            sparks = 10
            sparkRad = 35f
            sparkLen = 4f
            sparkStroke = 1.5f
          }, ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 4f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 70f
            baseLength = 0f
            lifetime = 10f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.white
            cone = 60f
          }, Fx.hitSquaresColor
        )
      }
      parts.add(RegionPart().apply {
        suffix = "-glow"
        outline = false
        color = Color.valueOf("F03B0E")
        blending = Blending.additive
      })
    }

    setWeapon("weapon2") {
      x = 11.25f
      y = 1.25f
      shake = 3f
      recoil = 3f
      shootY = 7f
      reload = 60f
      rotate = true
      rotateSpeed = 2f
      rotationLimit = 45f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(235f).apply {
        length = 280f
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
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        colors = arrayOf(
          Color.valueOf("D75B6E"),
          Color.valueOf("E78F92"),
          Color.valueOf("FFF0F0")
        )
        status = IStatus.熔融
        statusDuration = 90f
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
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
      }
    }
  }
}