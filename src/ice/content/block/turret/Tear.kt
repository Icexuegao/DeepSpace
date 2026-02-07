package ice.content.block.turret

import arc.math.Interp
import ice.audio.ISounds
import ice.content.IItems
import ice.content.ILiquids
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootHelix
import mindustry.entities.pattern.ShootMulti
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.draw.DrawTurret

class Tear :PowerTurret("tear"){
  init{
    squareSprite = false
    health = 19200
    size = 8
    range = 768f
    reload = 60f
    cooldownTime = 45f
    shake = 4f
    shootY = 0f
    recoil = 8f
    recoilTime = 45f
    shootCone = 5f
    rotateSpeed = 0.8f
    minWarmup = 0.97f
    shootWarmupSpeed = 0.08f
    warmupMaintainTime = 300f
    consumePower(272f)
    consumeItems(IItems.肃正协议, 4)
    consumeLiquids(ILiquids.急冻液, 4f)
    itemCapacity = 4
    liquidCapacity = 120f
    canOverdrive = false
    shoot = ShootMulti(ShootHelix().apply {
      scl = 3f
      mag = 0.75f
    }, ShootHelix().apply {
      mag = 3f
      scl = 0.75f
    })
    shootSound = ISounds.聚爆
    drawer = DrawTurret().apply {
      parts.addAll(RegionPart("-side").apply {
        heatProgress = DrawPart.PartProgress.warmup
        under = true
        mirror = true
        moveX = 1f
        moveY = -1f
        heatColor = "F03B0E".toColor()
      }, RegionPart("-part").apply {
        heatProgress = DrawPart.PartProgress.warmup
        drawRegion = false
        heatColor = "F03B0E".toColor()
      })
    }
    BaseBundle.bundle {
      desc(zh_CN, "撕裂", "一座强大的电磁轨道炮,超长轨道,超大力度,可以快速地进行精准射击")
    }
    requirements(Category.turret, IItems.铜锭, 9600, IItems.铬锭, 6400, IItems.铱板, 3600, IItems.导能回路, 2400, IItems.陶钢, 1920, IItems.生物钢, 1200)
    shootType = BasicBulletType(840f, 16f, "gauss-bullet").apply {
      lifetime = 48f
      shrinkY = 0f
      height = 32f
      width = 26f
      ammoMultiplier = 1f
      frontColor = "FF8663".toColor()
      backColor = "FF5845".toColor()
      hittable = false
      pierceCap = 2
      status = StatusEffects.melting
      statusDuration = 180f
      splashDamage = 240f
      splashDamageRadius = 80f
      buildingDamageMultiplier = 0.5f
      trailColor = "FF5845".toColor()
      trailLength = 24
      trailWidth = 3f
      trailSinScl = 0.75f
      trailSinMag = 1.5f
      hitShake = 3f
      despawnShake = 4f
      knockback = 5f
      lightning = 4
      lightningLength = 12
      lightningDamage = 255f
      lightningColor = "FF5845".toColor()
      hitEffect = MultiEffect(WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 65f
        strokeFrom = 4f
        strokeTo = 0f
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }, ParticleEffect().apply {
        line = true
        particles = 11
        lifetime = 30f
        length = 85f
        baseLength = 20f
        cone = -360f
        lenFrom = 7f
        lenTo = 0f
        interp = Interp.exp10In
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })

      despawnEffect = MultiEffect(ParticleEffect().apply {
        particles = 1
        sizeFrom = 45f
        sizeTo = 0f
        length = 0f
        interp = Interp.bounceOut
        lifetime = 60f
        region = "star".appendModName()
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
        layer = 110f
      }, WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 65f
        strokeFrom = 4f
        strokeTo = 0f
        interp = Interp.elasticOut
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }, ParticleEffect().apply {
        line = true
        particles = 11
        lifetime = 30f
        length = 85f
        baseLength = 20f
        cone = -360f
        lenFrom = 7f
        lenTo = 0f
        interp = Interp.exp10In
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })

      shootEffect = MultiEffect(ParticleEffect().apply {
        particles = 4
        sizeFrom = 6f
        sizeTo = 0f
        length = 70f
        lifetime = 30f
        interp = Interp.sineOut
        sizeInterp = Interp.sineIn
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
        cone = 15f
      }, WaveEffect().apply {
        lifetime = 30f
        sides = 0
        sizeFrom = 0f
        sizeTo = 40f
        strokeFrom = 4f
        strokeTo = 0f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })
    }
  }
}