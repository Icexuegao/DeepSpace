package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import arc.math.geom.Rect
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.bullet.PointBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WrapEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.world.meta.BlockFlag

class Siege : IceUnitType("siege") {
  init {
    bundle {
      desc(zh_CN, "攻城", " 配备180毫米口径冲击炮,在防御/攻坚战中皆有不俗表现\n冲击炮开火时产生的强大气浪足以吹飞小口径炮弹\n但因其剧烈的后坐力,需要展开脚架完全架起后才能开火", "炮平四海!!!")
    }
    health = 8400f
    armor = 12f
    hitSize = 27f
    speed = 0.8f
    range = 80f
    aimDst = 216f
    rotateSpeed = 2.4f
    hovering = true
    targetAir = false
    faceTarget = false
    singleTarget = true
    treadFrames = 18
    treadPullOffset = 1
    crushDamage = 5f
    outlineColor = Color.valueOf("1F1F1F")
    treadRects = arrayOf(Rect(-52f, -77f, 25f, 154f))
    targetFlags = arrayOf(BlockFlag.turret, BlockFlag.reactor)
    parts.add(RegionPart().apply {
      suffix = "-tread-top"
      mirror = true
      moveY = 3f
      moveRot = -10f
    })
    parts.add(RegionPart().apply {
      suffix = "-tread-bottom"
      mirror = true
      moveY = -3f
      moveRot = 10f
    })
    parts.add(RegionPart().apply {
      suffix = "-foot"
      mirror = true
      under = true
      moveX = 9.5f
      layerOffset = -0.01f
    })
    setWeapon {
      x = 0f
      shake = 0f
      reload = 30f
      shootCone = 1f
      mirror = false
      rotate = true
      display = false
      useAmmo = false
      rotateSpeed = 20f
      targetInterval = 10f
      shootSound = Sounds.none
      shootStatus = StatusEffects.unmoving
      shootStatusDuration = 60f
      bullet = BulletType().apply {
        damage = 0f
        lifetime = 90f
        speed = 8f
        collidesAir = false
        instantDisappear = true
        shootEffect = Fx.none
        smokeEffect = Fx.none
        despawnEffect = Fx.none
        hitEffect = Fx.none
      }
    }
    setWeapon("weapon") {
      x = 0f
      recoil = 1f
      shake = 3f
      shootY = 8f
      reload = 180f
      shootCone = 1f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      layerOffset = 0.01f
      targetInterval = 10f
      minWarmup = 0.99f
      cooldownTime = 210f
      shootStatus = StatusEffects.shielded
      shootStatusDuration = 60f
      shootWarmupSpeed = 0.04f
      shootSound = Sounds.shootMeltdown
      bullet = PointBulletType().apply {
        damage = 0f
        lifetime = 18f
        speed = 40f
        status = IStatus.熔融
        statusDuration = 120f
        collidesAir = false
        hitColor = Color.valueOf("D86E56")
        splashDamage = 380f
        splashDamageRadius = 60f
        scaledSplashDamage = true
        buildingDamageMultiplier = 2.25f
        shootEffect = ParticleEffect().apply {
          particles = 12
          lifetime = 36f
          sizeFrom = 4f
          cone = 30f
          length = 60f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FEB380")
        }
        trailEffect = Fx.none
        smokeEffect = Fx.shootSmokeTitan
        hitShake = 8f
        hitSound = Sounds.explosion
        hitEffect = MultiEffect(
          WrapEffect(Fx.dynamicSpikes, Color.valueOf("D86E56"), 80f), Fx.scatheExplosion, ParticleEffect().apply {
            particles = 7
            lifetime = 85f
            sizeFrom = 4f
            sizeTo = 0f
            cone = 360f
            length = 33f
            baseLength = 49f
            interp = Interp.pow10Out
            sizeInterp = Interp.pow10In
            colorFrom = Color.valueOf("727272")
            colorTo = Color.valueOf("727272")
          })
      }
      parts.add(RegionPart().apply {
        suffix = "-barrel"
        mirror = true
        under = true
        moveY = 7.25f
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
        children.add(RegionPart().apply {
          suffix = "-top"
          mirror = true
          under = true
          x = 0.25f
          layerOffset = -0.0001f
        })
      })
    }
  }
}