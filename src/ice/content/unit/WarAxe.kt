package ice.content.unit

import arc.graphics.Color
import ice.content.IUnitTypes
import ice.entities.bullet.ArtilleryBulletType
import ice.entities.bullet.ExplosionBulletType
import ice.entities.effect.MultiEffect
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import mindustry.content.Fx
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.type.Weapon
import mindustry.type.unit.MissileUnitType

class WarAxe : MissileUnitType("unit_warAxe") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "战斧")
    }
    health = 360f
    armor = 3f
    hitSize = 12f
    speed = 6f
    lifetime = 300f
    maxRange = 6f
    rotateSpeed = 1.2f
    missileAccelTime = 60f
    engineColor = "FEB380".toColor()
    trailColor = "FEB380".toColor()
    engineLayer = 100f
    engineOffset = 10f
    engineSize = 3.1f
    trailLength = 18
    lowAltitude = true
    fogRadius = 6f
    parts.add(RegionPart().apply {
      suffix = "-heat"
      progress = DrawPart.PartProgress.life
      color = "F03B0E00".toColor()
      colorTo = "F03B0E".toColor()
    })
    abilities.add(MoveEffectAbility().apply {
      y = -9f
      interval = 7f
      rotation = 180f
      color = "FEB38080".toColor()
      effect = Fx.missileTrailSmoke
    })
    deathExplosionEffect = Fx.massiveExplosion

    weapons.add(Weapon().apply {
      shake = 6f
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.shootBeamPlasmaSmall
      bullet = ExplosionBulletType().apply {
        hitColor = "FEB380".toColor()
        shootEffect = MultiEffect().apply {
          effects = arrayOf(
            Fx.massiveExplosion, Fx.scatheExplosion, Fx.scatheLight, WaveEffect().apply {
              lifetime = 10f
              strokeFrom = 4f
              sizeTo = 130f
            })
        }
        splashDamage = 750f
        splashDamageRadius = 96f
        buildingDamageMultiplier = 0.5f
        despawnShake = 3f
        despawnUnit = IUnitTypes.疟蚊
        fragVelocityMin = 1f
        fragSpread = 45f
        fragRandomSpread = 0f
        fragBullets = 8
        fragBullet = ArtilleryBulletType().apply {
          damage = 40f
          speed = 3.5f
          lifetime = 24f
          width = 18f
          height = 18f
          drag = 0.02f
          impact = true
          hitEffect = Fx.massiveExplosion
          despawnEffect = Fx.scatheSlash
          knockback = 0.8f
          splashDamage = 80f
          splashDamageRadius = 40f
          frontColor = Color.white
          backColor = "FEB380".toColor()
          trailColor = "FEB380".toColor()
          hitColor = "FEB380".toColor()
          smokeEffect = Fx.shootBigSmoke2
          despawnShake = 7f
          lightColor = "FEB380".toColor()
          lightOpacity = 0.5f
          trailLength = 20
          trailWidth = 3.5f
          trailEffect = Fx.none
          despawnUnit = IUnitTypes.飞蠓
        }
      }
    })
  }
}