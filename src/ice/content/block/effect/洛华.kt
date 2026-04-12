package ice.content.block.effect

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.util.Tmp
import ice.CubeCalculator
import ice.content.IItems
import ice.content.ILiquids
import ice.entities.ArcFieldBulletType
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import mindustry.content.Fx
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import singularity.world.blocks.turrets.ContinuousTurret
import singularity.world.draw.DrawSglTurret

class 洛华 :ContinuousTurret("mendTower") {
  init {
    bundle {
      desc(zh_CN, "洛华", "使用方菱折射投射出扇形修复光束覆盖建筑进行修复")
    }

    buildType = Prov {
      object :ContinuousTurretBuild() {
        val mat = CubeCalculator()
        val mat2 = CubeCalculator()

        override fun draw() {
          super.draw()
          // 绘制每条边

          Draw.z(Layer.effect)
          Lines.stroke(0.5f, Pal.heal)
          mat.size = 2f * warmup()
          mat.edges.forEach { (startIdx, endIdx) ->
            val start = mat.projectedPoints[startIdx]
            val end = mat.projectedPoints[endIdx]
            Tmp.v1.set(shootX, shootY).rotate(rotationu - 90f)
            val ox = Tmp.v1.x
            val oy = Tmp.v1.y
            Lines.line(start.x + x + ox, start.y + y + oy, end.x + x + ox, end.y + y + oy)
          }

          mat2.size = 0.8f * warmup()
          mat2.edges.forEach { (startIdx, endIdx) ->
            val start = mat2.projectedPoints[startIdx]
            val end = mat2.projectedPoints[endIdx]
            Tmp.v2.set(shootX, shootY).rotate(rotationu - 90f)
            val ox = Tmp.v2.x
            val oy = Tmp.v2.y
            Lines.line(start.x + x + ox, start.y + y + oy, end.x + x + ox, end.y + y + oy)
          }

          Draw.reset()

        }

        override fun updateTile() {
          super.updateTile()
          mat.update(0.005f)
          mat2.update(-0.003f)
        }
      }
    }

    requirements(Category.effect, IItems.单晶硅, 30, IItems.绿藻块, 10, IItems.石英玻璃, 40, IItems.高碳钢, 30, IItems.金锭, 20)
    size = 3
    shootSound = Sounds.none
    shootY = 8.8f
    targetAir = false
    targetGround = true
    buildingFilter = { b, build ->
      b.team == build.team
    }
    targetHealing = true
    rotateSpeed = 8f
    range = 20f * 8f
    newAmmo(object :ArcFieldBulletType() {}.apply {
      damage = 1f
      rangeChange = 5 * 8f
      shootEffect = Fx.none
      smokeEffect = Fx.none
      healAmount = 10f / 60f
      hitColor = Pal.heal
      hitEffect = Fx.none
      collidesGround = true
      collidesTeam = true
    })
    consume!!.liquid(ILiquids.氯气, 0.5f)
    val parts = (drawers as DrawSglTurret).parts
    parts.add(RegionPart("-mid"))
    parts.add(RegionPart("-blade-l").apply {
      x = -6.375f
      y = 2.75f
      moveX = -2f
      moveY = -1.25f
      moveRot = 10f
    }, RegionPart("-blade-r").apply {
      x = 6.375f
      y = 2.75f
      moveX = 2f
      moveY = -1.25f
      moveRot = -10f
    })
  }

  override fun init() {
    super.init()
    for(entry in ammoTypes) {
      entry.value.bulletType.apply {
        (this as ArcFieldBulletType).length = this@洛华.range
      }
    }
  }
}