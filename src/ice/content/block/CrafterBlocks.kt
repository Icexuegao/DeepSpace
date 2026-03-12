package ice.content.block

import ice.content.block.crafter.*
import ice.library.world.Load

@Suppress("unused")
object CrafterBlocks : Load {
  val 焚烧炉 = Incinerator()
  val 碳控熔炉 = CarbonSteelFactory()
  val 普适冶炼阵列 = UniversalSmelterArray()

  val 铸铜厂 = CopperFoundry()
  val 特化冶炼阵列 = SpecializedSmelterArray()
  val 硫化物混合器 = SulfideMixer()
  val 爆炸物混合器 = ExplosiveMixer()
  val 单晶硅厂 = MonocrystallineSiliconFactory()
  val 等离子蚀刻厂 = IntegratedFactory()

  val 低温混合器 = LowTemperatureMixer()
  val 矿石粉碎机 = MineralCrusher()
  val 蜂巢陶瓷合成巢 = CeramicKiln()
  val 冲压锻炉 = PressingForge()
  val 暮白高炉 = DuskFactory()
  val 玳渊缚能厂 = TortoiseshellFactory()
  val 萃取固化器 = ConcentrateSolidifier()
  val 电弧炉 = ArcFurnace()
  val 增压铈萃取器 = CeriumExtractorLarge()
  val 导能回路装配器 = ConductiveCircuitAssembler()
  val 高速粉碎机 = HighSpeedCrusher()
  val 钴钢压缩机 = CobaltSteelCompressor()
  val 以太封装器 =EtherEncapsulator()
  val 陶钢熔炼炉 = CeramicSteelFurnace()
  val 高能陶钢聚合炉 = HighEnergyCeramicSteelFurnace()
  val 铈凝块混合器 = CeriumBlockMixer()

  val 生物钢重组器 = BiomassReformer()
  val 血浆过滤器 = PlasmaFilter()
  val 血肉分离机 = BloodExtractor()

  val 电解机 = Electrolytor()
  val 渗透分离槽 = OsmoticSeparationTank()
  val 反应仓 = ReactingPool()
  val 燃烧室 = CombustionChamber()
  val 真空坩埚 = VacuumVrucible()
  val 干馏塔 = RetortColumn()
  val 激光解离机 = LaserResolver()

  val 洗矿机 = OreWasher()
  val 结晶器 = Crystallizer()

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

  val 强子重构仪 = HadronReconstructor()


  //待更改
  val 裂变编织器 = FissionWeaver()
  val 密匙编译器 = KeyCompiler()
  val 热能冶炼炉 = ThermalSmelter()
  val 蒸馏净化器 = DistillPurifier()
  val 渗透净化器 = OsmoticPurifier()
  val FEX相位混合器 = FEXPhaseMixer()
  val 析构器 = Destructors()


}