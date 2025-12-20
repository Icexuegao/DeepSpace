package ice.content

import arc.Core
import arc.graphics.g2d.TextureRegion
import arc.util.Time
import ice.core.UpdatePool
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.item.IceItem
import ice.world.content.item.OreItem
import mindustry.content.Items

@Suppress("unused")
object IItems : Load {
    val 脊髓末梢 = IceItem("spinalCordEnding", "bf3e47") {
        nutrientConcentration = 0.2f
        bundle {
            desc(zh_CN, "脊髓末梢", "一种从生物质中提取的神经传导组织,经过特殊处理后可用于强化机械单位的反应速度与攻击精准度")
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
            desc(zh_CN, "碎骨", "经过机械粉碎处理的生物骨质残骸,表面布满裂痕与强化接合点。可作为廉价的结构填充材料,或进一步精炼为高硬度复合材料")
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
        hardness = 1
        bundle {
            desc(zh_CN, "金珀沙", "这种闪耀着金色光泽的沙粒含有微量琥珀结晶,在光照下会呈现半透明的琥珀色光晕,可进一步提取矿物质")
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
            desc(zh_CN, "血囊孢子", "殷红树上采集的一种孢子,一般为殷红树单胞繁殖体,生物结构非常不稳定,也可能分化成[red]其他植株[]")
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
            desc(zh_CN, "闪锌矿", "闪锌矿是提炼锌的最重要矿物原料,纯闪锌矿近于无色,但通常因含铁而呈浅黄,浅绿,随含铁量的增加而变深")
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
            desc(zh_CN, "硫钴矿", "银白色金属光泽,常带粉红色调,立方体或八面体晶形,是提炼钴的重要原料", "这玩意儿比钻石难啃\n——采矿队长R-42的日志")
        }
    }
    val 铱锇矿 = OreItem("iridiumosm", "656565", 4) {
        bundle {
            desc(zh_CN, "铱锇矿", "一种稀有金属矿,含有铱和锇,是提炼这两种金属的重要原料")
        }
    }
    val 铈硅石 = OreItem("cerite", "BFC8E2", 2) {
        bundle {
            desc(zh_CN, "铈硅石", "稀土材料,含有铈,是提炼铈的重要原料")
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
            desc(zh_CN, "铅锭", "高密度金属,具有出色的辐射防护能力,既可用于防护设施,也可作为重型武器的材料", "从挡住辐射到砸穿装甲,这份量从来不会让人失望")
        }
    }
    val 锌锭 = IceItem("zincIngot", "578c80") {
        bundle {
            desc(zh_CN, "锌锭", "其独特的牺牲阳极保护和加热后的超塑性,既能作为装甲镀层大幅延长载具寿命,又可制成高能锌空气电池为单位护盾供电", "防锈?试试用锌箔包裹EMP炸弹:)")
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
    val 铈锭 = IceItem("ceriumIngot", "BFC8E2") {
        explosiveness = 0.25f
        flammability = 1.2f
        radioactivity = 0.6f
        cost = 1.2f
        healthScaling = 0.6f
        bundle {
            desc(zh_CN, "铈锭", "经过初步加工的铈矿石,具有研磨抛光剂,特种玻璃及推进器零件等多种用途")
        }
    }
    val 钴钢 = IceItem("cobaltSteel", "c5d1e0") {
        bundle {
            desc(zh_CN, "钴钢", "钴合金钢,具有优异的耐高温和耐腐蚀性能,常用于制造高强度建筑和防护装备")
        }
    }
    val 硫化合物 = IceItem("sulfurCompound", "ffaa5f") {
        flammability = 1.4f
        explosiveness = 0.4f
        buildable = false
        bundle {
            desc(zh_CN, "硫化合物")
        }
    }
    val 爆炸化合物 = IceItem("explosiveCompound", "ff795e") {
        flammability = 0.4f
        explosiveness = 1.2f
        buildable = false
        bundle {
            desc(zh_CN, "爆炸化合物")
        }
    }
    val 低温化合物 = IceItem("lowTemperatureCompound", "C0ECFF") {
        charge = 0.1f
        buildable = false
        bundle {
            desc(zh_CN, "低温化合物", "在极端低温中凝结而成的化合物,具有优异的导热性能,常用于制造高效率的冷却系统")
        }
    }
    val 铈凝块 = IceItem("ceriumClot", "929DB5") {
        buildable = false
        explosiveness = 1.5f
        flammability = 3.6f
        bundle {
            desc(zh_CN, "铈凝块", "对碰撞和温度极为敏感,同时极易自燃")
        }
    }
    val 铱板 = IceItem("iridiumPlate", "656565") {
        cost = 1.8f
        healthScaling = 1f
        bundle {
            desc(zh_CN, "铱板", "已经完成了压铸工序的铱合金板,具有极强的抗冲击性能,是一种优秀的各向异性介质")
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
            desc(zh_CN, "复合陶瓷", "凭借其超凡的抗冲击性和热稳定性,既能作为能量护盾发生器的核心基底材料,又能制成高速单位的防热瓦")
        }
    }
    val 陶钢 = IceItem("potterySteel", "D6DEC6") {
        cost = 1.8f
        healthScaling = 0.8f
        bundle {
            desc(zh_CN, "陶钢", "这种复合材料能够快速且均匀地将电磁粒子辐射分散传导至装甲各处,从而大幅度降低高强度动能")
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
    val 导能回路 = IceItem("conductingCircuit", "867F8C") {
        bundle {
            desc(zh_CN, "导能回路", "将高纯度单晶硅回路蚀刻进放射性能级降低的钍基座中,极大提升其导能性与稳固性")
        }
        charge = 0.8f
        cost = 1.5f
        healthScaling = 0.5f
    }
    val 电子元件 = IceItem("integratedCircuit", "53565c") {
        bundle {
            desc(zh_CN, "电子元件", "精密的电子器件,是制造高级设备和处理器的基础材料")
        }
    }
    val 肃正协议 = IceItem("solemnProtocol", "FF5845") {
        cost = 600f
        bundle {
            desc(zh_CN, "肃正协议", "装载该密匙激活协议以获取高级军械的控制及制造权")
        }
    }
    val 生物钢 = IceItem("biologicalSteel", "D75B6E") {
        bundle {
            desc(zh_CN, "生物钢", "被强行抑制了绝大部分活性的生物组织,此材料制造的装甲具有整体无缝,附着性强,耐酸碱,防辐射,防腐蚀,耐冲击等特点,只是....", "你真的要使用他吗?")
        }
        frames = 3
        transitionFrames = 13
        frameTime = 5f
        explosiveness = 0.75f
        flammability = 0.5f
        radioactivity = 0.1f
        charge = 0.4f
        cost = 2.4f
        healthScaling = 2.4f
    }
    val 钍锭= IceItem("thoriumIngot", Items.thorium.color.toString()) {
        radioactivity=1f
        cost = 2f
        bundle {
            desc(zh_CN, "钍锭", "一种放射性金属,具有高能量密度,常用于核反应堆和核武器")
        }
    }
    val 暮光合金 = IceItem("duskIngot", "deedff") {
        bundle {
            desc(zh_CN, "暮光合金", "在暮光时分呈现特殊光泽的合金材料,具有优异的能量传导特性,是高级能量设备的核心材料")
        }
    }
    val 以太能 = IceItem("etherealEnergy", "E6C4EE") {
        frames = 2
        transitionFrames = 24
        frameTime = 6f
        radioactivity = 0.4f
        charge = 1f
        cost = 2.4f
        healthScaling = 0.8f
        bundle {
            desc(zh_CN, "以太能", "以太,一高能粒子,在以特定结构排列时注入相位能量,可在一定程度上影响时空结构,曾在研究初期引发了回溯时空/空间错位/乱序传送在内的一系列时空现象", "在以太粒子以特定结构排列时注入相位能量,以太粒子会在法韦克内敛空间的能量辐射下,形成以伊塔宏粒子射线为场能的波态中子向心力场")
        }
    }
    val 玳渊矩阵 = IceItem("abyssMatrix", "d7bdff") {
        bundle {
            desc(zh_CN, "玳渊矩阵", "具有独特的能量传导和存储特性,是高级炮塔弹药的核心材料")
        }
    }
    var aluminium = IceItem("aluminium", "#C0ECFF") {
        hardness = 3
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        cost = 0.9f
        bundle {
            desc(zh_CN, "铝", "一种耐低温的常见金属,质量轻,在经过处理后强度大大提高,在航空航天领域十分常用的结构材料", "在绝大多数小质量的行星上,铝的丰富度通常都是在金属元素中排序最靠前的,而在地球地壳中的铝丰富度相比绝大多数行星而言更是可谓一骑绝尘,以至于人类在2043年完全停止对铝矿的开采至今仍有非常充足的库存可供使用...")
        }
    }
    var crystal_FEX = IceItem("crystal_FEX", "#D2393E") {
        hardness = 3
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0.4f
        cost = 1.25f
        bundle {
            desc(zh_CN, "FEX晶体", "晶体化的高纯度FEX,中子导率和核性能都有明显提高,核工业不可或缺的重要材料", "最初人们发现高纯度FEX的晶体时,由于其地壳中相对丰度比稀土金属都要低,FEX结晶的价值一度超越钻石,在几年间近乎取代整个钻石市场,直到某个倒霉鬼在核反应堆旁工作的时候被他兜里揣的戒指炸断了一条腿")
        }
    }
    var crystal_FEX_power = object : IceItem("crystal_FEX_power", "#E34248") {
        init {
            hardness = 3
            explosiveness = 3.6f
            flammability = 0f
            radioactivity = 3f
            cost = 1.35f
            frameTime = 9f
            bundle {
                desc(zh_CN, "活性FEX结晶", "经大量能量激发的FEX结晶,性质极其不稳定,危险且难以储存,但在需要释放中子能的地方不可或缺", "严禁用任何致密介质接触激发态的FEX结晶这是《中子工业操作管理条例》中加粗且划着红色下划线的条目,类似这样的条目都往往是来自一个个血淋淋的教训")
            }
        }

        override fun loadIcon() {
            super.loadIcon()
            val regions = arrayOfNulls<TextureRegion>(18)

            for (i in 0..9) {
                regions[i] = Core.atlas.find(name + "_" + i)
                if (i != 0 && i != 9) regions[regions.size - i] = regions[i]
            }

            fullIcon = TextureRegion(fullIcon)
            uiIcon = TextureRegion(uiIcon)

            UpdatePool.receive("dynamicIcon-$name") {
                val frame = (Time.globalTime / frameTime).toInt() % regions.size
                fullIcon.set(regions[frame])
                uiIcon.set(regions[frame])
            }
        }
    }
    var matrix_alloy = IceItem("matrix_alloy", "#929090") {
        hardness = 4
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        cost = 1.4f
        bundle {
            desc(zh_CN, "矩阵合金", "大量纳米机器人的集合体,机械强度较低,在经编程后可自由变换形态甚至物态,十分灵活", "很多年前有个科幻电影叫做《终结者》,电影里出现过矩阵多边拟态合金,科幻作品总是能够预言某些在当时看来是不可能的未来科技。")
        }
    }
    var strengthening_alloy = IceItem("strengthening_alloy", "#B1B1B0") {
        hardness = 5
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        cost = 1.25f
        bundle {
            desc(zh_CN, "强化合金", "各项性能都很优秀的合金,结构强度极高,抗性强,用途广泛", "这种合金研发时发生了这样一件趣事：\n在研究材料配比对强度的影响函数曲线时,冶金工程的所有专家一致认为钛的比例在30%左右时材料的韧性和刚性达到最佳的平衡,然而这样的合金与部分特种钢材差异不大,直到在某一次实验中某个冒失的材料学研究生意外的将钛和钍的比例混淆了。\n这之后,《nature》上刊登了一篇获得多项奖项的论文,那个幸运的孩子也跨极获得了高等学位,这一度让学会的教授们很尴尬。")
        }
    }
    var aerogel = IceItem("aerogel", "#D5EBEE") {
        hardness = 3
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        cost = 1.1f
        bundle {
            desc(zh_CN, "气凝胶", "硅基结构的气凝胶,微观上绝大部分体积几乎都被蜂窝状的孔洞所填充,强度高,密度极小,绝缘绝热等优秀性质,是十分常用的结构材料", "气凝胶在许多年前曾被称为凝固的烟,而现在孔洞体积已经达到了99%,这已经可以被称为固体空气了,但就是在这样低的密度下仍然具有相当优秀的结构强度,也是从气凝胶工艺的突破开始,材料学终于甩掉了人类科学拖油瓶的帽子")
        }
    }
    var degenerate_neutron_polymer = IceItem("degenerate_neutron_polymer", "#FF7FE0") {
        hardness = 10
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        cost = 3f
        bundle {
            desc(zh_CN, "简并态中子聚合物", "极高密度和硬度的特殊材料,极大的密度将电子压入原子核内呈中子简并态。因核子内力和强引力的作用,该材料坚度难以测量,具有无限接近于零的韧性和塑性。除了作为不可多得的结构材料,同时还是引力场技术当中相当优秀的场源", "一个成年人体内的血液可以在7秒内被抽干,而这只需要一个合适的引力源——摘自《what if？》章节如果发射一颗中子星密度的子弹")
        }
    }
    var uranium_238 = IceItem("uranium_238", "#7CA73D") {
        hardness = 2
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0.4f
        cost = 1.5f
        bundle {
            desc(zh_CN, "铀-238", "俗称贫铀,具有一定放射性的同位素,硬度较高,结构密度大,可用作种子反射板,在动能武器上还可作为穿甲弹头。另外铀238可以吸收一个中子并衰变为更有用的钚-239", "无论在哪一个星球上,自然状态下的铀238总是占据铀的绝大部分的丰富度,所幸它可以转变为更有用的东西,如果全部都用来造穿甲弹和防弹装甲之类的话,保有量可能可以用到下个地质纪...")
        }
    }
    var uranium_235 = IceItem("uranium_235", "#B5D980") {
        hardness = 2
        explosiveness = 0f
        flammability = 0f
        radioactivity = 1.6f
        bundle {
            desc(zh_CN, "铀-235", "主要的的裂变核燃料之一,同时也是天然的中子放射源", "在地球上,每年都会有多例急性放射病的案例报道,然而来自常用核燃料的U235的病例并不多,这或许和U235相关管理办法十分严格有关,毕竟在停滞时代时有一座倒霉的城市被恐怖组织用土制核弹炸上了天")
        }
    }
    var plutonium_239 = IceItem("plutonium_239", "#D1D19F") {
        hardness = 2
        explosiveness = 0f
        flammability = 0f
        radioactivity = 1.6f
        bundle {
            desc(zh_CN, "钚-239", "铀-238吸收中子后衰变得到的强放射性同位素,同铀-235一样是常用的裂变燃料,可通过铀238在核反应堆中吸收中子后衰变获得", "尽管铀238可以转换为钚239,但也许你需要一个不小规模的反应堆阵列才能实现量产钚239了")
        }
    }
    var encapsulated_hydrogen_cell = IceItem("encapsulated_hydrogen_cell", "#9EFFC6") {
        hardness = 2
        explosiveness = 2.4f
        flammability = 1.8f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "相位封装氢单元", "由相位物包裹的一份氢中子靶丸,在多数核反应堆中接收中子转化为核燃料", "相位物的中子光路学结构会将中子聚焦到中心存储氢的空腔,以最大化中央接收到的中子流,在中子流的轰击下,大量氢原子会转化为较为容易发生核聚变反应的同位素,继而参与核聚变")
        }
    }
    var encapsulated_helium_cell = IceItem("encapsulated_helium_cell", "#F9FFDE") {
        hardness = 2
        explosiveness = 0.3f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "相位封装氦单元", "由相位物包裹的一份氦中子靶丸,在多数核反应堆中接收中子转化为核燃料", "相位物的中子光路学结构会将中子聚焦到中心存储氦的空腔,以最大化中央接收到的中子流,在中子流的轰击下,大量氦原子会转化为较为容易发生核聚变反应的同位素,继而参与核聚变")
        }
    }
    var concentration_uranium_235 = IceItem("concentration_uranium_235", "#95B564") {
        hardness = 4
        explosiveness = 12f
        flammability = 0f
        radioactivity = 2.4f
        bundle {
            desc(zh_CN, "封装铀-235", "经高度浓缩的铀封装起来的核燃料,以接近绝对零度的温度钉死了原子核,可以在超过常温的临界压缩的情况下存储燃料,凭借高度压缩,使得核反应得以更加高效的进行", "盛装危险放射性物质的容器无论上面画了多大,多醒目的辐射警告标志,依然会被时日无多的倒霉蛋捡走,最后这个容器的设计者想到了一个有效的解决办法,那就是把容器做大,越大越好,越重越好。\n在那之后制造的重量至少1t的容器都没有再遗失过了...你问我为什么？一个人开着一辆卡车,大摇大摆的载着印满了辐射危险的核容器在公路上到处跑？")
        }
    }
    var concentration_plutonium_239 = IceItem("concentration_plutonium_239", "#B0B074") {
        hardness = 4
        explosiveness = 12f
        flammability = 0f
        radioactivity = 2.4f
        bundle {
            desc(zh_CN, "封装钚-239", "经高度浓缩的铀封装起来的核燃料,以接近绝对零度的温度钉死了原子核,可以在超过常温的临界压缩的情况下存储燃料,凭借高度压缩,使得核反应得以更加高效的进行", "盛装危险放射性物质的容器无论上面画了多大,多醒目的辐射警告标志,依然会被时日无多的倒霉蛋捡走,最后这个容器的设计者想到了一个有效的解决办法,那就是把容器做大,越大越好,越重越好。\n在那之后制造的重量至少1t的容器都没有再遗失过了...你问我为什么？一个人开着一辆卡车,大摇大摆的载着印满了辐射危险的核容器在公路上到处跑？")
        }
    }
    var hydrogen_fusion_fuel = IceItem("hydrogen_fusion_fuel", "#83D6A0") {
        hardness = 2
        explosiveness = 2.4f
        flammability = 1.8f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "氢聚变燃料", "一份压缩氢原子同位素,原子性质易于发生聚变,属轻核聚变燃料", "主要成分包括H-1,H-2,H-3核以及He-3,He-4核,还包含少量的Li及其同位素,大概以半个世纪以前的点火功率算,要点燃这样的核聚变几乎就是天方夜谭")
        }
    }
    var helium_fusion_fuel = IceItem("helium_fusion_fuel", "#D0D6B7") {
        hardness = 2
        explosiveness = 0.3f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "氦聚变燃料", "一份压缩氦原子同位素,原子性质易于发生聚变,属轻核聚变燃料", "包含He-3,He-4,Li-3,Li-4等多种同位素,在过去很长的时间里,人类聚变能源都非常依赖太阳风在月球上撒落的He-3,不过在点火功率取得突破性进展后,人类可以点燃的核聚变反应变得越来越多样,以至于混合原子核的燃料都已能被成功点燃")
        }
    }
    var anti_metter = IceItem("anti_metter", "734CD2") {
        hardness = 12
        explosiveness = 64f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "反物质", "引力场约束隔绝的一小份反物质,与任何正物质接触都会立即湮灭并将质量完全转换为纯能量,危险性极高,在强攻击性武器中被广泛使用", "通常每一个反物质储存单元都会具有一个独立的能源模块来维持约束力场,在远航星舰队伴随燃料仓库放出的耀眼的焰火消失后,停电被成为了携带反物质的情况下最危险的事")
        }
    }
    var chlorella_block = IceItem("chlorella_block", "#6CB855") {
        hardness = 1
        explosiveness = 0.4f
        flammability = 1.2f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "绿藻块", "经过分离掉各种杂质后,整合得到的绿藻细胞集团,可以提取到更有用的绿藻素", "应该说,你很难在地球以外的绝大多数行星上闻到氧气的味道...氧气没有味道？至少这个东西闻起来很舒服就是了")
        }
    }
    var chlorella = IceItem("chlorella", "#7BD261") {
        hardness = 1
        explosiveness = 1.2f
        flammability = 1.6f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "绿藻素", "从绿藻细胞中分离出的生物活性成分,绿藻的光合作用就是靠这种物质进行的,同样,我们也可以利用这种性质,将光和作用应用起来", "二十一世纪70年代,地球上的年轻群体中很流行藻素食品,像绿藻酥,藻香蛋糕什么的,年龄较大的人群似乎不太能接受这个的味道,一位21世纪初出生的退休喜剧演员调侃绿藻的味道就像加了糖的草,就是畜牧的绵羊吃的那种")
        }
    }
    var alkali_stone = IceItem("alkali_stone", "#B0BAC0") {
        hardness = 1
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "碱石", "主要由碱石灰组成,富含多种含氯金属盐类成分,可以用于电离获得碱液和氯", "通常来说,河流会携带附着在河床上的盐类物质,并汇入海洋或者湖泊,长久的积累使得水体中的盐浓度不断升高,进而形成高含盐量的海洋和咸水湖,绝大多数表面具有活跃水圈的星球都是符合这样的规律——《行星地质总论 (2083年编)》")
        }
    }
    var flocculant = IceItem("flocculant", "ffffff") {
        hardness = 1
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "絮凝剂", "投入水中会形成大量多孔絮状胶质,用于吸附或者分离固形物质,是一种重要的化工材料", "注意防潮\n请于阴凉环境储存,避免阳光直射\n保质期：六个月\n若不慎误食,请立即就医并向医师提供此说明书")
        }
    }
    var coke = IceItem("coke", "#6A6A69") {
        hardness = 1
        explosiveness = 1.5f
        flammability = 1.8f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "焦炭", "相比煤炭有更高的碳含量,热值更高,杂质更少,燃烧更彻底,在现代冶炼工业中作为补充热源及碳素材料被广泛使用", "在几十年前,学界曾普遍认为自然地层中的碳与生物圈的关系密切,或者说一般认为地层中的煤炭和石油等碳资源是来自生物圈,一直到在无生物圈的行星上发现了结晶碳后这样的认知才得以修正")
        }
    }
    var iridium = IceItem("iridium", "#E4EFEF") {
        hardness = 6
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        cost = 2.5f
        bundle {
            desc(zh_CN, "铱", "强度很高的天然金属,它的FEX复合物可认为就是核能的半导体,就如同硅一样,铱就是精密核电路的核心材料", "地壳中几乎是丰度最低的金属,需求量却一天比一天高,水涨船高的生产指标曾让资源工程专家气急败坏的说：钻进地核吧,地球上的铱都在那里")
        }
    }
    var nuclear_waste = IceItem("nuclear_waste", "#AAB3AE") {
        hardness = 1
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0.25f
        bundle {
            desc(zh_CN, "核废料", "核反应后留下的残迹,仍有很强的放射性,含有多种有用的衰变产物,质谱分析检测出少量的铱", "任何时候,请谨慎考虑核污染的处理,尽管你可能并不身在地球,但随意污染外空环境也许会给我们的太空朋友不太好的印象——《星际开发公约 第三版》")
        }
    }
    var black_crystone = IceItem("black_crystone", "808080") {
        hardness = 3
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "黑晶石", "富含各种金属化合物的矿石,经过提炼可以得到许多有用的金属", "这个名字听起来实在是很像游戏里的魔法道具...你们地质学上给矿物起名都这么随意么？孔雀石？黑曜石？")
        }
    }
    var rock_bitumen = IceItem("rock_bitumen", "#808A73") {
        hardness = 1
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "岩层沥青", "从地壳深层钻取的由FEX胶结的沥青状物质,含有不少较重的矿物", "矿石碎屑在某些条件下会自发的富集并成块,大部分都会形成矿层,但许多未成型的矿屑被粘度很高的胶体裹挟时会在深层底层中形成沥青状的矿石胶结物,成分复杂——摘自《行星地质与资源勘探（第4版）》")
        }
    }
    var uranium_rawore = IceItem("uranium_rawore", "#95B564") {
        hardness = 4
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0.04f
        bundle {
            desc(zh_CN, "铀原矿", "铀的常见半晶质矿物,经过处理离心后可以生产铀的两种常见同位素", "尽管是放射性元素的矿物,但在矿石中的铀密度还不足以产生致命的辐射...什么？你问我为什么手上的小盒子在哒哒响？")
        }
    }
    var uranium_rawmaterial = IceItem("uranium_rawmaterial", "#B5D980") {
        hardness = 0
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0.1f
        bundle {
            desc(zh_CN, "铀原料", "铀矿石的化学冶炼中间物,经过增热离心可以制造燃料级的铀235和高纯度的铀238", "储存这种原料的桶总是异常醒目,你很难在别的什么地方看到一个红色的金属桶上印满了小心辐射剧毒品禁止受潮远离明火易爆物等等十来个标志,从桶顶印满到桶底,大抵确实是想让人知道这个东西真的非常危险吧")
        }
    }
    var iridium_mixed_rawmaterial = IceItem("iridium_mixed_rawmaterial", "#AECBCB") {
        hardness = 0
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "铱金混合物", "含有铱金属氧化物固体集团,经过加工提纯可以获得冶炼铱的进一步中间物", "在足够高压的核裂变反应中,原子核的衰变和四处弹射的中子总是能带来一些令人意想不到的东西,它们会残留在核废料里,等待着变成更有用的东西")
        }
    }
    var iridium_chloride = IceItem("iridium_chloride", "#CBE0E0") {
        hardness = 0
        explosiveness = 0f
        flammability = 0f
        radioactivity = 0f
        bundle {
            desc(zh_CN, "氯铱酸盐", "高纯度含铱化合物,经过煅烧脱氯后可以得到产品铱", "几乎所有行星上都有铱,但它们几乎都下沉到了行星的核部,只有少量与铂族金属聚合伴生为矿物构成,可利用量极少,一般冶炼产率也是少的可怜,因此人们从未停止改良铱的冶炼工艺的尝试,然而哪怕提高1%的净产率都让所有专家绞尽了脑汁")
        }
    }
}
