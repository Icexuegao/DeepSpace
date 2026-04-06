package ice.content.block.effect

import ice.content.IItems
import ice.content.block.ProductBlocks
import ice.library.EventType.addContentInitEvent
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.effect.ItemExtractor
import mindustry.type.Category
import mindustry.type.ItemStack

class 传输矿仓:ItemExtractor("conveyOreWar"){
  init{
    size = 2
    buildSize = 8
    range = 10 * 8f
    addContentInitEvent {
      allowLink.add(ProductBlocks.纤汲钻井)
    }
    requirements(Category.effect, ItemStack.with(IItems.低碳钢, 30))
    bundle {
      desc(zh_CN, "传输矿仓")
    }
  }
}