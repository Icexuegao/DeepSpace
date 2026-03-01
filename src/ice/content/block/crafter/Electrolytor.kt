package ice.content.block.crafter

import arc.func.Func
import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.NormalCrafter
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDyColorCultivator
import universecore.world.consumers.ConsumeType

class Electrolytor:NormalCrafter("electrolytor"){
  init{
  bundle {
    desc(zh_CN, "电解机", "内置了几组电极以进行一系列电化学反应,将材料电解为一些有用的东西")
  }
  requirements(
    Category.crafting, IItems.铬锭, 80, IItems.铜锭, 100, IItems.铅锭, 80, IItems.单晶硅, 50, IItems.石英玻璃, 60, IItems.钴钢, 35
  )
  size = 3
  itemCapacity = 25
  liquidCapacity = 40f
  squareSprite = false
  newConsume().apply {
    liquid(Liquids.water, 0.6f)
    power(6f)
  }
  newProduce().apply {
    liquids(Liquids.ozone, 0.6f, ILiquids.氢气, 0.8f)
  }

  newConsume()
  consume!!.liquid(ILiquids.纯净水, 0.4f)
  consume!!.power(5.8f)
  newProduce()
  produce!!.liquids(
    *LiquidStack.with(
      Liquids.ozone, 0.6f, ILiquids.氢气, 0.8f
    )
  )

  newConsume()
  consume!!.time(120f)
  consume!!.liquids(
    *LiquidStack.with(
      ILiquids.复合矿物溶液, 0.4f, ILiquids.碱液, 0.2f
    )
  )
  consume!!.item(IItems.絮凝剂, 2)
  consume!!.power(3.5f)
  newProduce()
  produce!!.items(
    *ItemStack.with(
      IItems.铝锭, 4, IItems.铅锭, 3, IItems.铬锭, 1, IItems.钍锭, 2
    )
  )

  newConsume()
  consume!!.time(60f)
  consume!!.liquid(ILiquids.纯净水, 0.4f)
  consume!!.item(IItems.碱石, 1)
  consume!!.power(3f)
  newProduce()
  produce!!.liquids(
    *LiquidStack.with(
      ILiquids.碱液, 0.4f, ILiquids.氯气, 0.6f
    )
  )

  newConsume()
  consume!!.item(IItems.绿藻块, 1)
  consume!!.liquid(Liquids.water, 0.2f)
  consume!!.time(120f)
  consume!!.power(2.5f)
  newProduce()
  produce!!.item(IItems.绿藻素, 1)

  draw = DrawMulti(
    DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building?) {
        val e = build as NormalCrafterBuild
        if (e.consumer.current == null) return
        val l = e.consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!![0].liquid
        LiquidBlock.drawTiledFrames(size, e.x, e.y, 4f, l, e.liquids.get(l) / liquidCapacity)
      }
    }, object : DrawDyColorCultivator<NormalCrafterBuild>() {
      init {
        spread = 4f
        plantColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
        bottomColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
        plantColorLight = Func { e: NormalCrafterBuild? -> Color.white }
      }
    }, DrawDefault()
  )
}
}