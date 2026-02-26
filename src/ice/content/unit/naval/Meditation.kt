package ice.content.unit.naval

import ice.entities.bullet.ArtilleryBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ShieldRegenFieldAbility
import mindustry.entities.bullet.MissileBulletType
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds
import mindustry.gen.UnitWaterMove

class Meditation : IceUnitType("unit_meditation", UnitWaterMove::class.java) {
  init {
    BaseBundle.Companion.bundle {
      desc(zh_CN, "苦修", "中型海栖突击单位.发射炮弹与导弹攻击敌人,并加装护盾辅助发生器以维持友军护盾持续作战")
    }
    health = 1365f
    hitSize = 25f
    armor = 9f
    speed = 0.8f
    rotateSpeed = 1.8f
    trailLength = 32
    waveTrailX = 8f
    waveTrailY = -9f
    trailScl = 2.2f
    faceTarget = false

    setWeapon("导弹") {
      x = 6.5f
      y = -10f
      reload = 36f
      rotate = true
      inaccuracy = 10f
      shootCone = 20f
      rotateSpeed = 4f
      shoot = ShootPattern().apply {
        shots = 2
        shotDelay = 3f
      }
      shootSound = Sounds.shootMissile
      bullet = MissileBulletType().apply {
        damage = 40f
        lifetime = 60f
        speed = 4f
        weaveMag = 1f
        weaveScale = 8f
        homingRange = 80f
        splashDamage = 35f
        splashDamageRadius = 45f
        hitEffect = Fx.flakExplosionBig
        despawnEffect = Fx.flakExplosionBig
      }
    }
    setWeapon("weapon") {
      x = 0f
      shake = 2f
      recoil = 3f
      shootY = 7f
      reload = 65f
      rotate = true
      mirror = false
      inaccuracy = 2f
      shootCone = 20f
      rotateSpeed = 2f
      rotationLimit = 240f
      ejectEffect = Fx.casing3
      shootSound = Sounds.shootArtillery
      bullet = ArtilleryBulletType().apply {
        lifetime = 90f
        speed = 4f
        width = 11f
        height = 12f
        trailSize = 6f
        trailMult = 0.8f
        hitShake = 4f
        knockback = 2f
        status = StatusEffects.blasted
        splashDamage = 97f
        splashDamageRadius = 44f
        shootEffect = Fx.shootBig2
        buildingDamageMultiplier = 1.5f
      }
    }



    abilities.add(ShieldRegenFieldAbility(30f, 240f, 120f, 80f))
  }
}