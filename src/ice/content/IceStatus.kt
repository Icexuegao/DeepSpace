package ice.content

import arc.graphics.Color
import ice.Ice
import ice.library.type.baseContent.BaseContentSeq
import ice.library.type.baseContent.status.IceStatusEffect
import mindustry.Vars
import mindustry.content.Fx

object IceStatus {
    fun laod() {
        Vars.content.statusEffects().forEach {
            if (it.minfo.mod == Ice.ice) {
                BaseContentSeq.status.add(it as IceStatusEffect)
            }
        }
    }

    val 集群 = IceStatusEffect("assemble").apply {
        speedMultiplier = 1.5f
    }
    val 圣火 = IceStatusEffect("holyFlame").apply {
        damage = 70 / 60f
    }
    val 邪火 = IceStatusEffect("evilFlame").apply {
        damage = 75 / 60f
    }
    val 穿甲 = IceStatusEffect("armorPiercing").apply {
        speedMultiplier = 1.5f
        armorBreakPercent = 0.8f
    }
    val 破甲I = IceStatusEffect("armorBreakI").apply {
        speedMultiplier = 1.1f
        armorBreak = 10
    }
    val 破甲II = IceStatusEffect("armorBreakII").apply {
        speedMultiplier = 1.1f
        armorBreak = 20
    }
    val 破甲III = IceStatusEffect("armorBreakIII").apply {
        speedMultiplier = 1.1f
        armorBreak = 30
    }
    val 电磁脉冲 = IceStatusEffect("electromagneticPulse").apply {
        speedMultiplier = 0.2f
        healthMultiplier = 0.9f
    }
    val 流血 = IceStatusEffect("bleed").apply {
        damage = 1f
        color = Color.red
        effect = Fx.absorb
    }
    val 回响 = IceStatusEffect("resound").apply {
        speedMultiplier = 0.5f
        color = Color.red
        effect = Fx.absorb
    }
    val 搏动 = IceStatusEffect("throb").apply {
        healthMultiplier = 1.7f
        speedMultiplier = 1.4f
        color = Color.red
        effect = Fx.absorb
    }
    val 寄生 = IceStatusEffect("parasitism").apply {
        healthMultiplier = 1.2f
    }
    val 融合 = IceStatusEffect("merge").apply {
        healthMultiplier = 1.5f
    }
    val 维生I=IceStatusEffect("vitalFixI").apply {
        damage = -30f/60
    }
    val 维生II=IceStatusEffect("vitalFixII").apply {
        damage = -60f/60
    }
}
