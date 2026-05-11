package ice.content.remains

import ice.content.IItems
import ice.content.block.CrafterBlocks
import ice.type.Remains
import mindustry.type.ItemStack
import mindustry.world.meta.Stats
import universecore.world.consumers.ConsumeType

class 玄岩板:Remains("remains_basalt_plate"){
  init  {
    localization {
      zh_CN {
        this.localizedName = "玄岩板"
        description = "由奇异,沉重的玄武岩打磨而成"
      }
    }
    effect = "[${CrafterBlocks.碳控熔炉.localizedName}]所需燃料减少[1]"
    var itemStack = ItemStack()
    CrafterBlocks.碳控熔炉.consumers.find {
      it.get(ConsumeType.item)!!.consItems!!.find { stack ->
        val bool: Boolean = stack.item == IItems.生煤
        if (bool) itemStack = stack
        bool
      } != null
    }

    install = {
      itemStack.amount -= 1
      CrafterBlocks.碳控熔炉.stats = Stats()
      CrafterBlocks.碳控熔炉.checkStats()
    }
    uninstall = {
      itemStack.amount += 1
      CrafterBlocks.碳控熔炉.stats = Stats()
      CrafterBlocks.碳控熔炉.checkStats()
    }
  }
}