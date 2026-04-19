package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class 铈凝块混合器 : NormalCrafter("ceriumBlockMixer") {
  init {
    bundle {
      desc(zh_CN, "铈凝块混合器","将铈锭与爆炸混合物压制成铈凝块", "在特制的防静电车间内,研磨铈并与爆炸混合物混合后压制成型")
    }
    size = 2
    itemCapacity = 36
    craftEffect = IceEffects.square(IItems.铈凝块.color)
    newConsume().apply {
      power(1.5f)
      time(120f)
      items(IItems.爆炸化合物, 2, IItems.铈锭, 3)
    }
    newProduce().apply {
      item(IItems.铈凝块, 3)
    }
    requirements(Category.crafting, IItems.铬锭, 50, IItems.铪锭, 60, IItems.铈锭, 30, IItems.单晶硅, 35)
    drawers = DrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate").apply {
      rotateSpeed = 3f
    }, DrawDefault(), DrawRegion("-top"))
  }
}
