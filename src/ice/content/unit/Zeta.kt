package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.entities.bullet.EmpBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.PayloadUnit
import mindustry.gen.Sounds
import mindustry.type.weapons.RepairBeamWeapon

class Zeta : IceUnitType("unit_zeta") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "泽塔","拥有快速建造能力与强劲的武器系统,\n向敌人喷射等离子火焰的同时发射电磁震爆弹")
    }
    coreUnitDock = true
    lowAltitude = true
    isEnemy = false
    flying = true
    health = 7200f
    hitSize = 46f
    armor = 16f
    speed = 2.8f
    rotateSpeed=2f
    engineSize = 0f
    range = 360f
    mineTier = 9
    mineSpeed = 18f
    mineRange = 160f
    buildBeamOffset = -8f
    buildSpeed = 8f
    itemCapacity = 210
    payloadCapacity = 2304f
    abilities.add(
      ArmorPlateAbility().apply {
        healthMultiplier = 0.5f
      })

    engines.add(UnitEngine().apply {
      x = 8.5f
      y = -26.875f
      radius = 6f
      rotation = -90f
    }, UnitEngine().apply {
      x = -8.5f
      y = -26.875f
      radius = 6f
      rotation = -90f
    })

    setWeapon {
      x = 0f
      y = -0.375f
      reload = 300f
      mirror = false
      shake = 1f
      shootY = 0f
      shootCone = 20f
      cooldownTime = 145f
      shootSound = Sounds.shootLancer
      alwaysContinuous = true
      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FAAF878C"), Color.valueOf("FAAF87B2"), Color.valueOf("FAAF87CC"), Color.valueOf("FAAF87"), Color.valueOf("FFFFFFCC")
        )
        damage = 50f
        lifetime = 60f
        length = 240f
        width = 4.8f
        drawFlare = false
        status = StatusEffects.melting
        statusDuration = 150f
        buildingDamageMultiplier = 0.1f
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 7
          lifetime = 15f
          length = 65f
          cone = -360f
          strokeFrom = 2.5f
          strokeTo = 0f
          lenFrom = 8f
          lenTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
      }
    }


    setWeapon("cannon") {
      x = 15.75f
      y = 4.25f
      reload = 60f
      recoil = 3f
      shake = 1f
      shootY = 7.25f
      shootCone = 5f
      rotate = true
      rotateSpeed = 3f
      rotationLimit = 45f
      layerOffset = -0.001f
      shootSound = ISounds.月隐发射
      bullet = EmpBulletType().apply {
        sprite = "circle-bullet"
        damage = 120f
        lifetime = 45f
        speed = 8f
        width = 12f
        height = 12f
        shrinkY = 0f
        scaleLife = true
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        splashDamage = 60f
        splashDamageRadius = 80f
        buildingDamageMultiplier = 0.1f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FFDCD8")
          colorTo = Color.valueOf("FF5845")
        }
        hitPowerEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 22f
          line = true
          lenFrom = 6f
          lenTo = 6f
          cone = 360f
          length = 80f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        hitColor = Color.valueOf("FF8663")
        radius = 80f
        timeIncrease = 1.5f
        powerDamageScl = 1.5f
        unitDamageScl = 0.5f
        homingPower = 0.08f
        homingRange = 180f
        trailColor = Color.valueOf("FF5845")
        trailLength = 5
        trailWidth = 4f
        trailInterval = 12f
        trailChance = 1f
        trailRotation = true
        trailEffect = ParticleEffect().apply {
          line = true
          particles = 3
          lifetime = 25f
          length = 24f
          baseLength = 0f
          lenFrom = 12f
          lenTo = 0f
          cone = 15f
          offsetX = -15f
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
        hitEffect = MultiEffect(ParticleEffect().apply {
          line = true
          particles = 30
          lifetime = 45f
          length = 80f
          cone = 360f
          lenFrom = 12f
          lenTo = 0f
          interp = Interp.exp10Out
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          particles = 1
          lifetime = 60f
          length = 0f
          sizeFrom = 12f
          sizeTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          particles = 1
          lifetime = 15f
          sizeFrom = 80f
          sizeTo = 80f
          length = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF866300")
        }, WaveEffect().apply {
          lifetime = 60f
          sizeFrom = 80f
          sizeTo = 80f
          strokeFrom = 4f
          strokeTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        })
        despawnEffect = Fx.none
      }
    }


    setWeaponT<RepairBeamWeapon> {
      x = 18.75f
      y = -15.5f
      shootY = 0f
      reload = 20f
      beamWidth = 0.7f
      widthSinMag = 0.11f
      shootCone = 20f
      rotate = true
      rotateSpeed = 3f
      repairSpeed = 5f
      fractionRepairSpeed = 0.05f
      autoTarget = true
      controllable = false
      targetUnits = false
      targetBuildings = true
      laserColor = Color.valueOf("FFD37F")
      healColor = Color.valueOf("FFD37F")
      bullet = BulletType().apply {
        maxRange = 160f
      }
    }
  }
}