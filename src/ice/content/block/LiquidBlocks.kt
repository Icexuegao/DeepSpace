package ice.content.block

import ice.content.IItems
import ice.content.block.liquid.*
import universecore.world.Load
import ice.ui.bundle.localization
import ice.world.content.blocks.liquid.PumpChamber
import mindustry.type.Category
import mindustry.type.ItemStack

@Suppress("unused")
object LiquidBlocks :Load {
  val 泵腔 = PumpChamber("pumpChamber").apply {
    requirements(Category.liquid, ItemStack.with(IItems.肌腱, 40, IItems.碎骨, 10, IItems.无名肉块, 60))
    localization {
      zh_CN {
        localizedName = "泵腔"
        description = "一边跳动...一边泵出流体..."
      }
    }
  }
  val 动力泵 = 动力泵()
  val 谐振泵 = 谐振泵()
  val 涡流泵 = 涡流泵()
  val 心肌泵 = 心肌泵()

  val 谐振导管 = 谐振导管()
  val 流金导管 = 流金导管()
  val 紊态导管 = 紊态导管()
  val 动脉导管 = 动脉导管()

  val 基础导管桥 = 基础导管桥()
  val 装甲导管桥 = 装甲导管桥()
  val 导管桥 = 导管桥()
  val 长距导管桥 = 长距导管桥()
  val 动脉导管桥 = 动脉导管桥()

  val 基础流体路由器 = 基础流体路由器()
  val 装甲流体路由器 = 装甲流体路由器()
  val 基础流体交叉器 = 基础流体交叉器()

  val 流体容器 = 流体容器()
  val 流体仓库 = 流体仓库()
  val 装甲储液罐 = 装甲储液罐()
  val 流体枢纽 = 流体枢纽()
  val 流体抽离器 = 流体抽离器()
  val 流体分类阀 = 流体分类阀()
  val 流体装卸器 = 流体装卸器()
  val p2p流体节点 = P2PLiquidNode()
}
