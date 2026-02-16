package ice.content.unit

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootBarrel
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer

class Omega : IceUnitType("unit_omega") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "欧米茄", "舍弃了建造及挖掘能力换取了更大的载弹量和更强劲的武器系统\n向敌人喷射等离子火焰的同时发射电磁震爆弹")
    }
    lowAltitude = true
    flying = true
    health = 23700f
    hitSize = 66f
    armor = 24f
    speed = 2f
    drag = 0.15f
    rotateSpeed = 2f
    range = 480f
    outlineColor = Color.valueOf("313131")
    engineOffset = 42f
    engineSize = 8f

    engines.add(UnitEngine().apply {
      x = 11.5f
      y = -38f
      radius = 6f
      rotation = -90f
    }, UnitEngine().apply {
      x = -11.5f
      y = -38f
      radius = 6f
      rotation = -90f
    }, UnitEngine().apply {
      x = 22f
      y = -34f
      radius = 6f
      rotation = -135f
    }, UnitEngine().apply {
      x = -22f
      y = -34f
      radius = 6f
      rotation = -45f
    })
    abilities.add(StatusFieldAbility(IStatus.反扑, 90f, 60f, 160f).apply {
      activeEffect = Fx.none
    }, ArmorPlateAbility().apply {
      healthMultiplier = 1.5f
    })

    setWeapon {
      x = 0f
      y = -14f
      reload = 300f
      mirror = false
      shake = 1f
      shootY = 0f
      shootCone = 20f
      cooldownTime = 145f
      shootSound = Sounds.shootLaser
      alwaysContinuous = true
      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"), Color.valueOf("FF5845B2"), Color.valueOf("FF5845CC"), Color.valueOf("FF8663"), Color.valueOf("FFDCD8CC")
        )
        damage = 100f
        lifetime = 60f
        length = 360f
        width = 7f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f
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
    setWeapon("舷炮") {
      x = 28.5f
      y = -6f
      reload = 180f

      shoot = ShootBarrel().apply {
        shots = 8
        shotDelay = 5f
        barrels = floatArrayOf(0f, 0f, -15f)
      }
      shake = 2f
      shootY = 0f
      inaccuracy = 15f
      shootSound = Sounds.shootMalign
      bullet = BasicBulletType(8f, 85f, "arrows").apply {
        lifetime = 50f
        drag = -0.01f
        shrinkY = 0f
        height = 18f
        width = 8f
        shootEffect = ParticleEffect().apply {
          particles = 10
          length = 40f
          lifetime = 25f
          cone = 20f
          offset = 20f
          sizeFrom = 4f
          sizeTo = 0f
          interp = Interp.fastSlow
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        pierceCap = 2
        reflectable = false
        keepVelocity = false
        homingRange = 360f
        homingPower = 0.06f
        homingDelay = 10f
        splashDamage = 35f
        splashDamageRadius = 40f
        suppressionRange = 120f
        suppressionDuration = 600f
        suppressionEffectChance = 1f
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        weaveMag = 2f
        weaveScale = 6f
        trailChance = 1f
        trailWidth = 2f
        trailLength = 24
        trailColor = Color.valueOf("D86E56")
        trailRotation = true
        trailEffect = ParticleEffect().apply {
          particles = 6
          lifetime = 25f
          length = 6f
          cone = 360f
          offsetX = -5f
          sizeFrom = 3f
          sizeTo = 0f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        hitShake = 2f
        impact = true
        knockback = 4f
        hitEffect = ParticleEffect().apply {
          particles = 15
          length = 40f
          lifetime = 36f
          interp = Interp.circleOut
          cone = 360f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          sizeFrom = 5f
          sizeTo = 0f
        }
        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 6
          sizeFrom = 3f
          sizeTo = 0f
          length = 60f
          baseLength = 8f
          lifetime = 9f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
          cone = 20f
        }, WaveEffect().apply {
          lifetime = 10f
          sizeFrom = 8f
          sizeTo = 50f
          strokeFrom = 2f
          strokeTo = 0f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        })
        fragBullets = 4
        fragLifeMin = 0.5f
        fragBullet = BasicBulletType(4f, 45f, "star").apply {
          lifetime = 40f
          spin = 8f
          shrinkX = 0f
          shrinkY = 0f
          height = 15f
          width = 15f
          impact = true
          knockback = -24f
          frontColor = Color.valueOf("FF8663")
          backColor = Color.valueOf("FF5845")
          weaveMag = 2f
          weaveScale = 7f
          trailColor = Color.valueOf("FF5845")
          trailLength = 9
          trailWidth = 2f
          trailEffect = Fx.none
          status = IStatus.熔融
          statusDuration = 60f
          homingPower = 0.06f
          homingRange = 160f
          splashDamage = 15f
          splashDamageRadius = 20f
          hitEffect = ParticleEffect().apply {
            particles = 1
            sizeFrom = 6f
            sizeTo = 0f
            length = 0f
            spin = 3f
            interp = Interp.swing
            lifetime = 100f
            region = "star".appendModName()
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845")
          }
          despawnEffect = ParticleEffect().apply {
            particles = 1
            sizeFrom = 6f
            sizeTo = 0f
            length = 0f
            spin = 3f
            interp = Interp.swing
            lifetime = 100f
            region = "star".appendModName()
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF5845")
          }
        }
      }
    }

    setWeapon("副炮") {
      x = 22.5f
      y = -0.5f
      layerOffset = -0.001f
      reload = 60f
      recoil = 3f
      shake = 1f
      shootY = 7.25f
      shootCone = 5f
      rotate = true
      rotateSpeed = 3f
      rotationLimit = 45f
      shootSound = ISounds.月隐发射
      bullet = ice.entities.bullet.EmpBulletType().apply {
        sprite = "circle-bullet"
        damage = 120f
        lifetime = 60f
        speed = 8f
        width = 12f
        height = 12f
        shrinkY = 0f
        scaleLife = true
        keepVelocity = false
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        splashDamage = 60f
        splashDamageRadius = 120f
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
          interp = Interp.fastSlow
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FFDCD8")
          colorTo = Color.valueOf("FF5845")
        }
        hitPowerEffect = ParticleEffect().apply {
          line = true
          particles = 6
          lifetime = 22f
          length = 80f
          cone = 360f
          lenFrom = 6f
          lenTo = 6f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        hitColor = Color.valueOf("FF8663")
        radius = 120f
        timeIncrease = 1.5f
        powerDamageScl = 1.5f
        unitDamageScl = 0.5f
        status = IStatus.电链
        statusDuration = 90f
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
          length = 120f
          cone = 360f
          lenFrom = 18f
          lenTo = 0f
          strokeFrom = 3f
          strokeTo = 0f
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
          length = 0f
          lifetime = 15f
          sizeFrom = 120f
          sizeTo = 120f
          interp = Interp.circle
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF866300")
        }, WaveEffect().apply {
          lifetime = 60f
          sizeFrom = 120f
          sizeTo = 120f
          strokeFrom = 4f
          strokeTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        })
        despawnEffect = Fx.none
      }
    }
  }

  override fun draw(unit: Unit) {
    super.draw(unit)
    playerAim(unit, Color.valueOf("#FF5845"))
  }

  fun playerAim(unit: Unit, color: Color, size: Float = 1f, speed: Float = 4f) {

    if (!unit.isPlayer) return
    val x = unit.aimX
    val y = unit.aimY

    val tris = 3
    Draw.color(color)
    Draw.z(Layer.effect)

    for (i in 0 until tris) {
      val ang = i * 360f / tris + Time.time
      val xy = angleTrns(ang, (24f + Mathf.absin(speed, 4f)) * size)
      doubleTri(x + xy.first, y + xy.second, size * 4f, size * 16f, ang + 180f)
    }
  }

  fun angleTrns(ang: Float, rad: Float): Pair<Float, Float> {
    return Pair(Angles.trnsx(ang, rad), Angles.trnsy(ang, rad))
  }

  fun doubleTri(x: Float, y: Float, width: Float, length: Float, angle: Float, len: Float = 4f) {
    Drawf.tri(x, y, width, length, angle)
    Drawf.tri(x, y, width, length / (len), angle + 180f)
  }
}