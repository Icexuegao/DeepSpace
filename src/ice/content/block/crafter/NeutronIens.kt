package ice.content.block.crafter

import arc.func.Floatf
import arc.func.Func
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.draw.DrawMulti
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.world.blocks.product.NormalCrafter
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic

class NeutronIens:NormalCrafter("neutron_lens"){
  init{
  bundle {
    desc(zh_CN, "中子透镜", "通过相位物折射及引力透镜偏转中子流进行对焦,使中子直接轰击靶材料,在舱内完成需要高能中子流轰击的过程")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.强化合金, 120, IItems.FEX水晶, 80, IItems.充能FEX水晶, 100, IItems.铱锭, 60, IItems.气凝胶, 120, IItems.絮凝剂, 90
    )
  )
  size = 4
  itemCapacity = 20
  energyCapacity = 1024f
  basicPotentialEnergy = 256f
  squareSprite = false
  warmupSpeed = 0.005f

  newConsume()
  consume!!.time(60f)
  consume!!.item(IItems.铀238, 1)
  consume!!.energy(1.2f)
  newProduce()
  produce!!.item(IItems.钚239, 1)

  newConsume()
  consume!!.time(60f)
  consume!!.item(IItems.相位封装氢单元, 1)
  consume!!.energy(1.5f)
  newProduce()
  produce!!.item(IItems.氢聚变燃料, 1)

  newConsume()
  consume!!.time(60f)
  consume!!.item(IItems.相位封装氦单元, 1)
  consume!!.energy(1.6f)
  newProduce()
  produce!!.item(IItems.氦聚变燃料, 1)

  newConsume()
  consume!!.time(90f)
  consume!!.item(IItems.核废料, 2)
  consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
  consume!!.energy(2.2f)
  newProduce()
  produce!!.items(
    *ItemStack.with(
      IItems.铱金混合物, 1, IItems.强化合金, 1, IItems.钍锭, 1
    )
  )

  draw = DrawMulti(
    DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building) {
        LiquidBlock.drawTiledFrames(
          build.block.size, build.x, build.y, 4f, ILiquids.孢子云, (build as NormalCrafterBuild).consEfficiency()
        )
      }
    }, object : DrawRegionDynamic<NormalCrafterBuild?>("_light") {
      init {
        alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
        color = Func { e: NormalCrafterBuild? -> Tmp.c1.set(Pal.slagOrange).lerp(Pal.accent, Mathf.absin(5f, 1f)) }
      }
    }, object : DrawBlock() {
      override fun draw(build: Building) {

        val e = build as NormalCrafterBuild?
        val angle1 = MathTransform.gradientRotateDeg(build.totalProgress() * 0.8f, 180f, 0.5f, 4)
        val angle2 = MathTransform.gradientRotateDeg(build.totalProgress() * 0.8f, 0f, 0.25f, 4)

        Draw.color(Color.black)
        Fill.square(build.x, build.y, 3 * e!!.consEfficiency(), angle2 + 45)

        SglDraw.drawBloomUnderBlock(e) { b: NormalCrafterBuild? ->
          Lines.stroke(0.75f * b!!.consEfficiency(), SglDrawConst.fexCrystal)
          Lines.square(b.x, b.y, 4 * b.consEfficiency(), angle2 + 45)

          Lines.stroke(0.8f * b.consEfficiency())
          Lines.square(b.x, b.y, 6 * b.consEfficiency(), -angle1 + 45)
          Draw.reset()
        }
        Draw.z(35f)
        Draw.reset()
      }
    }, DrawDefault(), DrawRegion("_top")
  )
}
}