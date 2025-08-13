package ice.library.meta.stat

import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat
import mindustry.world.meta.StatCat.crafting as 输入输出

object IceStats {
    val 单位数量 = IceStat("unitcap")
    val 可连接建筑 = IceStat("linkBlocks",StatCat.function)
    val 最大连接 = IceStat("maxLinks",StatCat.function)
    val 营养浓度 = IceStat("nutrientConcentration")
    val 配方 = IceStat("formulas", 输入输出)
    val 建造时间花费 = IceStat("cost")
    val 建筑血量系数 = IceStat("healthScaling")
    val 硬度 = IceStat("hardness")
    val 是否用于建造 = IceStat("buildable")
    val 状态重载时间 = IceStat("effectTime")
    val 状态效果 = IceStat("effect")
    val 破甲 = IceStat("armorBreak")
    val 状态持续时间 = IceStat("statusTime")
    val 范围 = IceStat("radius",StatCat.function)
    val 生产进度 = IceStat("productionProgress")
    val 返回 = IceStat("back")
    val 鸣谢 = IceStat("thanks")
    val 关闭 = IceStat("close")
    val 进度 = IceStat("schedule")
    val 科技 = IceStat("tree")
    val 数据 = IceStat("data")
    val 成就 = IceStat("achievement")
    val 检验 = IceStat("bundle")
    val 设置 = IceStat("settings")
    val 日志 = IceStat("logs")
    val 数据库 = IceStat("database")
    val 蓝图 = IceStat("schematics")
    val 研究 = IceStat("research")
    val 星球 = IceStat("planetmap")
    val 深空 = IceStat("deepSpace")
    val 地板 = IceStat("floor")
    val 详情 = IceStat("details")
    val 连接 = IceStat("links")

    class IceStat(name: String, category: StatCat = StatCat.general) : Stat(name, category) {
         var localizedName=name
        override fun localized(): String {
            return localizedName
        }
    }
}