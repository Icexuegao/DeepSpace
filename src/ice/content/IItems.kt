package ice.content

import ice.library.content.blocks.abstractBlocks.IceBlock.Companion.desc
import ice.library.content.item.IceItem
import ice.library.content.item.OreItem
import ice.ui.BaseBundle.Companion.bundle

@Suppress("unused") object IItems {
    fun load() = Unit
    val 脊髓末梢 = IceItem("spinalCordEnding", "bf3e47") {
        nutrientConcentration = 0.2f
        bundle {
            desc(zh_CN, "脊髓末梢",
                "一种从生物质中提取的神经传导组织,经过特殊处理后可用于强化机械单位的反应速度与攻击精准度")
        }
    }
    val 无名肉块 = IceItem("namelessCut", "bf3e47") {
        nutrientConcentration = 0.5f
        bundle {
            desc(zh_CN, "无名肉块", "基础血肉原料,表面布满跳动的血管", "我们以为北极只是冷,现在才知道,寒冷也能如此饥饿")
        }
    }
    val 碎骨 = IceItem("bonesNap", "bf3e47") {
        nutrientConcentration = 0.1f
        bundle {
            desc(zh_CN, "碎骨",
                "经过机械粉碎处理的生物骨质残骸,表面布满裂痕与强化接合点。可作为廉价的结构填充材料,或进一步精炼为高硬度复合材料")
        }
    }
    val 肌腱 = IceItem("muscleTendon", "bf3e47") {
        nutrientConcentration = 0.25f
        bundle {
            desc(zh_CN, "肌腱", "一种高强度的生物纤维束,经过机械改造后具备惊人的弹性与韧性", "牛皮糖?")
        }
    }
    val 骨钢 = IceItem("fleshSteel", "bf3e47") {
        bundle {
            desc(zh_CN, "骨钢", "由血肉与骸骨熔铸的合金,用于建造自愈型建筑")
        }
    }
    val 金珀沙 = IceItem("goldSand", "f8efad") {
        hardness = 2
        bundle {
            desc(zh_CN, "金珀沙",
                "这种闪耀着金色光泽的沙粒含有微量琥珀结晶,在光照下会呈现半透明的琥珀色光晕,可进一步提取矿物质")
        }
    }
    val 黄玉髓 = IceItem("canaryStone", "f5c782") {
        hardness = 1
        bundle {
            desc(zh_CN, "黄玉髓", "一种淡黄色的半透明玉石,质地温润,可提取其他矿物")
        }
    }
    val 红冰 = IceItem("redIce", "ff7171") {
        radioactivity = 0.05f
        bundle {
            desc(zh_CN, "红冰", "一种呈红色的放射性冰晶,内部含有不稳定的能量结构")
        }
    }
    val 晶状孢芽 = IceItem("crystallineSpore", "52578a") {
        flammability = 0.2f
        hardness = 1
        bundle {
            desc(zh_CN, "晶状孢芽", "一种半透明的晶体状孢子,内部含有稳定的能量结构,可用于制造光学设备或能量传导材料")
        }
    }
    val 灼热孢团 = IceItem("pyroSpore", "eac73e") {
        explosiveness = 0.1f
        flammability = 0.4f
        bundle {
            desc(zh_CN, "灼热孢团", "一种散发着高温的孢子团,内部含有可燃物质,可作为燃料或爆炸物原料")
        }
    }
    val 寂温疮体 = IceItem("lonelyHeatSoreSpore", "b3f1ff") {
        flammability = 0.1f
        bundle {
            desc(zh_CN, "寂温疮体", "一种奇特的低温孢子组织,表面覆盖着冰晶,可用于冷却系统或制作低温材料")
        }
    }
    val 腐败孢团 = IceItem("rottenSpore", "a09bbd") {
        radioactivity = 0.1f
        flammability = 0.3f
        bundle {
            desc(zh_CN, "腐败孢团", "一种腐烂的孢子团块,散发着恶臭,可用于制造有机肥料或燃料")
        }
    }
    val 血囊孢子 = IceItem("bloodSpore", "ffa0a0") {
        radioactivity = 0.05f
        hardness = 1
        frames = 5
        frameTime = 15f
        bundle {
            desc(zh_CN, "血囊孢子",
                "殷红树上采集的一种孢子,一般为殷红树单胞繁殖体,生物结构非常不稳定,也可能分化成[red]其他植株[]")
        }
    }
    val 石英 = OreItem("quartz", "ffffff", 1) {
        bundle {
            desc(zh_CN, "石英", "高硬度晶体,主要成分为SiO2,对于极端酸性环境有优异的耐受性")
        }
    }
    val 生煤 = OreItem("rawCoal", "151515", 2) {
        flammability = 0.6f
        bundle {
            desc(zh_CN, "生煤", "未经精炼的原煤,含有较多杂质,但仍是重要的燃料来源,需要进一步加工以提高纯度")
        }
    }
    val 燃素水晶 = IceItem("phlogistonCrystal", "b38f8d") {
        bundle {
            desc(zh_CN, "燃素水晶", "一种蕴含高能量的晶体结构,燃烧时能释放大量热能,是高效燃料的重要原料")
        }
    }
    val 铬铁矿 = OreItem("chrome", "768a9a", 3) {
        bundle {
            desc(zh_CN, "铬铁矿", "重要的铬矿石,含有铬成分,是生产不锈钢和耐热合金的关键原料")
        }
    }
    val 赤铁矿 = OreItem("hematite", "c6a699", 2) {
        bundle {
            desc(zh_CN, "赤铁矿", "一种重要的铁矿石,呈红褐色,是合成碳素钢的主要原料之一,含有较高的铁含量")
        }
    }
    val 方铅矿 = OreItem("galena", "8c7fa9", 2) {
        hardness = 2
        radioactivity = 0.1f
        bundle {
            desc(zh_CN, "方铅矿", "重要的铅矿石,呈立方晶体结构,是提炼铅的主要来源,常伴有银等贵金属,具有弱放射性")
        }
    }
    val 黄铜矿 = OreItem("copperPyrites", "eac73e", 2) {
        bundle {
            desc(zh_CN, "黄铜矿", "重要的铜矿石,呈黄铜色金属光泽,是提炼铜的主要来源之一,常伴有金、银等贵金属")
        }
    }
    val 闪锌矿 = OreItem("sphalerite", "578c80", 2) {
        bundle {
            desc(zh_CN, "闪锌矿",
                "闪锌矿是提炼锌的最重要矿物原料,纯闪锌矿近于无色,但通常因含铁而呈浅黄,浅绿,随含铁量的增加而变深")
        }
    }
    val 金矿 = OreItem("goldOre", "f8df87", 3) {
        bundle {
            desc(zh_CN, "金矿", "含有金元素的珍贵矿石,虽然金含量不高但仍是重要的贵重金属来源")
        }
    }
    val 锆英石 = OreItem("azorite", "8c3e2d", 4) {
        bundle {
            desc(zh_CN, "锆英石", "一种坚硬的硅酸盐矿物,是提取锆的重要原料,广泛用于陶瓷工业、耐火材料")
        }
    }
    val 硫钴矿 = OreItem("linnaeite", "cfecf1", 3) {
        bundle {
            desc(zh_CN, "硫钴矿", "银白色金属光泽,常带粉红色调,立方体或八面体晶形,是提炼钴的重要原料",
                "这玩意儿比钻石难啃\n——采矿队长R-42的日志")
        }
    }
    val 低碳钢 = IceItem("lowCarbonSteel", "d4d7e4") {
        bundle {
            desc(zh_CN, "低碳钢", "含碳量较低的钢材,具有优异的塑性和韧性,是建筑和机械制造的常用基础材料")
        }
    }
    val 高碳钢 = IceItem("highCarbonSteel", "bedfee") {
        bundle {
            desc(zh_CN, "高碳钢", "含碳量较高的钢材,硬度优于低碳钢,适合用于制造需要高强度的建筑和部件")
        }
    }
    val 铜锭 = IceItem("copperIngot", "d99d73") {
        bundle {
            desc(zh_CN, "铜锭", "以优异的导电性和导热性著称,是电力设备和热交换系统的重要材料")
        }
    }
    val 铅锭 = IceItem("leadIngot", "8c7fa9") {
        bundle {
            desc(zh_CN, "铅锭", "高密度金属,具有出色的辐射防护能力,既可用于防护设施,也可作为重型武器的材料",
                "从挡住辐射到砸穿装甲,这份量从来不会让人失望")
        }
    }
    val 锌锭 = IceItem("zincIngot", "578c80") {
        bundle {
            desc(zh_CN, "锌锭",
                "其独特的牺牲阳极保护和加热后的超塑性,既能作为装甲镀层大幅延长载具寿命,又可制成高能锌空气电池为单位护盾供电",
                "防锈?试试用锌箔包裹EMP炸弹:)")
        }
    }
    val 黄铜锭 = IceItem("brassIngot", "eac73e") {
        bundle {
            desc(zh_CN, "黄铜锭", "一种铜合金,有较高的强度和耐腐蚀性,适用于制造各种结构零和连接件")
        }
    }
    val 铬锭 = IceItem("chromeIngot", "C8C8E4") {
        bundle {
            desc(zh_CN, "铬锭", "轻质高硬度的金属材料,具有优异的耐腐蚀性,常用于制造高强度建筑和防护装备")
        }
    }
    val 金锭 = IceItem("goldIngot", "f8df87") {
        bundle {
            desc(zh_CN, "金锭", "一种贵重金属,可以用来制作电子部件,能量传导部件的进阶材料")
        }
    }
    val 钴锭 = IceItem("cobaltIngot", "b3f1ff") {
        bundle {
            desc(zh_CN, "钴锭", "良好的耐高温,耐腐蚀是它成为建材的重要原因,很多测量仪器都要用到它")
        }
    }
    val 铪锭 = IceItem("hafniIngot", "f7e5f3") {
        bundle {
            desc(zh_CN, "铪锭", "稀有的高熔点金属,在能量传导和高温环境中表现出色,是高级能量设备的关键材料")
        }
    }
    val 钴钢 = IceItem("cobaltSteel", "c5d1e0") {
        bundle {
            desc(zh_CN, "钴钢", "钴合金钢,具有优异的耐高温和耐腐蚀性能,常用于制造高强度建筑和防护装备")
        }
    }
    val 燃能晶 = IceItem("burningCrystal", "737373") {
        bundle {
            desc(zh_CN, "燃能晶", "经过特殊工艺处理的晶体燃料,燃烧时能释放巨大能量,是工业设备的核心燃料")
        }
    }
    val 石英玻璃 = IceItem("quartzGlass", "ebeef5") {
        bundle {
            desc(zh_CN, "石英玻璃", "耐高温,低膨胀系数,有效抵御大部分化学反应,是理想的化学反应容器材料")
        }
    }
    val 复合陶瓷 = IceItem("compositeCeramic", "ebeef5") {
        bundle {
            desc(zh_CN, "复合陶瓷",
                "凭借其超凡的抗冲击性和热稳定性,既能作为能量护盾发生器的核心基底材料,又能制成高速单位的防热瓦")
        }
    }
    val 石墨烯 = IceItem("graphene", "52578a") {
        bundle {
            desc(zh_CN, "石墨烯", "超薄的单层碳原子材料,具有出色的导电性和机械强度,是高级电子设备的核心材料")
        }
    }
    val 单晶硅 = IceItem("monocrystallineSilicon", "575757") {
        bundle {
            desc(zh_CN, "单晶硅", "高纯度的硅晶体,是制造高级电子设备和处理器的基础材料")
        }
    }
    val 电子元件 = IceItem("integratedCircuit", "53565c") {
        bundle {
            desc(zh_CN, "电子元件", "精密的电子器件,是制造高级设备和处理器的基础材料")
        }
    }
    val 暮光合金 = IceItem("duskIngot", "deedff") {
        bundle {
            desc(zh_CN, "暮光合金", "在暮光时分呈现特殊光泽的合金材料,具有优异的能量传导特性,是高级能量设备的核心材料")
        }
    }
    val 玳渊矩阵 = IceItem("abyssMatrix", "d7bdff") {
        bundle {
            desc(zh_CN, "玳渊矩阵", "具有独特的能量传导和存储特性,是高级炮塔弹药的核心材料")
        }
    }
}
