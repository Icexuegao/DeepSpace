package ice.content

import arc.Core
import arc.graphics.g2d.TextureRegion
import arc.util.Time
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.item.IceItem
import ice.world.content.item.OreItem
import mindustry.content.Items
import singularity.core.UpdatePool

@Suppress("unused")
object IItems : Load {
  val 脊髓末梢 = IceItem("item_spinalCordEnding", "bf3e47") {
    nutrientConcentration = 0.2f
    bundle {
      desc(zh_CN, "脊髓末梢", "一种神经传导组织生物材料,一般从生物质中提取.可用于强化机械单位的反应速度与攻击精准度")
    }
  }
  val 无名肉块 = IceItem("item_namelessCut", "bf3e47") {
    nutrientConcentration = 0.5f
    bundle {
      desc(zh_CN, "无名肉块", "一种有机生物材料,表面布满跳动的血管", "我们以为北极只是冷,但是没想到,寒冷竟也能如此饥饿...")
    }
  }
  val 碎骨 = IceItem("item_bonesNap", "bf3e47") {
    nutrientConcentration = 0.1f
    bundle {
      desc(zh_CN, "碎骨", "一种经过粉碎处理的骨质生物材料,表面布满裂痕与强化接合点.可作为廉价的结构填充材料,或进一步强化为高硬度复合材料")
    }
  }
  val 肌腱 = IceItem("item_muscleTendon", "bf3e47") {
    nutrientConcentration = 0.25f
    bundle {
      desc(zh_CN, "肌腱", "一种高强度的纤维束生物材料,经过改造后具备惊人的弹性与韧性", "牛皮糖?")
    }
  }
  val 骨钢 = IceItem("item_fleshSteel", "bf3e47") {
    bundle {
      desc(zh_CN, "骨钢", "一种由血肉与骸骨组成的复合生物材料,坚硬且有微弱光泽,用于建造自愈型建筑")
    }
  }

  val 金珀沙 = IceItem("item_goldSand", "f8efad") {
    hardness = 1
    bundle {
      desc(zh_CN, "金珀沙", "一种闪耀着金色光泽的沙粒,其中混有微量琥珀结晶,在光照下会浮现半透明的琥珀色光晕.经适当工艺处理后,可从中提取出多种矿物质")
    }
  }
  val 黄玉髓 = IceItem("item_canaryStone", "f5c782") {
    hardness = 1
    bundle {
      desc(zh_CN, "黄玉髓", "一种淡黄色的半透明玉石,质地温润,可加工析出其他矿物")
    }
  }
  val 红冰 = IceItem("item_redIce", "ff7171") {
    radioactivity = 0.05f
    bundle {
      desc(zh_CN, "红冰", "一种红色的放射性晶体,内部含有不稳定的能量结构,在受到外部刺激时可能引发剧烈释放")
    }
  }
  val 晶状孢芽 = IceItem("item_crystallineSpore", "52578a") {
    flammability = 0.2f
    hardness = 1
    bundle {
      desc(zh_CN, "晶状孢芽", "一种半透明的晶体状孢子,内部含有稳定的能量结构,可用于制造光学设备或能量传导材料")
    }
  }
  val 灼热孢团 = IceItem("item_pyroSpore", "eac73e") {
    explosiveness = 0.1f
    flammability = 0.4f
    bundle {
      desc(zh_CN, "灼热孢团", "一种团状高温孢子,内部含有可燃物质,可作为燃料或爆炸物的原料使用")
    }
  }
  val 寂温疮体 = IceItem("item_lonelyHeatSoreSpore", "b3f1ff") {
    flammability = 0.1f
    bundle {
      desc(zh_CN, "寂温疮体", "一种奇特的低温孢子,表面覆盖有冰晶,可稳定吸收并传导热能,可用于冷却系统构建或低温材料的制备")
    }
  }
  val 腐败孢团 = IceItem("item_rottenSpore", "a09bbd") {
    radioactivity = 0.1f
    flammability = 0.3f
    bundle {
      desc(zh_CN, "腐败孢团", "一种团状腐烂孢子,持续散发着恶臭,可用于制造肥料或燃料")
    }
  }
  val 血囊孢子 = IceItem("item_bloodSpore", "ffa0a0") {
    radioactivity = 0.05f
    hardness = 1
    frames = 5
    frameTime = 15f
    bundle {
      desc(zh_CN, "血囊孢子", "一种采集自殷红树的单胞繁殖体孢子,生物结构极不稳定,在适宜条件下可能分化为[red]其他植株[]")
    }
  }
  val 石英 = OreItem("item_quartz", "ffffff", 1) {
    bundle {
      desc(zh_CN, "石英", "一种高硬度半透明晶体,质地纯净且常呈现玻璃光泽.对极端酸性环境具备优异的耐受性,常用于腐蚀性区域的设备建造")
    }
  }
  val 生煤 = OreItem("item_rawCoal", "#7D7D7D", 2) {
    flammability = 0.7f
    bundle {
      desc(zh_CN, "生煤", "一种未经精炼的原煤,杂质含量较高,燃烧效率有限.可作为基础燃料直接使用,亦可通过进一步加工提升纯度与热值")
    }
  }
  val 焦炭 = IceItem("item_coke", "#6A6A69") {
    hardness = 1
    explosiveness = 1.5f
    flammability = 1.8f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "焦炭", "一种经处理后的碳质材料,相较于原煤具有更高的碳含量与热值,杂质少,燃烧更为彻底,在冶炼工业中作为补充热源及碳素材料被广泛使用")
    }
  }
  val 燃素水晶 = IceItem("item_phlogistonCrystal", "b38f8d") {
    bundle {
      desc(zh_CN, "燃素水晶", "一种蕴含高能量的晶体,燃烧时能释放大量热能,是高效燃料的重要原料")
    }
  }

  val 铬铁矿 = OreItem("item_chrome", "768a9a", 3) {
    bundle {
      desc(zh_CN, "铬铁矿", "一种重要的铬矿石,表面呈现金属至亚金属光泽.含有高比例铬成分,是生产不锈钢与耐热合金的主要原料之一")
    }
  }
  val 赤铁矿 = OreItem("item_hematite", "c6a699", 2) {
    bundle {
      desc(zh_CN, "赤铁矿", "一种重要的铁矿石,呈红褐色,具有较高的铁含量,是合成碳素钢的主要原料之一")
    }
  }
  val 方铅矿 = OreItem("item_galena", "8c7fa9", 2) {
    hardness = 2
    radioactivity = 0.1f
    bundle {
      desc(zh_CN, "方铅矿", "一种重要的铅矿石,呈立方晶体结构,常伴有银等贵金属,具有弱放射性.是提炼铅的主要原料之一")
    }
  }
  val 黄铜矿 = OreItem("item_copperPyrites", "eac73e", 2) {
    bundle {
      desc(zh_CN, "黄铜矿", "一种重要的铜矿石,呈黄铜色金属光泽,常伴有金和银等贵金属.是提炼铜的主要原料之一")
    }
  }
  val 闪锌矿 = OreItem("item_sphalerite", "578c80", 2) {
    bundle {
      desc(zh_CN, "闪锌矿", "一种重要的铜矿石,纯闪锌矿近于无色,但通常因含铁而呈浅黄,浅绿,随含铁量的增加而变深.是提炼锌的主要原料之一")
    }
  }
  val 金矿 = OreItem("item_goldOre", "f8df87", 3) {
    bundle {
      desc(zh_CN, "金矿", "一种珍贵的金矿石,虽整体含量虽不高,但仍是提取贵重金属的核心来源之一")
    }
  }
  val 锆英石 = OreItem("item_azorite", "8c3e2d", 4) {
    bundle {
      desc(zh_CN, "锆英石", "一种坚硬的硅酸盐矿石,呈短柱状,广泛用于陶瓷工业与耐火材料制备.是提取锆的主要原料之一")
    }
  }
  val 硫钴矿 = OreItem("item_linnaeite", "cfecf1", 3) {
    bundle {
      desc(zh_CN, "硫钴矿", "一种重要的钴矿石,常带粉红色调,立方体或八面体晶形.是提炼钴的主要原料之一", "这玩意儿比钻石难啃\n——采矿队长R-42的日志")
    }
  }
  val 铱锇矿 = OreItem("item_iridiumosm", "656565", 4) {
    bundle {
      desc(zh_CN, "铱锇矿", "一种稀有的铱锇矿石,表面呈黯淡金属光泽,含有铱和锇,是提炼这两种金属的主要原料之一")
    }
  }
  val 铈硅石 = OreItem("item_cerite", "BFC8E2", 2) {
    bundle {
      desc(zh_CN, "铈硅石", "一种稀土矿石,常呈黄褐色至红褐色.含有较高比例的铈元素,是提炼铈的主要原料之一")
    }
  }
  val 铀原矿 = OreItem("item_uranium_rawore", "#95B564", 4) {
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0.04f
    bundle {
      desc(zh_CN, "铀原矿", "一种铀矿石,呈浅绿色沥青光泽,经处理离心后可分离出铀的两种常见同位素", "尽管是放射性元素的矿物,但在矿石中的铀密度还不足以产生致命的辐射...什么？你问我为什么手上的小盒子在哒哒响？")
    }
  }
  val 黑晶石 = OreItem("item_black_crystone", "808080", 3) {
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "黑晶石", "一种深色矿石,表面呈现玻璃至半金属光泽.内部富含多种金属化合物,经提炼后可分离出多种有用金属元素", "这个名字听起来实在是很像游戏里的魔法道具...你们地质学上给矿物起名都这么随意么？孔雀石？黑曜石？")
    }
  }

  val 低碳钢 = IceItem("item_lowCarbonSteel", "d4d7e4") {
    bundle {
      desc(zh_CN, "低碳钢", "一种含碳量较低的钢质金属材料,具有优异的塑性和韧性,是建筑和机械制造的常用基础材料")
    }
  }
  val 高碳钢 = IceItem("item_highCarbonSteel", "bedfee") {
    bundle {
      desc(zh_CN, "高碳钢", "一种含碳量较高的钢质金属材料,硬度优于低碳钢,适合用于制造需要高强度的建筑和部件")
    }
  }
  val 铜锭 = IceItem("item_copperIngot", "d99d73") {
    bundle {
      desc(zh_CN, "铜锭", "一种以优异导电性和导热性著称的金属材料,质地柔软且延展性良好.是电力设备和热交换系统的重要材料")
    }
  }
  val 铅锭 = IceItem("item_leadIngot", "8c7fa9") {
    bundle {
      desc(zh_CN, "铅锭", "一种高密度金属材料,具有出色的辐射防护能力,既可用于防护设施,也可作为重型武器的材料", "从挡住辐射到砸穿装甲,这份量从来不会让人失望")
    }
  }
  val 锌锭 = IceItem("item_zincIngot", "578c80") {
    bundle {
      desc(zh_CN, "锌锭", "一种兼具牺牲阳极保护与加热超塑性的金属材料.既可作为装甲镀层大幅延长载具寿命,又能制成高能锌空气电池为单位护盾供电", "防锈?试试用锌箔包裹EMP炸弹:)")
    }
  }
  val 黄铜锭 = IceItem("item_brassIngot", "eac73e") {
    bundle {
      desc(zh_CN, "黄铜锭", "一种铜合金,有较高的强度和耐腐蚀性,用于制造各种结构零和连接件")
    }
  }
  val 铬锭 = IceItem("item_chromeIngot", "C8C8E4") {
    bundle {
      desc(zh_CN, "铬锭", "一种轻质高硬度的金属材料,具有优异的耐腐蚀性,用于制造高强度建筑和防护装备")
    }
  }
  val 金锭 = IceItem("item_goldIngot", "f8df87") {
    bundle {
      desc(zh_CN, "金锭", "一种贵重金属材料,具有优异的导电性.多用于制作电子部件与能量传导部件")
    }
  }
  val 钴锭 = IceItem("item_cobaltIngot", "b3f1ff") {
    bundle {
      desc(zh_CN, "钴锭", "一种具备良好耐高温与耐腐蚀特性的金属材料,广泛用于建筑结构及精密测量仪器的制造")
    }
  }
  val 铪锭 = IceItem("item_hafniIngot", "f7e5f3") {
    bundle {
      desc(zh_CN, "铪锭", "一种稀有的高熔点金属材料,在能量传导和高温环境中表现出色,是高级能量设备的关键材料")
    }
  }
  val 铈锭 = IceItem("item_ceriumIngot", "BFC8E2") {
    explosiveness = 0.25f
    flammability = 1.2f
    radioactivity = 0.6f
    cost = 1.2f
    healthScaling = 0.6f
    bundle {
      desc(zh_CN, "铈锭", "一种广泛应用的金属材料,用于研磨抛光剂,特种玻璃及推进器零件等")
    }
  }
  val 钍锭 = IceItem("item_thoriumIngot", Items.thorium.color.toString()) {
    radioactivity = 1f
    cost = 2f
    bundle {
      desc(zh_CN, "钍锭", "一种放射性金属材料,具有高能量密度,常用于核反应堆和核武器")
    }
  }
  val 铱锭 = IceItem("item_iridium", "#E4EFEF") {
    hardness = 6
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    cost = 2.5f
    bundle {
      desc(zh_CN, "铱锭", "一种强度极高的稀有金属材料,其复合物具备核能的半导体特性(类似硅在电子工业中的半导体特性),是精密核电路制造的核心材料")
    }
  }
  val 钴钢 = IceItem("item_cobaltSteel", "c5d1e0") {
    bundle {
      desc(zh_CN, "钴钢", "一种钴合金,具备优异的耐高温与耐腐蚀性能,质地坚硬且结构稳定.常用于高强度建筑结构及防护装备的制造")
    }
  }
  val 铝锭 = IceItem("item_aluminium", "#C0ECFF") {
    hardness = 3
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    cost = 0.9f
    bundle {
      desc(zh_CN, "铝锭", "一种耐低温的常见金属材料,质地轻盈,经处理后强度显著提升.是航空航天领域广泛使用的结构材料", "在绝大多数小质量的行星上,铝的丰富度通常都是在金属元素中排序最靠前的")
    }
  }

  val 硫化合物 = IceItem("item_sulfurCompound", "ffaa5f") {
    flammability = 1.4f
    explosiveness = 0.4f
    buildable = false
    bundle {
      desc(zh_CN, "硫化合物", "硫素与金属化合而成的晶体常态下相对平和,但其受热分解后伴随发生着地急剧化学反应,足以融毁多数\n能够迸发出大量热能的可控燃素显然重要,因而硫化物得以被视作为一种重要材料,为工业或武器的方方面面所利用")
    }
  }
  val 爆炸化合物 = IceItem("item_explosiveCompound", "ff795e") {
    flammability = 0.4f
    explosiveness = 1.2f
    buildable = false
    bundle {
      desc(zh_CN, "爆炸化合物", "一种极端不稳定的化合物,为满足冲击武器需求而诞生的高能材料,爆炸威力极强,应避免长途运输与存储")
    }
  }
  val 低温化合物 = IceItem("item_lowTemperatureCompound", "C0ECFF") {
    charge = 0.1f
    buildable = false
    bundle {
      desc(zh_CN, "低温化合物", "一种在极端低温环境中凝结而成的化合物,具备优异的导热性能,常用于制造高效率的冷却系统")
    }
  }
  val 铈凝块 = IceItem("item_ceriumClot", "929DB5") {
    buildable = false
    explosiveness = 1.5f
    flammability = 3.6f
    bundle {
      desc(zh_CN, "铈凝块", "对碰撞和温度极为敏感的金属凝块,暴露于空气中极易自燃,需严格密封保存")
    }
  }
  val 铱板 = IceItem("item_iridiumPlate", "656565") {
    cost = 1.8f
    healthScaling = 1f
    bundle {
      desc(zh_CN, "铱板", "已经完成了压铸工序的铱合金,具备极强的抗冲击性能,是一种优秀的各向异性介质,适用于精密核电路及高性能防护结构的制造")
    }
  }
  val 燃能晶 = IceItem("item_burningCrystal", "737373") {
    bundle {
      desc(zh_CN, "燃能晶", "一种经过特殊工艺处理的晶体,燃烧时可释放巨大能量,是驱动各类工业设备的核心能源材料")
    }
  }
  val 石英玻璃 = IceItem("item_quartzGlass", "ebeef5") {
    bundle {
      desc(zh_CN, "石英玻璃", "由高纯度石英熔制而成的玻璃,具备优异的耐高温性能和极低的热膨胀系数,能够有效抵御大部分化学反应侵蚀,是理想的反应容器")
    }
  }
  val 复合陶瓷 = IceItem("item_compositeCeramic", "ebeef5") {
    bundle {
      desc(zh_CN, "复合陶瓷", "一种凭借超凡抗冲击性与热稳定性著称的复合材料.既可作为能量护盾发生器的核心基底材料,又能制成高速单位所需的耐热防护瓦")
    }
  }
  val 陶钢 = IceItem("item_potterySteel", "D6DEC6") {
    cost = 1.8f
    healthScaling = 0.8f
    bundle {
      desc(zh_CN, "陶钢", "复合装甲材料,能够快速且均匀地将电磁粒子辐射分散传导至装甲各处,从而大幅降低高强度动能冲击造成的局部破坏")
    }
  }
  val 石墨烯 = IceItem("item_graphene", "52578a") {
    bundle {
      desc(zh_CN, "石墨烯", "超薄的单层碳原子材料,具有出色的导电性和机械强度,是高级电子设备的核心材料")
    }
  }
  val 单晶硅 = IceItem("item_monocrystallineSilicon", "575757") {
    bundle {
      desc(zh_CN, "单晶硅", "一种高纯度的硅晶体,是制造高级电子设备和处理器的基础材料")
    }
  }
  val 导能回路 = IceItem("item_conductingCircuit", "867F8C") {
    bundle {
      desc(zh_CN, "导能回路", "将高纯度单晶硅回路蚀刻于放射性能级降低的钍基座中制成的能量传导组件,拥有极强的导能性与稳固性")
    }
    charge = 0.8f
    cost = 1.5f
    healthScaling = 0.5f
  }
  val 电子元件 = IceItem("item_integratedCircuit", "53565c") {
    bundle {
      desc(zh_CN, "电子元件", "精密的电子器件,是制造高级设备和处理器的基础材料")
    }
  }
  val 肃正协议 = IceItem("item_solemnProtocol", "FF5845") {
    cost = 600f
    bundle {
      desc(zh_CN, "肃正协议", "装载特定密匙的激活协议,授权后即可解锁高级军械的控制权限与制造权限")
    }
  }
  val 生物钢 = IceItem("item_biologicalSteel", "D75B6E") {
    bundle {
      desc(zh_CN, "生物钢", "一种被强行抑制了绝大部分活性的生物材料.此材料制造的装甲具有整体无缝,附着性强,耐酸碱,防辐射,防腐蚀,耐冲击等的优异特性,只是....", "你真的要使用他吗?")
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

  val 暮光合金 = IceItem("item_duskIngot", "deedff") {
    bundle {
      desc(zh_CN, "暮光合金", "一种在暮光时分呈现特殊光泽的合金,具有优异的能量传导特性,是高级能量设备的核心材料")
    }
  }
  val 以太能 = IceItem("item_etherealEnergy", "E6C4EE") {
    frames = 2
    transitionFrames = 24
    frameTime = 6f
    radioactivity = 0.4f
    charge = 1f
    cost = 2.4f
    healthScaling = 0.8f
    bundle {
      desc(zh_CN, "以太能", "存储在容器种的高能粒子能量,在特定结构排列下注入相位能量后可影响时空结构.研究初期曾引发时空回溯,空间错位及乱序传送等现象", "在以太粒子以特定结构排列时注入相位能量,以太粒子会在法韦克内敛空间的能量辐射下,形成以伊塔宏粒子射线为场能的波态中子向心力场")
    }
  }
  val 玳渊矩阵 = IceItem("item_abyssMatrix", "d7bdff") {
    bundle {
      desc(zh_CN, "玳渊矩阵", "具有独特的能量传导和存储特性,是高级炮塔弹药的核心材料")
    }
  }

  val FEX水晶 = IceItem("item_crystal_FEX", "#D2393E") {
    hardness = 3
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0.4f
    cost = 1.25f
    bundle {
      desc(zh_CN, "导能晶体", "一种高纯度低能阻晶体,中子导率与核性能均有显著提升,是核工业中不可或缺的核心材料")
    }
  }
  val 充能FEX水晶 = object : IceItem("item_crystal_FEX_power", "#E34248") {
    init {
      hardness = 3
      explosiveness = 3.6f
      flammability = 0f
      radioactivity = 3f
      cost = 1.35f
      frameTime = 9f
      bundle {
        desc(zh_CN, "活化导能结晶", "一种经大量能量激发的高纯度低能阻晶体,性质极其不稳定,危险且难以储存,但在需要释放中子能的地方不可或缺", "严禁用任何致密介质接触激发态的导能结晶.在《中子工业操作管理条例》中,此类条目均以醒目字体特别标注.每一行警示背后,都是无法挽回的事故与代价")
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
  val 矩阵合金 = IceItem("item_matrix_alloy", "#929090") {
    hardness = 4
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    cost = 1.4f
    bundle {
      desc(zh_CN, "矩阵合金", "一种由大量纳米机器人聚合而成的可编程合金,机械强度较低,但能够自由变换形态甚至物态,具备极高的适应性与灵活性")
    }
  }
  val 强化合金 = IceItem("item_strengthening_alloy", "#B1B1B0") {
    hardness = 5
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    cost = 1.25f
    bundle {
      desc(zh_CN, "强化合金", "一种各项性能均衡优异的合金,具备极高的结构强度与抗性,广泛应用于各类领域")
    }
  }
  val 气凝胶 = IceItem("item_aerogel", "#D5EBEE") {
    hardness = 3
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    cost = 1.1f
    bundle {
      desc(zh_CN, "气凝胶", "硅基结构的超轻材料.内部由大量细微蜂窝状孔洞填充,具备强度高,密度极小,绝缘绝热等优异特性,广泛应用于各领域")
    }
  }
  val 简并态中子聚合物 = IceItem("item_degenerate_neutron_polymer", "#FF7FE0") {
    hardness = 10
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    cost = 3f
    bundle {
      desc(zh_CN, "简并态中子聚合物", "由中子简并态构成的超高密度聚合物,硬度难以测量,韧性与塑性趋近于零.除作为极端环境下的结构材料外,亦是引力场技术的理想场源")
    }
  }
  val 铀238 = IceItem("item_uranium_238", "#7CA73D") {
    hardness = 2
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0.4f
    cost = 1.5f
    bundle {
      desc(zh_CN, "铀-238", "俗称贫铀的放射性同位素,硬度较高且结构致密.可用作中子反射板及动能穿甲弹头,亦可吸收中子后衰变为更有用的钚-239", "无论在哪一个星球上,自然状态下的铀238总是占据铀的绝大部分的丰富度,所幸它可以转变为更有用的东西,如果全部都用来造穿甲弹和防弹装甲之类的话,保有量可能可以用到下个地质纪...")
    }
  }
  val 铀235 = IceItem("item_uranium_235", "#B5D980") {
    hardness = 2
    explosiveness = 0f
    flammability = 0f
    radioactivity = 1.6f
    bundle {
      desc(zh_CN, "铀-235", "一种主要的裂变核燃料,同时也是天然的中子放射源")
    }
  }
  val 钚239 = IceItem("item_plutonium_239", "#D1D19F") {
    hardness = 2
    explosiveness = 0f
    flammability = 0f
    radioactivity = 1.6f
    bundle {
      desc(zh_CN, "钚-239", "由铀-238吸收中子后衰变而成的强放射性同位素,与铀-235同为常用的裂变核燃料", "尽管铀238可以转换为钚239,但也许你需要一个不小规模的反应堆阵列才能实现量产钚239了")
    }
  }
  val 相位封装氢单元 = IceItem("item_encapsulated_hydrogen_cell", "#9EFFC6") {
    hardness = 2
    explosiveness = 2.4f
    flammability = 1.8f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "相位封装氢单元", "由相位材料包裹的氢中子靶丸,可在核反应堆中接收中子并转化为核燃料", "相位物的中子光路学结构会将中子聚焦到中心存储氢的空腔,以最大化中央接收到的中子流,在中子流的轰击下,大量氢原子会转化为较为容易发生核聚变反应的同位素,继而参与核聚变")
    }
  }
  val 相位封装氦单元 = IceItem("item_encapsulated_helium_cell", "#F9FFDE") {
    hardness = 2
    explosiveness = 0.3f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "相位封装氦单元", "由相位材料包裹的氦中子靶丸,可在核反应堆中接收中子并转化为核燃料", "相位物的中子光路学结构会将中子聚焦到中心存储氦的空腔,以最大化中央接收到的中子流,在中子流的轰击下,大量氦原子会转化为较为容易发生核聚变反应的同位素,继而参与核聚变")
    }
  }
  val 浓缩铀235核燃料 = IceItem("item_concentration_uranium_235", "#95B564") {
    hardness = 4
    explosiveness = 12f
    flammability = 0f
    radioactivity = 2.4f
    bundle {
      desc(zh_CN, "封装铀-235", "一种经高度浓缩并超低温封装的铀核燃料,可在超过常温的临界压缩状态下稳定存储,是反应堆的主要燃料之一")
    }
  }
  val 浓缩钚239核燃料 = IceItem("item_concentration_plutonium_239", "#B0B074") {
    hardness = 4
    explosiveness = 12f
    flammability = 0f
    radioactivity = 2.4f
    bundle {
      desc(zh_CN, "封装钚-239", "一种经高度浓缩并超低温封装的钚核燃料,可在超过常温的临界压缩状态下稳定存储,是反应堆的主要燃料之一")
    }
  }
  val 氢聚变燃料 = IceItem("item_hydrogen_fusion_fuel", "#83D6A0") {
    hardness = 2
    explosiveness = 2.4f
    flammability = 1.8f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "氢聚变燃料", "一种核燃料,压缩氢原子同位素,原子性质易于发生聚变,属轻核聚变燃料")
    }
  }
  val 氦聚变燃料 = IceItem("item_helium_fusion_fuel", "#D0D6B7") {
    hardness = 2
    explosiveness = 0.3f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "氦聚变燃料", "一种核燃料,压缩氦原子同位素,原子性质易于发生聚变,属轻核聚变燃料")
    }
  }
  val 反物质 = IceItem("item_anti_metter", "734CD2") {
    hardness = 12
    explosiveness = 64f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "反物质", "由引力场约束隔绝的反物质,与正物质接触即发生完全湮灭并将质量转化为纯能量,危险性极高,广泛用于强攻击性武器", "通常每一个反物质储存单元都会具有一个独立的能源模块来维持约束力场,在远航星舰队伴随燃料仓库放出的耀眼的焰火消失后,停电被成为了携带反物质的情况下最危险的事")
    }
  }
  val 绿藻块 = IceItem("item_chlorella_block", "#6CB855") {
    hardness = 1
    explosiveness = 0.4f
    flammability = 1.2f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "绿藻块", "一种经分离杂质后整合而成的绿藻细胞生物材料,可用于提取更有价值的绿藻素")
    }
  }
  val 绿藻素 = IceItem("item_chlorella", "#7BD261") {
    hardness = 1
    explosiveness = 1.2f
    flammability = 1.6f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "绿藻素", "从绿藻细胞中分离出的生物活性成分,是绿藻进行光合作用的核心物质.用于将光合作用机制应用于工业生产")
    }
  }
  val 碱石 = IceItem("item_alkali_stone", "#B0BAC0") {
    hardness = 1
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "碱石", "一种以碱石灰为主要成分的矿石,内部富含多种含氯金属盐类.用于电离处理分离出碱液与氯气", "通常来说,河流会携带附着在河床上的盐类物质,并汇入海洋或者湖泊,长久的积累使得水体中的盐浓度不断升高,进而形成高含盐量的海洋和咸水湖,绝大多数表面具有活跃水圈的星球都是符合这样的规律")
    }
  }
  val 絮凝剂 = IceItem("item_flocculant", "ffffff") {
    hardness = 1
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "絮凝剂", "投入水中可形成大量多孔絮状胶质的化工材料,用于吸附或者分离固形物质,是重要的化工材料", "注意防潮\n请于阴凉环境储存,避免阳光直射\n保质期：六个月\n若不慎误食,请立即就医并向医师提供此说明书")
    }
  }

  val 核废料 = IceItem("item_nuclear_waste", "#AAB3AE") {
    hardness = 1
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0.25f
    bundle {
      desc(zh_CN, "核废料", "核反应后残留的放射性物质,仍具有较强的辐射性.虽已无太大核能利用价值,但仍能作为提炼铱元素的非常规来源")
    }
  }
  val 岩层沥青 = IceItem("item_rock_bitumen", "#808A73") {
    hardness = 1
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "岩层沥青", "一种从地壳深层钻取的沥青状矿石,质地粘稠,内含有多种重矿物成分", "矿石碎屑在某些条件下会自发的富集并成块,大部分都会形成矿层,但许多未成型的矿屑被粘度很高的胶体裹挟时会在深层底层中形成沥青状的矿石胶结物,成分复杂")
    }
  }
  val 铀原料 = IceItem("item_uranium_rawmaterial", "#B5D980") {
    hardness = 0
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0.1f
    bundle {
      desc(zh_CN, "铀原料", "铀矿石的化学冶炼中间物,经过增热离心可以制造燃料级的铀235和高纯度的铀238")
    }
  }
  val 铱金混合物 = IceItem("item_iridium_mixed_rawmaterial", "#AECBCB") {
    hardness = 0
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "铱金混合物", "一种含有铱金属氧化物固体化合物,经过加工提纯可以获得冶炼铱的进一步中间物", "在足够高压的核裂变反应中,原子核的衰变和四处弹射的中子总是能带来一些令人意想不到的东西,它们会残留在核废料里,等待着变成更有用的东西")
    }
  }
  val 氯铱酸盐 = IceItem("item_iridium_chloride", "#CBE0E0") {
    hardness = 0
    explosiveness = 0f
    flammability = 0f
    radioactivity = 0f
    bundle {
      desc(zh_CN, "氯铱酸盐", "一种高纯度含铱化合物,经过煅烧脱氯后可以得到产品铱", "几乎所有行星上都有铱,但它们几乎都下沉到了行星的核部,只有少量与铂族金属聚合伴生为矿物构成,可利用量极少")
    }
  }
}
