package ice.content.block

import ice.content.IItems
import ice.content.block.liquid.P2PLiquidNode
import ice.library.EventType.addContentInitEvent
import ice.library.world.Load
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.distribution.itemNode.TransferNode
import ice.world.content.blocks.liquid.*
import ice.world.content.blocks.liquid.base.LiquidRouter
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.production.Pump
import singularity.world.blocks.liquid.LiquidUnloader

@Suppress("unused")
object LiquidBlocks : Load {
  val 泵腔 = PumpChamber("pumpChamber").apply {
    requirements(Category.liquid, ItemStack.with(IItems.肌腱, 40, IItems.碎骨, 10, IItems.无名肉块, 60))
    bundle {
      desc(zh_CN, "泵腔")
    }
  }
  val 动力泵 = Pump("kineticPump").apply {
    bundle {
      desc(zh_CN, "动力泵", "抽取流体")
    }
    size = 1
    squareSprite = false
    requirements(Category.liquid, IItems.高碳钢, 20, IItems.锌锭, 5)
  }
  val 谐振泵 = Pump("resonancePump").apply {
    bundle {
      desc(zh_CN, "谐振泵", "抽取液体")
    }
    size = 2
    squareSprite = false
    requirements(Category.liquid, IItems.高碳钢, 40, IItems.锌锭, 10, IItems.黄铜锭, 5, IItems.石英玻璃, 10)
  }
  val 心肌泵 = Pump("myocardialPump").apply {
    bundle {
      desc(zh_CN, "心肌泵", "高阶液泵,生物科技的高级产物")
    }
    size = 4
    squareSprite = false
    pumpAmount = 0.625f
    liquidCapacity = 240f
    consumePower(8f)
    requirements(
      Category.liquid,
      IItems.石英玻璃,
      120,
      IItems.铱板,
      120,
      IItems.导能回路,
      85,
      IItems.陶钢,
      45,
      IItems.生物钢,
      15
    )
  }

  val 谐振导管 = Conduit("resonanceConduit").apply {
    bundle {
      desc(zh_CN, "谐振导管", "向前传输液体,效率较低")
    }
    requirements(Category.liquid, IItems.高碳钢, 1, IItems.锌锭, 1, IItems.石英玻璃, 1)
    addContentInitEvent {
      bridgeReplacement = 导管桥
      junctionReplacement = 基础流体交叉器
    }
  }
  val 流金导管 = Conduit("fluxGoldConduit").apply {
    bundle {
      desc(zh_CN, "流金导管", "向前传输液体,效率比谐振导管更高")
    }
    liquidCapacity = 40f
    liquidPressure = 1.025f
    requirements(Category.liquid, IItems.金锭, 2, IItems.锌锭, 1, IItems.石英玻璃, 1)
    addContentInitEvent {
      bridgeReplacement = 导管桥
      junctionReplacement = 基础流体交叉器
    }
  }
  val 紊态导管 = ArmoredConduit("disorderedConduit").apply {
    bundle {
      desc(zh_CN, "紊态导管", "向前传输液体,同时不接受侧面输入,并且阻止流体流出")
    }
    leaks = false
    liquidCapacity = 40f
    liquidPressure = 1.025f
    requirements(Category.liquid, IItems.钴钢, 1, IItems.铅锭, 2, IItems.石英玻璃, 1)
    addContentInitEvent {
      bridgeReplacement = 导管桥
      junctionReplacement = 基础流体交叉器
    }
  }
  val 动脉导管 = Conduit("arteryConduit").apply {
    bundle {
      desc(zh_CN, "动脉导管", "向前传输液体,同时阻止流体流出")
    }
    healAmount = 30f
    health = 600
    armor = 2f
    leaks = false
    liquidCapacity = 60f
    liquidPressure = 1.1f
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 1, IItems.铱板, 2, IItems.陶钢, 1, IItems.生物钢, 1)
    addContentInitEvent {
      bridgeReplacement = 动脉导管桥
      junctionReplacement = 基础流体交叉器
    }
  }

  val 基础导管桥 = TransferNode("baseBridgeConduit").apply {
    bundle {
      desc(zh_CN, "基础导管桥", "向被连接的输出节点传输液体,传输节点面向连接的一侧不可接收液体")
    }
    directionAny = false
    range = 5
    hasPower = false
    arrowSpacing = 6f
    liquidCapacity = 50f
    placeableLiquid = true
    requirements(Category.liquid, IItems.高碳钢,2,IItems.锌锭, 5, IItems.石英玻璃, 5)
  }
  val 装甲导管桥 = TransferNode("bridgeConduitArmored").apply {
    bundle {
      desc(zh_CN, "装甲导管桥", "向被连接的输出节点传输液体,传输节点面向连接的一侧不可接收液体")
    }
    directionAny = false
    armor = 4f
    allowDiagonal = false
    range = 10
    fadeIn = false
    hasItems = false
    bridgeWidth = 8f
    hasPower = false
    arrowSpacing = 6f
    liquidCapacity = 80f
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 8, IItems.陶钢, 3, IItems.铱板, 5)
  }
  val 导管桥 = TransferNode("bridgeConduit").apply {
    bundle {
      desc(zh_CN, "导管桥", "在以自我为中心且边长为${2 * 6 + 1}的正方形范围内,向任意方向传输流体,4个方向皆可输入输出")
    }
    range = 6
    hasItems = false
    hasPower = false
    liquidCapacity = 10f
    requirements(Category.liquid, IItems.单晶硅,3, IItems.锌锭, 5, IItems.石英玻璃, 10)
  }
  val 长距导管桥 = TransferNode("bridgeConduitLarge").apply {
    bundle {
      desc(
        zh_CN,
        "长距导管桥",
        "消耗电力,在以自我为中心且边长为${2 * 10 + 1}的正方形范围内,向任意方向传输流体,4个方向皆可输入输出"
      )
    }
    range = 10
    hasItems = false
    liquidCapacity = 10f
    consumePower(30f / 60f)
    requirements(Category.liquid, IItems.单晶硅,6, IItems.铜锭, 8, IItems.锌锭, 10, IItems.石英玻璃, 20)
  }
  val 动脉导管桥 = TransferNode("bridgeConduitArtery").apply {
    bundle {
      desc(zh_CN, "动脉导管桥", "消耗电力进行传输液体,范围较大")
    }
    healAmount = 60f
    allowDiagonal = false
    hasItems = false
    directionAny = false
    armor = 4f
    range = 18
    liquidCapacity = 32f
    placeableLiquid = true
    consumePower(0.5f)
    requirements(Category.liquid, IItems.石英玻璃, 20, IItems.导能回路, 10, IItems.生物钢, 5)
  }

  val 基础流体路由器 = LiquidRouter("baseLiquidRouter").apply {
    bundle {
      desc(zh_CN, "基础流体路由器", "接受一个方向的流体输入,并平均输出到其他3个方向,可以储存一定量的流体")
    }
    liquidCapacity=50f
    size = 1
    health = 100
    requirements(Category.liquid, IItems.铜锭, 4, IItems.石英玻璃, 2)
  }
  val 装甲流体路由器 = LiquidRouter("armoredLiquidRouter").apply {
    bundle {
      desc(zh_CN, "装甲流体路由器", "向各个方向快速运输流体")
    }
    armor = 4f
    liquidCapacity = 80f
    liquidPressure = 1.1f
    solid = false
    underBullets = true
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 2, IItems.陶钢, 1, IItems.铱板, 3)
  }
  val 基础流体交叉器 = LiquidJunction("baseLiquidJunction").apply {
    bundle {
      desc(zh_CN, "基础流体交叉器", "用于让两条流体管线交叉通过而互不干扰的基础设施,能够减少液体管线布局时的绕路问题")
    }
    size = 1
    health = 80
    requirements(Category.liquid, IItems.黄铜锭, 5, IItems.石英玻璃, 5)
  }

  val 流体容器 = LiquidRouter("liquidContainer").apply {
    bundle {
      desc(zh_CN, "流体容器","用于储存液体的基础设施,容量较小,缓冲液体供给压力")
    }
    size = 2
    solid = true
    health = 500
    squareSprite = false
    liquidPadding = 6f / 4f
    liquidCapacity = 800f
    requirements(Category.liquid, IItems.铜锭, 20, IItems.石英玻璃, 15)
  }
  val 流体仓库 = LiquidRouter("liquidStorage").apply {
    bundle {
      desc(zh_CN, "流体仓库","存储大量流体的设施,可作为液体生产线的中转与缓冲,方便集中调度和稳定供给")
    }
    size = 3
    solid = true
    health = 1000
    squareSprite = false
    liquidPadding = 6f / 4f
    liquidCapacity = 2000f
    requirements(Category.liquid, IItems.铜锭, 50, IItems.石英玻璃, 30)
  }
  val 装甲储液罐 = LiquidRouter("armorLiquidStorage").apply {
    bundle {
      desc(zh_CN, "装甲储液罐", "双层复合装甲,内置的修复夹层可快速修复罐体,使其更安全地储存大量流体")
    }
    healAmount = 120f
    health = 3200
    armor = 8f
    size = 4
    liquidPadding = 4f
    liquidCapacity = 6400f
    placeableLiquid = true
    requirements(Category.liquid, IItems.铱板, 85, IItems.陶钢, 55, IItems.石英玻璃, 35)
  }
  val 流体枢纽 = MultipleLiquidBlock("fluidJunction").apply {
    bundle {
      desc(
        zh_CN,
        "流体枢纽",
        "正规的的流体存储设施,能将多种流体独立存储于同一单元,有效解决了复杂流水线中的空间占用问题,是高级化生产的必备设施,必须使用流体抽离器卸载"
      )
    }
    size = 3
    liquidCapacity = 1000f
    health = size * size * 100
    requirements(Category.liquid, IItems.铜锭, 50, IItems.铬锭, 30, IItems.单晶硅, 20, IItems.石英玻璃, 50)
  }
  val 流体抽离器 = LiquidClassifier("liquidClassifier").apply {
    bundle {
      desc(zh_CN, "流体抽离器", "流体枢纽的流体卸载装置,将流体卸载于相邻的可输入建筑,本身并不存储流体")
    }
    size = 1
    requirements(Category.liquid, IItems.铜锭, 20, IItems.黄铜锭, 10, IItems.铬锭, 10, IItems.石英玻璃, 10)
  }
  val 流体装卸器 = LiquidUnloader("liquid_unloader").apply {
    bundle {
      desc(zh_CN, "流体装卸器", "从方块中卸载流体,就像装卸器提取物品一样")
    }
    requirements(Category.liquid, IItems.单晶硅, 20, IItems.铝锭, 25, IItems.铬锭, 15)
    size = 1
  }
  val p2p流体节点 = P2PLiquidNode()
}