package ice.content.block.crafter

import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class CeriumBlockMixer:NormalCrafter("ceriumBlockMixer"){
  init{
  size = 2
  itemCapacity = 36
  craftEffect = IceEffects.square(IItems.铈凝块.color)
  newConsume().apply {
    power(2f)
    time(90f)
    items(IItems.爆炸化合物, 3, IItems.铈锭, 2)
  }
  newProduce().apply {
    item(IItems.铈凝块, 2)
  }
  requirements(Category.crafting, IItems.铬锭, 80, IItems.铪锭, 60, IItems.铈锭, 50, IItems.单晶硅, 35)
  draw = DrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate").apply {
    rotateSpeed = 3f
  }, DrawDefault(), DrawRegion("-top"))
  bundle {
    desc(zh_CN, "铈凝块混合器", "在特制的防静电车间内,研磨铈并与爆炸混合物混合后压制成型")
  }
}
}