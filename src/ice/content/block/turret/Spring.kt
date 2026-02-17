package ice.content.block.turret

import arc.Core
import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Damage
import mindustry.entities.Units
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.Sgl
import singularity.world.SglFx
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import singularity.world.draw.part.CustomPart

class Spring: SglTurret("spring") {
  init{
    bundle {
      desc(zh_CN, "春分", "这座炮塔能够引导能量够修复我方单位和建筑,同时它会侵入敌方的机械结构中,阻止其行动", "成熟的能量引导技术赋予了这座巨物十分突出的能力")
    }
    requirements(
      Category.turret,
      IItems.强化合金, 120, IItems.铝锭, 140, IItems.絮凝剂, 80,

      IItems.矩阵合金, 100, IItems.绿藻素, 120, IItems.充能FEX水晶, 85, IItems.铱锭, 60
    )
    size = 5
    scaledHealth = 450f
    recoil = 1.8f
    rotateSpeed = 3f
    warmupSpeed = 0.022f
    linearWarmup = false
    fireWarmupThreshold = 0.6f
    range = 400f
    targetGround = true
    targetHealUnit = true
    targetAir = true
    targetHealing = true
    shootY = 12f
    shootEffect = Fx.none

    energyCapacity = 4096f
    basicPotentialEnergy = 1024f

    shootSound = Sounds.shootMalign

    shoot = object : ShootPattern() {
      override fun shoot(totalShots: Int, handler: BulletHandler) {
        for (i in intArrayOf(-1, 1)) {
          for (a in 1..2) {
            handler.shoot(0f, 0f, 4.57f * i, 0f) { b: Bullet? ->
              val len = b!!.time * 5f
              b.moveRelative(0f, i * (4 * a - 0.01f * a * len) * Mathf.sin(0.04f * len + 4))
            }
          }
        }
      }
    }
    shoot.shots = 2

    newAmmo(object : BulletType() {
      init {
        damage = 42f
        lifetime = 80f
        speed = 5f
        drawSize = 24f
        pierceCap = 4
        pierceBuilding = true
        collidesTeam = true
        smokeEffect = Fx.none
        hitColor = Pal.heal
        hitEffect = Fx.circleColorSpark
        healEffect = SglFx.impactBubble
        shootEffect = Fx.none
        healAmount = 24f
        healPercent = 0.1f
        hitSize = 8f
        trailColor = Pal.heal
        trailRotation = true
        trailEffect = Fx.disperseTrail
        trailInterval = 3f
        trailWidth = hitSize
        trailLength = 24

        hitSound = Sounds.drillImpact
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.color(Pal.heal)
        Fill.circle(b.x, b.y, b.hitSize)
      }

      override fun update(b: Bullet) {
        super.update(b)
        Units.nearby(b.team, b.x, b.y, b.hitSize, Cons { u: Unit? ->
          if (u!!.damaged() && !b.hasCollided(u.id)) {
            b.collided.add(u.id)

            u.heal(u.maxHealth * (b.type.healPercent / 100) + b.type.healAmount)
            u.apply(IStatus.临春, 30f)
            b.type.healEffect.at(b.x, b.y, b.rotation(), b.type.healColor)
          }
        })
        Damage.status(b.team, b.x, b.y, b.hitSize, IStatus.暮春, 12f, true, true)
      }
    }) { s, b: mindustry.entities.bullet.BulletType? ->
      s!!.add(
        (Core.bundle.get("misc.toTeam") + " " + IStatus.临春.emoji() + "[stat]" + IStatus.临春.localizedName + "[lightgray] ~ [stat]0.5[lightgray] " + Core.bundle.get("unit.seconds") + "[]" + Sgl.NL + Core.bundle.get("misc.toEnemy") + " " + IStatus.暮春.emoji() + "[stat]" + IStatus.暮春.localizedName + "[lightgray] ~ [stat]0.2[lightgray] " + Core.bundle.get("unit.seconds") + "[]")
      )
    }
    consume!!.energy(2.6f)
    consume!!.time(60f)

    draw = DrawSglTurret(object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 2f
        heatColor = Pal.heal
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -6f))
      }
    }, object : RegionPart("_blade") {
      init {
        mirror = true
        moveY = 4f
        moveRot = -30f
        heatColor = Pal.heal
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, -2f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = Pal.heal
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 10f
        y = 16f
        drawRadius = 0f
        drawRadiusTo = 4f
        rotation = -30f

        moveY = -10f
        moveX = 2f
        moveRot = -35f
        progress = PartProgress.warmup
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(Pal.heal)
          Drawf.tri(x, y, 4 * p, 6 + 10 * p, r)
          Drawf.tri(x, y, 4 * p, 4 * p, r + 180)
        }

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -3f))
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 10f
        y = 12f
        drawRadius = 0f
        drawRadiusTo = 4f
        rotation = -30f

        moveY = -10f
        moveX = 2f
        moveRot = -65f
        progress = PartProgress.warmup
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(Pal.heal)
          Drawf.tri(x, y, 6 * p, 8 + 12 * p, r)
          Drawf.tri(x, y, 6 * p, 6 * p, r + 180)
        }

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -5f))
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 8f
        y = 16f
        drawRadius = 0f
        drawRadiusTo = 5f
        rotation = -30f

        moveY = -20f
        moveX = 4f
        moveRot = -90f
        progress = PartProgress.warmup
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(Pal.heal)
          Drawf.tri(x, y, 8 * p, 8 + 16 * p, r)
          Drawf.tri(x, y, 8 * p, 8 * p, r + 180)
        }

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -6f))
      }
    })
  }
}