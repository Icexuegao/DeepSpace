package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ArmorPlateAbility
import mindustry.content.Fx
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootMulti
import mindustry.entities.pattern.ShootPattern
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.MechUnit
import mindustry.gen.Sounds

class ForbiddenArmy : IceUnitType("unit_forbiddenArmy", MechUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "禁军", "重型地面突击单位.对远距离敌人发射穿透能量弹,对近距离敌人则切换为高热激光,并对附近的友军提供坚忍效果.会缓慢恢复生命值,开火时减少所受伤害")
    }
    health = 119000f
    armor = 29f
    speed = 0.32f
    hitSize = 46f
    rotateSpeed = 1.6f
    hovering = true
    canBoost = true
    lowAltitude = true
    boostMultiplier = 2f
    mechLandShake = 5f
    stepShake = 1f
    mechSideSway = 0.6f
    mechFrontSway = 1.9f
    drownTimeMultiplier = 10f
    mechStepParticles = true
    engineSize = 8f
    engineOffset = 24f

    engines.add(UnitEngine().apply {
      x = 21f
      y = -24f
      radius = 5f
      rotation = -90f
    }, UnitEngine().apply {
      x = -21f
      y = -24f
      radius = 5f
      rotation = -90f
    })
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 1.2f
    }, RegenAbility().apply {
      percentAmount = 0.0125f
    })
    abilities.add(StatusFieldAbility(IStatus.坚忍, 180f, 210f, 160f).apply {
      activeEffect = ParticleEffect().apply {
        lifetime = 25f
        layer = 59f
        length = 0f
        sizeFrom = 12f
        sizeTo = 0f
        particles = 1
        colorFrom = Color.valueOf("FF5845")
        colorTo = Color.valueOf("FF8663")
        region = "particle"
      }
    })

    setWeapon("weapon1") {
      x = 31.25f
      y = 2f
      shoot = ShootMulti(ShootPattern().apply {
        shots = 2
        shotDelay = 10f
      }, ShootSpread().apply {
        shots = 7
        spread = 2f
      })
      shake = 8f
      recoil = 5f
      shootY = 27f
      reload = 90f
      inaccuracy = 10f
      shootCone = 10f
      cooldownTime = 150f
      layerOffset = -0.001f
      shootSound = ISounds.聚爆
      bullet = BasicBulletType(24f, 467f).apply {
        lifetime = 36f
        drag = 0.06f
        height = 12f
        width = 12f
        shrinkY = 0f
        absorbable = false
        reflectable = false
        status = IStatus.熔融
        statusDuration = 60f
        splashDamage = 373f
        splashDamageRadius = 40f
        shootEffect = MultiEffect(ParticleEffect().apply {
          lifetime = 30f
          particles = 3
          sizeFrom = 6f
          sizeTo = 0f
          cone = 20f
          length = 85f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          particles = 2
          lifetime = 40f
          line = true
          lenFrom = 17f
          lenTo = 3f
          cone = 20f
          length = 105f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, WaveEffect().apply {
          lifetime = 10f
          sizeFrom = 0f
          sizeTo = 40f
          strokeFrom = 4f
          strokeTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        })
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        trailColor = Color.valueOf("FF8663")
        trailLength = 8
        trailWidth = 2f
        trailChance = 0.2f
        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 25f
          sizeFrom = 2f
          sizeTo = 0f
          cone = 360f
          length = 13f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
        hitShake = 4f
        hitSound = Sounds.explosionPlasmaSmall
        hitEffect = ExplosionEffect().apply {
          lifetime = 43f
          waveStroke = 6f
          waveLife = 22f
          waveRadBase = 4f
          waveColor = Color.valueOf("FFA05C")
          waveRad = 44f
          smokes = 11
          smokeColor = Color.valueOf("FFA05C")
          sparkColor = Color.valueOf("FFA05C")
          sparks = 7
          sparkRad = 55f
          sparkLen = 16f
          sparkStroke = 3f
        }
        despawnEffect = Fx.none
        fragBullets = 1
        fragBullet = BasicBulletType(0f, 45f).apply {
          sprite = "large-bomb"
          lifetime = 120f
          height = 14f
          width = 12f
          shrinkY = 0f
          absorbable = false
          collidesTiles = false
          hitColor = Color.valueOf("FF8663")
          frontColor = Color.valueOf("FF8663")
          backColor = Color.valueOf("FF5845")
          splashDamage = 155f
          splashDamageRadius = 20f
          hitEffect = MultiEffect(
            ParticleEffect().apply {
            particles = 3
            lifetime = 36f
            sizeFrom = 5f
            sizeTo = 0f
            cone = 360f
            length = 40f
            interp = Interp.circleOut
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }, ParticleEffect().apply {
            particles = 7
            lifetime = 20f
            line = true
            lenFrom = 13f
            lenTo = 3f
            cone = 360f
            length = 75f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }, WaveEffect().apply {
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 40f
            strokeFrom = 4f
            strokeTo = 0f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }, Fx.hitSquaresColor
          )
          despawnEffect = Fx.none
        }
      }
      parts.add(RegionPart().apply {
        suffix = "-glow"
        outline = false
        color = Color.valueOf("F03B0E")
        blending = Blending.additive
      })
    }
  }
}