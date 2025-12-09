package ice.content

import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Interp.*
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import ice.library.world.ContentLoad
import ice.entities.effect.MultiEffect
import ice.world.content.status.PercentStatus
import ice.world.content.status.StatusEffect
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Damage
import mindustry.entities.Units
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.game.EventType
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
object IStatus : ContentLoad {
    val 封冻 = StatusEffect("freeze") {
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
            affinitys(StatusEffects.blasted) { unit, result, time ->
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
    val 集群 = StatusEffect("assemble") {
        speedMultiplier = 1.5f
        bundle {
            desc(zh_CN, "集群", "激活协同协议,单位间建立纳米机器人共享网络,效应随范围内友方单位数量增强")
        }
    }
    val 圣火 = StatusEffect("holyFlame") {
        damage = 70 / 60f
        bundle {
            desc(zh_CN, "圣火", "持续造成目标最大生命值百分比的火焰伤害")
        }
    }
    val 邪火 = StatusEffect("evilFlame") {
        damage = 75 / 60f
        bundle {
            desc(zh_CN, "邪火", "持续生命侵蚀,扣除单位生命上限")
        }
    }
    val 破甲I = StatusEffect("armorBreakI") {
        healthMultiplier = 0.8f
        speedMultiplier = 1.2f
        color = Color.valueOf("D1EFFF")
        bundle {
            desc(zh_CN, "破甲I", "目标单位护甲扣除,使其遭受的伤害显著提升")
        }
    }
    val 破甲II = StatusEffect("armorBreakII") {
        speedMultiplier = 1.1f
        armorBreak = 10f
        bundle {
            desc(zh_CN, "破甲II", "目标单位护甲扣除,使其遭受的伤害显著提升")
        }
    }
    val 破甲III = StatusEffect("armorBreakIII") {
        speedMultiplier = 1.1f
        armorBreak = 20f
        bundle {
            desc(zh_CN, "破甲III", "目标单位护甲扣除,使其遭受的伤害显著提升")
        }
    }
    val 破甲IV = StatusEffect("armorBreakIV") {
        speedMultiplier = 1.1f
        armorBreak = 30f
        bundle {
            desc(zh_CN, "破甲IV", "目标单位护甲扣除,使其遭受的伤害显著提升")
        }
    }
    val 穿甲 = StatusEffect("armorPiercing") {
        speedMultiplier = 1.5f
        armorBreakPercent = 0.8f
        bundle {
            desc(zh_CN, "穿甲", "完全无视目标护甲,直接穿透对本体造成伤害")
        }
    }
    val 电磁脉冲 = StatusEffect("electromagneticPulse") {
        speedMultiplier = 0.7f
        healthMultiplier = 0.9f
        bundle {
            desc(zh_CN, "电磁脉冲", "突发宽带电磁辐射的高强度脉冲,用于破坏敌人的电子设备")
        }
    }
    val 辐射 = StatusEffect("radiation") {
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
    val 染血 = StatusEffect("stainedBlood") {
        speedMultiplier = 0.8f
        bundle {
            desc(zh_CN, "染血", "染血")
        }
    }
    val 憎恨 = StatusEffect("hatred") {
        bundle {
            desc(zh_CN, "憎恨", "憎恨")
        }
    }
    val 流血 = StatusEffect("bleed") {
        color = Color.red
        bundle {
            desc(zh_CN, "流血", "流血")
        }
        setUpdate { u, s ->
            u.health -= (u.speed() * u.hitSize() / 60)
            u.clampHealth()
        }
        setStatsFun {
            stats.add(Stat.damage, "[negstat][hitSize]*[speed]${StatUnit.perSecond.localized()}[]")
        }
    }
    val 回响 = StatusEffect("resound") {
        speedMultiplier = 0.5f
        effect = Fx.absorb
        bundle {
            desc(zh_CN, "回响", "回响")
        }
    }
    val 搏动 = StatusEffect("throb") {
        healthMultiplier = 1.7f
        speedMultiplier = 1.4f
        effect = Fx.absorb
        bundle {
            desc(zh_CN, "搏动", "搏动")
        }
    }
    val 寄生 = StatusEffect("parasitism") {
        healthMultiplier = 0.9f
        speedMultiplier = 0.9f
        bundle {
            desc(zh_CN, "寄生", "寄生状态会逐渐消耗单位生命值致其死亡,随后生成血肉单位")
        }
    }
    val 融合 = StatusEffect("merge") {
        healthMultiplier = 1.5f
        bundle {
            desc(zh_CN, "融合", "当血肉单位满足特定条件时,融合状态触发,逐渐靠近结合,属性整合提升")
        }
    }
    val 维生I = StatusEffect("vitalFixI") {
        damage = -30f / 60
        bundle {
            desc(zh_CN, "维生I", "激活纳米机器人集群,将储存的硅矿微粒与裂解液转化为生物修复单元,持续重构受损机体")
        }
    }
    val 维生II = StatusEffect("vitalFixII") {
        damage = -60f / 60
        bundle {
            desc(zh_CN, "维生II", "激活纳米机器人集群,将储存的硅矿微粒与裂解液转化为生物修复单元,持续重构受损机体")
        }
    }
    val 脉冲 = StatusEffect("pulse") {
        setUpdate { unit, e ->
            if (unit.shield > 0) {
                val damage = max(unit.type.health / 100, unit.shield / 100)
                unit.damageContinuousPierce(damage / 60)
            }
        }
        setStatsFun {
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
    val 鼓舞 = StatusEffect("inspire") {
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
                zh_CN,
                "鼓舞",
                "为周围友军持续提供(具有鼓舞的单位)20%的属性倍率,且每秒回复相当于(具有鼓舞的单位)0.1%生命上限的生命值"
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
        val effects = ParticleEffect().apply {
            particles = 1
            lifetime = 60f
            line = true
            strokeFrom = 0f
            strokeTo = 1.2f
            lenFrom = 16f
            lenTo = 22.62f
            cone = 0f
            length = -40f
            offsetX = 13.85f
            baseLength = 32f
            baseRotation = 0f
            randLength = false
            interp = pow5Out
            sizeInterp = pow2In
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("F15454")
        }
        applyEffect = MultiEffect(effects, ParticleEffect().apply {
            particles = 1
            lifetime = 60f
            line = true
            strokeFrom = 0f
            strokeTo = 1.2f
            lenFrom = 16f
            lenTo = 22.62f
            cone = 0f
            length = -40f
            offsetX = 19.6f
            offsetY = -9.8f
            baseLength = 32f
            baseRotation = 120f
            randLength = false
            interp = pow5Out
            sizeInterp = pow2In
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("F15454")
        }, ParticleEffect().apply {
            particles = 1
            lifetime = 60f
            line = true
            strokeFrom = 0f
            strokeTo = 1.2f
            lenFrom = 16f
            lenTo = 22.62f
            cone = 0f
            length = -40f
            offsetX = 9.8f
            offsetY = -9.8f
            baseLength = 32f
            baseRotation = 240f
            randLength = false
            interp = pow5Out
            sizeInterp = pow2In
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("F15454")
        })
    }
    val 过热 = StatusEffect("overheat") {
        bundle {
            desc(
                zh_CN,
                "过热",
                "过载动力炉并重导向其能量配给以进行主炮开火或特殊行动,期间过量的能量可能损坏管路",
                "以此为信"
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
    val 屠戮 = StatusEffect("massacre") {
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
    val 损毁 = StatusEffect("destroyed") {
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
    val 衰变 = StatusEffect("decay") {
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
    val 突袭 = StatusEffect("pounces") {
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

        effect = ParticleEffect().apply {
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
                this.effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation + 180, this.color)
            }
        }
    }
    val 秽蚀 = StatusEffect("filthyErosion") {
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
        setStatsFun {
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
    val 电链 = StatusEffect("electricChain") {
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
        val all = Seq<mindustry.gen.Unit>()
        init {
            affinitys(status) { unit, result, time ->
                unit.damagePierce(unit.type.health / 100 * 2)
                Fx.dynamicSpikes.wrap(Color.valueOf("C0ECFF"), unit.hitSize / 2)
                    .at(unit.x + Mathf.range(unit.bounds() / 2), unit.y + Mathf.range(unit.bounds() / 2))
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
                    Sounds.spark.at(unit)
                    Fx.chainLightning.at(unit.x, unit.y, 0f, color, other)
                    Fx.hitLaserBlast.at(other.x, other.y, unit.angleTo(other), color)
                } else {
                    unit.apply(status, statusDuration)
                    Sounds.pulseBlast.at(unit)
                }
                unit.damagePierce(damage)
                Fx.hitLaserBlast.at(unit.x, unit.y, 0f, color)
                timer = 0f
            }
        }
        setStatsFun {
            stats.add(IceStats.连锁伤害, "$damage/次")
        }
    }
    val 坍缩 = StatusEffect("collapse") {
        disarm = true
        color = Color.valueOf("656565")
        speedMultiplier = 0f
        dragMultiplier = 0f
        buildSpeedMultiplier = 0f
        bundle {
            desc(zh_CN, "坍缩")
        }
    }
    val 幻像 = StatusEffect("illusion") {
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
    val 狂乱 = StatusEffect("frenzy") {
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
    val 坚忍 = StatusEffect("stoical") {
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
    val 庇护 = StatusEffect("asylum") {
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
    val 复仇 = StatusEffect("revenge") {
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
    val 反扑 = StatusEffect("counter") {
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
    val 作弊 = StatusEffect("cheat") {
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
    val 斩杀 = StatusEffect("kill") {
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
        setStatsFun {
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
}