package ice.content.unit

import arc.Core
import arc.graphics.Color
import arc.input.KeyCode
import arc.math.Interp
import ice.content.IStatus
import ice.content.IUnitTypes
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.SpawnDeathAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.units.WeaponMount
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.type.Weapon

class SpiderBomb : IceUnitType("unit_spiderBomb") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "炸蛛", "搭载了矢量推进器的多足自爆机甲\n能从意想不到的方位发起进攻,并产生足以撼动永固工事的剧烈爆炸")
    }

    health = 7680f
    speed = 1.2f
    hitSize = 18f
    armor = 11f
    drag = 0.1f
    range = 80f
    rotateSpeed = 4f
    groundLayer = 75f
    legCount = 6
    legLength = 18f
    stepShake = 0.2f
    legGroupSize = 3
    legMoveSpace = 1f
    legExtension = -3f
    legBaseOffset = 7f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.95f
    legForwardScl = 0.9f
    legSplashDamage = 5f
    legSplashRange = 2f
    hovering = true
    targetAir = false
    lockLegBase = true
    allowLegStep = true
    legContinuousMove = true
    outlineColor = Color.valueOf("1F1F1F")
    abilities.add(
      SpawnDeathAbility().apply {
        unit = IUnitTypes.爆蚊
        amount = 3
        randAmount = 1
      })

    setWeapon("weapon_name") {
      x = 0f
      reload = 600f
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.shootConquer
      bullet = BulletType().apply {
        damage = 0f
        speed = 8f
        lifetime = 8f
        collides = false
        hittable = false
        killShooter = true
        incendAmount = 96
        incendSpread = 96f
        status = IStatus.蚀骨
        statusDuration = 480f
        splashDamage = 1373f
        splashDamageRadius = 144f
        instantDisappear = true
        shootEffect = Fx.none
        despawnEffect = MultiEffect(ParticleEffect().apply {
          lifetime = 333f
          particles = 12
          sizeFrom = 16f
          sizeTo = 0f
          cone = 360f
          length = 156f
          baseLength = 35f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("F6E096")
          colorTo = Color.valueOf("F9C27A")
        }, ParticleEffect().apply {
          lifetime = 293f
          particles = 11
          sizeFrom = 16f
          sizeTo = 0f
          cone = 360f
          length = 156f
          baseLength = 35f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("F6E096")
          colorTo = Color.valueOf("F9C27A")
        }, ParticleEffect().apply {
          lifetime = 222f
          particles = 17
          sizeFrom = 12f
          sizeTo = 0f
          cone = 360f
          length = 144f
          baseLength = 23f
          interp = Interp.pow10Out
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("F6E096")
          colorTo = Color.valueOf("F9C27A")
        }, ParticleEffect().apply {
          lifetime = 150f
          particles = 13
          sizeFrom = 8f
          sizeTo = 0f
          cone = 360f
          length = 120f
          baseLength = 11f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("F6E096")
          colorTo = Color.valueOf("F9C27A")
        }, ExplosionEffect().apply {
          sparkColor = Color.valueOf("F6E096")
          smokeColor = Color.valueOf("F6E096")
          lifetime = 108f
          smokes = 30
          smokeSize = 13f
          smokeSizeBase = 0.6f
          smokeRad = 112f
          waveLife = 30f
          waveStroke = 2f
          waveRad = 183f
          waveRadBase = 2f
          sparkRad = 192f
          sparkLen = 13f
          sparkStroke = 4f
          sparks = 40
        })
      }
    }

   weapons.add(object :Weapon(){
     override fun update(unit: Unit?, mount: WeaponMount?) {
       alwaysShooting = Core.input.keyDown(KeyCode.shiftLeft)
       super.update(unit, mount)
     }
   }.apply {
      x = 0f
      y = -10f
      shootY = 0f
      reload = 300f
      rotate = true
      mirror = false
      rotateSpeed = 1f
      shootCone = 20f
      useAmmo = false
      rotationLimit = 20f
      alwaysShooting = false
      alwaysContinuous = true
      shootSound = Sounds.shootLaser
      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"), Color.valueOf("FF5845B2"), Color.valueOf("FF5845CC"), Color.valueOf("FF8663"), Color.valueOf("FFDCD8CC")
        )
        damage = 2f
        lifetime = 60f
        length = -16f
        width = 2f
        recoil = -0.2f
        drawFlare = false
        status = StatusEffects.melting
        statusDuration = 150f
        hitEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 15f
          line = true
          strokeFrom = 2.5f
          strokeTo = 0f
          lenFrom = 8f
          lenTo = 0f
          length = 21f
          cone = 360f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
      }
    })

    setWeapon("weapon_name") {
      x = 0f
      shake = 0f
      reload = 30f
      mirror = false
      display = false
      useAmmo = false
      shootCone = 360f
      shootSound = Sounds.none
      shootStatus = IStatus.突袭
      shootStatusDuration = 60f
      bullet = BulletType().apply {
        damage = 0f
        lifetime = 30f
        speed = 8f
        collidesAir = false
        instantDisappear = true
        shootEffect = Fx.none
        smokeEffect = Fx.none
        despawnEffect = Fx.none
        hitEffect = Fx.none
      }
    }
  }
}