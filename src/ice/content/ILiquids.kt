package ice.content

import arc.graphics.Color
import arc.struct.ObjectSet
import ice.library.world.ContentLoad
import ice.entities.IcePuddle
import ice.world.content.liquid.IceLiquid
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Fx.flakExplosionBig
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.type.CellLiquid

object ILiquids: ContentLoad {
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
    val 废水 = IceLiquid("wasteWater", "666666") {
        effect = IStatus.辐射
        heatCapacity = 0.25f
        viscosity = 0.99f
        temperature = 1.5f
        bundle {
            desc(zh_CN, "废水", "一种由工业生产排放的强放射性废水,被其污染过的地区极难再次使用")
        }
    }
    val 异溶质 = IceLiquid("strangeSolute", "9AA8E7") {
        heatCapacity = 0.4f
        boilPoint = 0.5f
        bundle {
            desc(zh_CN, "异溶质", "一种极性分子组成的无机液体,用于冷却机器和废物处理,和水类似")
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
            desc(zh_CN, "沼气", "一种天然气体,主要成分是甲烷,可替代部分工厂的燃料需求")
        }
    }
    val 急冻液= IceLiquid("swiftCryofluid","E1E9F0"){
        lightColor = Color.valueOf("E1E9F09A")
        effect = StatusEffects.freezing
        heatCapacity = 1.4f
        viscosity = 0.6f
        temperature = 0.15f
        bundle {
         desc(zh_CN, "急冻液", "由低温化合物与冷却液混合而成,比冷却液效果更强")
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
            desc(zh_CN, "氦气", "一种惰性气体,虽然不可直接燃烧,但是处理后可形成高能燃料")
        }
    }
    val 血肉赘生物= CellLiquid("bloodSlime").apply{
        incinerable = false
        cells = 8
        maxSpread = 0.5f
        spreadDamage = 0.1f
        spreadTarget = Liquids.water
        spreadConversion = 0.5f
        effect = IStatus.熔融
        colorFrom = Color.valueOf("FF5845")
        colorTo = Color.valueOf("BF3E47")
        color = Color.valueOf("C74E48")
        lightColor = Color.valueOf("C74E489A")
        canStayOn = ObjectSet.with(Liquids.water)
        particleEffect = flakExplosionBig
        explosiveness = 0.75f
        flammability = 0.5f
        heatCapacity = 0.25f
        viscosity = 0.9f
        temperature = 0.7f
        bundle {
            desc(zh_CN, "血肉赘生物", "一种高温且易燃易爆的烈性流体液体,制取或运输该液体时,请使用专用管道!","[red]鲜血必将流淌[]")
        }
    }
    val 超临界流体= IceLiquid("supercriticalFluids","E1776A"){
        incinerable = false
        lightColor = Color.valueOf("E1776A9A")
        effect = IStatus.蚀骨
        canStayOn = ObjectSet.with(Liquids.water)
        heatCapacity = 2.1f
        viscosity = 0.5f
        temperature = 0.1f
        bundle {
            desc(zh_CN, "超临界流体", "一种通过复杂工业化处理萃取出的特殊流体,具有良好的传质、传热及溶解性能")
        }
    }
    val 暮光液 = IceLiquid("duskLiquid", "deedff") {
        temperature = 0.2f
        bundle {
            desc(zh_CN, "暮光液", "暮光液")
        }
    }
}
