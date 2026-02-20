package ice.content.unit

import arc.func.Func
import arc.graphics.Color
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.ai.UnitCommand
import mindustry.ai.types.MinerAI
import mindustry.entities.bullet.LaserBoltBulletType
import mindustry.gen.Sounds

class Phantom : IceUnitType("unit_phantom") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "幻影", "轻型空中工程单位.具备不俗的挖掘速度与物品容量,可在采集途中应对零星威胁")
    }
    flying = true
    commands.add(UnitCommand.mineCommand)
    controller = Func { MinerAI() }
    isEnemy = false
    health = 160f
    hitSize = 7.5f
    speed = 2f
    range = 200f
    itemCapacity = 80
    engineOffset = 6f
    mineTier = 4
    mineSpeed = 3f

    setWeapon {
      x = 3.5f
      y = -2.4f
      rotate = true
      rotateSpeed = 6f
      reload = 60f
      shootSound = Sounds.shootLaser
      inaccuracy = 3f
      alternate = false
      bullet = LaserBoltBulletType().apply {
        damage = 13f
        lifetime = 28f
        speed = 8f
        healPercent = 5f
        collidesTeam = true
        frontColor = Color.white
        backColor = Color.valueOf("98FFA9")
      }
    }
  }
}