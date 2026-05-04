package ice.content.block.turret

import arc.Core
import arc.graphics.g2d.Draw
import arc.math.Mathf
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import mindustry.content.Fx
import mindustry.entities.part.DrawPart
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootAlternate
import mindustry.gen.Bullet
import mindustry.gen.Hitboxc
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.ProjectileTurret
import singularity.world.blocks.turrets.WarpedBulletType
import singularity.world.draw.DrawSglTurret
import singularity.world.meta.SglStat
import universecore.util.applyColor
import universecore.util.replaceNumericMatches
import kotlin.math.max
import kotlin.math.min

class Dew :ProjectileTurret("dew") {
  init {
    localization {
      zh_CN {
        localizedName = "白露"
        description = "连续高速发射一连串穿甲弹,向敌人倾泻如同暴雨般的火力"
      }
    }
    requirements(
      Category.turret, IItems.强化合金, 150, IItems.铝锭, 110, IItems.气凝胶, 120,

      IItems.矩阵合金, 160, IItems.钍锭, 100, IItems.电子元件, 85, IItems.铀238, 85
    )
    size = 5
    scaledHealth = 360f
    rotateSpeed = 2.5f
    range = 350f
    shootY = 17.4f
    warmupSpeed = 0.035f
    linearWarmup = false
    recoil = 0f
    fireWarmupThreshold = 0.75f
    shootCone = 15f
    shake = 2.2f

    shootSound = Sounds.shootCollaris

    shoot = object :ShootAlternate() {
      override fun shoot(totalShots: Int, handler: BulletHandler) {
        val off = totalShots % 2 - 0.5f

        for(i in 0..2) {
          handler.shoot(off * 16, 0f, 0f, firstShotDelay + 3 * i)
        }
      }
    }.apply {
      spread = 12f
    }

    var partProgressRecoil = PartProgress.recoil.delay(0.15f)
    drawers = object :DrawSglTurret(object :RegionPart("_side") {
      init {
        mirror = true
        moveX = 8f
        moveRot = -25f
        progress = PartProgress.warmup
        heatColor = SglDrawConst.dew
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, 1f, -1f, -5f))
      }
    }, object :RegionPart("_body") {
      init {
        heatColor = SglDrawConst.dew
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object :ShapePart() {
      init {
        layer = Layer.effect
        color = SglDrawConst.matrixNet
        x = 0f
        y = -16f
        circle = true
        hollow = true
        stroke = 0f
        strokeTo = 1.8f
        radius = 0f
        radiusTo = 8f
        progress = PartProgress.warmup
      }
    }, object :HaloPart() {
      init {
        progress = PartProgress.warmup
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        y = -16f
        shapes = 1
        triLength = 16f
        triLengthTo = 46f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4f
      }
    }, object :HaloPart() {
      init {
        progress = PartProgress.warmup
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        y = -16f
        shapes = 1
        triLength = 8f
        triLengthTo = 20f
        haloRotation = 180f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4f
      }
    }, object :HaloPart() {
      init {
        progress = PartProgress.warmup
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        y = -16f
        shapes = 2
        haloRotation = 90f
        triLength = 6f
        triLengthTo = 24f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 2.5f
      }
    }, object :HaloPart() {
      init {
        progress = partProgressRecoil
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 2f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 14f
        triLengthTo = 21f
        haloRadius = 10f
        tri = true
        radius = 0f
        radiusTo = 6f
      }
    }, object :HaloPart() {
      init {
        progress = partProgressRecoil
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 2f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 0f
        triLengthTo = 6f
        haloRadius = 10f
        tri = true
        radius = 0f
        radiusTo = 6f
        shapeRotation = 180f
      }
    }, object :HaloPart() {
      init {
        progress = partProgressRecoil
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 22f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 8f
        triLengthTo = 16f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4.5f
      }
    }, object :HaloPart() {
      init {
        progress = partProgressRecoil
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 22f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 0f
        triLengthTo = 4f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4.5f
        shapeRotation = 180f
      }
    }, object :HaloPart() {
      init {
        progress = partProgressRecoil
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 12f
        y = -4f
        haloRotation = -160f
        shapes = 1
        triLength = 12f
        triLengthTo = 20f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 5f
      }
    }, object :HaloPart() {
      init {
        progress = partProgressRecoil
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 12f
        y = -4f
        haloRotation = -160f
        shapes = 1
        triLength = 0f
        triLengthTo = 5f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 5f
        shapeRotation = 180f
      }
    }) {
      init {
        (0..1).forEach {
          parts.add(RegionPart("_blade-" + if (it == 0) "l" else "r").apply {
            recoilIndex = it
            moveX = if (it == 0) -4f else 4f
            progress = PartProgress.warmup
            heatColor = SglDrawConst.dew
            heatProgress = PartProgress.heat

            moves.add(DrawPart.PartMove(PartProgress.recoil, 0f, -2.6f, 0f))
          })
        }
      }
    }

    newCoolant(1f, 0.25f, { l -> l.heatCapacity > 0.7f && l.temperature < 0.35f }, 0.4f, 20f)
    setAmmo()
  }

  override fun setAmmo() {
    newAmmo(object :BulletType() {
      init {
        damage = 80f
        speed = 8f
        lifetime = 45f
        hitSize = 4.3f
        hitColor = SglDrawConst.matrixNet
        hitEffect = Fx.colorSpark
        despawnEffect = Fx.circleColorSpark
        trailEffect = SglFx.polyParticle
        trailRotation = true
        trailChance = 0.04f
        trailColor = SglDrawConst.matrixNet
        shootEffect = MultiEffect(Fx.shootBig, Fx.colorSparkBig)
        hittable = true
        pierceBuilding = true
        collidesTiles = true
        collidesGround = true
        pierceCap = 4
      }

      override fun update(b: Bullet) {
        super.update(b)
        b.damage = b.type.damage + b.type.damage * b.fin() * 0.3f
      }

      override fun draw(b: Bullet) {
        SglDraw.drawDiamond(b.x, b.y, 18f, 6f, b.rotation(), SglDrawConst.matrixNet)
        Draw.color(SglDrawConst.matrixNet)
        for(i in Mathf.signs) {
          Drawf.tri(b.x, b.y, 6f * b.fin(), 20f * b.fin(), b.rotation() + 156f * i)
        }
      }
    })
    consume!!.item(IItems.钍锭, 1)
    consume!!.time(5f)

    newAmmoCoating("贫铀穿甲弹头", Pal.accent, { b: BulletType ->

      object :WarpedBulletType<BulletType>(b) {
        init {
          damage = b.damage * 1.15f
          pierceArmor = true
          pierceCap = 5
        }

        override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
          if (entity is Unit) {
            if (entity.shield > 0) {
              val damageShield = min(max(entity.shield, 0f), damage * 0.85f)
              entity.shield -= damageShield
              Fx.colorSparkBig.at(b.x, b.y, b.rotation(), Pal.bulletYellowBack)
            }
          }
          super.hitEntity(b, entity, health)
        }

        override fun draw(b: Bullet) {
          SglDraw.drawDiamond(b.x, b.y, 24f, 6f, b.rotation(), Pal.accent)
          Draw.color(SglDrawConst.matrixNet)
          for(i in Mathf.signs) {
            Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i)
          }
        }
      }
    }, { t ->
      t.add("${SglStat.exDamageMultiplier.localized()}: 115%".replaceNumericMatches { it.applyColor(IceColor.stat) }).row()
      t.add("${SglStat.exShieldDamage.localized()}: 85%".replaceNumericMatches { it.applyColor(IceColor.stat) }).row()
      t.add(SglStat.exPierce.localized() + ": 1".replaceNumericMatches { it.applyColor(IceColor.stat) }).row()
      t.add("@bullet.armorpierce")
    })
    consume!!.time(10f)
    consume!!.item(IItems.铀238, 1)

    newAmmoCoating("FEX_结晶壳", SglDrawConst.fexCrystal, { b: BulletType ->
      object :WarpedBulletType<BulletType>(b) {
        init {
          damage = b.damage * 1.25f
          hitColor = SglDrawConst.fexCrystal
          trailEffect = SglFx.movingCrystalFrag
          trailInterval = 6f
          trailColor = SglDrawConst.fexCrystal

          status = IStatus.结晶化
          statusDuration = 15f
        }

        override fun draw(b: Bullet) {
          SglDraw.drawDiamond(b.x, b.y, 24f, 6f, b.rotation(), hitColor)
          Draw.color(SglDrawConst.matrixNet)
          for(i in Mathf.signs) {
            Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i)
          }
        }
      }
    }, { t ->
      t.add("${SglStat.exDamageMultiplier.localized()}: 125%".replaceNumericMatches { it.applyColor(IceColor.stat) }).row()
      t.add(IStatus.结晶化.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"))
    }, 2)
    consume!!.time(20f)
    consume!!.item(IItems.FEX水晶, 1)
    recoils = 2
  }
}