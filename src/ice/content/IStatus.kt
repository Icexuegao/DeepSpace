package ice.content

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp.*
import arc.math.Mathf
import arc.math.Rand
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pool
import arc.util.pooling.Pools
import ice.content.ILiquids.相位态FEX流体
import ice.content.block.turret.TurretBullets.破碎FEX结晶
import ice.entities.effect.MultiEffect
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.status.IceStatusEffect
import ice.world.content.status.PercentStatus
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Puddles
import mindustry.entities.Units
import mindustry.entities.abilities.Ability
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.units.StatusEntry
import mindustry.game.EventType
import mindustry.game.Team
import mindustry.gen.Sounds
import mindustry.gen.Tex
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.graphic.SglDraw
import singularity.graphic.SglDraw.DrawAcceptor
import singularity.graphic.SglDrawConst
import singularity.ui.UIUtils
import singularity.world.SglFx
import singularity.world.meta.SglStat
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
object IStatus : Load {
  val lastHealth = ObjectMap<Unit, Float>()
  val rand: Rand = Rand()
  val 封冻 = IceStatusEffect("freeze") {
    bundle {
      desc(zh_CN, "封冻", "超低温将快速脆化装甲直至开裂,而后渗透的寒气会对内部结构造成毁灭性打击")
    }

    setUpdate { unit, e ->
      val ice = 1f - (600 - e.time) / 600f * 0.4f
      if (unit.healthMultiplier > 0.4) unit.healthMultiplier *= ice
      if (unit.speedMultiplier > 0.4) unit.speedMultiplier *= ice
      if (unit.reloadMultiplier > 0.4) unit.reloadMultiplier *= ice
    }
    transitionDamage = 36f
    init {
      opposites(StatusEffects.burning, StatusEffects.melting)
      affinity(StatusEffects.blasted) { unit, result, time ->
        unit.damagePierce(this.transitionDamage)
        if (unit.team == Vars.state.rules.waveTeam) {
          Events.fire(EventType.Trigger.blastFreeze)
        }
      }
    }
    effect = ParticleEffect().apply {
      particles = 3
      lifetime = 120f
      sizeFrom = 4f
      cone = 360f
      length = 45f
      interp = pow5Out
      sizeInterp = pow2In
      colorFrom = Color.valueOf("87CEEB")
      colorTo = Color.valueOf("C0ECFF")
    }
    effectChance = 0.05f
    color = Color.valueOf("C0ECFF")
  }
  val 集群 = IceStatusEffect("assemble") {
    speedMultiplier = 1.5f
    bundle {
      desc(zh_CN, "集群", "激活协同协议,单位间建立纳米机器人共享网络,效应随范围内友方单位数量增强")
    }
  }
  val 圣火 = IceStatusEffect("holyFlame") {
    damage = 70 / 60f
    bundle {
      desc(zh_CN, "圣火", "持续造成目标最大生命值百分比的火焰伤害")
    }
  }
  val 邪火 = IceStatusEffect("evilFlame") {
    damage = 75 / 60f
    bundle {
      desc(zh_CN, "邪火", "持续生命侵蚀,扣除单位生命上限")
    }
  }
  val 破甲I = IceStatusEffect("armorBreakI") {
    healthMultiplier = 0.8f
    speedMultiplier = 1.2f
    color = Color.valueOf("D1EFFF")
    bundle {
      desc(zh_CN, "破甲I", "目标单位护甲扣除,使其遭受的伤害显著提升")
    }
  }
  val 破甲II = IceStatusEffect("armorBreakII") {
    speedMultiplier = 1.1f
    armorBreak = 10f
    bundle {
      desc(zh_CN, "破甲II", "目标单位护甲扣除,使其遭受的伤害显著提升")
    }
  }
  val 破甲III = IceStatusEffect("armorBreakIII") {
    speedMultiplier = 1.1f
    armorBreak = 20f
    bundle {
      desc(zh_CN, "破甲III", "目标单位护甲扣除,使其遭受的伤害显著提升")
    }
  }
  val 破甲IV = IceStatusEffect("armorBreakIV") {
    speedMultiplier = 1.1f
    armorBreak = 30f
    bundle {
      desc(zh_CN, "破甲IV", "目标单位护甲扣除,使其遭受的伤害显著提升")
    }
  }
  val 穿甲 = IceStatusEffect("armorPiercing") {
    speedMultiplier = 1.5f
    armorBreakPercent = 0.8f
    bundle {
      desc(zh_CN, "穿甲", "完全无视目标护甲,直接穿透对本体造成伤害")
    }
  }
  val 电磁脉冲 = IceStatusEffect("electromagneticPulse") {
    speedMultiplier = 0.7f
    healthMultiplier = 0.9f
    bundle {
      desc(zh_CN, "电磁脉冲", "突发宽带电磁辐射的高强度脉冲,用于破坏敌人的电子设备")
    }
  }
  val 辐射 = IceStatusEffect("radiation") {
    reloadMultiplier = 0.9f
    healthMultiplier = 0.9f
    speedMultiplier = 0.9f
    damage = 0.25f
    color = Color.valueOf("F9A3C7")
    effect = ParticleEffect().apply {
      particles = 4
      lifetime = 45f
      sizeFrom = 2f
      sizeTo = 0f
      length = 15f
      baseLength = 5f
      interp = slowFast
      sizeInterp = fastSlow
      colorFrom = Color.valueOf("F9A3C7")
      colorTo = Color.valueOf("A24FAA")
    }
    bundle {
      desc(zh_CN, "辐射", "经过一次能级降低的辐射,依旧能干扰精密电路并对其造成相当程度的损伤")
    }
  }
  val 染血 = IceStatusEffect("stainedBlood") {
    speedMultiplier = 0.8f
    bundle {
      desc(zh_CN, "染血", "染血")
    }
  }
  val 憎恨 = IceStatusEffect("hatred") {
    bundle {
      desc(zh_CN, "憎恨", "憎恨")
    }
  }
  val 流血 = IceStatusEffect("bleed") {
    color = Color.red
    bundle {
      desc(zh_CN, "流血", "流血")
    }
    setUpdate { u, s ->
      u.health -= (u.speed() * u.hitSize() / 60)
      u.clampHealth()
    }
    init {
      stats.add(Stat.damage, "[negstat][hitSize]*[speed]${StatUnit.perSecond.localized()}[]")
    }
  }
  val 回响 = IceStatusEffect("resound") {
    speedMultiplier = 0.5f
    effect = Fx.absorb
    bundle {
      desc(zh_CN, "回响", "回响")
    }
  }
  val 搏动 = IceStatusEffect("throb") {
    healthMultiplier = 1.7f
    speedMultiplier = 1.4f
    effect = Fx.absorb
    bundle {
      desc(zh_CN, "搏动", "搏动")
    }
  }
  val 寄生 = IceStatusEffect("parasitism") {
    healthMultiplier = 0.9f
    speedMultiplier = 0.9f
    bundle {
      desc(zh_CN, "寄生", "寄生状态会逐渐消耗单位生命值致其死亡,随后生成血肉单位")
    }
  }
  val 融合 = IceStatusEffect("merge") {
    healthMultiplier = 1.5f
    bundle {
      desc(zh_CN, "融合", "当血肉单位满足特定条件时,融合状态触发,逐渐靠近结合,属性整合提升")
    }
  }
  val 维生I = IceStatusEffect("vitalFixI") {
    damage = -30f / 60
    bundle {
      desc(zh_CN, "维生I", "激活纳米机器人集群,将储存的硅矿微粒与裂解液转化为生物修复单元,持续重构受损机体")
    }
  }
  val 维生II = IceStatusEffect("vitalFixII") {
    damage = -60f / 60
    bundle {
      desc(zh_CN, "维生II", "激活纳米机器人集群,将储存的硅矿微粒与裂解液转化为生物修复单元,持续重构受损机体")
    }
  }
  val 脉冲 = IceStatusEffect("pulse") {
    setUpdate { unit, e ->
      if (unit.shield > 0) {
        val damage = max(unit.type.health / 100, unit.shield / 100)
        unit.damageContinuousPierce(damage / 60)
      }
    }
    init {
      stats.add(IceStats.百分比护盾伤害, "1%当前护盾/秒")
      stats.add(IceStats.最小护盾伤害, "1%生命上限/秒")
    }
    disarm = true
    speedMultiplier = 0f
    dragMultiplier = 1f
    buildSpeedMultiplier = 0f
    color = Color.valueOf("5A58C4")
    effectChance = 0.25f
    effect = ParticleEffect().apply {
      particles = 3
      lifetime = 30f
      line = true
      length = 0.01f
      strokeFrom = 2f
      strokeTo = 0f
      lenFrom = 16f
      lenTo = 16f
      colorTo = Color.valueOf("5A58C4")
    }
    bundle {
      desc(zh_CN, "脉冲", "脉冲", " E!M!P!")
    }
  }
  val 鼓舞 = IceStatusEffect("inspires") {
    setUpdate { unit, e ->
      Units.nearby(unit.team, unit.x, unit.y, unit.type.hitSize * 4) { u ->
        if (u.team == unit.team && u != unit && !u.statusBits().get(this.id.toInt())) {
          u.damageMultiplier *= 1 + unit.damageMultiplier / 5
          u.healthMultiplier *= 1 + unit.healthMultiplier / 5
          u.speedMultiplier *= 1 + unit.speedMultiplier / 5
          u.reloadMultiplier *= 1 + unit.reloadMultiplier / 5
          u.heal(unit.maxHealth / 500 / 60)
        }
      }
    }
    bundle {
      desc(
        zh_CN, "鼓舞", "为周围友军持续提供(具有鼓舞的单位)20%的属性倍率,且每秒回复相当于(具有鼓舞的单位)0.1%生命上限的生命值"
      )
    }
    effectChance = 0.05f
    reloadMultiplier = 1.2f
    healthMultiplier = 1.2f
    effect = ParticleEffect().apply {
      region = "blank"
      lifetime = 60f
      particles = 3
      sizeFrom = 3f
      cone = 360f
      length = 25f
      offset = 45f
      interp = pow5Out
      sizeInterp = pow5In
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("F15454")
      color = Color.valueOf("F15454")
    }

    fun grt() = ParticleEffect().apply {
      particles = 1
      lifetime = 60f
      line = true
      strokeFrom = 0f
      strokeTo = 1.2f
      lenFrom = 16f
      lenTo = 22.62f
      cone = 0f
      length = -40f
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("F15454")
      interp = pow5Out
      sizeInterp = pow2In
      randLength = false
      baseLength = 32f
    }

    applyEffect = MultiEffect(grt().apply {
      offsetX = 13.85f
      baseRotation = 0f
    }, grt().apply {
      offsetX = 19.6f
      offsetY = -9.8f
      baseRotation = 120f
    }, grt().apply {
      offsetX = 9.8f
      offsetY = -9.8f
      baseRotation = 240f
    })
  }
  val 过热 = IceStatusEffect("overheat") {
    bundle {
      desc(
        zh_CN, "过热", "过载动力炉并重导向其能量配给以进行主炮开火或特殊行动,期间过量的能量可能损坏管路", "以此为信"
      )
    }
    disarm = true
    dragMultiplier = 1f
    speedMultiplier = 0f
    damage = 5f
    effectChance = 0.35f
    color = Color.valueOf("FFDCD8")
    // 子效果配置
    effect = ParticleEffect().apply {
      lifetime = 30f
      length = 16f
      sizeFrom = 3f
      sizeTo = 0f
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FFDCD8")
    }
  }
  val 屠戮 = IceStatusEffect("massacre") {
    bundle {
      desc(zh_CN, "屠戮", "屠戮")
    }
    damageMultiplier = 1.5f
    healthMultiplier = 0.8f
    speedMultiplier = 1.4f
    reloadMultiplier = 1.2f
    effectChance = 0.05f
    effect = ParticleEffect().apply {
      particles = 3
      lifetime = 30f
      length = 30f
      sizeFrom = 3f
      sizeTo = 0f
      cone = 360f
      colorFrom = Color.valueOf("E8D174")
      colorTo = Color.valueOf("F3E979")
    }

    color = Color.valueOf("F3E979")
  }
  val 损毁 = IceStatusEffect("destroyed") {
    bundle {
      desc(zh_CN, "损毁", "损毁")
    }
    healthMultiplier = 0.77f
    speedMultiplier = 0.85f
    reloadMultiplier = 0.95f
    effect = ParticleEffect().apply {
      lifetime = 200f
      particles = 1
      sizeFrom = 3f
      sizeTo = 0f
      cone = 360f
      length = 30f
      colorFrom = Color.valueOf("B0BAC0")
      colorTo = Color.valueOf("989AA4")
    }
    color = Color.valueOf("989AA4")
  }
  val 迅疗 = PercentStatus("rapidHealing", 0.8f, -6f) {
    bundle {
      desc(zh_CN, "迅疗", "释放纳米机器人极速修复机体")
    }
    healthMultiplier = 1.2f
    speedMultiplier = 1.05f
    color = Color.valueOf("73FFAE")

    effect = WaveEffect().apply {
      lifetime = 20f
      sides = 4
      sizeTo = 6f
      strokeFrom = 4f
      colorFrom = Color.valueOf("73FFAE")
      colorTo = Color.valueOf("50A385")
    }
  }
  val 熔融 = PercentStatus("melt", 0.2f, 0.25f, 25f, true) {
    bundle {
      desc(zh_CN, "熔融", "利用超高温的金属射流摧毁敌方单位的装甲及内部结构")
    }
    healthMultiplier = 0.8f
    speedMultiplier = 0.9f
    reloadMultiplier = 0.9f
    effect = Fx.melting
    color = Color.valueOf("FF5845")
  }
  val 衰变 = IceStatusEffect("decay") {
    bundle {
      desc(zh_CN, "衰变", "使原子迅速衰变,n被附着的单位会转变为放射源,持续辐射周围的一切事物")
    }
    reloadMultiplier = 0.8f
    healthMultiplier = 0.8f
    speedMultiplier = 0.8f
    damage = 1.25f
    color = Color.valueOf("A170F4")
    permanent = true

    effect = ParticleEffect().apply {
      particles = 4
      lifetime = 45f
      sizeFrom = 2f
      sizeTo = 0f
      length = 15f
      baseLength = 5f
      interp = slowFast
      sizeInterp = fastSlow
      colorFrom = Color.valueOf("A170F4")
      colorTo = Color.valueOf("774ACF")
    }
    setUpdate { unit, e ->
      Damage.status(null, unit.x, unit.y, unit.hitSize() * 1.5f, 辐射, 300f, true, true)
    }
    setDraw { unit ->
      Draw.z(Layer.shields)
      Draw.color(Color.valueOf("A170F4CC"))
      Fill.poly(unit.x, unit.y, 16, unit.hitSize() * 1.5f)
    }
  }
  val 蚀骨 = PercentStatus("boneErosion", 1f, 1.25f, 50f, true) {
    healthMultiplier = 0.7f
    speedMultiplier = 0.8f
    reloadMultiplier = 0.8f
    effect = ParticleEffect().apply {
      particles = 3
      lifetime = 20f
      sizeFrom = 3f
      sizeTo = 0f
      cone = 360f
      length = 30f
      interp = fastSlow
      colorFrom = Color.valueOf("FF666680")
      colorTo = Color.valueOf("FF6666")
    }
    color = Color.valueOf("FF6666")
    bundle {
      desc(zh_CN, "蚀骨", "烈焰如附骨之疽,除之不尽")
    }
  }
  val 突袭 = IceStatusEffect("pounces") {
    bundle {
      desc(zh_CN, "突袭")
    }
    damageMultiplier = 1.6f
    healthMultiplier = 1.8f
    speedMultiplier = 2f
    reloadMultiplier = 1.4f

    applyEffect = WaveEffect().apply {
      lifetime = 60f
      sides = 3
      sizeTo = 24f
      strokeFrom = 6f
      baseRotation = -150f
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("F15454")
    }

    var effect = ParticleEffect().apply {
      particles = 1
      lifetime = 85f
      line = true
      strokeFrom = 2f
      strokeTo = 0f
      lenFrom = 4f
      lenTo = 8f
      cone = 0f
      length = 60f
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("F15454")
    }

    color = Color.valueOf("F15454")

    setUpdate { unit, e ->
      if (Mathf.chanceDelta(effectChance.toDouble())) {
        Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2))
        effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation + 180f, this.color)
      }
    }
  }
  val 秽蚀 = IceStatusEffect("filthyErosion") {
    bundle {
      desc(zh_CN, "秽蚀", "打散目标的分子结构并干扰原子链,进而液化装甲与软组织", "污秽涌动,侵蚀不息")
    }
    damage = 5f
    effectChance = 0.2f
    color = Color.valueOf("AA88B2")
    effect = WaveEffect().apply {
      lifetime = 24f
      sides = 6
      sizeTo = 9f
      strokeFrom = 2f
      rotation = 90f
      colorFrom = Color.valueOf("E6C4EE")
      colorTo = Color.valueOf("AA88B2")
    }
    setUpdate { unit, e ->
      unit.armor -= 4f / 60f
      unit.maxHealth -= 50f / 60f
    }
    init {
      stats.add(IceStats.护甲降低, "护甲降低4/秒(永久)")
      stats.add(IceStats.生命上限降低, "50/秒")
    }
  }
  val 湍能 = PercentStatus("turbulentEnergy", 1f, 80f / 60f) {
    bundle {
      desc(zh_CN, "湍能", "利用剧烈反应的能量破坏表层装甲稳定性,而后侵蚀内部结构")
    }
    healthMultiplier = 0.9f
    color = Color.valueOf("A9D8FF")
    effect = WaveEffect().apply {
      lifetime = 20f
      sides = 3
      sizeTo = 6f
      strokeFrom = 4f
      baseRotation = 30f
      colorFrom = Color.valueOf("A9D8FF")
      colorTo = Color.valueOf("66B1FF")
    }
  }
  val 日耀 = PercentStatus("sunshine", 2f, 2.5f, 75f, true) {
    damageMultiplier = 0.8f
    healthMultiplier = 0.6f
    speedMultiplier = 0.7f
    reloadMultiplier = 0.7f
    bundle {
      desc(zh_CN, "日耀", "将太阳之力汇于指尖")
    }
    effect = ParticleEffect().apply {
      particles = 3
      lifetime = 20f
      sizeFrom = 3f
      sizeTo = 0f
      cone = 360f
      length = 30f
      interp = exp10In
      colorFrom = Color.valueOf("F1545480")
      colorTo = Color.valueOf("F15454")
    }

    color = Color.valueOf("F15454")
  }
  val 电链 = IceStatusEffect("electricChain") {
    color = Color.valueOf("C0ECFF")
    effectChance = 0.05f
    parentizeEffect = true
    effect = Fx.chainLightning

    bundle {
      desc(zh_CN, "电链", "闪电,会连击两次")
    }
    val range = 8 * 30f
    val reload = 60f
    val damage = 150f
    val status = 湍能
    val statusDuration = 600f
    val color = Color.valueOf("C0ECFF")
    var timer = 0f
    val all = Seq<Unit>()
    init {
      affinity(status) { unit, result, time ->
        unit.damagePierce(unit.type.health / 100 * 2)
        Fx.dynamicSpikes.wrap(Color.valueOf("C0ECFF"), unit.hitSize / 2).at(unit.x + Mathf.range(unit.bounds() / 2), unit.y + Mathf.range(unit.bounds() / 2))
        result.set(this, min(time + result.time, 60f))
      }
    }
    setUpdate { unit, e ->
      timer += Time.delta
      if (timer >= reload) {
        all.clear()
        Units.nearby(null, unit.x, unit.y, range) { other ->
          if (other.team == unit.team && other.hittable()) {
            all.add(other)
          }
        }
        all.sort { e ->
          e.dst2(unit.x, unit.y)
        }
        return@setUpdate
        if (all.size > 1) {
          var other = all.get(1)
          val absorber = Damage.findAbsorber(Vars.player.unit().team, unit.x, unit.y, other.x, other.y)
          absorber?.damagePierce(damage)
          other.unapply(this)
          other.apply(this, reload + 30)
          Sounds.shootBeamPlasma.at(unit)
          Fx.chainLightning.at(unit.x, unit.y, 0f, color, other)
          Fx.hitLaserBlast.at(other.x, other.y, unit.angleTo(other), color)
        } else {
          unit.apply(status, statusDuration)
          Sounds.shootPulsar.at(unit)
        }
        unit.damagePierce(damage)
        Fx.hitLaserBlast.at(unit.x, unit.y, 0f, color)
        timer = 0f
      }
    }
    init {
      stats.add(IceStats.连锁伤害, "$damage/次")
    }
  }
  val 坍缩 = IceStatusEffect("collapse") {
    disarm = true
    color = Color.valueOf("656565")
    speedMultiplier = 0f
    dragMultiplier = 0f
    buildSpeedMultiplier = 0f
    bundle {
      desc(zh_CN, "坍缩")
    }
  }
  val 幻像 = IceStatusEffect("illusion") {
    bundle {
      desc(
        zh_CN, "幻像", "通过全相投影装置,将光线转化为可以承受一定程度打击的固体形态,创造出作战单位的三维复制体"
      )
    }
    damageMultiplier = 0f
    color = Color.valueOf("AFCCF4")
    permanent = true
    setUpdate { u, e ->
      if (u.healthf() < 0.8f) u.kill()
    }
  }
  val 狂乱 = IceStatusEffect("frenzy") {
    damageMultiplier = 3f
    reloadMultiplier = 3f
    healthMultiplier = 0.2f
    speedMultiplier = 3f
    effect = Fx.overdriven
    color = Color.valueOf("D75B6E")
    bundle {
      desc(zh_CN, "狂乱", "混乱与疯狂")
    }
  }
  val 坚忍 = IceStatusEffect("stoical") {
    damageMultiplier = 1.15f
    reloadMultiplier = 1.4f
    speedMultiplier = 1.2f
    color = Color.valueOf("FFA665")
    effect = ParticleEffect().apply {
      particles = 1
      lifetime = 25f
      length = 0f
      sizeFrom = 4f
      sizeTo = 0f
      colorFrom = Color.valueOf("E8895C")
      colorTo = Color.valueOf("FFA665")
    }
    bundle {
      desc(zh_CN, "坚忍")
    }
  }
  val 庇护 = IceStatusEffect("asylum") {
    bundle {
      desc(zh_CN, "庇护", "为单位填充一层动能泡沫内衬,内衬在伸缩之间将大量分散装甲承受的猛烈冲击")
    }
    reloadMultiplier = 1.1f
    healthMultiplier = 3f
    speedMultiplier = 0.9f
    color = Color.valueOf("FFE18F")
    applyExtend = true
    effectChance = 0.1f

    applyEffect = WaveEffect().apply {
      lifetime = 60f
      sides = 4
      sizeFrom = 48f
      sizeTo = 0f
      strokeFrom = 0f
      strokeTo = 3f
      interp = exp10Out
      colorFrom = Color.valueOf("FFD37F")
      colorTo = Color.valueOf("FFD37F")
    }

    effect = ParticleEffect().apply {
      lifetime = 30f
      particles = 3
      line = true
      strokeFrom = 2f
      strokeTo = 0f
      lenFrom = 6f
      lenTo = 6f
      cone = 360f
      length = 40f
      colorFrom = Color.valueOf("FFE18F")
      colorTo = Color.valueOf("F8C26600")
    }
  }
  val 复仇 = IceStatusEffect("revenge") {
    bundle {
      desc(zh_CN, "复仇")
    }
    damageMultiplier = 1.8f
    healthMultiplier = 0.8f
    speedMultiplier = 1.1f
    reloadMultiplier = 1.5f
    permanent = true
    color = Color.valueOf("F15454")

    effect = ParticleEffect().apply {
      particles = 3
      lifetime = 25f
      sizeFrom = 2f
      cone = 360f
      length = 15f
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("F15454")
    }
  }
  val 反扑 = IceStatusEffect("counter") {
    bundle {
      desc(zh_CN, "反扑", "每损失1%生命值获得1.5%各项属性值")
    }
    color = Color.valueOf("FB7A83")
    effectChance = 0.01f
    setUpdate { unit, e ->
      val heal = 1f + 1.5f * (1 - unit.healthf())
      unit.speedMultiplier *= heal
      unit.healthMultiplier *= heal
      unit.damageMultiplier *= heal
      unit.reloadMultiplier *= heal
    }
  }
  val 作弊 = IceStatusEffect("cheat") {
    bundle {
      desc(zh_CN, "作弊", "没关就是开了?")
    }
    damageMultiplier = 99e9f
    healthMultiplier = 99e9f
    speedMultiplier = 99e9f
    reloadMultiplier = 99e9f
    buildSpeedMultiplier = 99e9f
    color = Color.valueOf("FF0000")
    effect = ParticleEffect().apply {
      particles = 3
      lifetime = 45f
      length = 0f
      baseLength = 15f
      cone = 360f
      sizeFrom = 4f
      sizeTo = 0f
      interp = fastSlow
      colorFrom = Color.valueOf("FF0000")
      colorTo = Color.valueOf("FFFFFF")
    }
  }
  val 斩杀 = IceStatusEffect("kill") {
    bundle {
      desc(zh_CN, "斩杀")
    }
    setUpdate { unit, e ->
      val x = unit.x
      val y = unit.y
      val size = unit.hitSize
      if (unit.maxHealth <= unit.type.health * 0.05 && !unit.dead) {
        unit.kill()
        IceEffects.prismaticSpikes.wrap(Color.valueOf("F15454"), size).at(x, y)
        Damage.status(null, x, y, size * 1.5f, this, 300f, true, true)
        Damage.damage(null, x, y, size, unit.type.health * 0.05f)
      }
      unit.maxHealth -= 10
      if (unit.health > unit.maxHealth) unit.health = unit.maxHealth
    }
    init {
      stats.add(IceStats.生命上限降低, "${10 * 60}/秒")
      stats.add(IceStats.斩杀生命值, "5%")
    }
    color = Color.valueOf("A04553")
    effect = WaveEffect().apply {
      lifetime = 60f
      sides = 4
      sizeTo = 9f
      strokeFrom = 2f
      colorFrom = Color.valueOf("F15454")
      colorTo = Color.valueOf("F15454")
    }
  }
  var 结晶化 = IceStatusEffect("crystallize") {
    bundle {
      desc(zh_CN, "结晶化", "FEX物质在单位表面富集结晶产生不稳定的晶体壳,使单位会与活性的FEX结晶相互作用,同时在受到攻击时会造成额外的衍生伤害")
    }
    speedMultiplier = 0.34f
    reloadMultiplier = 0.8f

    effect = SglFx.crystalFragFex
    effectChance = 0.1f

    init {
      stats.add(SglStat.damagedMultiplier, "115%")
      stats.add(SglStat.effect) { t ->
        t.defaults().left().padLeft(5f)
        t.row()
        t.table { a ->
          a.add(Core.bundle.get("infos.attach"))
          a.image(相位态FEX流体.uiIcon).size(25f)
          a.add(相位态FEX流体.localizedName).color(Pal.accent)
        }
        t.row()
        t.add(Core.bundle.format("infos.shots", 3))
        t.row()
        t.table(Tex.underline) { b -> UIUtils.buildAmmo(b, 破碎FEX结晶) }.padLeft(10f)
      }
    }
    setUpdate { unit, entry ->
      val t = unit.tileOn()
      val p = Puddles.get(t)
      if (t != null && p != null && p.liquid === 相位态FEX流体 && Mathf.chanceDelta(0.02)) {
        for (i in 0..2) {
          val len = Mathf.random(1f, 7f)
          val a = Mathf.range(360f)
          破碎FEX结晶.create(
            null, Team.derelict, unit.x + Angles.trnsx(a, len), unit.y + Angles.trnsy(a, len), a, Mathf.random(0.2f, 1f), Mathf.random(0.6f, 1f)
          )
        }
      }

      val health = lastHealth[unit]?:0f
      if (health != unit.health) {
        if (health - 10 > unit.health) {
          val damageBase = health - unit.health
          //crystallize
          unit.damage(damageBase * 0.15f)
        }
        lastHealth.put(unit, unit.health)
      }

    }

  }
  var 暮春 = IceStatusEffect("wild_growth") {
    bundle {
      desc(zh_CN, "暮春", "受力场控制的纳米机器人会干扰单位的行动,破坏其设施")
    }
    color = Tmp.c1.set(Pal.heal).lerp(Color.black, 0.25f).cpy()
    speedMultiplier = 0.3f
    reloadMultiplier = 1.2f
    damageMultiplier = 0.6f
    damage = 1.5f
  }
  var 临春 = IceStatusEffect("spring_coming") {
    bundle {
      desc(zh_CN, "临春", "纳米机器人矩阵会在力场的引导下为单位提供增益")
    }
    color = Pal.heal
    speedMultiplier = 1.23f
    reloadMultiplier = 1.16f
    damageMultiplier = 1.1f
    damage = -1f
  }
  var 电子干扰 = IceStatusEffect("electric_disturb") {
    bundle {
      desc(zh_CN, "电子干扰", "电子设备受到外部干扰,火控系统将无法正常工作")
    }
    init {
      color = Pal.accent
      stats.addPercent(Stat.damageMultiplier, 0.8f)
      stats.addPercent(Stat.speedMultiplier, 0.6f)
      stats.addPercent(Stat.reloadMultiplier, 0.75f)
      stats.add(Stat.damage, 12f, StatUnit.perSecond)
      stats.add(SglStat.special) { t ->
        t.row()
        t.add(Core.bundle.format("data.bulletDeflectAngle", 12.4f.toString() + StatUnit.degrees.localized()))
        t.row()
        t.add("[lightgray]" + Core.bundle.get("infos.attenuationWithTime") + "[]").padLeft(15f)
      }
    }

    setUpdate { unit, entry ->
      val scl = Mathf.clamp(entry.time / 120)
      unit.shield -= 0.4f * (entry.time / 120) * Time.delta
      unit.damageContinuousPierce(0.2f * (entry.time / 120))
      unit.speedMultiplier *= (0.6f + 0.4f * (1 - scl))
      unit.damageMultiplier *= (0.8f + 0.2f * (1 - scl))
      unit.reloadMultiplier *= (0.75f + 0.25f * (1 - scl))
    }
  }
  var 锁定 = IceStatusEffect("locking") {
    bundle {
      desc(zh_CN, "锁定", "单位受到的攻击有概率造成更高的伤害,这取决于锁定的强度")
    }
    color = Pal.remove
    init {
      stats.add(SglStat.damagedMultiplier, Core.bundle.get("infos.lockingMult"))
      stats.add(SglStat.damageProbably, Core.bundle.get("infos.lockingProb"))
    }
    setDraw { unit ->
      Draw.z(Layer.overlayUI)
      Draw.color(Pal.gray)
      Fill.square(unit.x, unit.y, 2f)
      Draw.color(Pal.remove)
      Fill.square(unit.x, unit.y, 1f)
      Drawf.square(unit.x, unit.y, unit.hitSize, Pal.remove)
      Tmp.v1.set(unit.hitSize + 4, 0f)
      Tmp.v2.set(unit.hitSize + 12, 0f)

      for (i in 0..3) {
        Drawf.line(
          Pal.remove, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.x + Tmp.v2.x, unit.y + Tmp.v2.y
        )
        Tmp.v1.rotate90(1)
        Tmp.v2.rotate90(1)
      }
    }
    setUpdate { unit, entry ->

      val health = lastHealth.get(unit, unit.health)
      if (health != unit.health) {
        if (health - 10 > unit.health) {
          val damageBase = health - unit.health

          //locking
          val str = unit.getDuration(this)
          if (Mathf.chance((0.1f + str / 100).toDouble())) {
            unit.damage(damageBase * 12 * str / 100f, false)
          }
        }
        lastHealth.put(unit, unit.health)
      }
    }
  }
  var 熔毁 = object : IceStatusEffect("meltdown", {
    bundle {
      desc(zh_CN, "熔毁")
    }
    damage = 2.2f
    effect = Fx.melting
    init {
      opposites(StatusEffects.freezing, StatusEffects.wet)
      affinity(StatusEffects.tarred, TransitionHandler { unit: Unit?, result: StatusEntry?, time: Float ->
        unit!!.damagePierce(8f)
        Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f))
        result!!.set(this, 180 + result.time)
      })

      affinity(冻结, TransitionHandler { e: Unit, s: StatusEntry, t: Float ->
        e.damage(t)
        s.time -= t
      })

      transs(凛冻, TransitionHandler { e: Unit?, s: StatusEntry?, t: Float ->
        s!!.time -= t
        e!!.apply(StatusEffects.blasted)
        e.damage(max(e.getDuration(凛冻), t) / 2f)
      })
      stats.add(SglStat.exShieldDamage, Core.bundle.get("infos.meltdownDamage"))
    }
    setUpdate { unit, entry ->
      if (unit.shield > 0) {
        unit.shieldAlpha = 1f
        unit.shield -= Time.delta * entry.time / 6
      }
    }
  }) {
    override fun draw(unit: Unit, time: Float) {
      super.draw(unit, time)

      SglDraw.drawBloomUponFlyUnit<Unit?>(unit, DrawAcceptor { u: Unit ->
        val rate = Mathf.clamp(90 / (time / 30))
        Lines.stroke(2.2f * rate, Pal.lighterOrange)
        Draw.alpha(rate * 0.7f)
        Lines.circle(u.x, u.y, u.hitSize / 2 + rate * u.hitSize / 2)
        rand.setSeed(unit.id.toLong())
        for (i in 0..7) {
          SglDraw.drawTransform(u.x, u.y, u.hitSize / 2 + rate * u.hitSize / 2, 0f, Time.time + rand.random(360f)) { x: Float, y: Float, r: Float ->
            val len = rand.random(u.hitSize / 4, u.hitSize / 1.5f)
            SglDraw.drawDiamond(x, y, len, len * 0.135f, r)
          }
        }
        Draw.reset()
      })
    }
  }
  var 电磁损毁 = IceStatusEffect("emp_damaged") {
    bundle {
      desc(zh_CN, "电磁损毁", "单位的系统中枢及各周边电子设备严重损毁,火控核心几乎失效,所有功能设备完全失效,近乎废铁")
    }
    color = Pal.accent
    speedMultiplier = 0.5f
    buildSpeedMultiplier = 0.1f
    reloadMultiplier = 0.6f
    damageMultiplier = 0.7f
    init {
      stats.add(SglStat.effect) { t ->
        t.defaults().left().padLeft(5f)
        t.row()
        t.add(Core.bundle.format("data.bulletDeflectAngle", "45" + StatUnit.degrees.localized())).color(Color.lightGray)
        t.row()
        t.add(Core.bundle.get("infos.banedAbilities")).color(Color.lightGray)
        t.row()
        t.add(Core.bundle.get("infos.empDamagedInfo"))
      }
    }
    setUpdate { unit, entry ->
      if (Sgl.empHealth.empDamaged(unit)) {
        if (unit.getDuration(this) <= 60) {
          unit.apply(this, 60f)
        } else {
          unit.speedMultiplier = 0.01f
          unit.reloadMultiplier = 0f
          unit.buildSpeedMultiplier = 0f
        }

        unit.shield = 0f
        unit.damageContinuousPierce((1 - Sgl.empHealth.healthPresent(unit)) * Sgl.empHealth.get(unit).model.empContinuousDamage)

        for (i in unit.abilities.indices) {
          if (unit.abilities[i] !is BanedAbility) {
            val baned = Pools.obtain(BanedAbility::class.java, ::BanedAbility)
            baned.index = i
            baned.masked = unit.abilities[i]
            unit.abilities[i] = baned
          }
        }
      } else {
        unit.unapply(this)
      }
    }

  }

  var 冻结: IceStatusEffect = object : IceStatusEffect("frost", {
    bundle {
      desc(zh_CN, "冻结", "在极低的温度下,单位的系统将很难正常工作,在寒气完全渗透到单位的核心后,它将被冻成一个巨大的冰块")
    }
    color = SglDrawConst.frost
    speedMultiplier = 0.5f
    reloadMultiplier = 0.8f
    effect = Fx.freezing

    init {
      opposites(StatusEffects.burning, StatusEffects.melting)
      affinity(熔毁, TransitionHandler { e: Unit, s: StatusEntry, t: Float ->
        e.damage(s.time)
        s.time -= t
      })
      stats.add(SglStat.effect) { t ->
        t.add(Core.bundle.get("infos.frostInfo"))
        t.image(凛冻.uiIcon).size(25f)
        t.add(凛冻.localizedName).color(Pal.accent)
      }
    }
    setUpdate { unit, entry ->
      if (entry.time >= 30 * unit.hitSize + unit.maxHealth / unit.hitSize) {
        if (unit.getDuration(凛冻) <= 0) {
          unit.unapply(this)
          unit.apply(凛冻, max(entry.time / 2, 180f))
        }
      }
    }
  }) {
    override fun draw(unit: Unit, time: Float) {
      super.draw(unit)
      if (unit.hasEffect(凛冻)) return
      val rate = time / (30 * unit.hitSize + unit.maxHealth / unit.hitSize)
      rand.setSeed(unit.id.toLong())
      val ro = rand.random(360).toFloat()
      Draw.color(SglDrawConst.frost)
      Draw.alpha(0.85f * rate)
      Draw.z(Layer.flyingUnit)
      SglDraw.drawDiamond(unit.x, unit.y, unit.hitSize * 2.35f * rate, unit.hitSize * 2 * rate, ro, 0.2f * rate)
    }
  }
  var 凛冻: IceStatusEffect = object : IceStatusEffect("frost_freeze", {
    bundle {
      desc(zh_CN, "凛冻", "单位被寒气被彻底冰封,无法行动,如果寒气继续加深,在冰块碎裂时,它会彻底碎成一堆粉末")
    }
    speedMultiplier = 0f
    reloadMultiplier = 0f
    dragMultiplier = 10f
    effect = SglFx.particleSpread

    init {
      opposites(StatusEffects.burning, StatusEffects.melting)
      stats.add(SglStat.effect) { t ->
        t.image(冻结.uiIcon).size(25f)
        t.add(冻结.localizedName).color(Pal.accent)
        t.add(Core.bundle.get("infos.frostFreezeInfo"))
      }
    }
  }) {

    override fun update(unit: Unit, entry: StatusEntry) {
      super.update(unit, entry)
      if (unit.getDuration(冻结) >= 60 * unit.hitSize + 3 * unit.maxHealth / unit.hitSize) {
        Fx.pointShockwave.at(unit.x, unit.y)
        SglFx.freezingBreakDown.at(unit.x, unit.y, 0f, unit)
        unit.kill()
        unit.unapply(this)
        Effect.shake(8f, 8f, unit)
      }
    }

    override fun draw(unit: Unit) {
      super.draw(unit)
      rand.setSeed(unit.id.toLong())
      val ro = rand.random(360).toFloat()

      val time = unit.getDuration(冻结)
      val rate = time / (60 * unit.hitSize + 3 * unit.maxHealth / unit.hitSize)
      Draw.color(SglDrawConst.frost, SglDrawConst.winter, rate)
      Draw.alpha(0.85f)
      Draw.z(Layer.flyingUnit)
      SglDraw.drawDiamond(unit.x, unit.y, unit.hitSize * 2.35f, unit.hitSize * 2, ro, 0.3f)

      Draw.alpha(0.7f)
      val n = (unit.hitSize / 8 + rand.random(2, 5)).toInt()
      for (i in 0..<n) {
        val v = rand.random(0.75f)
        val re = 1 - Mathf.clamp((1 - rate - v) / (1 - v))

        val off = rand.random(unit.hitSize * 0.5f, unit.hitSize)
        val len = rand.random(unit.hitSize) * re
        val wid = rand.random(unit.hitSize * 0.4f, unit.hitSize * 0.8f) * re
        val rot = rand.random(360).toFloat()

        SglDraw.drawDiamond(unit.x + Angles.trnsx(rot, off), unit.y + Angles.trnsy(rot, off), len, wid, rot, 0.2f)
      }
    }
  }

  class BanedAbility : Ability(), Pool.Poolable {
    var masked: Ability? = null
    var index: Int = -1
    override fun update(unit: Unit) {
      if (!unit.hasEffect(电磁损毁)) {
        unit.abilities[index] = masked
        Pools.free(this)
      }
    }

    override fun reset() {
      masked = null
      index = -1
    }
  }
}