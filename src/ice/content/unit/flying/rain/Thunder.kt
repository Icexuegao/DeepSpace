package ice.content.unit.flying.rain

import arc.func.Func2
import arc.graphics.Color
import ice.content.block.turret.TurretBullets
import ice.content.block.turret.TurretBullets.lightning
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import universecore.graphics.lightnings.generator.RandomGenerator
import universecore.graphics.lightnings.generator.VectorLightningGenerator

class Thunder :IceUnitType("unit_thunder") {
  val colorValue1: Color = Color.valueOf("F3E979")

  init {
    localization {
      zh_CN {
        localizedName = "惊雷"
        description = "重型空中突击单位.发射缓慢移动的球状闪电攻击敌人,同时以闪电场电击附近敌军并治疗友军"
      }
    }
    lowAltitude = true
    flying = true
    health = 10800f
    armor = 14f
    hitSize = 37f
    speed = 1.2f
    drag = 0.05f
    rotateSpeed = 2.85f
    engineOffset = 23.5f
    engineSize = 8f

    engines.add(UnitEngine().apply {
      x = 10.75f
      y = -23.25f
      radius = 4f
      rotation = -90f
    }, UnitEngine().apply {
      x = -10.75f
      y = -23.25f
      radius = 4f
      rotation = -90f
    })


    setWeapon("weapon") {
      x = 0f
      y = 0f
      mirror = false
      shake = 1f
      shootY = 10.25f
      reload = 240f
      shootCone = 15f
      cooldownTime = 210f
      shootSound = Sounds.shootBeamPlasma
      bullet = BasicBulletType().apply {
        damage = 625f
        splashDamage = 425f
        splashDamageRadius = 100f
        speed = 1.5f
        lifetime = 240f
        shrinkY = 0f
        width = 16f
        height = 16f
        hitSize = 25f
        knockback = 2f
        shootEffect = ParticleEffect().apply {
          particles = 9
          sizeFrom = 5f
          sizeTo = 0f
          length = 65f
          baseLength = 16f
          lifetime = 46f
          colorFrom = colorValue1
          colorTo = Color.valueOf("FEEBB3")
          cone = 60f
        }
        hitSound = Sounds.shootBeamPlasma
        sprite = "circle-bullet"
        trailLength = 32
        trailWidth = 4f
        trailColor = colorValue1
        frontColor = Color.valueOf("FEEBB3")
        backColor = colorValue1
        pierce = true
        absorbable = false
        bulletInterval = 2.5f
        intervalBullets = 3
        val cv=RandomGenerator()
        var lig = RandomGenerator().apply {
          maxDeflect = 55f
          branchChance = 0.2f
          minBranchStrength = 0.8f
          maxBranchStrength = 3f
          maxLength = 5 * 8f
          branchMaker= Func2{ a,b->
           cv
          }
        }
        intervalBullet = lightning(20f, 31f, 3f, colorValue1, true) {
          lig
        }

        hitEffect = Fx.none
        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 26
          sizeFrom = 8f
          sizeTo = 0f
          length = 85f
          baseLength = 16f
          lifetime = 35f
          colorFrom = colorValue1
          colorTo = Color.valueOf("FEEBB3")
          cone = 40f
        }, WaveEffect().apply {
          lifetime = 18f
          sizeFrom = 2f
          sizeTo = 90f
          strokeFrom = 6f
          strokeTo = 0f
          colorFrom = colorValue1
          colorTo = Color.valueOf("FEEBB3")
        })
        fragBullets = 4
        fragBullet = BasicBulletType().apply {
          damage = 325f
          splashDamage = 185f
          splashDamageRadius = 50f
          speed = 1.5f
          lifetime = 120f
          knockback = 1f
          shrinkY = 0f
          width = 8f
          height = 8f
          hitEffect = Fx.none
          despawnEffect = MultiEffect(ParticleEffect().apply {
            particles = 13
            sizeFrom = 4f
            sizeTo = 0f
            length = 42.5f
            baseLength = 8f
            lifetime = 25f
            colorFrom = colorValue1
            colorTo = Color.valueOf("FEEBB3")
            cone = 40f
          }, WaveEffect().apply {
            lifetime = 18f
            sizeFrom = 1f
            sizeTo = 45f
            strokeFrom = 3f
            strokeTo = 0f
            colorFrom = colorValue1
            colorTo = Color.valueOf("FEEBB3")
          })
          hitSound = Sounds.shootBeamPlasma
          sprite = "circle-bullet"
          trailLength = 11
          trailWidth = 3f
          trailColor = colorValue1
          frontColor = Color.valueOf("FEEBB3")
          backColor = colorValue1
          pierce = true
          absorbable = false
          bulletInterval = 2.5f
          intervalBullets = 1

          val cv1= VectorLightningGenerator()
          var lig1 = RandomGenerator().apply {
            maxLength=18f
            branchMaker= Func2{ a,b->
              cv1
            }
          }
          intervalBullet = TurretBullets.lightning(20f, 8f, 3f, colorValue1, true) {
            lig1
          }
        }
      }
    }

    abilities.add(
      EnergyFieldAbility(165f, 300f, 320f).apply {
        maxTargets = 5
        healPercent = 1f
        x = 0f
        y = 10.25f
        sectors = 3
        sectorRad = 0.2f
        rotateSpeed = 2f
        effectRadius = 3f
        color = colorValue1
        status = StatusEffects.shocked
        hitEffect = ParticleEffect().apply {
          particles = 15
          line = true
          lenFrom = 10f
          lenTo = 0f
          strokeFrom = 2f
          strokeTo = 0f
          length = 35f
          baseLength = 0f
          lifetime = 10f
          colorFrom = colorValue1
          colorTo = Color.valueOf("FEEBB3")
          cone = 360f
        }
      })
  }
}