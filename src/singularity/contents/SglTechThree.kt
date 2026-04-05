package singularity.contents

import ice.content.IPlanets.阿德里
import ice.content.block.*
import mindustry.content.Blocks
import singularity.game.researchs.Inspire.PlaceBlockInspire
import singularity.game.researchs.Inspire.ResearchInspire
import singularity.game.researchs.ResearchManager.ResearchSDL
import singularity.game.researchs.RevealGroup.ResearchReveal

@Suppress("unused", "LocalVariableName")
object SglTechThree : ResearchSDL(), ContentList {

  override fun load() {
    CrafterBlocks.apply {
      Distributions.apply {
        EffectBlocks.apply {
          ProductBlocks.apply {
            DefenseBlocks.apply {
              PowerBlocks.apply {

                makePlanetContext(阿德里) {
                  val 初级科技 = research("初级科技", 180, 34) {
                    addContent(纤汲钻井, 基础传送带, 基础路由器, 基础交叉器)
                  }
                  val 基础存储与物流 = research("基础存储与物流", 120) {
                    addDependency(初级科技)
                    apply {
                      addContent(强化传送带, 特种传送带, 基础卸载器, 转换分类器, 转换溢流门, 盒子)
                    }
                  }

                  val 进阶存储与物流 = research("进阶存储与物流", 120) {
                    addDependency(基础存储与物流)
                    addContent(钴熠传送带, 运输节点, 梯度传送带, 极速卸载器, 装甲传送带桥, 仓库, 传输矿仓)
                  }

                  research("血肉与物流", 120) {
                    addDependency(进阶存储与物流)
                    addContent(血肉装甲传送带, 生物钢传送带, 交叉神经链路, 增生传送带桥)
                  }

                  research("高阶存储与物流", 120) {
                    addDependency(进阶存储与物流)
                    addContent(量子卸载器, 相位运输节点, 铱制高效运输节点, 重型质量驱动器, 无人机供货端, 无人机需求端)
                  }

                  research("基础物流管理模块", 120) {
                    addDependency(进阶存储与物流)
                    addContent(物流枢纽核心, 枢纽管道, 物流输入器, 物流输出器)
                  }

                  val 初级能量设施 = research("初级能量设施", 120) {
                    addDependency(初级科技)
                    addContent(能量节点, 大型能量节点, 小型能量电池, 能量电池,光伏板,地热发电机,风力发电机,蒸汽冷凝机,沼气发电机)
                  }
                  val 增益装置 = research("增益装置", 120) {
                    addDependency(初级能量设施)
                    addContent(小型照明器,大型照明器,定向超速器)
                  }


                  val 高级能量设施 = research("高级能量设施", 120) {
                    addDependency(初级能量设施)
                    addContent(大型能量电池,远程能量节点,大型风力发电机,中子能发电机)
                  }
                  val 这是你的神经吗 = research("这是你的神经吗", 120) {
                    addDependency(高级能量设施)
                    addContent(神经束节点,神经索节点)
                  }
                  val 裂与聚 = research("裂与聚", 120) {
                    addDependency(高级能量设施)
                    addContent(热核裂变反应堆,终归反应堆,核子冲击反应堆)
                  }

                  val 基础防御 = research("基础防护", 120) {
                    addDependency(初级科技)
                    addContent(碳钢墙, 大型碳钢墙)
                  }
                  val 中阶防护1 = research("中阶防护1", 120) {
                    addDependency(基础防御)
                    addContent(铱墙, 大型铱墙, 流金墙, 大型流金墙, 铬墙, 大型铬墙)
                  }
                  val 中阶防护2 = research("中阶防护2", 120) {
                    addDependency(基础防御)
                    inspire = (ResearchInspire(中阶防护1))
                    addContent(强化合金墙, 大型强化合金墙, 陶钢墙, 大型陶钢墙, 钴钢墙, 大型钴钢墙)
                  }

                  research("皮肉之苦", 120) {
                    addDependency(中阶防护2)
                    addContent(生物钢墙,大型生物钢墙)
                  }


                  val 高阶防护1 = research("高阶防护1", 120) {
                    inspire = (PlaceBlockInspire(Blocks.daciteBoulder))

                    addDependency(中阶防护2)
                    addContent(相位合金墙, 大型相位合金墙, 装甲闸门)
                  }

                  val researchReveal = ResearchReveal("reveal_test", 高阶防护1)

                  research("高阶防护2", 120) {
                    reveal = researchReveal
                    showRevealess()
                    addDependency(中阶防护2)
                    addContent(相控雷达, 混沌矩阵, 简并态中子聚合物墙, 大型简并态中子墙)
                  }

                  val 基础合成工厂 = research("基础合成工厂", 120) {
                    addDependency(初级科技)
                    addContent(普适冶炼阵列, 电弧炉, 萃取固化器, 铸铜厂)
                  }
                  val 进阶合成工厂 = research("进阶合成工厂", 120) {
                    addDependency(基础合成工厂)
                    addContent(等离子蚀刻厂, 蜂巢陶瓷合成巢, 铈凝块混合器, 钴钢压缩机, 陶钢熔炼炉, 冲压锻炉, 增压铈萃取器, 导能回路装配器)
                  }

                  val 矿物加工1 = research("矿物加工1", 120) {
                    addDependency(进阶合成工厂)
                    addContent(高速粉碎机, 矿石粉碎机)
                  }

                  research("矿物加工2", 120) {
                    addDependency(矿物加工1)
                    addContent(激光解离机, 洗矿机, 热能离心机)
                  }

                  val 高阶合成工厂 = research("高阶合成工厂", 120) {
                    addDependency(进阶合成工厂)
                    apply {
                      addContent(密匙编译器, 热能冶炼炉, 暮白高炉, 以太封装器, 高能陶钢聚合炉, 玳渊缚能厂)
                    }
                  }

                  research("我们的工厂", 120) {
                    addDependency(高阶合成工厂)
                    addContent(生物钢重组器, 血肉分离机, 血浆过滤器)
                  }

                  val 特化冶炼阵列 = research("特化冶炼阵列", 120) {
                    addDependency(基础合成工厂)
                    addContent(特化冶炼阵列, 单晶硅厂, 焚烧炉)
                  }

                  research("混合物制作器", 120) {
                    addDependency(特化冶炼阵列)
                    addContent(硫化物混合器, 爆炸物混合器, 低温混合器)
                  }
                  research("化合产物1", 120) {
                    addDependency(特化冶炼阵列)
                    addContent(电解机, 渗透分离槽, 反应仓, 燃烧室)
                  }
                  val 化合产物2 = research("化合产物2", 120) {
                    addDependency(特化冶炼阵列)
                    addContent(真空坩埚, 干馏塔, 蒸馏净化器, 渗透净化器, 热能冶炼炉)
                  }

                  val 中子基础 = research("中子基础", 120) {
                    addDependency(化合产物2)
                    addContent(FEX相位混合器, 结晶器, 燃料封装机, 气体相位封装机, 晶格构建器, 结晶器, 矩阵切割机)
                  }

                  research("中子进阶", 120) {
                    addDependency(中子基础)
                    addContent(中子透镜, 聚合引力发生器)
                  }
                  research("中子高阶", 120) {
                    addDependency(中子基础)
                    addContent(质量生成器, 物质逆化器, 强子重构仪, 析构器)
                  }

                  val 资源生成1 = research("资源生成1", 120) {
                    addDependency(初级科技)
                    addContent(抽水机, 大型抽水机, 蛮荒钻井, 晶簇粉碎器, 曼哈德钻井, 勘探雷达)
                  }
                  research("牙齿", 120) {
                    addDependency(资源生成1)
                    addContent(血肉钻井)
                  }
                  research("资源生成2", 120) {
                    addDependency(资源生成1)
                    addContent(热熔钻井, 潮汐钻头, 引力延展室)
                  }

                  research("光合作用", 120) {
                    addDependency(资源生成1)
                    addContent(绿藻池, 沼气池)
                  }

                  research("深层挖掘", 120) {
                    addDependency(资源生成1)
                    addContent(岩层钻井机, 岩石粉碎机)
                  }

                }
              }
            }
          }
        }
      }
    }
  }
}