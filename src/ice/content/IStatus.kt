package ice.content

import arc.graphics.Color
import ice.library.content.blocks.abstractBlocks.IceBlock.Companion.desc
import ice.library.content.status.IceStatusEffect
import ice.ui.BaseBundle.Companion.bundle
import mindustry.content.Fx
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

object IStatus {
    fun load() = Unit
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

    val 穿甲 = IceStatusEffect("armorPiercing") {
        speedMultiplier = 1.5f
        armorBreakPercent = 0.8f
        bundle {
            desc(zh_CN, "穿甲", "完全无视目标护甲,直接穿透对本体造成伤害")
        }
    }

    val 破甲I = IceStatusEffect("armorBreakI") {
        speedMultiplier = 1.1f
        armorBreak = 10f
        bundle {
            desc(zh_CN, "破甲I", "目标单位护甲扣除,使其遭受的伤害显著提升")
        }
    }

    val 破甲II = IceStatusEffect("armorBreakII") {
        speedMultiplier = 1.1f
        armorBreak = 20f
        bundle {
            desc(zh_CN, "破甲II", "目标单位护甲扣除,使其遭受的伤害显著提升")
        }
    }

    val 破甲III = IceStatusEffect("armorBreakIII") {
        speedMultiplier = 1.1f
        armorBreak = 30f
        bundle {
            desc(zh_CN, "破甲III", "目标单位护甲扣除,使其遭受的伤害显著提升")
        }
    }

    val 电磁脉冲 = IceStatusEffect("electromagneticPulse") {
        speedMultiplier = 0.2f
        healthMultiplier = 0.9f
        bundle {
            desc(zh_CN, "电磁脉冲", "突发宽带电磁辐射的高强度脉冲,用于破坏敌人的电子设备")
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
        setStatsFun {
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
        healthMultiplier = 1.2f
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
}
