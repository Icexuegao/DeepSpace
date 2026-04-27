package ice.content.block.liquid

import ice.content.IItems
import ice.world.content.blocks.liquid.MultipleLiquidBlock
import mindustry.type.Category

class 流体枢纽:MultipleLiquidBlock("fluidJunction"){
  init {
    localization {
      zh_CN {
        localizedName = "流体枢纽"
        description = "存储大量不同种类的流体.可以使用流体抽离器抽取"
        details = "正规的的流体存储设施,能将多种流体独立存储于同一单元,有效解决了复杂流水线中的空间占用问题,是高级化生产的必备设施"
      }
    }
    size = 3
    liquidCapacity = 1000f
    health = size * size * 100
    requirements(Category.liquid, IItems.铜锭, 50, IItems.铬锭, 30, IItems.单晶硅, 20, IItems.石英玻璃, 50)
  }
}