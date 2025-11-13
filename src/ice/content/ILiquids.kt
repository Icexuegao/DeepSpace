package ice.content

import ice.library.content.blocks.abstractBlocks.IceBlock.Companion.desc
import ice.library.content.liquid.IceLiquid
import ice.library.entities.IcePuddle
import ice.ui.BaseBundle.Companion.bundle

object ILiquids {
    fun load() = Unit
    val 腐殖浆体 = IceLiquid("humusSlurry", "a09bbd") {
        viscosity = 0.6f
        temperature = 0.3f
        bundle {
            desc(zh_CN, "腐殖浆体", "一种富含有机质的浆体,可用于土壤改良")
        }
    }
    val 温热孢液 = IceLiquid("warmSpore", "fa9c28") {
        temperature = 0.8f
        viscosity = 0.5f
        bundle {
            desc(zh_CN, "温热孢液", "一种温暖的孢子悬浮液，具有生物活性")
        }
    }
    val 芥蒂液 = IceLiquid("cressLiquid", "7f7f7f") {
        viscosity = 0.4f
        temperature = 0.5f
        bundle {
            desc(zh_CN, "芥蒂液", "一种灰色的中性液体，可用于中和反应")
        }
    }

    val 异溶质 = IceLiquid("strangeSolute", "9AA8E7") {
        heatCapacity = 0.4f
        boilPoint = 0.5f
        bundle {
            desc(zh_CN, "异溶质", "用于冷却机器和废物处理,和水类似")
        }
    }
    val 浓稠血浆 = IceLiquid("thickPlasma", "cc3737") {
        nutrientConcentration = 0.2f
        bundle {
            desc(zh_CN, "浓稠血浆", "从朔方蔓延而来")
        }
        setUpdate { pud ->
            pud.buildOn()?.let {
                if (pud is IcePuddle) {
                    if (it.team != pud.team) {
                        it.damage(30f / 60f)
                    }
                }
            }
        }
    }
    val 沼气 = IceLiquid("methane", "bb2912") {
        gas = true
        explosiveness = 0.5f
        flammability = 0.8f
        bundle {
            desc(zh_CN, "沼气", "主要成分是甲烷,可以替代部分工厂的燃料需求")
        }
    }
    val 灵液 = IceLiquid("ichors", "ffaa5f") {
        viscosity = 0.7f
        boilPoint = 1.7f
        bundle {
            desc(zh_CN, "灵液", "一种酸性极强的溶液,可以用来处理金属")
        }
    }
    val 氦气 = IceLiquid("helium", "f2ffbd") {
        explosiveness = 0.3f
        flammability = 1f
        gas = true
        bundle {
            desc(zh_CN, "氦气", "虽然不可燃烧,但是处理之后依旧可形成高能燃料")
        }
    }
    val 暮光液 = IceLiquid("duskLiquid", "deedff") {
        temperature = 0.2f
        bundle {
            desc(zh_CN, "暮光液", "暮光液")
        }
    }
}
