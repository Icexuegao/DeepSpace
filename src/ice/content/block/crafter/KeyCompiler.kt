package ice.content.block.crafter

import arc.graphics.Blending
import arc.math.Interp
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.library.util.toColor
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.draw.DrawArcSmelt
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.effect.WrapEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawParticles
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class KeyCompiler : NormalCrafter("keyCompiler") {
  init {
    bundle {
      desc(
        zh_CN,
        "密匙编译器",
        "通过量子通信接收数据以编译密匙,需要大量冷却液以支持运行\n为确保量子通道稳定性及数据准确性,不会受到时空加速的影响\n为了保护内部精密结构及能量管路,配备了极为厚重的装甲",
        "[#FF5845]数据正常下行,密匙编译稳定进行中.是时候给那些异族一些颜色看看了!"
      )
    }
    size = 10
    armor = 48f
    liquidCapacity = 120f
    squareSprite = false
    canOverdrive = false
    updateEffectChance = 0.4f
    updateEffect = WrapEffect().apply {
      effect = ParticleEffect().apply {
        line = true
        particles = 2
        lifetime = 180f
        randLength = false
        strokeFrom = 2f
        strokeTo = 0f
        lenFrom = 20f
        lenTo = 2f
        cone = 0f
        length = -180f
        baseLength = 170f
        colorFrom = "FF584500".toColor()
        colorTo = "FF5845".toColor()
      }
      rotation = 90f
    }
    craftEffect = MultiEffect(ParticleEffect().apply {
      line = true
      particles = 25
      lifetime = 120f
      length = 120f
      cone = 360f
      strokeFrom = 2.5f
      lightColor = "FFDCD8".toColor()
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, ExplosionEffect().apply {
      waveColor = "FFDCD8".toColor()
      sparkColor = "FFDCD8".toColor()
      smokes = 20
      smokeSize = 4.7f
      smokeSizeBase = 1.6f
      smokeRad = 36f
      waveLife = 30f
      waveStroke = 4f
      waveRad = 15f
      waveRadBase = 2.0f
      sparkLen = 7f
      sparks = 12
    }, WaveEffect().apply {
      lifetime = 360f
      sizeTo = 40f
      strokeFrom = 14f
      interp = Interp.pow2Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 360f
      sizeTo = 60f
      strokeFrom = 12f
      interp = Interp.pow3Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 360f
      sizeTo = 80f
      strokeFrom = 10f
      interp = Interp.pow4Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 360f
      sizeTo = 100f
      strokeFrom = 8f
      interp = Interp.pow5Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 360f
      sizeTo = 120f
      strokeFrom = 6f
      interp = Interp.pow10Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    })
    destroyEffect = MultiEffect(ParticleEffect().apply {
      particles = 30
      length = 140f
      baseLength = 10f
      lifetime = 160f
      layer = 106f
      sizeFrom = 27f
      sizeTo = 16f
      interp = Interp.circleOut
      sizeInterp = Interp.pow10Out
      colorFrom = "3D3D3D".toColor()
      colorTo = "3D3D3D10".toColor()
    }, ParticleEffect().apply {
      particles = 10
      length = 140f
      baseLength = 10f
      lifetime = 160f
      layer = 106f
      sizeFrom = 27f
      sizeTo = 16f
      interp = Interp.circleOut
      sizeInterp = Interp.pow10Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD810".toColor()
    }, WaveEffect().apply {
      lifetime = 840f
      sizeTo = 80f
      strokeFrom = 12f
      interp = Interp.pow5Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 840f
      sizeTo = 100f
      strokeFrom = 10f
      interp = Interp.pow5Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 840f
      sizeTo = 120f
      strokeFrom = 8f
      interp = Interp.pow5Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 840f
      sizeTo = 140f
      strokeFrom = 6f
      interp = Interp.pow5Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    }, WaveEffect().apply {
      lifetime = 840f
      sizeTo = 160f
      strokeFrom = 4f
      interp = Interp.pow5Out
      colorFrom = "FF5845".toColor()
      colorTo = "FFDCD8".toColor()
    })
    destroyBullet = BasicBulletType().apply {
      sprite = "star"
      damage = 0f
      splashDamage = 1250f
      splashDamageRadius = 120f
      lifetime = 600f
      speed = 0f
      height = 60f
      width = 60f
      spin = 2f
      shrinkY = 0f
      shrinkX = 0f
      collides = false
      frontColor = "FFDCD8".toColor()
      backColor = "FF5845".toColor()
      lightRadius = 240f
      despawnShake = 12f
      bulletInterval = 4f
      intervalBullets = 4
      intervalRandomSpread = 360f
      intervalBullet = BulletType().apply {
        damage = 0f
        lifetime = 0f
        speed = 0f
        hitEffect = Fx.none
        despawnEffect = Fx.none
        fragBullets = 1
        fragLifeMin = 1f
        fragLifeMax = 2f
        fragVelocityMin = 1f
        fragVelocityMax = 2f
        fragBullet = BasicBulletType().apply {
          sprite = "star"
          damage = 225f
          lifetime = 60f
          speed = 8f
          height = 24f
          width = 24f
          spin = 2f
          shrinkY = 0f
          shrinkX = 0f
          pierce = true
          pierceBuilding = true
          status = IStatus.熔融
          statusDuration = 120f
          splashDamage = 275f
          splashDamageRadius = 40f
          homingPower = 0.08f
          homingRange = 120f
          trailLength = 16
          trailWidth = 2f
          trailSinScl = 0.7853982f
          trailSinMag = 1.5707964f
          weaveMag = 5f
          weaveScale = 5f
          trailColor = "FF5845".toColor()
          trailEffect = Fx.none
          lightRadius = 40f
          impact = true
          knockback = -48f
          hitShake = 2f
          despawnShake = 4f
          hitEffect = MultiEffect(
            ParticleEffect().apply {
              line = true
              particles = 7
              lifetime = 15f
              length = 45f
              cone = -360f
              strokeFrom = 3f
              strokeTo = 0f
              lenFrom = 7f
              lenTo = 0f
              colorFrom = "FF5845".toColor()
              colorTo = "FFDCD8".toColor()
            })
        }
        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 1
          sizeFrom = 16f
          sizeTo = 0f
          length = 0f
          spin = 3f
          interp = Interp.swing
          lifetime = 80f
          region = "star".appendModName()
          lightColor = "FF5845".toColor()
          colorFrom = "FF5845".toColor()
          colorTo = "FFDCD8".toColor()
        }, ParticleEffect().apply {
          line = true
          particles = 7
          lifetime = 15f
          length = 45f
          cone = -360f
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 7f
          lenTo = 0f
          colorFrom = "FF5845".toColor()
          colorTo = "FFDCD8".toColor()
        })
      }
      frontColor = "FFDCD8".toColor()
      backColor = "FF5845".toColor()


      despawnEffect = MultiEffect(WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 175f
        strokeFrom = 8f
        strokeTo = 0f
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FFDCD8".toColor()
      }, WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 125f
        strokeFrom = 8f
        strokeTo = 0f
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FFDCD8".toColor()
      }, ParticleEffect().apply {
        line = true
        particles = 11
        lifetime = 40f
        length = 85f
        baseLength = 20f
        cone = -360f
        strokeFrom = 6f
        strokeTo = 0f
        lenFrom = 7f
        lenTo = 0f
        colorFrom = "FF5845".toColor()
        colorTo = "FFDCD8".toColor()
      })

    }
    requirements(Category.crafting, IItems.铬锭, 1800, IItems.铱板, 1200, IItems.导能回路, 1080, IItems.陶钢, 840, IItems.生物钢, 480)
    drawers = DrawMulti(DrawRegion().apply {
      suffix = "-bottom"
    }, DrawArcSmelt().apply {
      flameColor = "F58349".toColor()
      midColor = "F2D585".toColor()
      flameRad = 3.6f
      circleSpace = 4.8f
      circleStroke = 1.6f
      flameRadiusScl = 3f
      flameRadiusMag = 0.3f
      alpha = 0.68f
      particles = 180
      particleLife = 90f
      particleLen = 3f
      particleRad = 36f
      particleStroke = 1.1f
      drawCenter = true
      blending = Blending.additive
    }, DrawDefault(), DrawParticles().apply {
      color = "FF5845".toColor()
      alpha = 0.6f
      particles = 60
      particleLife = 60f
      particleRad = 60f
      particleSize = 4f
      fadeMargin = 0.25f
      rotateScl = 3.6f
      blending = Blending.additive
      particleSizeInterp = Interp.linear
    }, DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 18.84f
      color = "F0511D".toColor()
    })

    ambientSound= Sounds.loopElectricHum
    ambientSoundVolume=0.2f

    newConsume().apply {
      time(10800f)
      power(600f)
      liquid(ILiquids.急冻液, 4f)
    }
    newProduce().apply {
      item(IItems.肃正协议, 1)
    }
  }
}