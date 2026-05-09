package ice.content.block.turret

import arc.func.Prov
import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.entities.bullet.base.BasicBulletType
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Bullet
import mindustry.type.Category
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret

class 绪终 :SglTurret("turret_thinkEnd") {
  init {
    localization {
      zh_CN {
        localizedName = "绪终"
      }
    }
    size = 5
    health = 2000
    armor = 10f
    warmupSpeed = 0.04f
    shoot.apply {
      recoils = 1
    }
    buildType = Prov(::ThinkEndBuild)
    requirements(Category.turret, IItems.铜锭, 10, IItems.单晶硅, 5)
    drawers = DrawSglTurret().apply {
      parts.add(RegionPart("4-l").apply {
        moveY = -4f
        moveX = -8f
        moveRot = 60f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
      parts.add(RegionPart("4-r").apply {
        moveY = -4f
        moveX = 8f
        moveRot = -60f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
      parts.add(RegionPart("1").apply {
        moveY = 2f
        progress = DrawPart.PartProgress.warmup.curve(Interp.pow2)
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
      parts.add(RegionPart("2-l").apply {
        moveY = -2f
        moveRot = 25f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup.curve(Interp.pow5In)
      })
      parts.add(RegionPart("2-r").apply {
        moveY = -2f
        moveRot = -25f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup.curve(Interp.pow5In)
      })
      parts.add(RegionPart("3").apply {
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
    }
    setAmmo()
    limitRange()
  }

  fun setAmmo() {
    newAmmo(BasicBulletType(4f, 4f))
    consume?.apply {
      time(120f)
      item(IItems.玳渊矩阵, 1)
    }
  }

  private inner class ThinkEndBuild :SglTurretBuild() {
    override fun handleBullet(bullet: Bullet, offsetX: Float, offsetY: Float, angleOffset: Float) {
      super.handleBullet(bullet, offsetX, offsetY, angleOffset)
      bullet.mover {

      }
    }
  }
}