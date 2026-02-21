package ice.content.block.crafter

import arc.func.Floatf
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic

class MatrixCutter:NormalCrafter("matrix_cutter"){
  init{
  bundle {
    desc(zh_CN, "矩阵切割器", "以纳米尺度的高能激光将金属切割为纳米颗粒,并在上方雕刻微电路,以生产矩阵合金")
  }
  requirements(
    Category.crafting, IItems.强化合金, 80, IItems.充能FEX水晶, 75, IItems.石英玻璃, 80, IItems.絮凝剂, 90, IItems.暮光合金, 120
  )
  size = 4

  itemCapacity = 20
  basicPotentialEnergy = 256f



  newConsume()
  consume!!.time(120f)
  consume!!.energy(4.85f)
  consume!!.items(
    *ItemStack.with(
      IItems.充能FEX水晶, 1, IItems.强化合金, 2
    )
  )
  consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
  newProduce()
  produce!!.item(IItems.矩阵合金, 1)

  craftEffect = Fx.smeltsmoke

  draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_alloy") {
    init {
      alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.get(IItems.强化合金) >= 2) 1f else 0f }
    }
  }, object : DrawBlock() {
    override fun draw(build: Building) {

      val e = build as NormalCrafterBuild?
      SglDraw.drawBloomUnderBlock<NormalCrafterBuild?>(e) { b: NormalCrafterBuild? ->
        val dx = 5 * Mathf.sinDeg(build.totalProgress() * 1.35f)
        val dy = 5 * Mathf.cosDeg(build.totalProgress() * 1.35f)

        Draw.color(SglDrawConst.fexCrystal)
        Lines.stroke(0.8f * e!!.workEfficiency())

        Lines.line(b!!.x + dx, b.y + 6, b.x + dx, b.y - 6)
        Lines.line(b.x + 6, b.y + dy, b.x - 6, b.y + dy)
      }
      Draw.z(35f)
      Draw.reset()
    }
  }, DrawDefault(), object : DrawBlock() {
    override fun draw(build: Building?) {

      Draw.z(Layer.effect)
      val e = build as NormalCrafterBuild
      val angle = e.totalProgress()
      val realRotA = MathTransform.gradientRotateDeg(angle, 0f, 4)
      val realRotB = MathTransform.gradientRotateDeg(angle, 180f, 4)

      Lines.stroke(1.4f * e.workEfficiency(), SglDrawConst.fexCrystal)
      Lines.square(e.x, e.y, 20 + 4 * Mathf.sinDeg(realRotB), 45 + realRotA)

      Lines.stroke(1.6f * e.workEfficiency())
      Lines.square(e.x, e.y, 20 + 4 * Mathf.sinDeg(realRotA), 45 - realRotB)
    }
  })
}
}