package ice.content.block.crafter

import arc.Core
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter
import kotlin.math.max

class RetortColumn : NormalCrafter("retort_column") {init {
  bundle {
    desc(zh_CN, "干馏塔", "通过隔绝空气的高温分离煤炭中的物质,以制造焦炭")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.铬锭, 70, IItems.钴锭, 75, IItems.铜锭, 90, IItems.石英玻璃, 90, IItems.钴钢, 50
    )
  )
  size = 3
  itemCapacity = 12
  liquidCapacity = 20f



  newConsume()
  consume!!.time(90f)
  consume!!.power(2f)
  consume!!.item(IItems.生煤, 3)
  newProduce()
  produce!!.items(
    *ItemStack.with(
      Items.pyratite, 1, IItems.焦炭, 1
    )
  )

  craftEffect = Fx.smeltsmoke

  draw = DrawMulti(
    DrawDefault(), object : DrawFlame() {
      override fun load(block: Block) {
        top = Core.atlas.find(block.name + "_top")
        block.clipSize = max(block.clipSize, (lightRadius + lightSinMag) * 2f * block.size)
      }
    })
}
}