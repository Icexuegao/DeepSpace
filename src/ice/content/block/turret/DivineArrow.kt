package ice.content.block.turret

import arc.graphics.Color
import arc.math.Mathf
import arc.util.Time
import ice.content.IItems
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle
import ice.world.draw.part.IcePartProgress
import ice.world.meta.IceEffects
import mindustry.entities.bullet.MissileBulletType
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootAlternate
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.draw.DrawTurret

class DivineArrow:PowerTurret("divineArrow") {
  init{
    BaseBundle.bundle {
      desc(zh_CN, "神矢")
    }
    size = 2
    health = 1000
    requirements(Category.turret, ItemStack.with(IItems.铬铁矿, 10, IItems.低碳钢, 20))
    reload = 30f
    recoils = 2
    squareSprite = false
    drawer = DrawTurret().apply {
      for (i in 0..1) {
        parts.add(object : RegionPart("-" + (if (i == 0) "l" else "r")) {
          init {
            progress = PartProgress.recoil
            recoilIndex = i
            under = true
            moveY = -1.5f
          }
        })
      }
      parts.add(ShapePart().apply {
        hollow = true
        radius = 4f
        layer = 110f
        sides = 4
        y = -4f
        color = IceColor.b4
        rotateSpeed = 2f
        progress = DrawPart.PartProgress.recoil
      })
      parts.add(ShapePart().apply {
        hollow = true
        radius = 0f
        radiusTo = 4f
        layer = 110f
        sides = 4
        stroke = 0.5f
        rotateSpeed = 2f
        y = -4f
        color = IceColor.b4
        progress = IcePartProgress { p: DrawPart.PartParams ->
          DrawPart.PartProgress.warmup.get(p) * ((Time.time / 15) % 1)
        }
      })
    }
    shoot = object : ShootAlternate() {
      var scl: Float = 2f
      var mag: Float = 1.5f
      var offset: Float = Mathf.PI * 1.25f
      override fun shoot(totalShots: Int, handler: BulletHandler, barrelIncrementer: Runnable?) {
        for (i in 0..<shots) {
          for (sign in Mathf.signs) {
            val index = ((totalShots + i + barrelOffset) % barrels) - (barrels - 1) / 2f
            handler.shoot(index * spread * -Mathf.sign(mirror), 0f, 0f, firstShotDelay + shotDelay * i) { b ->
              b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * sign))
            }
          }
          barrelIncrementer?.run()
        }
      }
    }.apply {
      barrelOffset = 8
      spread = 5f
      shots = 2
      shotDelay = 15f
    }
    shootSound = Sounds.shootMissile
    shootY = 6f
    shootEffect = IceEffects.squareAngle(range = 30f, color1 = IceColor.b4, color2 = Color.white)
    shootType = MissileBulletType(6f, 30f).apply {
      splashDamageRadius = 30f
      splashDamage = 30f * 1.5f
      lifetime = 45f
      trailLength = 20
      trailWidth = 1.5f
      trailColor = IceColor.b4
      backColor = IceColor.b4
      hitColor = IceColor.b4
      frontColor = IceColor.b4
      despawnEffect = IceEffects.blastExplosion(IceColor.b4)
      hitEffect = despawnEffect
    }
    range = shootType.speed * shootType.lifetime
  }
}