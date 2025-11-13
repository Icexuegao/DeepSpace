package ice.library.meta.stat

import ice.ui.BaseBundle
import ice.ui.BaseBundle.Companion.bundle
import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat
import mindustry.world.meta.StatCat.crafting as 输入输出

object IceStats {
    fun load() = Unit
    val 单位数量 = IceStat("unitcap").apply {
        bundle {
            desc(zh_CN, "单位数量")
        }
    }
    val 可连接建筑 = IceStat("linkBlocks", StatCat.function).apply {
        bundle {
            desc(zh_CN, "可连接建筑")
        }
    }
    val 最大连接 = IceStat("maxLinks", StatCat.function).apply {
        bundle {
            desc(zh_CN, "最大连接")
        }
    }
    val 营养浓度 = IceStat("nutrientConcentration").apply {
        bundle {
            desc(zh_CN, "营养浓度")
        }
    }
    val 配方 = IceStat("formulas", 输入输出).apply {
        bundle {
            desc(zh_CN, "配方")
        }
    }
    val 反射概率基数 = IceStat("baseDeflectChance").apply {
        bundle {
            desc(zh_CN, "反射概率基数")
        }
    }
    val 建造时间花费 = IceStat("cost").apply {
        bundle {
            desc(zh_CN, "建造时间花费")
        }
    }
    val 建筑血量系数 = IceStat("healthScaling").apply {
        bundle {
            desc(zh_CN, "建筑血量系数")
        }
    }
    val 硬度 = IceStat("hardness").apply {
        bundle {
            desc(zh_CN, "硬度")
        }
    }
    val 是否用于建造 = IceStat("buildable").apply {
        bundle {
            desc(zh_CN, "是否用于建造")
        }
    }
    val 状态效果 = IceStat("effect").apply {
        bundle {
            desc(zh_CN, "状态效果")
        }
    }
    val 破甲 = IceStat("armorBreak").apply {
        bundle {
            desc(zh_CN, "破甲")
        }
    }
    val 状态持续时间 = IceStat("statusTime").apply {
        bundle {
            desc(zh_CN, "状态持续时间")
        }
    }
    val 范围 = IceStat("radius", StatCat.function).apply {
        bundle {
            desc(zh_CN, "范围")
        }
    }
    val 生产进度 = IceStat("productionProgress").apply {
        bundle {
            desc(zh_CN, "生产进度")
        }
    }
    val 鸣谢 = IceStat("thanks").apply {
        bundle {
            desc(zh_CN, "鸣谢")
        }
    }
    val 关闭 = IceStat("close").apply {
        bundle {
            desc(zh_CN, "关闭")
        }
    }
    val 进度 = IceStat("schedule").apply {
        bundle {
            desc(zh_CN, "进度")
        }
    }
    val 科技 = IceStat("tree").apply {
        bundle {
            desc(zh_CN, "科技")
        }
    }
    val 数据 = IceStat("datas").apply {
        bundle {
            desc(zh_CN, "数据")
        }
    }
    val 成就 = IceStat("achievement").apply {
        bundle {
            desc(zh_CN, "成就")
        }
    }
    val 遗物 = IceStat("remains").apply {
        bundle {
            desc(zh_CN, "遗物")
        }
    }
    val 设置 = IceStat("settings").apply {
        bundle {
            desc(zh_CN, "设置")
        }
    }
    val 日志 = IceStat("logs").apply {
        bundle {
            desc(zh_CN, "日志")
        }
    }
    val 连接 = IceStat("links").apply {
        bundle {
            desc(zh_CN, "连接 {0}/{1}")
        }
    }
    val 正面免伤 = IceStat("frontReduceHarm", StatCat.function).apply {
        bundle {
            desc(zh_CN, "正面免伤")
        }
    }
    val 伤害 = IceStat("damage").apply {
        bundle {
            desc(zh_CN, "伤害")
        }
    }
    val 拦截护盾 = IceStat("interceptShield").apply {
        bundle {
            desc(zh_CN, "拦截护盾")
        }
    }
    val 拦截伤害 = IceStat("interceptDamage").apply {
        bundle {
            desc(zh_CN, "拦截伤害")
        }
    }
    val 拦截范围 = IceStat("interceptRange").apply {
        bundle {
            desc(zh_CN, "拦截范围")
        }
    }
    val 钻探等级 = IceStat("drillLevel", 输入输出).apply {
        bundle {
            desc(zh_CN, "钻探等级")
        }
    }

    class IceStat(name: String, category: StatCat = StatCat.general) : Stat(name, category) {
        var localizedName = name
        override fun localized(): String {
            return localizedName
        }

        fun IceStat.desc(bundle: BaseBundle, name: String) {
            bundle.runBun.add {
                localizedName = name
            }
        }
    }
}