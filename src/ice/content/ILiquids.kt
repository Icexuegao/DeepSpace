package ice.content

import ice.Ice
import ice.library.baseContent.BaseContentSeq
import ice.library.baseContent.liquid.IceLiquid
import mindustry.Vars

object ILiquids {
    fun load() {
        Vars.content.liquids().forEach {
            if (it.minfo.mod== Ice.ice){
                BaseContentSeq.liquids.add(it as IceLiquid)
            }
        }
    }

    val 浓稠血浆 = IceLiquid("thickPlasma", "cc3737").apply {
        nutrientConcentration = 0.2f
    }
    val 沼气 = IceLiquid("methane", "bb2912").apply {
        gas = true
        explosiveness = 0.5f
        flammability = 0.8f
    }
    val 灵液 = IceLiquid("ichor", "ffaa5f").apply {
        viscosity = 0.7f
        boilPoint = 1.7f
    }
    val 氦气 = IceLiquid("helium", "f2ffbd").apply {
        explosiveness = 0.3f
        flammability = 1f
        gas = true
    }
}
