package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import universecore.world.consumers.ConsumeType

class OsmoticSeparationTank : NormalCrafter("osmotic_separation_tank") {
  init {
    bundle {
      desc(zh_CN, "渗透分离槽","使材料渗透以分离出需求产物,可配置" "内置加压可控粒径反渗透过滤器,用于进行一些需要分离颗粒的反应工艺")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 60, IItems.铅锭, 90, IItems.钴锭, 100, IItems.石英玻璃, 80, IItems.单晶硅, 70
      )
    )
    size = 3
    squareSprite = false
    itemCapacity = 20
    liquidCapacity = 40f

    newConsume()
    consume!!.time(60f)
    consume!!.liquids(ILiquids.碱液, 0.2f, ILiquids.铀盐溶液, 0.2f, ILiquids.氧气, 0.2f)
    consume!!.item(IItems.絮凝剂, 1)
    consume!!.power(1.2f)
    newProduce()
    produce!!.item(IItems.铀原料, 2)

    newConsume()
    consume!!.time(120f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.酸液, 0.2f, ILiquids.氧气, 0.4f
      )
    )
    consume!!.item(IItems.铱金混合物, 1)
    consume!!.power(1.2f)
    newProduce()
    produce!!.item(IItems.氯铱酸盐, 1)

    newConsume()
    consume!!.time(90f)
    consume!!.liquid(ILiquids.藻泥, 0.4f)
    consume!!.power(1f)
    newProduce()
    produce!!.item(IItems.绿藻块, 1)
    produce!!.liquid(ILiquids.纯净水, 0.2f)

    newConsume().apply {
      time(30f)
      power(1f)
      item(IItems.低温化合物,1)
      liquid(ILiquids.氢气,6f/60f)
    }

    newProduce().apply {
      liquid(ILiquids.急冻液, 20f/60f)
    }


    newConsume().apply {
      time(60f)
      power(1.2f)
      item(IItems.低温化合物,1)
      liquid(ILiquids.碱液,32f/60f)
    }
    newProduce().apply {
      liquid(ILiquids.暮光液, 32f/60f)
    }

    draw = DrawMulti(
      DrawBottom(), object : DrawBlock() {
        override fun draw(build: Building?) {
          val e = build as NormalCrafterBuild
          if (e.consumer.current == null) return
          val l = e.consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!![0].liquid
          LiquidBlock.drawTiledFrames(size, e.x, e.y, 4f, l, e.liquids.get(l) / liquidCapacity)
        }
      }, DrawDefault()
    )
  }
}