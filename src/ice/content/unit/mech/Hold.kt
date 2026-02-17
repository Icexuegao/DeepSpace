package ice.content.unit.mech

import arc.graphics.Color
import ice.entities.bullet.LaserBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ArmorPlateAbility
import mindustry.entities.abilities.ForceFieldAbility
import mindustry.entities.abilities.RegenAbility
import mindustry.gen.MechUnit
import mindustry.gen.Sounds

class Hold : IceUnitType("unit_hold", MechUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "固守", "中型地面突击单位.连续发射穿透性激光束攻击敌人,并以自身为中心投射一片立场墙.会缓慢恢复生命值,开火时减少所受伤害")
    }
    health = 1270f
    hitSize = 14f
    armor = 11f
    speed = 0.5f
    rotateSpeed = 3f
    lowAltitude = true
    canBoost = true
    boostMultiplier = 2f
    engineOffset = 5f
    engineSize = 4f
    mechLandShake = 3f
    riseSpeed = 0.05f
    mechFrontSway = 0.55f
    ammoCapacity = 12
    abilities.add(ForceFieldAbility(60f, 1.5f, 450f, 240f).apply {
      sides = 4
    }, ArmorPlateAbility().apply {
      healthMultiplier = 0.3f
    }, RegenAbility().apply {
      percentAmount = 0.0125f
    })
    setWeapon("weapon1") {
      x = 9f
      shake = 2f
      top = false
      shoot.apply {
        shots = 3
        shotDelay = 10f
      }
      reload = 135f
      cooldownTime = 90f
      recoil = 4f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(75f).apply {
        length = 168f
        recoil = 1f
        sideAngle = 30f
        sideWidth = 1f
        sideLength = 35f
        colors = arrayOf(
          Color.valueOf("FF5845"), Color.valueOf("FFDCD8"), Color.valueOf("D1EFFF")
        )
      }
    }
  }
}
