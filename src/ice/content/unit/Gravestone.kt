package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Interp
import ice.content.IStatus
import ice.content.unit.flying.Veto
import ice.entities.bullet.sizeBulletType
import ice.library.util.toColor
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.DeathGiftAbility
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.entities.part.DrawPart.PartProgress as pro

class Gravestone :IceUnitType("unit_gravestone") {
  var by = Pal.bulletYellowBack

  init {
    localization {
      zh_CN {
        this.localizedName = "摧枯"
        description = "精英作战部队,具有凶猛的火力.以生物钢作为主要材料,辅以陶钢作为电磁屏蔽层,一般装备甚至无法留下划痕,同时在澎湃的能量输出下,其回复速度令人惊异\n控制中枢与动力炉紧密相连,在内部结构大规模受损导致动力炉失稳融毁后会一同损毁"
      }
    }
    health = 68700f
    hitSize = 48f
    armor = 77f
    speed = 1.08f
    groundLayer = 75f
    rotateSpeed = 1.2f

    legCount = 6
    legLength = 36f
    stepShake = 0.2f
    legGroupSize = 3
    legMoveSpace = 1f
    legExtension = -3f
    legBaseOffset = 16f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.95f
    legForwardScl = 0.9f
    legSplashRange = 16f
    legSplashDamage = 150f
    hovering = true
    lockLegBase = true
    allowLegStep = true
    outlineColor = "1F1F1F".toColor()
    legContinuousMove = true
    abilities.add(RegenAbility().apply {
      percentAmount = 1f / 100f
    })
    abilities.add(DeathGiftAbility(160f, IStatus.复仇, 900f, 0.1f, 1000f))

    val yb = Veto.ArmorBrokenBulletType(17f, 1773f, 35f, 50f, 25f).apply {
      width = 6f
      height = 35f
      hitColor = by
      trailColor = by
      trailLength = 9
      trailWidth = 1.5f
      trailInterval = 3f
      trailRotation = true
      trailEffect = Effect(30f) { e ->
        Draw.color(e.color, Color.white, e.fin())
        val interp = Interp.pow5In.apply(e.fin())
        Lines.stroke((1.5f + 2f * e.fout()) * Interp.pow5In.apply(e.fslope()))
        Lines.ellipse(e.x, e.y, 5f, 16f * interp, 8f * interp, e.rotation - 90f)
      }
      status = IStatus.破甲I
      statusDuration = 120f
      pierceDamageFactor = 0.4f
      hitEffect = Fx.hitSquaresColor
      hitSound = Sounds.explosionPlasmaSmall
      smokeEffect = Fx.shootSmokeSmite
      shootEffect = Fx.shootSmokeSquareBig
    }
    val b = sizeBulletType(8f, 213f, 45f).apply {
      width = 13f
      height = 14f
      drag = -0.01f
      hitColor = by
      hitEffect = Fx.hitSquaresColor
      shootEffect = Fx.shootSmokeSquare
    }
    setWeapon("weapon1") {
      bullet = b
      x = 18.75f
      y = 15f
      recoil = 2f
      shake = 3f
      shootY = 8f
      reload = 25f
      rotate = true
      shootCone = 5f
      rotateSpeed = 3f
      rotationLimit = 25f
      layerOffset = -0.001f
      shootStatus = IStatus.鼓舞
      shootStatusDuration = 120f
      shoot.apply {
        shots = 3
        shotDelay = 4f
      }
      shootSound = Sounds.shootConquer
    }

    setWeapon("weapon2") {
      bullet = yb
      x = 0f
      y = 5f
      shake = 7f
      recoil = 4f
      shootY = 33f
      reload = 145f
      rotate = true
      shootCone = 1f
      mirror = false
      recoilTime = 185f
      rotateSpeed = 1.2f
      rotationLimit = 45f
      minWarmup = 0.90f
      layerOffset = 0.001f
      cooldownTime = 185f
      shootStatus = IStatus.反扑
      shootStatusDuration = 120f
      shootWarmupSpeed = 0.06f
      shoot.apply {
        shots = 2
      }
      shootSound = Sounds.shootCollaris
      parts.addAll(RegionPart("-glow").apply {
        progress = pro.warmup
        outline = false
        color = Color.valueOf("F03B0E")
        blending = Blending.additive
      }, RegionPart("-side").apply {
        progress = pro.warmup.delay(0.2f)
        heatProgress = pro.warmup.delay(0.2f)
        moveX = 1.5f
        moveY = -1.5f
        under = true
        mirror = true
        heatColor = Color.valueOf("F03B0E")
      })

    }

  }
}