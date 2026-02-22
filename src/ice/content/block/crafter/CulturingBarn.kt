package ice.content.block.crafter

import arc.Core
import arc.func.*
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Mathf
import arc.math.Rand
import arc.util.Time
import ice.content.IItems
import ice.content.ILiquids
import ice.library.struct.AttachedProperty
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Liquids
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.ui.Bar
import mindustry.world.draw.DrawBlock
import mindustry.world.meta.Attribute
import singularity.graphic.SglDraw
import singularity.graphic.SglShaders
import singularity.world.blocks.product.SpliceCrafter
import singularity.world.draw.DrawAntiSpliceBlock
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic
import kotlin.math.max

class CulturingBarn : SpliceCrafter("culturing_barn") {
  var SpliceCrafterBuild.highlight: Boolean by AttachedProperty(false)

  init {
    bundle {
      desc(zh_CN, "绿藻池", "使用光水培养低等的藻类生物,除氧气外,还能收获不少藻泥")
    }
    requirements(Category.production, IItems.铜锭, 10, IItems.石英玻璃, 12, IItems.钴锭, 8)
    hasLiquids = true
    negativeSplice = true
    allowRectanglePlacement = true
    buildType = Prov(::CulturingBarnBuild)

    newConsume().apply {
      liquid(Liquids.water, 0.02f)
    }
    newProduce().apply {
      liquids(Liquids.ozone, 0.01f, ILiquids.藻泥, 0.006f)
    }

    structUpdated = Cons { e: SpliceCrafterBuild ->
      val right = e.nearby(0)
      val top = e.nearby(1)
      val left = e.nearby(2)
      val bottom = e.nearby(3)
      e.highlight = (right !is SpliceCrafterBuild || right.chains.container !== e.chains.container) && (top !is SpliceCrafterBuild || top.chains.container !== e.chains.container) && (left is SpliceCrafterBuild && left.chains.container === e.chains.container) && (bottom is SpliceCrafterBuild && bottom.chains.container === e.chains.container)

    }
    draw = DrawMulti(DrawBottom(), object : DrawBlock() {
      val rand: Rand = Rand()
      val drawID: Int = SglDraw.nextTaskID()

      override fun draw(build: Building) {
        Draw.z(Draw.z() + 0.001f)
        val cap = build.block.liquidCapacity
        val drawCell = Cons { b: Building? ->

          val alp = max(b!!.warmup(), 0.7f * b.liquids.get(ILiquids.藻泥) / cap)
          if (alp <= 0.01f) return@Cons

          rand.setSeed(b.id.toLong())
          val am = (1 + rand.random(3) * b.warmup()).toInt()
          val move = 0.2f * Mathf.sinDeg(Time.time + rand.random(360f)) * b.warmup()
          Draw.color(ILiquids.藻泥.color)
          Draw.alpha(alp)
          Angles.randLenVectors(b.id.toLong(), am, 3.5f) { dx: Float, dy: Float ->
            Fill.circle(
              b.x + dx + move, b.y + dy + move, (rand.random(0.2f, 0.8f) + Mathf.absin(5f, 0.1f)) * max(b.warmup(), b.liquids.get(ILiquids.藻泥) / cap)
            )
          }
          Draw.reset()
        }

        SglDraw.drawTask(drawID, build, SglShaders.boundWater) { e: Building ->
          drawCell.get(e)
          Draw.alpha(0.75f * (e.liquids.get(Liquids.water) / cap))
          Draw.rect(Blocks.water.region, e.x, e.y)
          Draw.flush()
        }
      }
    }, object : DrawAntiSpliceBlock<SpliceCrafterBuild>() {
      init {
        planSplicer = Boolf2 { plan: BuildPlan, other: BuildPlan ->
          if (plan.block is SpliceCrafter && other.block is SpliceCrafter) {
            val block = plan.block as SpliceCrafter
            val otherBlock = other.block as SpliceCrafter
            return@Boolf2 block.chainable(otherBlock) && otherBlock.chainable(block)
          } else return@Boolf2 false

        }
        splicer = Intf { it.splice }
        layerRec = false
      }
    }, object : DrawRegionDynamic<SpliceCrafterBuild>("_highlight") {
      init {
        alpha = Floatf { e: SpliceCrafterBuild -> if (e.highlight) 1f else 0f }
      }
    })
  }

  override fun setBars() {
    super.setBars()
    addBar("efficiency") { entity: SglBuilding? ->
      Bar({ Core.bundle.format("bar.efficiency", (entity!!.efficiency() * 100).toInt()) }, { Pal.lightOrange }, { entity!!.efficiency() })
    }
  }

  inner class CulturingBarnBuild : SpliceCrafterBuild() {
    var efficiencys: Float = 0f

    override fun efficiency(): Float {
      return super.efficiency() * efficiencys
    }

    override fun updateTile() {
      super.updateTile()

      efficiencys = if (enabled) Mathf.maxZero(
        Attribute.light.env() + (if (Vars.state.rules.lighting) 1f - Vars.state.rules.ambientLight.a else 1f)
      ) else 0f
    }
  }
}