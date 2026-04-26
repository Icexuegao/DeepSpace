package ice.content.block.turret

import arc.graphics.Color
import arc.graphics.g2d.Draw
import ice.audio.ISounds
import ice.content.IItems
import ice.entities.bullet.base.BasicBulletType
import ice.graphics.IceColor
import ice.world.meta.IceEffects
import mindustry.entities.Effect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootSummon
import mindustry.graphics.Drawf
import mindustry.type.Category
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret

class 碎冰 :SglTurret("trashIce") {
  init {
    localization {
      zh_CN {
        localizedName = "碎冰"
        description = "小型速射散射炮塔,散布较大,不适合对付高速单体或重甲目标"
      }
    }
    size = 1
    health = 250
    recoil = 0.5f
    shootY = 3f
    range = 20*8f
    inaccuracy = 10f
    squareSprite = false
    shoot = ShootSummon().apply {
      x = 0f
      y = 0f
      spread = 5f
      shots = 5
      shotDelay = 3f
    }
    shootSound = ISounds.laser1
    shootEffect = Effect(8.0f) { e: Effect.EffectContainer ->
      Draw.color(IceColor.b4, Color.white, e.fin())
      val w = 1.0f + 5.0f * e.fout()
      Drawf.tri(e.x, e.y, w, 15.0f * e.fout(), e.rotation)
      Drawf.tri(e.x, e.y, w, 3.0f * e.fout(), e.rotation + 180.0f)
    }
    requirements(Category.turret, IItems.铬铁矿, 10, IItems.低碳钢, 20)
    drawers = DrawSglTurret().apply {
      parts.add(RegionPart().apply {
        outline = true
      }, RegionPart("-barrel").apply {
        outline = true
        progress = DrawPart.PartProgress.recoil
        under = true
        heatColor = IceColor.b4
        heatProgress = DrawPart.PartProgress.recoil
        moveY = -1.5f
      })
    }
  }

  override fun setAmmo() {
    newAmmo(getAmmoType(IItems.高碳钢.color).apply {
      damage = 20f
      rangeChange = 6f * 8f
    }).setReloadAmount(6)
    consume!!.apply {
      time(45f)
      items(IItems.高碳钢, 1)
    }
    newAmmo(getAmmoType(IItems.黄铜锭.color).apply {
      damage = 18f
      rangeChange = 8f * 8f
    }).setReloadAmount(6)
    consume!!.apply {
      time(45f)
      items(IItems.黄铜锭, 1)
    }
    newAmmo(getAmmoType(IItems.钴锭.color).apply {
      damage = 24f
      rangeChange = 6f * 8f
    }).setReloadAmount(4)
    consume!!.apply {
      time(45f)
      items(IItems.钴锭, 1)
    }
    newAmmo(getAmmoType(IItems.铬锭.color).apply {
      damage = 24f
      rangeChange = 6f * 8f
    }).setReloadAmount(4)
    consume!!.apply {
      time(45f)
      items(IItems.铬锭, 1)
    }
    newAmmo(getAmmoType(IItems.硫钴矿.color)).setReloadAmount(2)
    consume!!.apply {
      time(45f)
      items(IItems.硫钴矿, 1)
    }
    newAmmo(getAmmoType(IItems.铬铁矿.color)).setReloadAmount(2)
    consume!!.apply {
      time(45f)
      items(IItems.铬铁矿, 1)
    }
    newAmmo(getAmmoType(IItems.黄铜矿.color).apply {
      damage = 10f
    }).setReloadAmount(3)
    consume!!.apply {
      time(45f)
      items(IItems.黄铜矿, 1)
    }
  }

  fun getAmmoType(color: Color = IceColor.b4): BasicBulletType {
    return BasicBulletType(5f, 14f).apply {
      width = 2f
      height = 9f
      despawnEffect = IceEffects.基础子弹击中特效(color)
      hitEffect = despawnEffect
      trailColor = color
      backColor = color
      hitColor = color
      frontColor = color
    }
  }
}