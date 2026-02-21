package ice.content.block.crafter

import arc.graphics.Color
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter

class TortoiseshellFactory : NormalCrafter("tortoiseshellFactory") {
  init {
    bundle {
      desc(
        zh_CN, "玳渊缚能厂", "为了生产大型能量武器设施,由枢机批准的能量生产建筑,将狂暴的玳渊能量封印在稳定的矩阵结构中,每一块矩阵都蕴含着巨大的能量"
      )
    }
    size = 4
    health = 700
    itemCapacity = 20
    draw = DrawMulti(DrawDefault(), DrawFlame().apply {
      flameColor = Color.valueOf("c4aee4")
    })
    requirements(Category.crafting, IItems.铬锭, 300, IItems.铪锭, 200, IItems.黄铜锭, 170)

    newConsume().apply {
      time(120f)
      items(IItems.铪锭, 10, IItems.暮光合金, 3, IItems.铱锭, 1)
    }
    newProduce().apply {
      items(IItems.玳渊矩阵, 1)
    }
  }
}