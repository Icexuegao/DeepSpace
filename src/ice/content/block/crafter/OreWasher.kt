package ice.content.block.crafter

import arc.func.Floatf
import arc.func.Func
import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.consumers.SglConsumers
import singularity.world.draw.DrawRegionDynamic

class OreWasher:NormalCrafter("ore_washer"){
  init {
  bundle {
    desc(zh_CN, "洗矿机", "用高速的水流冲刷沥青粗矿以除去轻杂质,以及洗脱附着在岩石间的FEX物质")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.铬锭, 60, IItems.钴锭, 40, IItems.铅锭, 45, IItems.石英玻璃, 60
    )
  )
  size = 2
  hasLiquids = true
  itemCapacity = 20
  liquidCapacity = 24f


  newConsume()
  consume!!.time(120f)
  consume!!.liquid(Liquids.water, 0.2f)
  consume!!.item(IItems.岩层沥青, 1)
  consume!!.power(1.8f)
  newProduce()
  produce!!.liquid(ILiquids.FEX流体, 0.2f)
  produce!!.items(
    *ItemStack.with(
      IItems.金珀沙, 5, IItems.黑晶石, 3, IItems.铀原矿, 2
    )
  ).random()

  craftEffect = Fx.pulverizeMedium

  draw = DrawMulti(DrawDefault(), object : DrawLiquidRegion(Liquids.water) {
    init {
      suffix = "_liquid"
    }
  }, object : DrawRegion("_rotator") {
    init {
      rotateSpeed = 4.5f
      spinSprite = true
    }
  }, DrawRegion("_top"), object : DrawRegionDynamic<NormalCrafterBuild?>("_point") {
    init {
      color = Func { e: NormalCrafterBuild? ->
        val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as SglConsumers).first()
        if (cons is universecore.world.consumers.ConsumeItems<*>) {
          val item = cons.consItems!![0].item
          return@Func item.color
        } else return@Func Color.white
      }
      alpha = Floatf { e: NormalCrafterBuild? ->
        val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as SglConsumers).first()
        if (cons is universecore.world.consumers.ConsumeItems<*>) {
          val item = cons.consItems!![0].item
          return@Floatf e.items.get(item).toFloat() / e.block.itemCapacity
        } else return@Floatf 0f
      }
    }
  })
}
}