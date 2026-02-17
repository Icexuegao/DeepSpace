package ice.content.block.crafter

import arc.func.Floatf
import arc.graphics.g2d.Draw
import arc.util.Tmp
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.graphic.SglDraw
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic

class LaserResolver : NormalCrafter("laser_resolver") {init {
  bundle {
    desc(zh_CN, "激光解离机", "使用不同频段的激光来定向分离物质以得到更加有用的东西")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.FEX水晶, 45, IItems.强化合金, 70, IItems.单晶硅, 90, IItems.絮凝剂, 65, IItems.石英玻璃, 120
    )
  )
  size = 3
  itemCapacity = 20
  warmupSpeed = 0.01f


  newConsume()
  consume!!.time(60f)
  consume!!.power(3.2f)
  consume!!.item(IItems.核废料, 1)
  newProduce().color = IItems.核废料.color
  produce!!.items(
    *ItemStack.with(
      IItems.铱金混合物, 2, IItems.铅锭, 5, IItems.钍锭, 3
    )
  ).random()

  newConsume()
  consume!!.time(30f)
  consume!!.item(Items.scrap, 2)
  consume!!.liquid(Liquids.slag, 0.1f)
  consume!!.power(3.5f)
  newProduce().color = Items.scrap.color
  produce!!.items(
    *ItemStack.with(
      IItems.钍锭, 3, IItems.铬锭, 4, IItems.铅锭, 5, IItems.铜锭, 3
    )
  ).random()

  newConsume()
  consume!!.time(60f)
  consume!!.item(IItems.黑晶石, 1)
  consume!!.power(2.8f)
  newProduce().color = IItems.黑晶石.color
  produce!!.items(
    *ItemStack.with(
      IItems.铬锭, 2, IItems.钍锭, 1, IItems.铅锭, 3, IItems.铝锭, 4
    )
  ).random()


  draw = DrawMulti(
    DrawBottom(), object : DrawBlock() {
    override fun draw(build: Building) {
      val e = build as NormalCrafterBuild
      if (e.producer!!.current == null) return

      val region = Vars.renderer.fluidFrames[0][Liquids.water.animationFrame]
      val toDraw = Tmp.tr1

      val bounds = size / 2f * Vars.tilesize - 3
      val color = e.producer!!.current!!.color

      for (sx in 0..<size) {
        for (sy in 0..<size) {
          val relx = sx - (size - 1) / 2f
          val rely = sy - (size - 1) / 2f

          toDraw.set(region)
          val rightBorder = relx * Vars.tilesize + 3
          val topBorder = rely * Vars.tilesize + 3
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
  }, DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild>("_laser") {
    init {
      rotation = Floatf { e: NormalCrafterBuild? -> e!!.totalProgress * 1.5f }
      alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
    }

    override fun draw(build: Building?) {
      SglDraw.drawBloomUnderBlock(build) { build: Building -> super.draw(build) }
      Draw.z(Layer.block + 5)
    }
  }, object : DrawRegion("_rotator") {
    init {
      rotateSpeed = 1.5f
      spinSprite = true
    }
  }, DrawRegion("_top")
  )
}
}