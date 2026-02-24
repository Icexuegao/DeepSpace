package ice.content.unit

import arc.graphics.Color
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.type.weapons.PointDefenseWeapon
import mindustry.type.weapons.RepairBeamWeapon

class Meteorite:IceUnitType("meteorite") {
  init{
    bundle {
      desc(zh_CN, "陨石", "轻型空中突击单位.以机炮持续扫射攻击敌人,以闪电场电击附近敌军并治疗友军.配备裂解炮抵御敌人的攻击")
    }
    flying = true
    lowAltitude = true
    health = 3450f
    armor = 13f
    hitSize = 18f
    speed = 2.4f
    accel = 0.08f
    drag = 0.04f
    rotateSpeed = 4.8f
    engineSize = 3f
    engineOffset = 12f
    healColor = "FFA665".toColor()
    outlineColor = "1F1F1F".toColor()
    setWeapon("weapon") {
      x = -6f
      y = 5.5f
      recoil = 1f
      shake = 0.5f
      reload = 35f
      mirror = false
      shootCone = 5f
      inaccuracy = 1f
      rotationLimit = 25f
      cooldownTime = 65f
      ejectEffect = Fx.casing1
      shoot.apply {
        shots = 4
        shotDelay = 4f
        layerOffset = -0.001f
        shootSound = Sounds.shoot
        bullet = BasicBulletType(7f, 37f).apply {
          width = 8f
          height = 12f
          lifetime = 27f
          hitColor = Pal.bulletYellowBack
          hitEffect = Fx.hitSquaresColor
          shootEffect = Fx.shootSmokeSquare
        }
      }
    }
    setWeaponT<RepairBeamWeapon>("陨石修复") {
      x = 0.5f
      y = -2f
      shootY = 0f
      mirror = false
      laserColor = "FFA665".toColor()
      repairSpeed = 5f
      bullet = object : BulletType() {
        init {
          maxRange = 120f
        }
      }
    }
    setWeaponT<PointDefenseWeapon>("陨石点防") {
      x = 0.5f
      y = -2f
      shootY = 0f
      color = "FFA665".toColor()
      reload = 9f
      targetInterval = reload
      targetSwitchInterval = reload
      bullet = BulletType().apply {
        damage = 67f
        maxRange = 216f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    }
    abilities.add(EnergyFieldAbility(155f, 85f, 160f).apply {
      x = 0.5f
      y = -2f
      healPercent = 2f
      effectRadius = 2f
      maxTargets = 20
      statusDuration = 180f
      color = Color.valueOf("FFA665")
      status = StatusEffects.melting
    })
  }
}