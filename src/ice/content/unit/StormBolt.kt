package ice.content.unit

import arc.math.Interp
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ArmorPlateAbility
import mindustry.content.Fx
import mindustry.entities.abilities.ShieldRegenFieldAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds

class StormBolt : IceUnitType("unit_storBolt") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "风暴", "坚固的超远程炮舰\n可以对敌人进行超视距定点打击,对于近距离的敌人则发射高热激光")
    }
    flying = true
    health = 87000f
    armor = 134f
    hitSize = 89f
    speed = 0.4f
    range = 960f
    rotateSpeed = 1.5f
    engineOffset = 29f
    engineSize = 10f
    engines.addAll(
      UnitEngine(19f, -32.5f, 6f, -90f),
      UnitEngine(-19f, -32.5f, 6f, -90f),
      UnitEngine(36f, -24f, 6f, -90f),
      UnitEngine(-36f, -24f, 6f, -90f)
    )
    lightRadius = 320f
    lowAltitude = true
    outlineColor = "1F1F1F".toColor()
    abilities.add(
      ArmorPlateAbility().apply {
        healthMultiplier = 1f
      },
      ShieldRegenFieldAbility(400f, 2000f, 120f, 120f)
    )

    setWeapon("主炮") {
      x = 0f
      y = 24f
      mirror = false
      shoot = ShootPattern().apply {
        firstShotDelay = 120f
      }
      recoil = 0f
      shake = 4f
      shootY = 0f
      reload = 600f
      shootCone = 1f
      cooldownTime = 720f
      shootStatus = IStatus.过热
      shootStatusDuration = 120f
      chargeSound = Sounds.chargeLancer
      shootSound = Sounds.shootBreach
      bullet = BasicBulletType(8f, 2400f, "circle-bullet").apply {
        lifetime = 120f
        height = 32f
        width = 32f
        shrinkY = 0f
        hittable = false
        absorbable = false
        reflectable = false
        status = IStatus.蚀骨
        statusDuration = 600f
        homingRange = 320f
        homingPower = 0.01f
        splashDamage = 8400f
        splashDamageRadius = 120f
        scaledSplashDamage = true
        frontColor = "FF8663".toColor()
        backColor = "FF5845".toColor()
        lightColor = "FF5845".toColor()
        trailColor = "FF5845".toColor()
        trailChance = 1f
        trailLength = 12
        trailWidth = 8f
        trailEffect = ParticleEffect().apply {
          lifetime = 25f
          particles = 7
          sizeFrom = 7f
          cone = 360f
          length = 9f
          baseLength = 9f
          interp = Interp.circleOut
          lightColor = "FF5845".toColor()
          colorFrom = "FF8663".toColor()
          colorTo = "FF5845".toColor()
        }
        lightning = 7
        lightningDamage = 157f
        lightningLength = 9
        lightningLengthRand = 12
        lightningColor = "FF8663".toColor()
        chargeEffect = MultiEffect(
          ParticleEffect().apply {
            particles = 1
            lifetime = 120f
            sizeFrom = 0f
            sizeTo = 16f
            length = 0f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 4f
            cone = 360f
            length = -160f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 6f
            cone = 360f
            length = -160f
            startDelay = 15f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 8f
            cone = 360f
            length = -160f
            startDelay = 30f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 10f
            cone = 360f
            length = -160f
            startDelay = 45f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 12f
            cone = 360f
            length = -160f
            startDelay = 60f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 14f
            cone = 360f
            length = -160f
            startDelay = 75f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 30f
            sizeFrom = 0f
            sizeTo = 16f
            cone = 360f
            length = -160f
            startDelay = 90f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          WaveEffect().apply {
            lifetime = 40f
            sizeFrom = 130f
            sizeTo = 0f
            strokeFrom = 0f
            strokeTo = 8f
            interp = Interp.pow5In
            lightColor = "FF8663".toColor()
            colorFrom = "FF8663".toColor()
            colorTo = "FF5845".toColor()
          }
        )
        shootEffect = MultiEffect(
          ParticleEffect().apply {
            particles = 15
            lifetime = 30f
            line = true
            lenFrom = 10f
            lenTo = 10f
            cone = 30f
            length = 85f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          WaveEffect().apply {
            lifetime = 25f
            sizeTo = 75f
            strokeFrom = 4f
            lightColor = "FF5845".toColor()
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          }
        )
        despawnEffect = Fx.none
        hitEffect = MultiEffect(
          ParticleEffect().apply {
            particles = 23
            lifetime = 20f
            line = true
            strokeFrom = 6f
            strokeTo = 0f
            lenFrom = 24f
            lenTo = 0f
            cone = 360f
            length = 160f
            baseLength = 30f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 15
            lifetime = 20f
            line = true
            lenFrom = 9f
            lenTo = 0f
            cone = 360f
            length = -180f
            baseLength = 160f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          ParticleEffect().apply {
            particles = 23
            lifetime = 60f
            sizeFrom = 12f
            cone = 360f
            length = 120f
            interp = Interp.pow5Out
            sizeInterp = Interp.pow10In
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          },
          WaveEffect().apply {
            lifetime = 10f
            sizeTo = 120f
            strokeFrom = 6f
            colorFrom = "FF5845".toColor()
            colorTo = "FF8663".toColor()
          }
        )
      }
      parts.addAll(
        ShapePart().apply {
          progress = PartProgress.smoothReload
          hollow = true
          sides = 4
          radius = 6f
          stroke = 1.2f
          strokeTo = 0f
          color = "FF5845".toColor()
          rotateSpeed = -0.5f
          layer = 110f
        },
        ShapePart().apply {
          progress = PartProgress.smoothReload
          hollow = true
          sides = 4
          radius = 10.5f
          stroke = 1.2f
          strokeTo = 0f
          color = "FF5845".toColor()
          rotateSpeed = 0.5f
          layer = 110f
        },
        ShapePart().apply {
          progress = PartProgress.smoothReload
          hollow = true
          circle = true
          radius = 15f
          stroke = 1.2f
          strokeTo = 0f
          color = "FF5845".toColor()
          layer = 110f
        }
      )
    }

    setWeapon("closeDefense") {
      x = 12.5f
      y = -14.25f
      reload = 90f
      shoot = ShootPattern().apply {
        shots = 2
        shotDelay = 15f
        firstShotDelay = 55f
      }
      recoil = 2f
      recoilTime = 210f
      shake = 5f
      rotate = true
      rotateSpeed = 3f
      shootCone = 5f
      alternate = false
      autoTarget = true
      controllable = false
      cooldownTime = 210f
      chargeSound = ISounds.月隐蓄力  // 修正：使用原始JSON中的声音名称
      shootSound = ISounds.月隐发射   // 修正：使用原始JSON中的声音名称
      bullet = LaserBulletType(425f).apply {
        lifetime = 15f
        length = 360f
        width = 32f
        colors = arrayOf("DE4136".toColor(), "FF5845".toColor(), "FF8663".toColor())
        status = IStatus.熔融
        statusDuration = 60f
        hitEffect = MultiEffect(
          Fx.hitLancer,
          ParticleEffect().apply {
            particles = 3
            lifetime = 25f
            sizeFrom = 1.6f
            sizeTo = 0f
            length = 12f
            interp = Interp.pow5Out
            sizeInterp = Interp.pow2In
            colorFrom = "FF8663".toColor()
            colorTo = "FF5845".toColor()
          }
        )
      }
    }.copyAdd {
      x = 40f
      y = -2.75f
    }
  }
}