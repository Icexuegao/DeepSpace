package ice.content.block.effect

import ice.content.IItems
import ice.content.block.ProductBlocks
import ice.game.EventType.addContentInitEvent

import ice.world.content.blocks.effect.ItemExtractor
import mindustry.type.Category
import mindustry.type.ItemStack

class 传输矿仓:ItemExtractor("conveyOreWar"){
  init{
    localization {
      zh_CN {
        this.localizedName = "传输矿仓"
        description = "自动链接范围内的钻井,并远程从中提取产物输出.链接数量有限"
      }
    }
    size = 2
    buildSize = 8
    range = 10 * 8f
    addContentInitEvent {
      allowLink.add(ProductBlocks.纤汲钻井)
    }
    requirements(Category.effect, ItemStack.with(IItems.低碳钢, 30))
  }

  override fun outputsItems()=true
}