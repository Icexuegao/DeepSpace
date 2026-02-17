package ice.content.unit.mech

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ArmorPlateAbility
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.gen.MechUnit
import mindustry.gen.Sounds
import mindustry.type.ammo.ItemAmmoType

class IronGuard : IceUnitType("unit_ironGuard", MechUnit::class.java) {
  init {
    BaseBundle.Companion.bundle {
      desc(zh_CN, "铁卫", "快速交替发射炮弹攻击敌人")
    }
    health = 11000f
    armor = 11f
    hitSize = 22f
    speed = 0.4f
    range = 260f
    rotateSpeed = 2.4f
    boostMultiplier = 2f
    mechLandShake = 4f
    riseSpeed = 0.05f
    engineOffset = 12f
    engineSize = 5f
    canBoost = true
    lowAltitude = true
    stepShake = 0.15f
    mechFrontSway = 1f
    drownTimeMultiplier = 4f
    mechStepParticles = true
    ammoType = ItemAmmoType(IItems.钍锭)
    ammoCapacity = 240
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.4f
    }, RegenAbility().apply {
      percentAmount = 0.0125f
    })
    engines.add(UnitEngine().apply {
      x = 11f
      y = -11f
      radius = 3f
      rotation = -90f
    }, UnitEngine().apply {
      x = -11f
      y = -11f
      radius = 3f
      rotation = -90f
    })

    setWeapon("weapon1") {
      x = 8.5f
      y = 0.75f
      shoot.apply {
        shots = 2
        shotDelay = 5f
      }
      shootY+=3
      recoil = 1f
      shake = 0.5f
      reload = 25f
      rotate = true
      rotateSpeed = 6f
      ejectEffect = Fx.casing1
      bullet = BasicBulletType(6f, 19f).apply {
        lifetime = 32f
      }
    }

    setWeapon("weapon2") {
      top = false
      x = 15.5f
      y = 1f
      recoil = 3f
      shake = 3f
      reload = 24f
      shoot.apply {
        shots = 2
        shotDelay = 5f
      }
      shootY = 11.75f
      shootCone = 15f
      cooldownTime = 48f
      ejectEffect = Fx.casing3
      shootSound = Sounds.shootAtrax
      shootStatus = IStatus.屠戮
      shootStatusDuration = 30f
      bullet = BasicBulletType(9f, 85f).apply {
        lifetime = 29f
        width = 11f
        height = 20f
        pierceCap = 2
        homingPower = 0.02f
        status = StatusEffects.blasted
        statusDuration = 60f
        splashDamage = 55f
        splashDamageRadius = 30f
        lightColor = Color.valueOf("FF8663")
        frontColor = Color.white
        backColor = Color.valueOf("FF8663")
        hitColor = Color.valueOf("FF8663")
        shootEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 36f
          sizeFrom = 4f
          cone = 30f
          length = 60f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FEB380")
        }
        hitEffect = MultiEffect(
          ExplosionEffect().apply {
            lifetime = 20f
            waveStroke = 2f
            waveColor = Color.valueOf("FF8663")
            sparkColor = Color.valueOf("FF8663")
            waveRad = 12f
            smokeSize = 0f
            smokeSizeBase = 0f
            sparks = 10
            sparkRad = 35f
            sparkLen = 4f
            sparkStroke = 1.5f
          }, Fx.hitSquaresColor
        )
      }
    }
  }
}