package ice.content.unit

import arc.graphics.Color
import ice.entities.bullet.LaserBulletType
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.gen.Sounds
import mindustry.gen.TankUnit

class 碎甲 :IceUnitType("shatter", TankUnit::class.java) {
  init {
    localization {
      zh_CN {
        localizedName = "碎甲"
        description = "轻型地面突击单位.发射高热激光攻击敌人"
      }
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
    setTreadRects(19f, 8f, 20f, 96f, 96f, 112f)
    setWeapon("weapon") {
      x = 0f
      shootY += 4f
      reload = 50f
      recoil = 2.5f
      mirror = false
      continuous = true
      rotate = true
      rotateSpeed = 3f
      bullet = LaserBulletType(100f).apply {
        width = 13f
        shootSound = Sounds.shootLaser
        shootEffect = IceEffects.squareAngle(color2 = Color.valueOf("ffa763"))
        colors = arrayOf(Color.valueOf("ffa763"), Color.valueOf("ffa763"), Color.valueOf("fabd8e"))
      }
    }
  }
}