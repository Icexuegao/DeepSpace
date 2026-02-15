package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.HoverPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootHelix
import mindustry.entities.pattern.ShootMulti
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.draw.DrawTurret

class Judgment : PowerTurret("judgment") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "决断", "一次性发射四道湍能弹精准攻击敌人")
    }
    health = 6350
    armor = 6f
    size = 6
    shake = 5f
    recoil = 6f
    range = 672f
    reload = 90f
    shootY = 8f
    recoilTime = 75f
    rotateSpeed = 1.6f
    cooldownTime = 120f
    canOverdrive = false
    coolantMultiplier = 0.2f
    minWarmup = 0.99f
    shootWarmupSpeed = 0.08f
    shootSound = Sounds.shootBeamPlasma

    consumePower(45f)
    consumeCoolant(2.5f)

    shoot = ShootMulti().apply {
      source = ShootHelix().apply {
        scl = 3f
        mag = 0.75f
      }
      dest = arrayOf(ShootHelix().apply {
        mag = 3f
        scl = 0.75f
      })
    }

    shootType = BasicBulletType().apply {
      damage = 0f
      lifetime = 42f
      speed = 16f
      width = 16f
      height = 24f
      hitSize = 40f
      hittable = false
      trailLength = 8
      trailWidth = 3.6f
      trailColor = Color.valueOf("FEB380")
      frontColor = Color.white
      backColor = Color.valueOf("FEB380")
      hitColor = Color.valueOf("FEB380")
      pierceCap = 2
      ammoMultiplier = 1f
      status = IStatus.湍能
      statusDuration = 150f
      shootEffect = Fx.shootTitan
      smokeEffect = Fx.shootSmokeTitan
      splashDamage = 325f
      splashDamageRadius = 68f
      trailChance = 1f
      trailInterval = 30f
      trailEffect = ParticleEffect().apply {
        particles = 2
        lifetime = 20f
        sizeFrom = 4f
        sizeTo = 0f
        cone = 360f
        length = 10f
        baseLength = 16f
        colorFrom = Color.valueOf("FEB380")
        colorTo = Color.valueOf("FEB380")
      }
      hitShake = 7f
      hitSound = Sounds.explosionPlasmaSmall
      hitEffect = MultiEffect(ParticleEffect().apply {
        region = "blank"
        particles = 4
        lifetime = 55f
        sizeFrom = 6f
        sizeTo = 0f
        cone = 360f
        length = 72f
        offset = 45f
        interp = Interp.pow5Out
        sizeInterp = Interp.pow5In
        colorFrom = Color.valueOf("FEB380")
        colorTo = Color.valueOf("FEB380")
      }, ParticleEffect().apply {
        particles = 23
        lifetime = 35f
        line = true
        strokeFrom = 3f
        strokeTo = 0f
        lenFrom = 20f
        lenTo = 0f
        cone = 360f
        length = 84f
        colorFrom = Color.valueOf("FEB380")
        colorTo = Color.valueOf("FEB380")
      }, WaveEffect().apply {
        lifetime = 30f
        sizeFrom = 0f
        sizeTo = 84f
        strokeFrom = 6f
        strokeTo = 0f
        colorFrom = Color.valueOf("FEB380")
        colorTo = Color.valueOf("FEB380")
      })
      fragBullets = 4
      fragBullet = BasicBulletType().apply {
        sprite = "crystal"
        damage = 73f
        lifetime = 90f
        drag = 0.035f
        speed = 5f
        width = 8f
        height = 12f
        shrinkY = 0f
        trailWidth = 2f
        trailLength = 12
        trailColor = Color.valueOf("FEB380")
        frontColor = Color.white
        backColor = Color.valueOf("FEB380")
        hitColor = Color.valueOf("FEB380")
        pierce = true
        absorbable = false
        homingRange = 60f
        homingPower = 0.08f
        status = IStatus.湍能
        statusDuration = 60f
        hitSound = Sounds.explosion
        hitEffect = Fx.hitSquaresColor
        despawnEffect = Fx.hitSquaresColor
      }
    }

    requirements(
      Category.turret, IItems.铬锭, 1080, IItems.铱板, 840, IItems.石英玻璃, 210, IItems.导能回路, 480, IItems.陶钢, 320
    )

    drawer = DrawTurret().apply {
      parts.add(RegionPart().apply {
        suffix = "-glow"
        heatProgress = DrawPart.PartProgress.warmup
        drawRegion = false
        heatColor = Color.valueOf("F03B0E")
      })
      parts.add(HoverPart().apply {
        color = Color.valueOf("FEB380")
        phase = 120f
        circles = 3
        stroke = 1f
        y = -5.75f
        layer = 110f
      })
    }
  }
}
