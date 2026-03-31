package ice.content.block.crafter

import arc.func.Func
import arc.graphics.Color
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.bundle
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDyColorCultivator
import universecore.world.consumers.ConsumeType
import kotlin.math.max

class ReactingPool : NormalCrafter("reacting_pool") {
  init {
    bundle {
      desc(zh_CN, "反应仓","将多种材料置入反应并生产特定产物,可配置" ,"一个精准控制进料的化学反应容器,是普遍使用的化工设备")
    }
    requirements(
      Category.crafting, IItems.铬锭, 100, IItems.石英玻璃, 100, IItems.铅锭, 80, IItems.钴锭, 85, IItems.单晶硅, 80, IItems.钴钢, 70
    )
    size = 3
    squareSprite = false
    itemCapacity = 35
    liquidCapacity = 45f

    newFormula {consumers, producers ->
      consumers.apply {
        time(60f)
        item(IItems.黑晶石, 3)
        liquid(ILiquids.酸液, 0.2f)
        power(0.8f)
      }
      producers.apply {
        liquid(ILiquids.复合矿物溶液, 0.4f)
      }
    }


    newConsume().apply {
      time(60f)
      liquid(ILiquids.酸液, 0.2f)
      item(IItems.铀原矿, 1)
      power(0.8f)
    }
    newProduce().apply {
      liquids(ILiquids.铀盐溶液, 0.2f, ILiquids.氦气,48f/60f)
    }

    newConsume().apply {
      liquids(ILiquids.纯净水, 0.4f, ILiquids.二氧化硫, 0.4f, ILiquids.氯气, 0.2f)
      power(0.6f)
    }
    newProduce().apply {
      liquid(ILiquids.酸液, 0.6f)
    }

    newConsume().apply {
      time(120f)
      items(IItems.单晶硅, 2, IItems.絮凝剂, 1)
      liquids(ILiquids.纯净水, 0.4f, ILiquids.氯气, 0.2f)
      power(1.5f)
    }
    newProduce().apply {
      liquids(ILiquids.氯化硅溶胶, 0.4f, ILiquids.酸液, 0.2f)
    }

    newConsume().apply {
      time(60f)
      item(IItems.铝锭, 1)
      liquid(ILiquids.碱液, 0.2f)
      power(1f)
    }
    newProduce().apply {
      item(IItems.絮凝剂, 2)
    }

    newConsume().apply {
      time(30f)
      items(IItems.生煤, 1, IItems.燃素水晶, 1)
      liquids(ILiquids.酸液, 0.2f)
      power(1f)
    }
    newProduce().apply {
      item(IItems.爆炸化合物, 1)
    }

    newConsume().apply {
      time(60f)
      items(IItems.生煤, 2, IItems.单晶硅, 1)
      liquids(ILiquids.氢气, 0.2f)
      power(1f)
    }
    newProduce().apply {
      item(IItems.石墨烯, 1)
    }


    drawers = DrawMulti(
      DrawBottom(), object : DrawBlock() {
        override fun draw(build: Building?) {
          val e = build as NormalCrafterBuild
          if (e.consumer.current == null || e.producer!!.current == null) return
          val l = e.consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!![0].liquid
          val region = Vars.renderer.fluidFrames[if (l.gas) 1 else 0][l.animationFrame]
          val toDraw = Tmp.tr1
          val bounds = size / 2f * Vars.tilesize - 3
          val color = Tmp.c1.set(l.color).a(1f).lerp(e.producer!!.current!!.color, e.warmup())

          for (sx in 0..<size) {
            for (sy in 0..<size) {
              val relx = sx - (size - 1) / 2f
              val rely = sy - (size - 1) / 2f

              toDraw.set(region)
              //truncate region if at border
              val rightBorder = relx * Vars.tilesize + 3
              val topBorder = rely * Vars.tilesize + 3
              val squishX = rightBorder + Vars.tilesize / 2f - bounds
              val squishY = topBorder + Vars.tilesize / 2f - bounds
              var ox = 0f
              var oy = 0f

              if (squishX >= 8 || squishY >= 8) continue
              //cut out the parts that don't fit inside the padding
              if (squishX > 0) {
                toDraw.setWidth(toDraw.width - squishX * 4f)
                ox = -squishX / 2f
              }

              if (squishY > 0) {
                toDraw.setY(toDraw.y + squishY * 4f)
                oy = -squishY / 2f
              }

              Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, max(e.warmup(), e.liquids.get(l) / liquidCapacity), color)
            }
          }
        }
      }, object : DrawDyColorCultivator<NormalCrafterBuild>() {
        init {
          spread = 4f
          plantColor = Func {e: NormalCrafterBuild? -> SglDrawConst.transColor}
          bottomColor = Func {e: NormalCrafterBuild? -> SglDrawConst.transColor}
          plantColorLight = Func {e: NormalCrafterBuild? -> Color.white}
        }
      }, DrawDefault()
    )
  }
}