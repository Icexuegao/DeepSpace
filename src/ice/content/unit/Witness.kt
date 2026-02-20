package ice.content.unit

import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.bullet.MissileBulletType
import mindustry.gen.Sounds
import mindustry.gen.UnitWaterMove

class Witness : IceUnitType("unit_witness", UnitWaterMove::class.java) {

  init {
    BaseBundle.bundle {
      desc(zh_CN, "见证", "轻型海栖突击单位.发射机炮与小型鱼雷攻击敌人,机动性能优异,擅长清理落单的敌方舰只")
    }
    health = 300f
    hitSize = 11f
    armor = 4f
    speed = 1.35f
    rotateSpeed = 3f
    trailLength = 20
    waveTrailX = 0f
    waveTrailY = -8f
    trailScl = 2f
    faceTarget = false
    setWeapon("weapon") {
      x = 0f
      y = -1f
      reload = 6f
      recoil = 0.6f
      shootY = 3f
      rotate = true
      mirror = false
      rotateSpeed = 6f
      inaccuracy = 2f
      ejectEffect = Fx.casing1
      shootSound = Sounds.shootPulsar
      bullet = BasicBulletType(9f, 11f).apply {
        lifetime = 20f
        width = 5f
        height = 8f
      }
    }


    setWeapon("weapon_name") {
      x = 3.5f
      y = 4f
      reload = 120f
      shootCone = 20f
      baseRotation = -15f
      ignoreRotation = true
      shootSound = Sounds.shootMissile
      bullet = MissileBulletType().apply {
        damage = 25f
        lifetime = 120f
        speed = 1f
        drag = -0.01f
        width = 8f
        height = 20f
        homingDelay = 8f
        homingPower = 0.1f
        homingRange = 250f
        splashDamage = 45f
        splashDamageRadius = 35f
        absorbable = false
        collidesAir = false
        collideFloor = true
        shootEffect = Fx.none
        hitEffect = Fx.flakExplosionBig
        hitSound = Sounds.explosion
        despawnEffect = Fx.flakExplosionBig
      }
    }
  }
}
