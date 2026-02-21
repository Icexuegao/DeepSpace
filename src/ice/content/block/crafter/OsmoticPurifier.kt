package ice.content.block.crafter

import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class OsmoticPurifier : NormalCrafter("osmotic_purifier") {
  init {
    bundle {
      desc(zh_CN, "渗透净化器", "使用物质吸附及反渗透过滤技术制造的高效净化装置,能更有效的分离水中的杂质")
    }
    size = 3
    hasLiquids = true
    liquidCapacity = 30f
    squareSprite = false
    requirements(Category.crafting, IItems.铝锭, 50, IItems.钴锭, 60, IItems.单晶硅, 45, IItems.铬锭, 45, IItems.气凝胶, 50)
    newConsume().apply {
      time(60f)
      liquid(Liquids.water, 2f)
      item(IItems.钴锭, 1)
      power(1f)
    }
    newProduce().apply {
      liquid(ILiquids.纯净水, 2f)
      item(IItems.碱石, 2)
    }


    draw = DrawMulti(
      DrawBottom(), DrawLiquidTile(Liquids.water, 3f), object : DrawBlock() {
        override fun draw(build: Building?) {
          val e = build as NormalCrafterBuild
          val region = Vars.renderer.fluidFrames[0][Liquids.water.animationFrame]
          val toDraw = Tmp.tr1
          val bounds = size / 2f * Vars.tilesize - 8
          val color = ILiquids.纯净水.color

          for (sx in 0..<size) {
            for (sy in 0..<size) {
              val relx = sx - (size - 1) / 2f
              val rely = sy - (size - 1) / 2f

              toDraw.set(region)
              val rightBorder = relx * Vars.tilesize + 8
              val topBorder = rely * Vars.tilesize + 8
              val squishX = rightBorder + Vars.tilesize / 2f - bounds
              val squishY = topBorder + Vars.tilesize / 2f - bounds
              var ox = 0f
              var oy = 0f

              if (squishX >= 8 || squishY >= 8) continue

              if (squishX > 0) {
                toDraw.setWidth(toDraw.width - squishX * 4f)
                ox = -squishX / 2f
              }

              if (squishY > 0) {
                toDraw.setY(toDraw.y + squishY * 4f)
                oy = -squishY / 2f
              }

              Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, e.warmup(), color)
            }
          }
        }
      }, DrawDefault()
    )
  }
}