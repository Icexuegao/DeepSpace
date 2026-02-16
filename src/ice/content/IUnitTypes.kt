package ice.content

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.struct.Seq
import ice.ai.AIController
import ice.audio.ISounds
import ice.content.unit.*
import ice.entities.IcePuddle
import ice.entities.bullet.MultiBasicBulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.CorrodflyEnd
import ice.world.content.unit.entity.CorrodflyHead
import ice.world.content.unit.entity.CorrodflyMiddle
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Puddles
import mindustry.gen.Bullet
import mindustry.graphics.Drawf
import singularity.world.unit.types.AuroraType
import singularity.world.unit.types.KaguyaType
import singularity.world.unit.types.MornstarType

@Suppress("unused")
object IUnitTypes : Load {
  val 飞蠓 = FlyingMidges()
  val 疟蚊 = Mosquito()
  val 血俎 = BloodAltar()
  val 工蜂 = WorkerBee()

  val 星光 = Starlight()
  val 火花 = Spark()
  val 战斧 = WarAxe()
  val 火狱 = HellFire()

  val 坚盾 = StrongShield()
  val 围护 = Enclosure()
  val 固守 = Hold()
  val 铁卫 = IronGuard()
  val 死誓 = DeathOath()
  val 禁军 = ForbiddenArmy()

  val 扑火 = PutotFire()
  val 趋火 = Tuihuo()
  val 奔火 = BenFire()
  val 逐火 = ZhuFire()
  val 赴火 = FuFire()
  val 化火 = HuaFire()

  val 雨滴 = Raindrop()
  val 骤雨 = TorrentialRain()
  val 暴雨 = Storm()
  val 惊雷 = Thunder()
  val 雷劫 = ThunderTribulation()

  val 见证 = Witness()
  val 履行 = Execution()
  val 苦修= Meditation()
  val 沧溟 = Abyss()

  val 收割 = Harvester()
  val 幻影 = Phantom()
  val 弧光 = ArcLight()
  val 蜂后 = QueenBee()
  val 剑戟 = SwordSpear()

  val 黑棘 = BlackThorns()

  val 和弦 = Chord()
  val 突刺 = BarbProtrusion()
  val 碎甲 = Shatter()
  val 破军 = BreakArmy()
  val 攻城 = Siege()
  val 重压 = HeavyPress()
  val 悲鸣 = Scream()
  val 毒刺 = PoisonBarb()
  val 爆蚊 = ExplosiveMosquito()
  val 加百列 = Gabriel()
  val 路西法 = Lucifer()
  val 欧米茄 = Omega()
  val 泽塔 = Zeta()
  val 伊普西龙 = Ipsiglon()
  val 炸蛛 = SpiderBomb()

  val 异种 = Heterogeneous()
  val 冥 = Pluto()
  val 玄 = Hyun()

  val 仆从 = Footman()
  val 传教者 = Missionary()
  val 裂片集群 = ClusterLobes()
  val 断业 = BreakUp()
  val 焚棘 = ArdenThorn()
  val 青壤 = Schizovegeta()
  val 丰穰之瘤 = RichTumor()
  val 蚀虻 = IceUnitType("corrodfly-head", CorrodflyHead::class.java) {
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

    hitSize = 8f
    rotateSpeed = 2.5f
    speed = 0.8f
    createScorch = false
    drawCell = false
    outlineRadius = 3
    outlineColor = IceColor.r2
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    bundle {
      desc(zh_CN, "蚀虻")
    }
  }
  val 蚀虻Middle = IceUnitType("corrodfly-middle", CorrodflyMiddle::class.java) {
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
  val 蚀虻End = IceUnitType("corrodfly-end", CorrodflyEnd::class.java) {
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
      bullet = object : MultiBasicBulletType("flesh") {
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
  val 糜蝇 = Flies()
  val 晨星 = MornstarType()
  val 辉夜 = KaguyaType()
  val 极光 = AuroraType()
  val 虚宿 = Emptiness()

  val 无畏 = Fearless()

  val 陨石 = Meteorite()
  val 陨铁 = MeteoricIron()
  val 陨星 = MeteoricStar()

  val 冥刻 = DarkCarving()

  val 甘霖 = Ganlin()

  fun getCoreUnits(): Seq<IceUnitType> {
    return Seq.with(加百列, 路西法)
  }
}