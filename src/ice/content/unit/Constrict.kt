package ice.content.unit

import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.MissileBulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.DeathGiftAbility
import mindustry.content.Fx
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.LegsUnit
import mindustry.gen.Sounds
import mindustry.type.Weapon

class Constrict : IceUnitType("unit_constrict", LegsUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "构陷", "敌特种多足步进机甲,能在短时间内倾斜大量的火力", "以一种取之不尽的原生六足甲壳生物为基底,移除不必要的器官,进行代谢优化,植入控制芯片,然后整体置入标准外骨骼中")
    }
    health = 57900f
    hitSize = 32f
    armor = 57f
    drag = 0.05f
    speed = 1.08f
    groundLayer = 75f
    rotateSpeed = 3.6f
    legCount = 6
    legLength = 36f
    stepShake = 0.2f
    legGroupSize = 3
    legMoveSpace = 1f
    legExtension = -3f
    legBaseOffset = 14f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.95f
    legForwardScl = 0.9f
    legSplashRange = 12f
    legSplashDamage = 100f
    hovering = true
    singleTarget = true
    lockLegBase = true
    allowLegStep = true
    outlineColor = "1F1F1F".toColor()
    legContinuousMove = true

    abilities.add(RegenAbility().apply {
      percentAmount = 1f / 100f
    }, DeathGiftAbility(160f, IStatus.屠戮, 900f, 0.1f, 1000f))
    weapons.add(missile(13.75f, 11.5f, 15f, true), missile(10.5f, 0.5f, 25f), missile(8.25f, -13f, 35f))
  }

  fun missile(wx: Float, wy: Float, rotation: Float, under: Boolean = false): Weapon {
    return Weapon("unit_constrict-missile".appendModName()).apply {
      x = wx
      y = wy
      recoil = 2f
      shake = 3f
      shootY = 7f
      reload = 150f
      inaccuracy = 5f
      shootCone = 45f
      baseRotation = -rotation
      cooldownTime = 65f
      shootStatus = IStatus.突袭
      shootStatusDuration = 120f
      shoot = ShootSpread(2, 10f)
      layerOffset = if (under) -0.001f else 0f
      shootSound = Sounds.shootMissile
      bullet = MissileBulletType(0.6f, 373f, "crystal").apply {
        width = 6f
        height = 9f
        hitShake = 3f
        lifetime = 118f
        drag = -0.03f
        makeFire = true
        despawnShake = 3f
        homingDelay = 55f
        homingRange = 160f
        homingPower = 0.08f
        status = IStatus.熔融
        statusDuration = 120f
        keepVelocity = false
        splashDamage = 173f
        splashDamageRadius = 32f
        scaledSplashDamage = true
        shootEffect = Fx.shootSmokeSquare
        backColor = ("F9C27A").toColor()
        frontColor = ("FFD37F").toColor()
        trailColor = ("FFD37F").toColor()
        trailLength = 5
        trailWidth = 2f
        trailChance = 1f
        trailInterval = 12f
        trailRotation = true
        trailEffect = MultiEffect(ParticleEffect().apply {
          line = true
          particles = 3
          lifetime = 45f
          length = 24f
          baseLength = 0f
          lenFrom = 12f
          lenTo = 0f
          cone = 15f
          offsetX = -15f
          lightColor = ("F9C27A").toColor()
          colorFrom = ("FFD37F").toColor()
          colorTo = ("F9C27A").toColor()
        }, ParticleEffect().apply {
          particles = 1
          sizeFrom = 3f
          sizeTo = 0f
          cone = 15f
          length = -50f
          lifetime = 30f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow5In
          colorFrom = ("F9C27A").toColor()
          colorTo = ("FFD37F").toColor()
        })
        hitEffect = MultiEffect(Fx.titanExplosionSmall, Fx.hitSquaresColor)
        hitColor = ("F9C27A").toColor()
      }
    }
  }
}