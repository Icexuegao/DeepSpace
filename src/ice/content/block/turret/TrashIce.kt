package ice.content.block.turret

import arc.graphics.Color
import arc.graphics.g2d.Draw
import ice.audio.ISounds
import ice.content.IItems
import ice.entities.bullet.base.BasicBulletType
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.meta.IceEffects
import mindustry.entities.Effect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootSummon
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.draw.DrawTurret

class TrashIce:ItemTurret("trashIce") {
  init{
    BaseBundle.bundle {
      desc(zh_CN, "碎冰")
    }
    size = 1
    health = 250
    recoil = 0.5f
    shootY = 3f
    reload = 45f
    range = 160f
    shootCone = 30f
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
    ammo(IItems.硫钴矿, BasicBulletType(5f, 9f).apply {
      width = 2f
      height = 9f
      lifetime = 30f
      ammoMultiplier = 2f
      despawnEffect = IceEffects.基础子弹击中特效
      hitEffect = despawnEffect
      trailColor = IceColor.b4
      backColor = IceColor.b4
      hitColor = IceColor.b4
      frontColor = IceColor.b4
    })
    requirements(Category.turret, IItems.铬铁矿, 10, IItems.低碳钢, 20)
    drawer = DrawTurret().apply {
      parts.add(RegionPart("-barrel").apply {
        progress = DrawPart.PartProgress.recoil
        under = true
        heatColor = IceColor.b4
        heatProgress = DrawPart.PartProgress.recoil
        moveY = -1.5f
      })
    }
  }
}