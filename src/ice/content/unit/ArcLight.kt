package ice.content.unit

import arc.graphics.Color
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.ai.UnitCommand
import mindustry.content.Fx
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootHelix
import mindustry.gen.Sounds

class ArcLight : IceUnitType("unit_arcLight") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "弧光", "中型空中支援单位.发射激光与湍能弹攻击附近敌人,会自动修复受损建筑,并对附近的友军提供迅疗效果")
    }

    defaultCommand = UnitCommand.repairCommand
    flying = true
    lowAltitude = true
    health = 2300f
    hitSize = 20f
    armor = 5f
    speed = 2.4f
    drag = 0.03f
    rotateSpeed = 4f
    mineTier = 4
    mineSpeed = 4.5f
    buildSpeed = 3.5f
    engineSize = 4f
    engineOffset = 14f

    setWeapon("speedWeapon") {
      x = 9f
      y = -2f
      top = false
      shootY = 6f
      reload = 40f
      shoot = ShootHelix().apply {
        shotDelay = 5f
        shots = 2
        mag = 2f
        scl = 6f
      }
      shootSound = Sounds.shootLaser
      bullet = BasicBulletType().apply {
        sprite = "circle-bullet"
        damage = 38f
        lifetime = 30f
        speed = 8f
        width = 4f
        height = 4f
        shrinkY = 0f
        healPercent = 4f
        homingRange = 16f
        homingPower = 0.5f
        collidesTeam = true
        frontColor = Color.valueOf("73FFAE")
        backColor = Color.valueOf("57D993")
        trailColor = Color.valueOf("73FFAE")
        trailLength = 3
        trailWidth = 1.5f
        status = IStatus.湍能
        statusDuration = 10f
        hitEffect = MultiEffect(ParticleEffect().apply {
          particles = 3
          sizeFrom = 3f
          sizeTo = 0f
          length = 25f
          baseLength = 0f
          lifetime = 25f
          colorFrom = Color.valueOf("73FFAE")
          colorTo = Color.valueOf("73FFAE90")
          cone = 360f
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 1f
          sizeTo = 20f
          strokeFrom = 4f
          strokeTo = 0f
          colorFrom = Color.valueOf("73FFAE")
          colorTo = Color.valueOf("73FFAE90")
        })
        despawnEffect = Fx.none
      }
    }

    setWeapon("弧光炮") {
      x = 0f
      shake = 3f
      shootY = 4f
      reload = 120f
      mirror = false
      shootSound = ISounds.灼烧
      bullet = LaserBulletType(85f).apply {
        recoil = 1.6f
        width = 16f
        length = 240f
        knockback = 2.3f
        collidesTeam = true
        healPercent = 16f
        shootEffect = Fx.bigShockwave
        buildingDamageMultiplier = 0.2f
        colors = arrayOf(
          Color.valueOf("50A385"), Color.valueOf("57D993"), Color.valueOf("73FFAE")
        )
        sideAngle = 45f
        sideWidth = 2f
        sideLength = 45f
        status = IStatus.湍能
        statusDuration = 60f
      }
    }

    abilities.add(
      StatusFieldAbility(IStatus.迅疗, 60f, 480f, 80f).apply {
        activeEffect = WaveEffect().apply {
          lifetime = 30f
          sizeFrom = 0f
          sizeTo = 80f
          strokeFrom = 2f
          strokeTo = 0f
          colorFrom = Color.valueOf("57D993")
          colorTo = Color.valueOf("73FFAE")
        }
      })
  }
}