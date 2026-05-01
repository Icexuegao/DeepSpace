package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems

import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.draw.DrawCultivator
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class 蜂巢陶瓷合成巢 : NormalCrafter("ceramicKiln") {
  init {
    localization {
      zh_CN {
        this.localizedName = "陶瓷合成巢"
        description = "将金珀沙转化为复合陶瓷,需要通入水"
        details = "利用硅基菌群分泌陶瓷基质,再经激光固化,生产过程中会发出蜂鸣般的共振声\n资源蜜蜂?"
      }
    }
    size = 4
    health = 300
    squareSprite = false
    itemCapacity = 30
    liquidCapacity = 210f
    drawers = DrawMulti(DrawRegion("-bottom"), DrawCultivator().apply {
      plantColor = Liquids.water.color
      plantColorLight = Color.valueOf("abbaff")
      spread = 2 * 8f - 6f
    }, DrawDefault())
    requirements(Category.crafting, IItems.铬锭, 50, IItems.铜锭, 20, IItems.锌锭, 30, IItems.黄铜锭, 10)
    newFormula {consumers, producers ->
      consumers.apply {
        time(120f)
        items(IItems.金珀沙, 8)
        liquid(Liquids.water, 32f / 60f)
        power(2f)
      }
      producers.apply {
        items(IItems.复合陶瓷, 4)
      }
    }
  }
}
