package ice.content.unit

import ice.entities.bullet.ArtilleryBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.bullet.MissileBulletType
import mindustry.gen.Sounds
import mindustry.gen.UnitWaterMove

class Execution : IceUnitType("unit_execution", UnitWaterMove::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "履行", "轻型海栖突击单位.发射炮弹与导弹攻击敌人,在见证的火力上进行了增强")
    }
    health = 750f
    hitSize = 16f
    armor = 6f
    speed = 0.95f
    rotateSpeed = 2.6f
    trailLength = 24
    waveTrailX = 4f
    waveTrailY = -8f
    trailScl = 1.6f
    fogRadius = 250f
    lightRadius = 250f
    faceTarget = false

    setWeapon("gunCon") {
      x = 6.5f
      y = -4f
      reload = 30f
      rotate = true
      shootSound = Sounds.shootMissile
      bullet = MissileBulletType().apply {
        damage = 15f
        lifetime = 60f
        speed = 4f
        weaveMag = 2f
        weaveScale = 3f
        homingDelay = 11f
        homingRange = 80f
        homingPower = 0.03f
        splashDamage = 25f
        splashDamageRadius = 8f
        hitEffect = Fx.flakExplosion
        despawnEffect = Fx.flakExplosion
      }
    }

    setWeapon("Cannon") {
      x = 0f
      shake = 1f
      recoil = 3f
      shootY = 7f
      reload = 87f
      rotate = true
      mirror = false
      rotateSpeed = 2f
      inaccuracy = 2f
      shootSound = Sounds.shootArtillery
      ejectEffect = Fx.casing3
      bullet = ArtilleryBulletType().apply {
        lifetime = 60f
        speed = 4f
        width = 7f
        height = 8f
        trailSize = 4f
        trailMult = 0.8f
        knockback = 1f
        status = StatusEffects.blasted
        splashDamage = 65f
        splashDamageRadius = 24f
        shootEffect = Fx.shootBig2
      }
    }
  }
}