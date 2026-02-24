package ice.content.unit

import arc.graphics.Color
import arc.math.geom.Rect
import ice.entities.bullet.LaserBulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.gen.Sounds

class Shatter : IceUnitType("shatter") {
  init {
    bundle {
      desc(zh_CN, "碎甲","轻型地面突击单位.发射高热激光攻击敌人")
    }
    armor = 10f
    speed = 0.8f
    health = 1200f
    hitSize = 20f
    rotateSpeed = 3.3f
    squareShape = true
    drawCell = false
    omniMovement = false
    rotateMoveFirst = true
    treadRects = arrayOf(Rect(19f - (96f / 2), 8f - (112f / 2), 20f, 96f))
    setWeapon("weapon") {
      x = 0f
      shootY += 4f
      reload = 50f
      recoil = 2.5f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      bullet = LaserBulletType(100f).apply {
        width = 10f
        shootSound = Sounds.shootLaser
        shootEffect = IceEffects.squareAngle(color2 = Color.valueOf("ffa763"))
        colors = arrayOf(Color.valueOf("ffa763"), Color.valueOf("ffa763"), Color.valueOf("fabd8e"))
      }
    }
  }
}