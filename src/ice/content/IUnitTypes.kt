package ice.content

import arc.Events
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.ai.AIController
import ice.audio.ISounds
import ice.content.block.turret.TurretBullets
import ice.content.unit.*
import ice.content.unit.flying.Veto
import ice.content.unit.flying.fire.*
import ice.content.unit.flying.rain.*
import ice.content.unit.mech.*
import ice.content.unit.naval.Abyss
import ice.content.unit.naval.Execution
import ice.content.unit.naval.Meditation
import ice.content.unit.naval.Witness
import ice.entities.IcePuddle
import ice.entities.bullet.MultiBasicBulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import universecore.struct.AttachedProperty
import universecore.world.Load
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.CorrodflyEnd
import ice.world.content.unit.entity.CorrodflyHead
import ice.world.content.unit.entity.CorrodflyMiddle
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Puddles
import mindustry.entities.units.UnitController
import mindustry.game.EventType.ClientLoadEvent
import mindustry.game.Team
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType
import singularity.Sgl
import singularity.graphic.MathRenderer
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.particles.SglParticleModels
import singularity.world.unit.SglUnitEntity
import singularity.world.unit.SglUnitType
import singularity.world.unit.types.AuroraType
import singularity.world.unit.types.KaguyaType
import singularity.world.unit.types.MornstarType
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.generator.CircleGenerator
import universecore.graphics.lightnings.generator.ShrinkGenerator
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
object IUnitTypes : Load {
  val 星光 = Starlight()
  val 火花 = Spark()
  val 战斧 = WarAxe()
  val 火狱 = HellFire()
  val 火苗 = FlameMissile()
  val 重创 = HeavyDamageMissile()
  val 雷精 = Lightning()

  val 工蜂 = WorkerBee()
  val 绒刺 = Barb()
  val 和弦 = Chord()
  val 收割 = Harvester()

  val 坚盾 = StrongShield()
  val 围护 = Enclosure()
  val 固守 = Hold()
  val 铁卫 = IronGuard()
  val 死誓 = DeathOath()
  val 禁军 = ForbiddenArmy()

  val 飞蠓 = FlyingMidges()
  val 疟蚊 = Mosquito()
  val 血俎 = BloodAltar()

  val 扑火 = PutotFire()
  val 趋火 = Tuihuo()
  val 奔火 = BenFire()
  val 逐火 = ZhuFire()
  val 赴火 = FuFire()
  val 化火 = HuaFire()

  val 陨石 = Meteorite()
  val 陨铁 = MeteoricIron()
  val 陨星 = MeteoricStar()

  val 雨滴 = Raindrop()
  val 骤雨 = TorrentialRain()
  val 暴雨 = Storm()
  val 惊雷 = Thunder()
  val 雷劫 = ThunderTribulation()

  val 见证 = Witness()
  val 履行 = Execution()
  val 苦修 = Meditation()
  val 沧溟 = Abyss()

  val 突刺 = BarbProtrusion()
  val 碎甲 = Shatter()
  val 破军 = BreakArmy()
  val 攻城 = Siege()
  val 重压 = HeavyPress()
  val 悲鸣 = Scream()
  val 断业 = BreakUp()
  val 涤罪 = ClearingGround()

  val 幻影 = Phantom()
  val 弧光 = ArcLight()
  val 蜂后 = QueenBee()
  val 剑戟 = SwordSpear()
  val 否决 = Veto()
  val 风暴 = StormBolt()

  val 黑棘 = BlackThorns()
  val 噬星 = StarEater()

  val 毒刺 = PoisonBarb()
  val 爆蚊 = ExplosiveMosquito()

  val 加百列 = Gabriel()
  val 米迦勒 = Michael()
  val 路西法 = Lucifer()

  val 伊普西龙 = Ipsiglon()
  val 泽塔 = Zeta()
  val 欧米茄 = Omega()

  val 炸蛛 = SpiderBomb()
  val 罗织 = Weaver()
  val 构陷 = Constrict()
  val 甘霖 = Ganlin()
  val 摧枯 = Gravestone()
  val 异种 = Heterogeneous()
  val 奔袭 = 奔袭()

  val 冥 = Pluto()
  val 玄 = Hyun()
  val 文漪 = Wripple()

  val 仆从 = Footman()
  val 传教者 = Missionary()
  val 裂片集群 = ClusterLobes()

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
    localization {
      zh_CN {
        this.localizedName = "蚀虻"
        description = "小型陆行污染生物.拥有多段体节,尾部体节带有喷口,会喷射腐蚀胶体"
      }
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
        hitEffect = Effect(14f) {e ->
          Draw.color(IceColor.r3, IceColor.r1, e.fin())
          e.scaled(7f) {s ->
            Lines.stroke(0.5f + s.fout())
            Lines.circle(e.x, e.y, s.fin() * 5f)
          }
          Lines.stroke(0.5f + e.fout())
          Angles.randLenVectors(e.id.toLong(), 5, e.fin() * 15f) {x: Float, y: Float ->
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f)
          }
          Drawf.light(e.x, e.y, 20f, IceColor.r3, 0.6f * e.fout())
        }
        despawnEffect = hitEffect
        smokeEffect = Effect(20f) {e ->
          Draw.color(IceColor.r1, IceColor.r2, e.fin())
          Angles.randLenVectors(e.id.toLong(), 5, e.finpow() * 6f, e.rotation, 20f) {x: Float, y: Float ->
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
  val 冥刻 = DarkCarving()
  var SglUnitEntity.controlTime by AttachedProperty(0f)

  /**棱镜 */
  var prism: UnitType? = null

  /**流形 */
  var manifold: UnitType? = null


  val unstable_energy_body = object : SglUnitType<SglUnitEntity>("unstable_energy_body", SglUnitEntity::class.java) {
    val FULL_SIZE_ENERGY: Float = 3680f

    var SglUnitEntity.lightnings: LightningContainer? by AttachedProperty(null)
    var SglUnitEntity.lin: LightningContainer? by AttachedProperty(null)
    var SglUnitEntity.timer by AttachedProperty(15f)
    var SglUnitEntity.bullTime by AttachedProperty(0f)

    init {


      Events.on(ClientLoadEvent::class.java) { e: ClientLoadEvent? ->
        immunities.addAll(Vars.content.statusEffects())
        Sgl.empHealth.setEmpDisabled(this)
      }

      isEnemy = false

      health = 10f
      hidden = true
      hitSize = 32f
      playerControllable = false
      createWreck = false
      createScorch = false
      logicControllable = false
      useUnitCap = false

      aiController = Prov {
        object : UnitController {
          override fun unit(unit: Unit) {
          }

          override fun unit(): Unit? {
            return null
          }

          override fun hit(bullet: Bullet) {
          }

          override fun isValidController(): Boolean {
            return true
          }

          override fun isLogicControllable(): Boolean { return false }

          override fun updateUnit() {}

          override fun removed(unit: Unit) {}

          override fun afterRead(unit: Unit) {}
        }
      }
    }

    val generator: CircleGenerator = CircleGenerator()

    val linGen: ShrinkGenerator = ShrinkGenerator().apply {
      minInterval = 2.8f
      maxInterval = 4f
      maxSpread = 4f
    }

    override fun create(team: Team?): Unit {
      val res = super.create(team) as SglUnitEntity
      res.controlTime = Time.time
      return res
    }

    override fun init(unit: SglUnitEntity) {
      val cont = LightningContainer()
      cont.time = 0f
      cont.lifeTime = 18f
      cont.minWidth = 0.8f
      cont.maxWidth = 1.8f
      unit.lightnings = cont

      val lin = LightningContainer()
      lin.headClose = true
      lin.endClose = true
      lin.time = 12f
      lin.lifeTime = 22f
      lin.minWidth = 1.2f
      lin.maxWidth = 2.4f
      unit.lin = lin
    }

    override fun update(u: Unit) {
      val unit = u as SglUnitEntity

      super.update(unit)

      val lightnings: LightningContainer = unit.lightnings!!
      val lin: LightningContainer = unit.lin!!
      if (Mathf.chanceDelta(0.08)) {
        generator.radius = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f)
        generator.minInterval = 4.5f
        generator.maxInterval = 6.5f
        generator.maxSpread = 5f
        lightnings.create(generator)

        Angles.randLenVectors(
          System.nanoTime(), 1, 1.8f, 2.75f
        ) { x: Float, y: Float ->
          SglParticleModels.floatParticle.create(u.x, u.y, Pal.reactorPurple, x, y, Mathf.random(3.55f, 4.25f)).strength = 0.22f
        }
      }

      if (Mathf.chanceDelta(0.1)) {
        linGen.maxRange = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f)
        linGen.minRange = linGen.maxRange
        val n = Mathf.random(1, 3)
        for (i in 0..<n) {
          lin.create(linGen)
        }
      }
      unit.timer -= Time.delta
      if (unit.timer <= 0f) {
        unit.timer = 12f
        generator.minInterval = 3.5f
        generator.maxInterval = 4.5f
        generator.maxSpread = 4f
        generator.radius = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f) / 2
        lightnings.create(generator)
      }

      lightnings.update()
      lin.update()

      unit.hitSize = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f)
      val controlTime: Float = 900 - Time.time + unit.controlTime
      if (controlTime <= 0) {
        if (unit.health >= 1280) {
          Effect.shake(8f, 120f, u.x, u.y)
          Damage.damage(u.x, u.y, unit.hitSize * 5, unit.health / FULL_SIZE_ENERGY * 4680)

          Sounds.explosionReactorNeoplasm.at(u.x, u.y, 0.8f, 3.5f)

          SglFx.reactorExplode.at(u.x, u.y, 0f, unit.hitSize * 5)
          Angles.randLenVectors(System.nanoTime(), Mathf.random(20, 34), 2.8f, 6.5f) { x: Float, y: Float ->
            val len = Tmp.v1.set(x, y).len()
            SglParticleModels.floatParticle.create(u.x, u.y, Pal.reactorPurple, x, y, Mathf.random(5f, 7f) * ((len - 3) / 4.5f))
          }
        }

        unit.kill()
      } else if (controlTime <= 300) {
        unit.bullTime -= Time.delta
        val bullTime: Float = unit.bullTime
        if (bullTime <= 0) {
          TurretBullets.溢出能量.create(u, u.team, u.x, u.y, Mathf.random(0f, 360f), Mathf.random(0.5f, 1f))
          unit.health -= 180f
          unit.bullTime = max(controlTime / 10, 2f)
        }

        if (Mathf.chanceDelta((1 - controlTime / 300).toDouble())) {
          val lerp: Float = (900 - Time.time + unit.controlTime) / 900
          Tmp.v1.rnd(Mathf.random(u.hitSize / (3 - lerp), max(u.hitSize / (2.5f - lerp), 15f)))
          SglFx.impWave.at(u.x + Tmp.v1.x, u.y + Tmp.v1.y)
        }
      }
    }

    override fun draw(u: Unit) {
      val unit = u as SglUnitEntity?

      Draw.z(Layer.effect)

      val radius = u.hitSize
      val lerp: Float = (900 - Time.time + unit!!.controlTime) / 900
      val lerpStart = Mathf.clamp((1 - lerp) / 0.1f)
      val lerpEnd = Interp.pow3Out.apply(Mathf.clamp(lerp / 0.2f))

      Lines.stroke(radius * 0.055f * lerpStart, Pal.reactorPurple)
      Lines.circle(u.x, u.y, radius * lerpEnd + radius * Interp.pow2In.apply(1 - lerpStart))

      Draw.draw(Draw.z()) {
        MathRenderer.setThreshold(0.4f, 0.7f)
        MathRenderer.setDispersion(lerpStart * 1.2f)
        Draw.color(Pal.reactorPurple)
        MathRenderer.drawCurveCircle(u.x, u.y, radius * 0.7f + radius * Interp.pow2In.apply(1 - lerpStart), 3, radius * 0.6f, Time.time * 1.2f)
        MathRenderer.setDispersion(lerpStart)
        Draw.color(SglDrawConst.matrixNet)
        MathRenderer.drawCurveCircle(u.x, u.y, radius * 0.72f + radius * Interp.pow2In.apply(1 - lerpStart), 4, radius * 0.67f, Time.time * 1.6f)
      }

      Draw.color(SglDrawConst.matrixNet)
      Fill.circle(u.x, u.y, radius / (2.4f - lerp) * Interp.pow2Out.apply(lerpStart) * lerpEnd)
      Lines.stroke(lerp)
      Lines.circle(u.x, u.y, radius * 1.2f * lerpEnd)
      unit.lightnings!!.draw(u.x, u.y)
      unit.lin!!.draw(u.x, u.y)

      Draw.color(Color.white)
      Fill.circle(u.x, u.y, Mathf.maxZero(radius / (2.6f - lerp)) * Interp.pow2Out.apply(lerpStart) * lerpEnd)
    }

    override fun read(sglUnitEntity: SglUnitEntity, read: Reads, revision: Int) {
      sglUnitEntity.controlTime = Time.time + read.f()
    }

    override fun write(sglUnitEntity: SglUnitEntity, write: Writes) {
      write.f(Time.time - sglUnitEntity.controlTime)
    }
  }.apply {

  }
  fun getCoreUnits(): Seq<IceUnitType> {
    return Seq.with(加百列, 路西法)
  }
}