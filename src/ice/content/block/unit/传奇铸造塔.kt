package ice.content.block.unit

import arc.func.Cons2
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.util.Time
import ice.content.IItems
import ice.content.ILiquids
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.UnitType
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.HoveringUnitFactory
import universecore.math.MathTransform
import universecore.world.consumers.BaseConsumers
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import kotlin.times

class 传奇铸造塔: HoveringUnitFactory("cstr_2"){
  init  {
    localization {
      zh_CN {
        localizedName = "传奇铸造塔"
        description = "大型集成单位建造塔,可以直接进行大型单位的建造,完成建造的目标可设置释放位置,它可以以很快的速度建造小型单位"
      }
    }
    requirements(
      Category.units,
      Items.silicon,
      180,
      Items.surgeAlloy,
      160,
      Items.phaseFabric,
      190,
      IItems.铝锭,
      200,
      IItems.气凝胶,
      120,
      IItems.强化合金,
      215,
      IItems.矩阵合金,
      180,
      IItems.FEX水晶,
      140,
      IItems.铱锭,
      100
    )
    size = 7
    liquidCapacity = 280f
    energyCapacity = 1024f
    basicPotentialEnergy = 1024f
    squareSprite = false
    payloadSpeed = 1f

    consCustom = Cons2 { u: UnitType?, c: BaseConsumers? ->
      c!!.power(Mathf.round(u!!.health / u.hitSize) * 0.02f).showIcon = true
      if (u.hitSize >= 38) c.energy(u.hitSize / 24)
    }

    matrixDistributeOnly = true

    sizeLimit = 68f
    healthLimit = 43000f
    machineLevel = 6
    timeMultiplier = 18f
    baseTimeScl = 0.25f

    outputRange = 340f

    hoverMoveMinRadius = 36f
    hoverMoveMaxRadius = 72f
    defHoverRadius = 23.5f

    laserOffY = 2f

    newBooster(1.6f)
    consume!!.liquid(ILiquids.急冻液, 3.2f)
    newBooster(1.9f)
    consume!!.liquid(ILiquids.FEX流体, 2.6f)
    newBooster(2.4f)
    consume!!.liquid(ILiquids.相位态FEX流体, 2.6f)

    drawers = DrawMulti(
      DrawDefault(), object :DrawBlock() {
        override fun draw(build: Building?) {
          val b = build as HoveringUnitFactory.HoveringUnitFactoryBuild
          Draw.z(Layer.effect)
          Draw.color(b.team.color)

          Lines.stroke(2 * b.warmup())

          Lines.circle(b.x, b.y, 12 * b.warmup())
          Lines.square(b.x, b.y, (size * Vars.tilesize).toFloat(), Time.time * 1.25f)
          Lines.square(b.x, b.y, 32f, Time.time * 3.25f)
          var p: ProducePayload<*>?

          b.producer?.current?.let {
            p = it.get(ProduceType.payload)
            if (p != null && p.payloads[0].item is UnitType) {
              SglDraw.arc(b.x, b.y, (p.payloads[0].item as UnitType).hitSize + 8, 360 * b.progress(), -Time.time * 0.8f)
            }
          }


          Draw.color(Pal.reactorPurple)
          Lines.square(b.x, b.y, 28f, -MathTransform.gradientRotateDeg(Time.time, 0f) + 45f)

          for(i in 0..3) {
            for(j in 0..2) {
              val lerp = b.warmup() * (1 - Mathf.clamp((Time.time + 30 * j) % 90 / 70))
              SglDraw.drawTransform(b.x, b.y, (40 + j * 18).toFloat(), 0f, (i * 90 + 45).toFloat()) { rx: Float, ry: Float, r: Float ->
                Draw.rect((SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), rx, ry, 15 * lerp, 15 * lerp, r + 90)
              }
            }
          }
        }
      })
  }
}