package ice.content.block

import ice.content.IItems
import ice.content.block.crafter.*
import ice.graphics.IceColor
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.crafting.Incinerator
import mindustry.type.Category

@Suppress("unused")
object CrafterBlocks : Load {
  val 焚烧炉 = Incinerator("incinerator").apply {
    size = 1
    flameColor = IceColor.b4
    consumePower(20 / 60f)
    requirements(Category.crafting, IItems.高碳钢, 20, IItems.铜锭, 5, IItems.铅锭, 5)
    bundle {
      desc(zh_CN, "焚烧炉")
    }
  }

  val 碳控熔炉 = CarbonSteelFactory()
  val 普适冶炼阵列 = UniversalSmelterArray()

  val 铸铜厂 = CopperFoundry()
  val 特化冶炼阵列 = SpecializedSmelterArray()
  val 硫化物混合器 = SulfideMixer()
  val 爆炸物混合器 = ExplosiveMixer()
  val 单晶硅厂 = MonocrystallineSiliconFactory()
  val 等离子蚀刻厂 = IntegratedFactory()

  val 矿石粉碎机 = MineralCrusher()
  val 蜂巢陶瓷合成巢 = CeramicKiln()
  val 冲压锻炉 = PressingForge()
  val 暮白高炉 = DuskFactory()
  val 玳渊缚能厂 = TortoiseshellFactory()
  val 萃取固化器 = ConcentrateSolidifier()
  val 电弧炉 = ArcFurnace()
  val 铈提取器 = CeriumExtractor()
  val 增压铈萃取器 = CeriumExtractorLarge()
  val 导能回路装配器 = ConductiveCircuitAssembler()
  val 高速粉碎机 = HighSpeedCrusher()
  val 钴钢压缩机 = CobaltSteelCompressor()
  val 陶钢熔炼炉 = CeramicSteelFurnace()
  val 高能陶钢聚合炉 = HighEnergyCeramicSteelFurnace()
  val 铈凝块混合器 = CeriumBlockMixer()

  val 裂变编织器 = FissionWeaver()
  val 绿藻池 = CulturingBarn()
  val 沼气池 = Incubator()
  val 电解机 = Electrolytor()
  val 渗透分离槽 = OsmoticSeparationTank()
  val 反应仓 = ReactingPool()
  val 燃烧室 = CombustionChamber()
  val 真空坩埚 = VacuumVrucible()
  val 热能冶炼炉 = ThermalSmelter()
  val 干馏塔 = RetortColumn()
  val 激光解离机 = LaserResolver()
  val 蒸馏净化器 = DistillPurifier()
  val 渗透净化器 = OsmoticPurifier()
  val 洗矿机 = OreWasher()
  val 结晶器 = Crystallizer()
  val FEX相位混合器 = FEXPhaseMixer()
  val 燃料封装机 = FuelPackager()
  val 气体相位封装机 = GasPhasePacker()
  val 热能离心机 = ThermalCentrifuge()
  val 晶格构建器 = LatticeConstructor()
  val FEX充能座 = FEXCrystalCharger()
  val 矩阵切割机 = MatrixCutter()
  val 中子透镜 = NeutronIens()
  val 聚合引力发生器 = PolymerGravitationalGenerator()
  val 质量生成器 = QualityGenerator()
  val 物质逆化器 = SubstanceInverter()
  val 析构器 = Destructors()
  val 强子重构仪 = HadronReconstructor()
}