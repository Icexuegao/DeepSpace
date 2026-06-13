package ice.content.block.unit

import arc.func.Cons2
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
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

class 神话影射仪:HoveringUnitFactory("cstr_3"){
  init  {
    localization {
      zh_CN {
        localizedName = "神话影射仪"
        description = "大超大型单位建造工厂,可以进行几乎所有单位的建造任务,并且可以以极快的速度建造中小型单位"
        details =
          "“无论从什么角度上看,这台机器已经足以完全改变战场局势,由这台机器可以建造的宏伟巨筑所具备的可怕力量,以及由它在短短几分钟内创造出的浩浩荡荡的钢铁洪流,无论哪个都能轻而易举的将敌人碾碎"
      }
    }
    requirements(
      Category.units, ItemStack.with(
        Items.silicon,
        240,
        Items.surgeAlloy,
        240,
        Items.phaseFabric,
        200,
        IItems.强化合金,
        280,
        IItems.矩阵合金,
        280,
        IItems.FEX水晶,
        200,
        IItems.充能FEX水晶,
        160,
        IItems.铱锭,
        150,
        IItems.简并态中子聚合物,
        100
      )
    )
    size = 9
    liquidCapacity = 420f
    energyCapacity = 4096f
    basicPotentialEnergy = 4096f
    squareSprite = false
    payloadSpeed = 1.2f

    consCustom = Cons2 { u: UnitType?, c: BaseConsumers? ->
      c!!.power(Mathf.round(u!!.health / u.hitSize) * 0.02f).showIcon = true
      if (u.hitSize >= 38) c.energy(u.hitSize / 24)
    }

    matrixDistributeOnly = true

    sizeLimit = 120f
    healthLimit = 126000f
    machineLevel = 8
    timeMultiplier = 16f
    baseTimeScl = 0.22f

    beamWidth = 0.8f
    pulseRadius = 5f
    pulseStroke = 1.7f

    outputRange = 420f

    hoverMoveMinRadius = 48f
    hoverMoveMaxRadius = 98f
    defHoverRadius = 29f

    laserOffY = 4f

    newBooster(1.6f)
    consume!!.liquid(ILiquids.急冻液, 4f)
    newBooster(1.9f)
    consume!!.liquid(ILiquids.FEX流体, 3.8f)
    newBooster(2.4f)
    consume!!.liquid(ILiquids.相位态FEX流体, 3.8f)

    drawers = DrawMulti(
      DrawDefault(), object :DrawBlock() {
        override fun draw(build: Building?) {
          val b = build as HoveringUnitFactory.HoveringUnitFactoryBuild
          Draw.z(Layer.effect)
          Draw.color(b.team.color)

          Lines.stroke(2.2f * b.warmup())

          Lines.circle(b.x, b.y, 18 * b.warmup())
          Lines.square(b.x, b.y, (size * Vars.tilesize).toFloat(), Time.time * 1.25f)
          SglDraw.drawCornerTri(b.x, b.y, 58f, 14f, Time.time * 3.5f, true)
          var p: ProducePayload<*>? = null
          if (b.producer!!.current != null && (b.producer!!.current!!.get(ProduceType.payload)
              .also { p = it }) != null && p!!.payloads[0].item is UnitType
          ) {
            SglDraw.arc(b.x, b.y, (p.payloads[0].item as UnitType).hitSize + 8, 360 * b.progress(), -Time.time * 0.8f)
          }

          Draw.color(Pal.reactorPurple)
          Lines.square(b.x, b.y, 34f, -Time.time * 2.6f)
          SglDraw.drawCornerTri(b.x, b.y, 36f, 8f, -MathTransform.gradientRotateDeg(Time.time, 0f, 3) + 60, true)

          for(i in 0..3) {
            Draw.color(Pal.reactorPurple)
            for(j in 0..3) {
              val lerp = b.warmup() * (1 - Mathf.clamp((Time.time + 30 * j) % 120 / 85f))
              SglDraw.drawTransform(b.x, b.y, (50 + j * 20).toFloat(), 0f, (i * 90 + 45).toFloat()) { rx: Float, ry: Float, r: Float ->
                Draw.rect((SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), rx, ry, 16 * lerp, 16 * lerp, r + 90)
              }
            }

            Draw.color(b.team.color)
            for(j in 0..2) {
              val lerp = b.warmup() * (1 - Mathf.clamp((Time.time + 24 * j) % 72 / 60f))
              SglDraw.drawTransform(b.x, b.y, (40 + j * 20).toFloat(), 0f, (i * 90 + 45).toFloat()) { rx: Float, ry: Float, r: Float ->
                Tmp.v1.set(18f, 0f).setAngle(r + 90)
                Lines.stroke(2 * lerp)
                Lines.square(rx + Tmp.v1.x, ry + Tmp.v1.y, 6 * lerp, r + 45)
                Lines.square(rx - Tmp.v1.x, ry - Tmp.v1.y, 6 * lerp, r + 45)
              }
            }
          }
        }
      })
  }
}