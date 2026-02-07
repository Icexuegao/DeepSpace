package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.draw.DrawTurret

class ThinkEnd:ItemTurret("thinkEnd") {
  init{
    BaseBundle.bundle {
      desc(zh_CN, "绪终")
    }
    size = 5
    shoot.apply {
      firstShotDelay = 120f
      recoils = 1
      reload = 120f
      shootWarmupSpeed = 0.05f
    }
    ammo(IItems.暮光合金, BasicBulletType(4f, 4f))
    requirements(Category.turret, ItemStack.with(IItems.铜锭, 10, IItems.单晶硅, 5))
    drawer = DrawTurret().apply {
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
      BaseBundle.bundle {
        desc(zh_CN, "绪终")
      }
    }
  }
}