package ice.world.meta

import ice.library.world.Load
import mindustry.world.meta.StatCat

object IceStats :Load {
  val 最大能量势 = getStat("maxEnergyPotential", IceStatCats.neutron) {
    localization {
      zh_CN {
        localizedName = "最大能量势"
      }
    }
  }
  val 最大结构尺寸 = getStat("maxStructureSize", IceStatCats.结构) {
    localization {
      zh_CN {
        localizedName = "最大结构尺寸"
      }
    }
  }

  val 电磁脉冲伤害 = getStat("empDamage") {
    localization {
      zh_CN {
        localizedName = "电磁脉冲伤害"
      }
    }
  }
  val 修复 = getStat("repair", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "修复"
      }
    }
  }
  val 修复量 = getStat("repairAmount", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "修复量"
      }
    }
  }
  val 反射率 = getStat("albedo", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "反射率"
      }
    }
  }
  val 立场强度 = getStat("fieldStrength", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "立场强度"
      }
    }
  }
  val 无人机制造 = getStat("mechs", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "无人机制造"
      }
    }
  }
  val 格挡数量 = getStat("resistCont", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "格挡数量"
      }
    }
  }
  val 伤害减免 = getStat("damagereduction", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "伤害减免"
      }
    }
  }
  val 生命值恢复 = getStat("regenAmount", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "生命值恢复"
      }
    }
  }
  val 连锁伤害 = getStat("chaindamage") {
    localization {
      zh_CN {
        localizedName = "连锁伤害"
      }
    }
  }
  val 护甲降低 = getStat("reducearmor") {
    localization {
      zh_CN {
        localizedName = "护甲降低"
      }
    }
  }
  val 生命上限降低 = getStat("reducemaxhealth") {
    localization {
      zh_CN {
        localizedName = "生命上限降低"
      }
    }
  }
  val 斩杀生命值 = getStat("killhealth") {
    localization {
      zh_CN {
        localizedName = "斩杀生命值"
      }
    }
  }
  val 单位数量 = getStat("unitcap") {
    localization {
      zh_CN {
        localizedName = "单位数量"
      }
    }
  }
  val 百分比护盾伤害 = getStat("percentshielddamage") {
    localization {
      zh_CN {
        localizedName = "百分比护盾伤害"
      }
    }
  }
  val 最小护盾伤害 = getStat("minshielddamage") {
    localization {
      zh_CN {
        localizedName = "最小护盾伤害"
      }
    }
  }
  val 可连接建筑 = getStat("linkBlocks", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "可连接建筑"
      }
    }
  }
  val 最大连接 = getStat("maxLinks", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "最大连接"
      }
    }
  }
  val 营养浓度 = getStat("nutrientConcentration") {
    localization {
      zh_CN {
        localizedName = "营养浓度"
      }
    }
  }
  val 反射概率基数 = getStat("baseDeflectChance") {
    localization {
      zh_CN {
        localizedName = "反射概率基数"
      }
    }
  }
  val 建造时间花费 = getStat("cost") {
    localization {
      zh_CN {
        localizedName = "建造时间花费"
      }
    }
  }
  val 建筑血量系数 = getStat("healthScaling") {
    localization {
      zh_CN {
        localizedName = "建筑血量系数"
      }
    }
  }
  val 硬度 = getStat("hardness") {
    localization {
      zh_CN {
        localizedName = "硬度"
      }
    }
  }
  val 是否用于建造 = getStat("buildable") {
    localization {
      zh_CN {
        localizedName = "是否用于建造"
      }
    }
  }
  val 状态效果 = getStat("effect") {
    localization {
      zh_CN {
        localizedName = "状态效果"
      }
    }
  }
  val 破甲 = getStat("armorBreak") {
    localization {
      zh_CN {
        localizedName = "破甲"
      }
    }
  }
  val 状态持续时间 = getStat("statusTime") {
    localization {
      zh_CN {
        localizedName = "状态持续时间"
      }
    }
  }
  val 秒 = getStat("seconds") {
    localization {
      zh_CN {
        localizedName = "秒"
      }
    }
  }
  val 范围 = getStat("radius", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "范围"
      }
    }
  }

  val 状态 = getStat("status") {
    localization {
      zh_CN {
        localizedName = "状态"
      }
    }
  }
  val 生产进度 = getStat("productionProgress") {
    localization {
      zh_CN {
        localizedName = "生产进度"
      }
    }
  }
  val 能否超速 = getStat("canOverSpeed") {
    localization {
      zh_CN {
        localizedName = "能否超速"
      }
    }
  }
  val 发射数量 = getStat("shots") {
    localization {
      zh_CN {
        localizedName = "发射数量"
      }
    }
  }

  val 公告 = getStat("publicInfo") {
    localization {
      zh_CN {
        localizedName = "公告"
      }
      en {
        localizedName = "News"
      }
    }
  }
  val 研究 = getStat("research") {
    localization {
      zh_CN {
        localizedName = "研究"
      }
      en {
        localizedName = "Research"
      }
    }
  }
  val 数据 = getStat("datas") {
    localization {
      zh_CN {
        localizedName = "数据"
      }
      en {
        localizedName = "Data"
      }
    }
  }
  val 成就 = getStat("achievement") {
    localization {
      zh_CN {
        localizedName = "成就"
      }
      en {
        localizedName = "Achieve"
      }
    }
  }
  val 遗物 = getStat("remains") {
    localization {
      zh_CN {
        localizedName = "遗物"
      }
      en {
        localizedName = "Relics"
      }
    }
  }
  val 模组 = getStat("mod") {
    localization {
      zh_CN {
        localizedName = "模组"
      }
      en {
        localizedName = "mod"
      }
    }
  }
  val 捐赠 = getStat("contribute") {
    localization {
      zh_CN {
        localizedName = "捐赠"
      }
    }
  }
  val 设置 = getStat("settings") {
    localization {
      zh_CN {
        localizedName = "设置"
      }
      en {
        localizedName = "Settings"
      }
    }
  }
  val 关闭 = getStat("close") {
    localization {
      zh_CN {
        localizedName = "关闭"
      }
      en {
        localizedName = "close"
      }
    }
  }

  val 连接 = getStat("links") {
    localization {
      zh_CN {
        localizedName = "连接 {0}/{1}"
      }
      en {
        localizedName = "Links {0}/{1}"
      }
    }
  }

  val 连接范围 = getStat("linkRange", IceStatCats.流体传输) {
    localization {
      zh_CN {
        localizedName = "连接范围"
      }
    }
  }
  val 传输速度 = getStat("transportSpeed", IceStatCats.流体传输) {
    localization {
      zh_CN {
        localizedName = "传输速度"
      }
    }
  }

  val 正面免伤 = getStat("frontReduceHarm", StatCat.function) {
    localization {
      zh_CN {
        localizedName = "正面免伤"
      }
    }
  }
  val 伤害 = getStat("damage") {
    localization {
      zh_CN {
        localizedName = "伤害"
      }
    }
  }
  val 拦截护盾 = getStat("interceptShield") {
    localization {
      zh_CN {
        localizedName = "拦截护盾"
      }
    }
  }
  val 拦截伤害 = getStat("interceptDamage") {
    localization {
      zh_CN {
        localizedName = "拦截伤害"
      }
    }
  }
  val 拦截范围 = getStat("interceptRange") {
    localization {
      zh_CN {
        localizedName = "拦截范围"
      }
    }
  }
  val 钻探等级 = getStat("drillLevel", StatCat.crafting) {
    localization {
      zh_CN {
        localizedName = "钻探等级"
      }
    }
  }
  val 百分比治疗 = getStat("percentHealth") {
    localization {
      zh_CN {
        localizedName = "百分比治疗"
      }
    }
  }
  val 最小治疗 = getStat("minhealth") {
    localization {
      zh_CN {
        localizedName = "最小治疗"
      }
    }
  }
  val 百分比伤害 = getStat("percentdamage") {
    localization {
      zh_CN {
        localizedName = "百分比伤害"
      }
    }
  }
  val 最小伤害 = getStat("mindamage") {
    localization {
      zh_CN {
        localizedName = "最小伤害"
      }
    }
  }

  val 主菜单 = getStat("mainMenu") {
    localization {
      zh_CN {
        localizedName = "主菜单"
      }
    }
  }
  val 作者 = getStat("author") {
    localization {
      zh_CN {
        localizedName = "作者"
      }
    }
  }
  val 亲爱的贡献者 = getStat("contributors") {
    localization {
      zh_CN {
        localizedName = "亲爱的贡献者"
      }
    }
  }
  val 版本 = getStat("version") {
    localization {
      zh_CN {
        localizedName = "版本"
      }
    }
  }
  val 版本发布日期 = getStat("releaseDate") {
    localization {
      zh_CN {
        localizedName = "版本发布日期"
      }
    }
  }

  val 支持详情 = getStat("support.info") {
    localization {
      zh_CN {
        localizedName = "首先,谢谢您愿意点开这个页面,若您喜欢这个mod,您的支持将是我们继续这个项目的莫大动力"
      }
    }
  }
  val 支持github = getStat("support.star") {
    localization {
      zh_CN {
        localizedName = "如果您想要支持这个mod,我们比较推荐您从下面的这个按钮跳转到这个项目的github页面,并为我们点亮一个star"
      }
    }
  }
  val 支持githubStar = getStat("support.githubStar") {
    localization {
      zh_CN {
        localizedName = "前往mod的github页面,并点亮一个star"
      }
    }
  }
  val 支持捐赠 = getStat("support.donate") {
    localization {
      zh_CN {
        localizedName =
          "如果您认为我们的作品值得您提供物质上的支持,您可以通过爱发电或者patreon来向我们提供赞助,我们无意要求您为我们的工作买单,但是如果您愿意为我们的所做给予肯定,我们亦会万分感激."
      }
    }
  }
  val 爱发电 = getStat("afdian") {
    localization {
      zh_CN {
        localizedName = "爱发电"
      }
    }
  }
  val Patreon = getStat("patreon") {
    localization {
      zh_CN {
        localizedName = "Patreon"
      }
    }
  }
  val 支持爱发电 = getStat("support.afdian") {
    localization {
      zh_CN {
        localizedName = "来自中国的玩家可以选择通过爱发电赞助"
      }
    }
  }
  val 支持patreon = getStat("support.patreon") {
    localization {
      zh_CN {
        localizedName = "通过patreon赞助我们"
      }
    }
  }

  val 可选输入 = getStat("optionalInputs", IceStatCats.其他) {
    localization {
      zh_CN {
        localizedName = "可选输入"
      }
    }
  }
  val 未选择 = getStat("noSelect") {
    localization {
      zh_CN {
        localizedName = "未选择"
      }
    }
  }

  val 物品 = getStat("item") {
    localization {
      zh_CN {
        localizedName = "物品"
      }
    }
  }
  val 流体 = getStat("liquid") {
    localization {
      zh_CN {
        localizedName = "流体"
      }
    }
  }
  val 热量 = getStat("heat") {
    localization {
      zh_CN {
        localizedName = "热量"
      }
    }
  }

  fun getStat(localizedName: String, cat: StatCat = StatCat.general, block: IceStat.() -> Unit): IceStat {
    return IceStat(localizedName, cat).apply {
      block(this)
    }
  }
}