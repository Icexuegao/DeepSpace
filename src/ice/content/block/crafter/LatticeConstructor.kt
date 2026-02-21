package ice.content.block.crafter

import arc.Core
import arc.func.Cons
import arc.func.Floatf
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import singularity.Singularity
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.SglBlock
import singularity.world.blocks.product.NormalCrafter
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic
import universecore.components.blockcomp.FactoryBuildComp

class LatticeConstructor:NormalCrafter("lattice_constructor"){
  init{
  bundle {
    desc(zh_CN, "晶格构建器", "先进的FEX结晶技术,以光束引导和力场聚合的方式人工构建晶格结构,更高效地生产FEX结晶")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.强化合金, 80, IItems.充能FEX水晶, 60, IItems.FEX水晶, 75, IItems.絮凝剂, 80
    )
  )
  size = 3

  itemCapacity = 20
  basicPotentialEnergy = 128f

  newConsume()
  consume!!.time(120f)
  consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
  consume!!.item(IItems.强化合金, 1)
  consume!!.energy(1.25f)
  newProduce()
  produce!!.item(IItems.FEX水晶, 4)

  craftEffect = SglFx.FEXsmoke



  initialed = Cons { e: SglBlock.SglBuilding ->
    e.drawAlphas = floatArrayOf(2.9f, 2.2f, 1.5f)
  }
  draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_framework") {
    init {
      alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.强化合金) || e.progress() > 0.4f) 1f else 0f }
    }
  }, object : DrawRegionDynamic<NormalCrafterBuild?>() {
    init {
      alpha = Floatf { e: FactoryBuildComp -> e.progress }
    }

    override fun load(block: Block?) {
      region = Singularity.getModAtlas("FEX_crystal")
    }
  }, object : DrawBlock() {
    override fun draw(build: Building?) {
      val e = build as NormalCrafterBuild
      Draw.alpha(e.workEfficiency())
      Lines.lineAngleCenter(
        e.x + Mathf.sin(e.totalProgress(), 6f, Vars.tilesize.toFloat() / 3 * size), e.y, 90f, size.toFloat() * Vars.tilesize / 2
      )
      Draw.color()
    }
  }, DrawDefault(), object : DrawBlock() {
    var wave: TextureRegion? = null

    override fun load(block: Block?) {
      wave = Core.atlas.find(name + "_wave")
    }

    override fun draw(build: Building?) {

      val e = build as NormalCrafterBuild
      val alphas: FloatArray = e.drawAlphas

      Draw.z(Layer.effect)
      for (dist in 2 downTo 0) {
        Draw.color(SglDrawConst.fexCrystal)
        Draw.alpha((if (alphas[dist] <= 1) alphas[dist] else (if (alphas[dist] <= 1.5) 1 else 0).toFloat()) * e.workEfficiency())
        if (e.workEfficiency() > 0) {
          if (alphas[dist] < 0.4) alphas[dist] += 0.6.toFloat()
          for (i in 0..3) {
            Draw.rect(
              wave, e.x + dist * Geometry.d4(i).x * 3 + 5 * (Geometry.d4(i).x.compareTo(0)), e.y + dist * Geometry.d4(i).y * 3 + 5 * (Geometry.d4(i).y.compareTo(0)), ((i + 1) * 90).toFloat()
            )
          }
          alphas[dist] -= (0.02 * e.edelta()).toFloat()
        } else {
          alphas[dist] = 1.5f + 0.7f * (2 - dist)
        }
      }
    }
  })
}
}