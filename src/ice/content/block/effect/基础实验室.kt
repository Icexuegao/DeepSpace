package ice.content.block.effect

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.science.Laboratory
import mindustry.type.Category

class 基础实验室:Laboratory("laboratory"){
  init  {
    consumePower(100f / 60)
    bundle {
      desc(zh_CN, "基础实验室")
    }
    itemCapacity = 100
    alwaysUnlocked = true
    requirements(Category.effect, IItems.高碳钢, 50, IItems.低碳钢, 50, IItems.铜锭, 50)
  }
}