package ice.content.block.product

import arc.func.Boolf2
import arc.func.Intf
import arc.graphics.g2d.Draw
import ice.content.IItems
import ice.content.block.ProductBlocks
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.graphic.SglDraw
import singularity.world.SglFx
import singularity.world.blocks.drills.ExtendMiner
import singularity.world.blocks.drills.ExtendableDrill
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDirSpliceBlock

class 引力延展室 :ExtendMiner("force_field_extender") {
  init {
    localization {
      zh_CN {
        localizedName = "引力延展室"
        description = "用于延伸潮汐钻头的设备,贴近潮汐钻井,并与其他延展室彼此正对连接可扩大钻头覆盖的范围"
      }
    }
    requirements(
      Category.production, ItemStack.with(
        IItems.简并态中子聚合物, 20, IItems.FEX水晶, 20, IItems.铱锭, 8, IItems.强化合金, 30
      )
    )
    size = 2
    squareSprite = false
    master = ProductBlocks.潮汐钻头 as ExtendableDrill?
    mining = SglFx.shrinkParticle(10f, 1.5f, 120f, Pal.reactorPurple)

    drawers = DrawMulti(DrawBottom(), DrawDefault(), object :DrawDirSpliceBlock<ExtendMinerBuild?>() {
      init {
        simpleSpliceRegion = true

        spliceBits = Intf { e: ExtendMinerBuild? -> e!!.spliceDirBits }

        planSplicer = Boolf2 { plan: BuildPlan?, other: BuildPlan? ->
          plan!!.block is ExtendMiner && other!!.block is ExtendMiner && (other.block as ExtendMiner).chainable((plan.block as ExtendMiner)) && (plan.block as ExtendMiner).chainable(
            (other.block as ExtendMiner)
          )
        }

        layerRec = false
      }
    }, object :DrawBlock() {
      override fun draw(build: Building?) {
        val e = build as ExtendMinerBuild

        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        SglDraw.drawLightEdge(e.x, e.y, 8 * e.warmup, 2f * e.warmup, 8 * e.warmup, 2f * e.warmup, 45f)
        SglDraw.drawLightEdge(e.x, e.y, 15 * e.warmup, 2f * e.warmup, 45f, 0.6f, 15 * e.warmup, 2f * e.warmup, 45f, 0.6f)
      }
    })
  }
}