package ice.content.unit.flying.rain

import arc.graphics.Color
import ice.entities.bullet.LaserBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.gen.Sounds

class TorrentialRain : IceUnitType("unit_torrentialRain") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "骤雨", "轻型空中突击单位.发射聚焦激光攻击敌人")
    }
    lowAltitude = true
    flying = true
    health = 455f
    hitSize = 15f
    armor = 3f
    speed = 1.7f
    engineSize = 3f
    engineOffset = 10.5f

    setWeapon("weapon") {
      x = 0f
      y = 0f
      recoil = 0f
      shootY = 6f
      reload = 145f
      mirror = false
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(150f).apply {
        lifetime = 12f
        length = 200f
        width = 12f
        colors = arrayOf(
          Color.valueOf("FAAF87"), Color.valueOf("EBBD86"), Color.valueOf("FFF0F0")
        )
        buildingDamageMultiplier = 0.8f
        hitEffect = Fx.hitLancer
        despawnEffect = Fx.none
      }
    }
  }
}