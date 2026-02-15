package ice.content.unit

import arc.graphics.Color
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
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

class MeteoricIron : IceUnitType("meteoricIron") {
  init {
    bundle {
      desc(zh_CN, "陨铁", "多功能异构飞行器,具有强大的纳米修复系统,集群作战时尤为强大\n具有一门略小于船体主结构的光束炮")
    }
    flying = true
    lowAltitude = true
    health = 10700f
    armor = 21f
    hitSize = 23f
    speed = 1.9f
    accel = 0.04f
    drag = 0.016f
    rotateSpeed = 3.1f
    engineSize = 5f
    engineOffset = 14f
    healColor = Color.valueOf("FFA665")
    outlineColor = Color.valueOf("1F1F1F")
    setWeapon("陨铁激光") {
      x = 8f
      y = 4f
      recoil = 0f
      shake = 3f
      reload = 85f
      mirror = false
      shootCone = 5f
      cooldownTime = 115f
      shoot.apply {
        shots = 2
        shotDelay = 10f
      }
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(334f).apply {
        width = 25f
        length = 230f
        sideAngle = 20f
        sideWidth = 1.5f
        sideLength = 80f
        colors = arrayOf("EC7458AA".toColor(), "FF9C5A".toColor(), Color.white)
        shootEffect = Fx.shockwave
      }
    }
    setWeaponT<RepairBeamWeapon>("陨铁修复") {
      x = -3.75f
      y = -6.75f
      shootY = 0f
      mirror = false
      laserColor = Color.valueOf("FFA665")
      repairSpeed = 8f
      bullet = BulletType().apply {
        maxRange = 160f
      }
    }
    setWeaponT<PointDefenseWeapon>("陨铁点防") {
      x = -3.75f
      y = -6.75f
      shootY = 0f
      color = Color.valueOf("FFA665")
      reload = 7f
      targetInterval = reload
      targetSwitchInterval = reload
      bullet = BulletType().apply {
        damage = 85f
        maxRange = 216f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    }
    setWeapon("machineGun") {
      x = -2.25f
      y = 1.5f
      recoil = 1f
      shake = 1f
      reload = 43f
      rotate = true
      mirror = false
      shootCone = 5f
      inaccuracy = 1f
      rotateSpeed = 6f
      cooldownTime = 65f
      ejectEffect = Fx.casing2
      shoot.apply {
        shots = 2
        shotDelay = 4f
      }
      shootSound = Sounds.shoot
      bullet = BasicBulletType(37f, 7f).apply {
        width = 8f
        height = 12f
        lifetime = 39f
        hitColor = Pal.bulletYellowBack
        splashDamage = 25f
        splashDamageRadius = 16f
        status = IStatus.损毁
        statusDuration = 60f
        hitEffect = Fx.flakExplosion
        shootEffect = Fx.shootSmokeSquare
        despawnEffect = Fx.hitSquaresColor
      }
    }
    abilities.add(EnergyFieldAbility(225f, 85f, 200f).apply {
      x = -3.75f
      y = -6.75f
      healPercent = 2f
      effectRadius = 2f

      maxTargets = 20
      statusDuration = 180f
      color = "FFA665".toColor()
      status = StatusEffects.melting
    })
  }
}