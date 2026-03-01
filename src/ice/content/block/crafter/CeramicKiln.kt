package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.draw.DrawCultivator
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class CeramicKiln : NormalCrafter("ceramicKiln") {
  init {
    bundle {
      desc(
        zh_CN, "蜂巢陶瓷合成巢", "利用基因改造的硅基菌群分泌陶瓷基质,再经激光固化,生产过程中会发出蜂鸣般的共振声", "资源蜜蜂?"
      )
    }
    size = 4
    health = 300
    squareSprite = false
    draw = DrawMulti(DrawRegion("-bottom"), DrawCultivator().apply {
      plantColor = Liquids.water.color
      plantColorLight = Color.valueOf("abbaff")
      spread = 2 * 8f - 6f
    }, DrawDefault())
    requirements(Category.crafting, IItems.铬锭, 50, IItems.铜锭, 20, IItems.锌锭, 30, IItems.黄铜锭, 10)

    newConsume().apply {
      time(120f)
      items(IItems.金珀沙, 10)
      liquid(Liquids.water, 32f / 60f)
      power(1f)
    }
    newProduce().apply {
      items(IItems.复合陶瓷, 3)
    }
  }
}