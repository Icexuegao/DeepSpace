package ice.content.unit

import arc.Core
import arc.graphics.Color
import arc.input.KeyCode
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.content.IStatus
import ice.content.IUnitTypes
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.world.content.unit.IceUnitType
import universecore.world.ability.DeathGiftAbility
import universecore.world.ability.HealthRequireAbility
import mindustry.Vars
import mindustry.audio.SoundLoop
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Predict
import mindustry.entities.Sized
import mindustry.entities.abilities.SpawnDeathAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.units.WeaponMount
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.type.Weapon
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SpiderBomb : IceUnitType("unit_spiderBomb") {

  init {
    localization {
      zh_CN {
        this.localizedName = "炸蛛"
        description = "中型地面突击单位.向敌人发起剧烈自杀式攻击.配备推进器以快速接近敌人.阵亡后会分裂出爆蚊"
      }
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
    abilities.add(
      DeathGiftAbility(80f, IStatus.庇护, 120f, 0.05f, 240f),
      HealthRequireAbility(0.2f, StatusEffects.none, IStatus.迅疗)
    )


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

    weapons.add(object : Weapon() {
      override fun update(unit: Unit, mount: WeaponMount) {
        val can = unit.canShoot()
        val lastReload = mount.reload
        mount.reload = max(mount.reload - Time.delta * unit.reloadMultiplier, 0f)
        mount.recoil = Mathf.approachDelta(mount.recoil, 0f, unit.reloadMultiplier / recoilTime)
        if (recoils > 0) {
          if (mount.recoils == null) mount.recoils = FloatArray(recoils)
          for (i in 0..<recoils) {
            mount.recoils[i] = Mathf.approachDelta(mount.recoils[i], 0f, unit.reloadMultiplier / recoilTime)
          }
        }
        mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed)
        mount.charge = if (mount.charging && shoot.firstShotDelay > 0) Mathf.approachDelta(mount.charge, 1f, 1 / shoot.firstShotDelay) else 0f

        val warmupTarget = if ((can && mount.shoot) || (continuous && mount.bullet != null) || mount.charging) 1f else 0f
        if (linearWarmup) {
          mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed)
        } else {
          mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed)
        }

        val mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y)
        val mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y)

        //find a new target
        if (!controllable && autoTarget) {
          if ((Time.delta.let {mount.retarget -= it; mount.retarget}) <= 0f) {
            mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround)
            mount.retarget = if (mount.target == null) targetInterval else targetSwitchInterval
          }

          if (mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)) {
            mount.target = null
          }

          var shoot = false

          if (mount.target != null) {
            shoot = mount.target.within(mountX, mountY, bullet.range + abs(shootY) + (if (mount.target is Sized) (mount.target as Sized).hitSize() / 2f else 0f)) && can

            if (predictTarget) {
              val to = Predict.intercept(unit, mount.target, bullet)
              mount.aimX = to.x
              mount.aimY = to.y
            } else {
              mount.aimX = mount.target.x()
              mount.aimY = mount.target.y()
            }
          }

          mount.rotate = shoot
          mount.shoot = mount.rotate

          //note that shooting state is not affected, as these cannot be controlled
          //logic will return shooting as false even if these return true, which is fine
        }

        //rotate if applicable
        if (rotate && (mount.rotate || mount.shoot) && can) {
          val axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y)
          val axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y)

          mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation
          mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta)
          if (rotationLimit < 360) {
            val dst = Angles.angleDist(mount.rotation, baseRotation)
            if (dst > rotationLimit / 2f) {
              mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit / 2f)
            }
          }
        } else if (!rotate) {
          mount.rotation = baseRotation
          mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY)
        }

        val weaponRotation = unit.rotation - 90 + (if (rotate) mount.rotation else baseRotation)
        val bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY)
        val bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY)
        val shootAngle = bulletRotation(unit, mount, bulletX, bulletY)

        if (alwaysShooting) mount.shoot = true

        //update continuous state
        if (continuous && mount.bullet != null && (Core.input.keyDown(KeyCode.shiftLeft) && unit.isPlayer)) {
          if (!mount.bullet.isAdded || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type !== bullet) {
            mount.bullet = null
          } else {
            mount.bullet.rotation(weaponRotation + 90)
            mount.bullet.set(bulletX, bulletY)
            mount.reload = reload
            mount.recoil = 1f
            unit.vel.add(Tmp.v1.trns(mount.bullet.rotation() + 180f, mount.bullet.type.recoil * Time.delta))
            if (shootSound !== Sounds.none && !Vars.headless) {
              if (mount.sound == null) mount.sound = SoundLoop(shootSound, 1f)
              mount.sound.update(bulletX, bulletY, true)
            }

            //target length of laser
            val shootLength = min(Mathf.dst(bulletX, bulletY, mount.aimX, mount.aimY), range())
            //current length of laser
            val curLength = Mathf.dst(bulletX, bulletY, mount.bullet.aimX, mount.bullet.aimY)
            //resulting length of the bullet (smoothed)
            val resultLength = Mathf.approachDelta(curLength, shootLength, aimChangeSpeed)
            //actual aim end point based on length
            Tmp.v1.trns(shootAngle, resultLength.also {mount.lastLength = it}).add(bulletX, bulletY)

            mount.bullet.aimX = Tmp.v1.x
            mount.bullet.aimY = Tmp.v1.y

            if (alwaysContinuous && mount.shoot) {
              mount.bullet.time = mount.bullet.lifetime * mount.bullet.type.optimalLifeFract * mount.warmup
              mount.bullet.keepAlive = true

              unit.apply(shootStatus, shootStatusDuration)
            }
          }
        } else {
          //heat decreases when not firing
          mount.heat = max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0f)

          if (mount.sound != null) {
            mount.sound.update(bulletX, bulletY, false)
          }
        }

        //flip weapon shoot side for alternating weapons
        val wasFlipped = mount.side
        if (otherSide >= 0 && alternate && mount.side == flipSprite && otherSide < unit.mounts.size && mount.reload <= reload / 2f && lastReload > reload / 2f) {
          unit.mounts[otherSide].side = !unit.mounts[otherSide].side
          mount.side = !mount.side
        }

        if (!Vars.headless && activeSound !== Sounds.none && mount.shoot && can && mount.warmup >= minWarmup) {
          Vars.control.sound.loop(activeSound, unit, activeSoundVolume)
        }

        val velLen = if (unit.isRemote) unit.vel.len() else unit.deltaLen() / Time.delta

        //shoot if applicable
        if ((mount.shoot &&  //must be shooting
                  can &&  //must be able to shoot
                  !(bullet.killShooter && mount.totalShots > 0) &&  //if the bullet kills the shooter, you should only ever be able to shoot once
                  (!useAmmo || unit.ammo > 0 || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) &&  //check ammo
                  (!alternate || wasFlipped == flipSprite) && mount.warmup >= minWarmup &&  //must be warmed up
                  velLen >= minShootVelocity &&  //check velocity requirements
                  (mount.reload <= 0.0001f || (alwaysContinuous && mount.bullet == null)) &&  //reload has to be 0, or it has to be an always-continuous weapon
                  (alwaysShooting || Angles.within(if (rotate) mount.rotation else unit.rotation + baseRotation, mount.targetRotation, shootCone))) //has to be within the cone
        ) {
          shoot(unit, mount, bulletX, bulletY, shootAngle)

          mount.reload = reload

          if (useAmmo) {
            unit.ammo--
            if (unit.ammo < 0) unit.ammo = 0f
          }
        }
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
      alwaysShooting = true
      alwaysContinuous = true
      shootSound = Sounds.loopFire
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