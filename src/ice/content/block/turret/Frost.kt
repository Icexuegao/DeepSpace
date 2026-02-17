package ice.content.block.turret

import arc.func.Cons
import arc.graphics.Color
import arc.math.Angles
import arc.math.Mathf
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.crushedIce
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.bullet.ContinuousLaserBulletType
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.LaserTurret
import singularity.world.draw.DrawSglTurret
import singularity.world.draw.part.CustomPart

class Frost : LaserTurret("frost") {
  init {

    bundle {
      desc(zh_CN, "霜降", "从激光冷却发展而来的巨型冷冻光束炮,用巨大的冰冻光束给敌人沉痛而寒冷的重击")
    }
    requirements(
      Category.turret, IItems.强化合金, 160, IItems.铝锭, 110, IItems.絮凝剂, 100,

      IItems.矩阵合金, 120, IItems.充能FEX水晶, 100, IItems.铱锭, 100
    )
    size = 5
    scaledHealth = 420f
    recoil = 2.8f
    rotateSpeed = 2f
    warmupSpeed = 0.02f
    fireWarmupThreshold = 0.9f
    linearWarmup = false
    range = 360f
    targetGround = true
    targetAir = true
    shootEffect = SglFx.railShootRecoil

    energyCapacity = 4096f
    basicPotentialEnergy = 1024f

    shootSound = Sounds.shootLaser


    updating = Cons { e: SglBuilding ->
      val t = e as SglTurretBuild
      if (Mathf.chanceDelta((0.08f * e.warmup()).toDouble())) SglFx.iceParticle.at(
        t.x + Angles.trnsx(t.rotationu, -12f), t.y + Angles.trnsy(t.rotationu, -12f), t.rotationu + 90 * Mathf.randomSign(), SglDrawConst.frost
      )
      if (Mathf.chanceDelta((0.05f * e.warmup()).toDouble())) SglFx.iceParticle.at(
        t.x + Angles.trnsx(t.rotationu, 22f), t.y + Angles.trnsy(t.rotationu, 22f), t.rotationu + 15 * Mathf.randomSign(), SglDrawConst.frost
      )
    }

    newAmmo(object : ContinuousLaserBulletType() {
      init {
        pierceCap = 5
        damage = 115f
        lifetime = 240f
        damageInterval = 6f
        fadeTime = 30f
        length = 360f
        width = 8f
        hitColor = SglDrawConst.frost
        fragBullet = crushedIce
        fragBullets = 2
        fragSpread = 10f
        fragOnHit = true
        despawnHit = false
        fragRandomSpread = 60f
        incendAmount = 0
        incendChance = 0f
        drawSize = 500f
        pointyScaling = 0.7f
        oscMag = 0.8f
        oscScl = 1.2f
        frontLength = 220f
        lightColor = SglDrawConst.frost
        colors = arrayOf<Color?>(
          Color.valueOf("6CA5FF").a(0.6f), Color.valueOf("6CA5FF").a(0.85f), Color.valueOf("ACE7FF"), Color.valueOf("DBFAFF")
        )
      }

      override fun update(b: Bullet) {
        super.update(b)
        val owner = b.owner
        if (owner is SglTurretBuild) {
          owner.heat = 1f
          owner.curRecoil = owner.heat
          owner.warmup = owner.curRecoil
        }
      }

      override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
        if (entity is Healthc) {
          entity.damage(b.damage)
        }

        if (entity is Unit) {
          entity.apply(IStatus.冻结, entity.getDuration(IStatus.冻结) + 10)
        }
      }
    })
    consume!!.liquid(Liquids.cryofluid, 0.4f)
    consume!!.energy(2.4f)
    consume!!.time(210f)

    draw = DrawSglTurret(object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 8f
        moveRot = -22f
        heatColor = SglDrawConst.frost
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -8f))
      }
    }, object : RegionPart("_blade") {
      init {
        mirror = true
        moveY = 2f
        moveX = 4f
        moveRot = -24f
        heatColor = SglDrawConst.frost
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -6f))
      }
    }, object : CustomPart() {
      init {
        y = 4f
        progress = PartProgress.warmup
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.gradientTri(x, y, 40 + 260 * p, 60 * p, r, SglDrawConst.frost, 0f)
          SglDraw.gradientTri(x, y, 40 * p, 60 * p, r + 180, SglDrawConst.frost, 0f)
        }
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = SglDrawConst.frost
        heatProgress = PartProgress.warmup.delay(0.5f)
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 16f
        y = 16f
        rotation = -12f

        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.gradientTri(x, y, 8 + 32 * p, 6 * p, r, SglDrawConst.frost, 0f)
          SglDraw.drawDiamond(x, y, 8 + 16 * p, 6 * p, r, SglDrawConst.frost)
        }
        progress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -8f))
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 30f
        y = 4f
        rotation = -45f

        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.gradientTri(x, y, 12 + 36 * p, 6 * p, r, SglDrawConst.frost, 0f)
          SglDraw.drawDiamond(x, y, 12 + 18 * p, 6 * p, r, SglDrawConst.frost)
        }
        progress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 2f, -1.5f, -9f))
      }
    }, object : HaloPart() {
      init {
        color = SglDrawConst.frost
        tri = true
        y = -12f
        radius = 0f
        radiusTo = 8f
        triLength = 8f
        triLengthTo = 18f
        haloRadius = 0f
        shapes = 2
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : ShapePart() {
      init {
        circle = true
        color = Color.white
        y = 24f
        radius = 0f
        radiusTo = 6f
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : ShapePart() {
      init {
        circle = true
        color = SglDrawConst.frost
        y = 24f
        radius = 0f
        radiusTo = 6f
        hollow = true
        stroke = 0f
        strokeTo = 2.5f
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : CustomPart() {
      init {
        y = -12f
        layer = Layer.effect
        progress = PartProgress.warmup
        rotation = 90f

        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawDiamond(x, y, 20 + 76 * p, 32 * p, r, SglDrawConst.frost, 0f)
        }
      }
    })
  }
}