package ice.content.unit

import arc.graphics.Color
import ice.content.IStatus
import ice.entities.bullet.ContinuousFlameBulletType
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.gen.Sounds

class 奔袭 : IceUnitType("unit_garrison") {
  init {
    health = 192000f
    speed = 0.65f
    hitSize = 75f
    armor = 48f
    drag = 0.1f
    rotateSpeed = 0.75f
    targetPriority = 3f
    groundLayer = 75f

    legCount = 6
    legGroupSize = 3
    legBaseOffset = 32f
    legLengthScl = 0.95f
    legMoveSpace = 1f
    legExtension = -12f
    legLength = 72f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    stepShake = 3f
    rippleScale = 9f
    legForwardScl = 0.9f
    legSplashDamage = 1550f
    legSplashRange = 60f
    legContinuousMove = true
    allowLegStep = true
    lockLegBase = true
    hovering = true
    outlineColor = Color.valueOf("1F1F1F")

    setWeapon {
      x=44f
      y=-20f
      shootY = 0f
      reload = 300f
      rotate = true
      rotateSpeed = 1f
      shootCone = 10f
      rotationLimit = 45f
      baseRotation = 60f
      alternate = false
      useAmmo = false
      alwaysShooting = true
      alwaysContinuous = true
      shootSound =Sounds.shootBeamPlasma

      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"),
          Color.valueOf("FF5845B2"),
          Color.valueOf("FF5845CC"),
          Color.valueOf("FF6666"),
          Color.valueOf("FFDCD8CC")
        )
        damage = 100f
        lifetime = 60f
        length = -80f
        width = 4f
        recoil = -0.05f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f

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
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF6666")
        }
      }
    }

    setWeapon {
      x=34f
      y=-31f
      shootY = 0f
      reload = 300f
      rotate = true
      rotateSpeed = 1f
      shootCone = 10f
      rotationLimit = 45f
      baseRotation = 30f
      alternate = false
      useAmmo = false
      alwaysShooting = true
      alwaysContinuous = true
      shootSound = Sounds.shootBeamPlasma

      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"),
          Color.valueOf("FF5845B2"),
          Color.valueOf("FF5845CC"),
          Color.valueOf("FF6666"),
          Color.valueOf("FFDCD8CC")
        )
        damage = 100f
        lifetime = 60f
        length = -80f
        width = 4f
        recoil = -0.05f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f

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
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF6666")
        }
      }
    }

    setWeapon {
      x=0f
      y=-38.5f
      shootY = 0f
      reload = 300f
      mirror = false
      rotate = true
      rotateSpeed = 1f
      shootCone = 20f
      rotationLimit = 20f
      useAmmo = false
      alwaysShooting = true
      alwaysContinuous = true
      shootSound =Sounds.shootBeamPlasma

      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"),
          Color.valueOf("FF5845B2"),
          Color.valueOf("FF5845CC"),
          Color.valueOf("FF6666"),
          Color.valueOf("FFDCD8CC")
        )
        damage = 200f
        lifetime = 60f
        length = -160f
        width = 8f
        recoil = -0.1f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f

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
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF6666")
        }
      }
    }

    abilities.add(StatusFieldAbility(IStatus.反扑,90f,60f, 160f).apply {
      applyEffect = Fx.none
      activeEffect = Fx.none
    })

    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 1.5f
    })
    localization {
      zh_CN {
        name = "奔袭"
        description = "重型地面突击单位.装备多门等离子火焰喷射器,对建筑与重甲单位造成毁灭性伤害"
      }
    }
  }
}
