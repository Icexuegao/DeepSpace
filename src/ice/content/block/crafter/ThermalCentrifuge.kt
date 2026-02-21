package ice.content.block.crafter

import arc.func.Floatf
import arc.func.Func
import arc.util.Tmp
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.NormalCrafter
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic
import universecore.world.consumers.ConsumeType

class ThermalCentrifuge:NormalCrafter("thermal_centrifuge"){
  init{
  bundle {
    desc(zh_CN, "热能离心机", "以极高的温度将物质熔化成液态,以差速离心分离其中不同质量的物质")
  }
  requirements(
    Category.crafting, IItems.强化合金, 100, IItems.气凝胶, 80, IItems.铜锭, 120, IItems.单晶硅, 70, IItems.钴钢, 75
  )
  size = 3
  itemCapacity = 28

  warmupSpeed = 0.006f

  newConsume()
  consume!!.time(120f)
  consume!!.item(IItems.铀原料, 4)
  consume!!.power(3.8f)
  newProduce().color = IItems.铀原料.color
  produce!!.items(*ItemStack.with(IItems.铀238, 3, IItems.铀235, 1))

  newConsume()
  consume!!.time(180f)
  consume!!.item(IItems.铱金混合物, 2)
  consume!!.power(3f)
  newProduce().color = IItems.氯铱酸盐.color
  produce!!.item(IItems.铱锭, 1)

  newConsume()
  consume!!.time(120f)
  consume!!.item(IItems.黑晶石, 5)
  consume!!.power(2.8f)
  setByProduct(0.3f, IItems.钍锭)
  newProduce().color = IItems.黑晶石.color
  produce!!.items(
    *ItemStack.with(
      IItems.铝锭, 3, IItems.铅锭, 2
    )
  )

  craftEffect = Fx.smeltsmoke
  updateEffect = Fx.plasticburn

  draw = DrawMulti(DrawBottom(), object : DrawBlock() {
    override fun draw(build: Building) {
      val e = build as NormalCrafterBuild
      if (e.producer!!.current == null) return

      val region = Vars.renderer.fluidFrames[0][Liquids.slag.animationFrame]
      val toDraw = Tmp.tr1
      val bounds = size / 2f * Vars.tilesize
      val color = Liquids.slag.color

      for (sx in 0..<size) {
        for (sy in 0..<size) {
          val relx = sx - (size - 1) / 2f
          val rely = sy - (size - 1) / 2f

          toDraw.set(region)
          val rightBorder = relx * Vars.tilesize
          val topBorder = rely * Vars.tilesize
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
  }, DrawRegion("_rim", 0.8f, true), DrawDefault(), DrawRegion("_rotator", 1.8f, true), DrawRegion("_toprotator", -1.2f, true), object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
    init {
      rotation = Floatf { e: NormalCrafterBuild? -> -e!!.totalProgress() * 1.2f }
      color = Func { e: NormalCrafterBuild? -> if (e!!.producer!!.current != null) e.producer!!.current!!.color else SglDrawConst.transColor }
      alpha = Floatf { e: NormalCrafterBuild? ->
        val cons = if (e!!.consumer.current == null) null else e.consumer.current!!.get(ConsumeType.item)
        val i = if (cons == null) null else cons.consItems!![0].item
        if (cons == null) 0f else (e.items.get(i).toFloat()) / itemCapacity
      }
    }
  })
}
}