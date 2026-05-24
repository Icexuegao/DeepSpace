package ice.content.unit

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import ice.audio.ISounds
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.IcePuddle
import ice.entities.bullet.MultiBasicBulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.CorrodflyEnd
import ice.world.content.unit.entity.CorrodflyHead
import ice.world.content.unit.entity.CorrodflyMiddle
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Puddles
import mindustry.entities.units.AIController
import mindustry.gen.Bullet
import mindustry.graphics.Drawf

class 蚀虻 :IceUnitType("corrodfly-head", CorrodflyHead::class.java) {
  init {
    rotateMoveFirst = true
    allowedInPayloads = false
    legStraightness = 0.3f
    stepShake = 0f
    legCount = 2
    legLength = 18f
    legGroupSize = 4
    lockLegBase = true
    legBaseUnder = true
    legContinuousMove = true
    legExtension = -2f
    legBaseOffset = 3f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.96f
    legForwardScl = 1.1f
    rippleScale = 0.2f
    legMoveSpace = 1f

    health = 120f
    armor = 1.5f
    hitSize = 8f
    rotateSpeed = 2.5f
    speed = 0.8f
    createScorch = false
    drawCell = false
    outlineRadius = 3
    outlineColor = IceColor.r2
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    localization {
      zh_CN {
        localizedName = "蚀虻"
        description = "小型陆行污染生物.拥有多段体节,尾部体节带有喷口,会喷射腐蚀胶体"
      }
    }
  }
}

class 蚀虻Middle :IceUnitType("corrodfly-middle", CorrodflyMiddle::class.java) {
  init {
    hitSize = 5f
    drawCell = false
    outlineRadius = 3
    allowedInPayloads = false
    outlineColor = IceColor.r2
    hidden = true
    playerControllable = false
    createScorch = false
    deathSound = ISounds.chizovegeta
    aiController = Prov(::AIController)
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
  }
}

class 蚀虻End :IceUnitType("corrodfly-end", CorrodflyEnd::class.java) {
  init {
    legStraightness = 0.3f
    stepShake = 0f
    legCount = 2
    allowedInPayloads = false
    legLength = 18f
    legGroupSize = 4
    lockLegBase = true
    legBaseUnder = true
    legContinuousMove = true
    legExtension = -2f
    legBaseOffset = 3f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.96f
    legForwardScl = 1.1f
    rippleScale = 0.2f
    legMoveSpace = 1f
    hitSize = 8f
    outlineRadius = 3
    outlineColor = IceColor.r2
    drawCell = false
    createScorch = false
    hidden = true
    faceTarget = false
    playerControllable = false
    deathSound = ISounds.chizovegeta
    aiController = Prov(::AIController)
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    setWeapon("weapon") {
      x = 0f
      y = -4f
      shootX += 1
      recoil = 1f
      mirror = false
      rotate = true
      reload = 50f
      shootY += 2f
      shoot.shots = 2
      shoot.shotDelay = 15f
      shootSound = ISounds.flblSquirt
      bullet = object :MultiBasicBulletType("flesh") {
        override fun removed(b: Bullet) {
          super.removed(b)
          val puddle = IcePuddle.create()
          puddle.team = b.team
          puddle.tile = b.tileOn()
          puddle.liquid = ILiquids.浓稠血浆
          puddle.amount = IceEffects.rand.random((height + width) / 2, height * width / 2)
          puddle.set(b.x, b.y)
          Puddles.register(puddle)
          puddle.add()
        }
      }.apply {
        speed = 3f

        width = 7f
        height = width
        shrinkInterp = Interp.one
        status = IStatus.流血
        statusDuration = 2 * 60f
        lightColor = IceColor.r3
        backColor = IceColor.r3
        frontColor = IceColor.r3
        lightOpacity = 0.2f
        shootEffect = Fx.none
        hitEffect = Effect(14f) { e ->
          Draw.color(IceColor.r3, IceColor.r1, e.fin())
          e.scaled(7f) { s ->
            Lines.stroke(0.5f + s.fout())
            Lines.circle(e.x, e.y, s.fin() * 5f)
          }
          Lines.stroke(0.5f + e.fout())
          Angles.randLenVectors(e.id.toLong(), 5, e.fin() * 15f) { x: Float, y: Float ->
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f)
          }
          Drawf.light(e.x, e.y, 20f, IceColor.r3, 0.6f * e.fout())
        }
        despawnEffect = hitEffect
        smokeEffect = Effect(20f) { e ->
          Draw.color(IceColor.r1, IceColor.r2, e.fin())
          Angles.randLenVectors(e.id.toLong(), 5, e.finpow() * 6f, e.rotation, 20f) { x: Float, y: Float ->
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f)
          }
        }

      }
    }
  }
}