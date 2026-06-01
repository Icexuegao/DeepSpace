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
import ice.content.block.turret.TurretBullets
import ice.content.unit.*
import ice.content.unit.flying.fire.*
import ice.content.unit.flying.rain.*
import ice.content.unit.flying.否决
import ice.content.unit.flying.雨燕
import ice.content.unit.mech.*
import ice.content.unit.naval.履行
import ice.content.unit.naval.沧溟
import ice.content.unit.naval.苦修
import ice.content.unit.naval.见证
import ice.world.content.unit.IceUnitType
import mindustry.Vars
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.units.UnitController
import mindustry.game.EventType.ClientLoadEvent
import mindustry.game.Team
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.Unit
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
import singularity.world.unit.types.晨星
import singularity.world.unit.types.极光
import singularity.world.unit.types.辉夜
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.generator.CircleGenerator
import universecore.graphics.lightnings.generator.ShrinkGenerator
import universecore.struct.AttachedProperty
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
object IUnitTypes {
  fun load()= Unit
  val 星光 = 星光()
  val 火花 = 火花()
  val 战斧 = 战斧()
  val 火狱 = 火狱()
  val 火苗 = 火苗()
  val 重创 = 重创()
  val 雷精 = 雷精()

  val 飞蠓 = 飞蠓()
  val 疟蚊 = 疟蚊()
  val 血俎 = 血俎()

  val 工蜂 = 工蜂()
  val 绒刺 = 绒刺()
  val 和弦 = 和弦()
  val 收割 = 收割()

  val 坚盾 = 坚盾()
  val 围护 = 围护()
  val 固守 = 固守()
  val 铁卫 = 铁卫()
  val 死誓 = 死誓()
  val 禁军 = 禁军()

  val 扑火 = 扑火()
  val 趋火 = 趋火()
  val 奔火 = 奔火()
  val 逐火 = 逐火()
  val 赴火 = 赴火()
  val 化火 = 化火()

  val 陨石 = 陨石()
  val 陨铁 = 陨铁()
  val 陨星 = 陨星()

  val 雨滴 = 雨滴()
  val 骤雨 = 骤雨()
  val 暴雨 = 暴雨()
  val 惊雷 = 惊雷()
  val 雷劫 = 雷劫()

  val 见证 = 见证()
  val 履行 = 履行()
  val 苦修 = 苦修()
  val 沧溟 = 沧溟()

  val 突刺 = 突刺()
  val 碎甲 = 碎甲()
  val 破军 = 破军()
  val 攻城 = 攻城()
  val 重压 = 重压()
  val 悲鸣 = 悲鸣()
  val 断业 = 断业()
  val 涤罪 = 涤罪()

  val 幻影 = 幻影()
  val 弧光 = 弧光()
  val 蜂后 = 蜂后()
  val 剑戟 = 剑戟()
  val 雨燕 = 雨燕()
  val 否决 = 否决()
  val 风暴 = 风暴()

  val 黑棘 = 黑棘()
  val 噬星 = 噬星()
  val 渊狱 = 渊狱()

  val 毒刺 = 毒刺()
  val 爆蚊 = 爆蚊()

  val 加百列 = 加百列()
  val 米迦勒 = 米迦勒()
  val 路西法 = 路西法()

  val 伊普西龙 = 伊普西龙()
  val 泽塔 = 泽塔()
  val 欧米茄 = 欧米茄()

  val 炸蛛 = 炸蛛()
  val 罗织 = 罗织()
  val 构陷 = 构陷()
  val 甘霖 = 甘霖()
  val 摧枯 = 摧枯()
  val 异种 = 异种()
  val 奔袭 = 奔袭()

  val 冥 = 冥()
  val 玄 = 玄()
  val 文漪 = 文漪()

  val 仆从 = 仆从()
  val 传教者 = 传教者()
  val 裂片集群 = 裂片集群()

  val 焚棘 = 焚棘()
  val 青壤 = 青壤()
  val 丰穰之瘤 = 丰穰之瘤()
  val 蚀虻 = 蚀虻()
  val 蚀虻Middle = 蚀虻Middle()
  val 蚀虻End = 蚀虻End()
  val 糜蝇 = Flies()
  val 晨星 = 晨星()
  val 辉夜 = 辉夜()
  val 极光 = 极光()
  val 虚宿 = 虚宿()
  val 无畏 = 无畏()
  val 冥刻 = 冥刻()
  var SglUnitEntity.controlTime by AttachedProperty { 0f }

  /**棱镜 */
  var prism: UnitType? = null

  /**流形 */
  var manifold: UnitType? = null

  val unstable_energy_body = object :SglUnitType<SglUnitEntity>("unstable_energy_body", SglUnitEntity::class.java) {
    val FULL_SIZE_ENERGY: Float = 3680f

    var SglUnitEntity.lightnings: LightningContainer? by AttachedProperty { null }
    var SglUnitEntity.lin: LightningContainer? by AttachedProperty { null }
    var SglUnitEntity.timer by AttachedProperty { 15f }
    var SglUnitEntity.bullTime by AttachedProperty { 0f }

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
        object :UnitController {
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

          override fun isLogicControllable(): Boolean {
            return false
          }

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
        for(i in 0..<n) {
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
        MathRenderer.drawCurveCircle(
          u.x, u.y, radius * 0.7f + radius * Interp.pow2In.apply(1 - lerpStart), 3, radius * 0.6f, Time.time * 1.2f
        )
        MathRenderer.setDispersion(lerpStart)
        Draw.color(SglDrawConst.matrixNet)
        MathRenderer.drawCurveCircle(
          u.x, u.y, radius * 0.72f + radius * Interp.pow2In.apply(1 - lerpStart), 4, radius * 0.67f, Time.time * 1.6f
        )
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
    return Seq.with(加百列, 米迦勒, 路西法)
  }
}