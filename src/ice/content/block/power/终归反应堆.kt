package ice.content.block.power

import arc.graphics.Blending
import arc.graphics.g2d.Draw
import arc.math.Interp
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.core.IFiles.appendModName
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.world.blocks.power.ImpactReactor
import mindustry.world.draw.DrawArcSmelt
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawParticles
import mindustry.world.draw.DrawPlasma
import mindustry.world.draw.DrawRegion
import mindustry.world.draw.DrawSoftParticles
import universecore.ui.bundle.localization
import universecore.util.toColor

class 终归反应堆:ImpactReactor("endImpactReactor"){
  init {
    size = 6
    armor = 12f
    lightColor = "EBFFFE".toColor()
    itemDuration = 72f
    warmupSpeed = 0.0004f
    powerProduction = 10550f
    itemCapacity = 32
    liquidCapacity = 600f
    squareSprite = false
    canOverdrive = false
    consumeItems(IItems.以太能, 8)
    consumePower(55f)
    consumeLiquids(ILiquids.急冻液, 1f)
    explosionShake = 8f
    explosionRadius = 84
    explosionDamage = 12000
    explodeSound = Sounds.shootCollaris
    localization {
      zh_CN {
        localizedName = "终归反应堆"
        description = "约束以太能的剧烈反应产生巨量电力,需要持续输入能量维持力场稳定,否则将引发灾难性爆炸"
      }
    }
    destroyEffect = MultiEffect(ParticleEffect().apply {
      particles = 1
      sizeFrom = 80f
      sizeTo = 9f
      length = 0f
      baseLength = 0f
      lifetime = 18f
      colorFrom = "F3E979".toColor()
      colorTo = "FFFFFF00".toColor()
      cone = 360f
    }, ParticleEffect().apply {
      particles = 20
      sizeFrom = 10f
      sizeTo = 0f
      length = 35f
      baseLength = 133f
      lifetime = 35f
      colorFrom = "F3E979".toColor()
      colorTo = "FFFFFF".toColor()
      cone = 360f
    }, WaveEffect().apply {
      lifetime = 30f
      sizeTo = 160f
      strokeFrom = 11f
      colorFrom = "F3E979".toColor()
      colorTo = "FFFFFF".toColor()
    }, WaveEffect().apply {
      lifetime = 10f
      sizeTo = 78f
      strokeFrom = 8f
      colorFrom = "F3E979".toColor()
      colorTo = "FFFFFF".toColor()
    }, ParticleEffect().apply {
      particles = 13
      line = true
      lifetime = 22f
      strokeFrom = 6f
      lenFrom = 200f
      lenTo = 0f
      length = 1f
      baseLength = 3f
      colorFrom = "FFFFFF".toColor()
      colorTo = "F3E979".toColor()
      cone = 360f
    })

    destroyBullet = BasicBulletType().apply {
      damage = 0f
      splashDamage = 1250f
      splashDamageRadius = 120f
      lifetime = 600f
      speed = 0f
      height = 60f
      width = 60f
      spin = 2f
      shrinkY = 0f
      hittable = false
      collides = false
      absorbable = false
      reflectable = false
      frontColor = "FF8663".toColor()
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
        hittable = false
        collides = false
        absorbable = false
        reflectable = false
        hitEffect = Fx.none
        despawnEffect = Fx.none
        fragBullets = 1
        fragLifeMin = 1f
        fragLifeMax = 2f
        fragVelocityMin = 1f
        fragVelocityMax = 2f

        fragBullet = BasicBulletType(8f, 225f, "star").apply {
          lifetime = 60f
          height = 24f
          width = 24f
          spin = 2f
          shrinkY = 0f
          hittable = false
          absorbable = false
          reflectable = false
          pierce = true
          pierceBuilding = true
          status = IStatus.蚀骨
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

          hitEffect = ParticleEffect().apply {
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
            colorTo = "FF8663".toColor()
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
            colorTo = "FF8663".toColor()
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
            colorTo = "FF8663".toColor()
          })


          frontColor = "FF8663".toColor()
          backColor = "FF5845".toColor()
        }
      }

      fragBullets = 60
      fragVelocityMin = 1.2f

      fragBullet = BulletType().apply {
        damage = 325f
        shrinkY = 0f
        speed = 4f
        lifetime = 75f
        drag = -0.01f

        shootEffect = ParticleEffect().apply {
          particles = 10
          length = 40f
          lifetime = 25f
          interp = Interp.circleOut
          cone = 20f
          offset = 20f
          colorFrom = "FF8663".toColor()
          colorTo = "FF5845".toColor()
          sizeFrom = 4f
          sizeTo = 0f
        }

        pierce = true
        pierceCap = 4
        hittable = false
        absorbable = false
        reflectable = false
        homingDelay = 45f
        homingRange = 80f
        homingPower = 0.12f
        frontColor = "FF5845".toColor()
        backColor = "FF8663".toColor()
        weaveMag = 1f
        weaveScale = 5f
        trailChance = 1f
        trailWidth = 2f
        trailLength = 25
        trailColor = "FF5845".toColor()

        trailEffect = ParticleEffect().apply {
          particles = 6
          length = 3f
          baseLength = 3f
          lifetime = 25f
          interp = Interp.circleOut
          cone = 360f
          offset = 3f
          colorFrom = "FF8663".toColor()
          colorTo = "FF5845".toColor()
          sizeFrom = 3f
          sizeTo = 0f
        }

        splashDamage = 135f
        splashDamageRadius = 40f
        status = IStatus.熔融
        statusDuration = 60f
        hitSound = Sounds.explosion
        hitEffect = Fx.none

        despawnEffect = ParticleEffect().apply {
          particles = 15
          length = 40f
          lifetime = 36f
          interp = Interp.circleOut
          cone = 360f
          colorFrom = "FF8663".toColor()
          colorTo = "FF5845".toColor()
          sizeFrom = 5f
          sizeTo = 0f
        }
      }

      despawnEffect = MultiEffect(WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 175f
        strokeFrom = 8f
        strokeTo = 0f
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }, WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 125f
        strokeFrom = 8f
        strokeTo = 0f
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
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
        colorTo = "FF8663".toColor()
      })
    }
    requirements(
      Category.power,
      IItems.铬锭,
      4500,
      IItems.石英玻璃,
      1150,
      IItems.铱板,
      3300,
      IItems.生物钢,
      1200,
      IItems.陶钢,
      1800,
      IItems.导能回路,
      2400
    )
    drawer = DrawMulti(DrawRegion("-bottom"), DrawArcSmelt().apply {
      alpha = 0.6f
      particles = 240
      particleLife = 90f
      particleLen = 3f
      particleRad = 22f
      particleStroke = 1.1f
      drawCenter = false
      blending = Blending.additive
    }, DrawRegion("-mid"), object :DrawSoftParticles() {
      override fun draw(build: Building) {
        Draw.z(Layer.power)
        super.draw(build)
      }
    }.apply {
      particles = 27
      particleLife = 120f
      particleSize = 9f
      particleRad = 12f
      color = "FF8663".toColor()
      color2 = "FF5845".toColor()
      alpha = 0.35f
    }, DrawPlasma().apply {
      plasma1 = "FF5845".toColor()
      plasma2 = "FF8663".toColor()
    }, DrawDefault(), DrawParticles().apply {
      color = "FEB380".toColor()
      alpha = 0.6f
      particles = 60
      particleLife = 60f
      particleRad = 80f
      particleSize = 2f
      fadeMargin = 0.5f
      rotateScl = 3.6f
      blending = Blending.additive
      particleSizeInterp = Interp.linear
    }, DrawGlowRegion("-glow"))
    ambientSound = Sounds.loopPulse
    ambientSoundVolume = 0.12f
  }
}