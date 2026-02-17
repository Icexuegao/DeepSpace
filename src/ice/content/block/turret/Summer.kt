package ice.content.block.turret

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.UnitSorts
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.graphic.SglDraw
import singularity.world.SglFx
import singularity.world.blocks.turrets.HeatBulletType
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret

class Summer : SglTurret("summer") {
  init {
    bundle {
      desc(zh_CN, "夏至", "高速释放巨量的受控热能团,以太阳风暴摧毁一切敌人,它开火伴随着猛烈的热浪,将被击中的一切化为铁水灰烬")
    }
    requirements(
      Category.turret, IItems.强化合金, 210, IItems.简并态中子聚合物, 80, IItems.絮凝剂, 180, IItems.铱锭, 120, IItems.气凝胶, 240, IItems.矩阵合金, 140, IItems.充能FEX水晶, 150, IItems.FEX水晶, 100
    )
    size = 6
    accurateDelay = false
    accurateSpeed = false
    scaledHealth = 410f
    recoil = 2f
    recoilTime = 120f
    rotateSpeed = 2f
    shootCone = 45f
    warmupSpeed = 0.025f
    fireWarmupThreshold = 0.85f
    linearWarmup = false
    range = 500f
    targetGround = true
    targetAir = true
    shootY = 8f
    shake = 2f

    energyCapacity = 4096f
    basicPotentialEnergy = 4096f

    unitSort = UnitSorts.strongest

    shootSound = Sounds.shootReign
    shootSoundPitch = 2f

    shoot = object : ShootPattern() {
      override fun shoot(totalShots: Int, handler: BulletHandler) {
        var i = 0
        while (i < shots) {
          for (sign in Mathf.signs) {
            Tmp.v1.set(sign.toFloat(), 1f).setLength(Mathf.random(2.5f)).scl(Mathf.randomSign().toFloat())
            handler.shoot(12 * sign + Tmp.v1.x, Tmp.v1.y, (-45 * sign + Mathf.random(-20, 20)).toFloat(), i / 2f * shotDelay) { b: Bullet? ->
              if (b!!.owner is SglTurretBuild && (b.owner as SglTurretBuild).wasShooting()) {
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo((b.owner as SglTurretBuild).targetPos), b.type.homingPower * Time.delta * 50f))
              }
            }
          }
          i += 2
        }
      }
    }
    shoot.shots = 12
    shoot.shotDelay = 5f

    newAmmo(object : HeatBulletType() {
      init {
        speed = 4.5f
        lifetime = 180f
        damage = 85f
        hitSize = 2f
        homingPower = 0.06f
        trailEffect = SglFx.glowParticle
        trailRotation = true
        trailChance = 0.12f
        trailColor = Pal.lightishOrange.cpy().a(0.7f)
        hitColor = Pal.lightishOrange
        shootEffect = Fx.shootSmallColor
        hitEffect = MultiEffect(
          Fx.absorb, Fx.circleColorSpark
        )
        smokeEffect = Fx.none
        despawnEffect = Fx.none
        despawnHit = false
        trailWidth = 2f
        trailLength = 26

        hitSound = Sounds.mechStep
        hitSoundPitch = 2f
        hitSoundVolume = 1.6f

        meltDownTime = 12f
        melDamageScl = 0.3f
        maxExDamage = 120f
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.z(Layer.bullet)
        Draw.color(Pal.lighterOrange)
        val fout = b.fout(Interp.pow4Out)
        val z = Draw.z()
        Draw.z(z - 0.0001f)
        b.trail.draw(trailColor, trailWidth * fout)
        Draw.z(z)

        SglDraw.drawLightEdge(b.x, b.y, 35 * fout + Mathf.absin(0.5f, 3.5f), 2f, 14 * fout + Mathf.absin(0.4f, 2.5f), 2f, 30f, Pal.lightishOrange)
        SglDraw.drawDiamond(b.x, b.y, 16 * fout + Mathf.absin(0.6f, 2f), 2f, 90f, Pal.lightishOrange)
        Fill.circle(b.x, b.y, 2.2f * fout)
      }

      override fun drawTrail(b: Bullet?) {}

      override fun removed(b: Bullet?) {}
    })
    consume!!.energy(5f)
    consume!!.time(60f)

    draw = DrawSglTurret(object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 4f
        progress = PartProgress.warmup
        heatColor = Pal.lightishOrange
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : RegionPart("_bot") {
      init {
        mirror = true
        moveY = -4f
        moveX = 2f
        progress = PartProgress.warmup
        heatColor = Pal.lightishOrange
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : RegionPart("_body") {
      init {
        progress = PartProgress.recoil
        heatProgress = PartProgress.warmup.delay(0.25f)
        heatColor = Pal.lightishOrange
        moveY = -4f
      }
    }, object : RegionPart("_blade") {
      init {
        mirror = true
        moveX = 2f
        moveY = 8f
        moveRot = -45f
        progress = PartProgress.warmup
        heatColor = Pal.lightishOrange
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : ShapePart() {
      init {
        color = Pal.lighterOrange
        circle = true
        hollow = true
        stroke = 0f
        strokeTo = 2f
        y = -18f
        radius = 0f
        radiusTo = 12f
        progress = PartProgress.warmup
        layer = Layer.effect
      }
    }, object : ShapePart() {
      init {
        circle = true
        y = -18f
        radius = 0f
        radiusTo = 3.5f
        color = Pal.lighterOrange
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        haloRotation = 90f
        shapes = 2
        triLength = 0f
        triLengthTo = 32f
        haloRadius = 0f
        haloRadiusTo = 12f
        tri = true
        radius = 2f
        radiusTo = 5f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        haloRotation = 90f
        shapes = 2
        triLength = 0f
        triLengthTo = 8f
        haloRadius = 0f
        haloRadiusTo = 12f
        tri = true
        radius = 2f
        radiusTo = 5f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        haloRotation = 0f
        haloRotateSpeed = 1f
        shapes = 2
        triLength = 0f
        triLengthTo = 10f
        haloRadius = 16f
        tri = true
        radius = 6f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        haloRotation = 0f
        haloRotateSpeed = 1f
        shapes = 2
        triLength = 0f
        triLengthTo = 6f
        haloRadius = 16f
        tri = true
        radius = 6f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        haloRotation = 0f
        haloRotateSpeed = -1f
        shapes = 4
        triLength = 0f
        triLengthTo = 4f
        haloRadius = 12f
        tri = true
        radius = 5f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        haloRotation = 0f
        haloRotateSpeed = -1f
        shapes = 4
        triLength = 0f
        triLengthTo = 6f
        haloRadius = 12f
        tri = true
        radius = 5f
        shapeRotation = 180f
      }
    })
  }
}