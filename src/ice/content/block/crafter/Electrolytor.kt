package ice.content.block.crafter

import arc.func.Func
import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawDefault
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDyColorCultivator
import universecore.world.consumers.ConsumeType

class Electrolytor :NormalCrafter("electrolytor") {
  init {
    localization {
      zh_CN {
        localizedName = "电解机"
        description = "将材料电解以分离出需求产物,可配置"
        details = "内置了几组电极以进行一系列电化学反应"
      }
    }
    size = 3
    itemCapacity = 25
    liquidCapacity = 120f
    squareSprite = false
    requirements(
      Category.crafting, IItems.铬锭, 80, IItems.铜锭, 100, IItems.铅锭, 80, IItems.单晶硅, 50, IItems.石英玻璃, 60, IItems.钴钢, 35
    )

    drawers = DrawMulti(
      DrawBottom(), DrawBuild<NormalCrafterBuild> {
        if (consumer.current == null) return@DrawBuild
        val l = consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!![0].liquid
        LiquidBlock.drawTiledFrames(size, x, y, 4f, l, liquids.get(l) / liquidCapacity)
      }, object :DrawDyColorCultivator<NormalCrafterBuild>() {
        init {
          spread = 4f
          plantColor = Func { _ -> SglDrawConst.transColor }
          bottomColor = Func { _ -> SglDrawConst.transColor }
          plantColorLight = Func { _ -> Color.white }
        }
      }, DrawDefault()
    )

    newFormula { consumers, producers ->
      consumers.apply {
        liquid(Liquids.water, 0.6f)
        power(6f)
      }
      producers.apply {
        liquids(ILiquids.氧气, 0.6f, ILiquids.氢气, 0.8f)
      }
    }
    newFormula { consumers, producers ->
      consumers.apply {
        liquid(ILiquids.纯净水, 0.4f)
        power(5.8f)
      }
      producers.apply {
        liquids(ILiquids.氧气, 0.6f, ILiquids.氢气, 0.8f)
      }
    }
    newFormula { consumers, producers ->
      consumers.apply {
        time(120f)
        liquids(ILiquids.复合矿物溶液, 0.4f, ILiquids.碱液, 0.2f)
        item(IItems.絮凝剂, 2)
        power(3.5f)
      }
      producers.apply {
        items(IItems.铝锭, 4, IItems.铅锭, 3, IItems.铬锭, 1, IItems.钍锭, 2)
      }
    }
    newFormula { consumers, producers ->
      consumers.apply {
        time(60f)
        liquid(ILiquids.纯净水, 0.4f)
        item(IItems.碱石, 1)
        power(3f)
      }
      producers.apply {
        liquids(ILiquids.碱液, 0.4f, ILiquids.氯气, 0.6f)
      }
    }
    newFormula { consumers, producers ->
      consumers.apply {
        item(IItems.绿藻块, 1)
        liquid(Liquids.water, 0.2f)
        time(120f)
        power(2.5f)
      }
      producers.apply {
        item(IItems.绿藻素, 1)
      }
    }


  }
}
