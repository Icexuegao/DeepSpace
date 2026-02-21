package ice.content.block.crafter

import arc.func.Floatf
import arc.func.Func
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.NormalCrafter
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.draw.DrawRegionDynamic

class FuelPackager: NormalCrafter("fuel_packager"){
  init{
  bundle {
    desc(zh_CN, "燃料封装机", "利用力场固定低温技术制造亚绝对零度环境,将核燃料以极高的浓度和压力压缩封装起来")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.强化合金, 45, IItems.絮凝剂, 40, IItems.单晶硅, 45, IItems.钴锭, 30
    )
  )
  size = 2
  autoSelect = true

  newConsume()
  consume!!.time(120f)
  consume!!.items(*ItemStack.with(IItems.铀235, 2, IItems.强化合金, 1))
  consume!!.power(1.5f)
  newProduce()
  produce!!.item(IItems.浓缩铀235核燃料, 1)
  newConsume()
  consume!!.time(120f)
  consume!!.items(*ItemStack.with(IItems.钚239, 2, IItems.强化合金, 1))
  consume!!.power(1.5f)
  newProduce()
  produce!!.item(IItems.浓缩钚239核燃料, 1)

  craftEffect = Fx.smeltsmoke

  draw = DrawMulti(DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>("_flue") {
    init {
      alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.get(IItems.强化合金) > 0 || e.progress() > 0.4f) 1f else 0f }
    }
  }, object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
    // 正确的写法
    init {
      alpha = Floatf<NormalCrafterBuild?> { it?.progress() ?: 0f }
      color = Func { e: NormalCrafterBuild? -> if (e!!.producer!!.current != null) e.producer!!.current!!.color else SglDrawConst.transColor }
    }
  })
}
}