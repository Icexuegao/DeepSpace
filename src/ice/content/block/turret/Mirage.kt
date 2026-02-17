package ice.content.block.turret

import arc.Core
import arc.func.Boolf
import arc.func.Func2
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.branch
import ice.content.block.turret.TurretBullets.lightning
import ice.content.block.turret.TurretBullets.破碎FEX结晶
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.UnitSorts
import mindustry.entities.Units
import mindustry.entities.part.RegionPart
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.meta.StatUnit
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.LightningBulletType
import singularity.world.blocks.turrets.MultiTrailBulletType
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.generator.VectorLightningGenerator

class Mirage : SglTurret("mirage") {
  init {
    bundle {
      desc(zh_CN, "虚妄", "高能FEX结晶弹射器,将大块结晶态FEX发射向目标,不同的结晶状态会产生截然不同的效果,在互相作用下可以造成相当大的杀伤效果")
    }
    requirements(
      Category.turret,
        IItems.强化合金, 260, IItems.矩阵合金, 120, IItems.气凝胶, 200,

      IItems.铀238, 160, IItems.铱锭, 80, IItems.FEX水晶, 120
    )
    size = 5

    scaledHealth = 380f
    recoil = 2.8f
    recoilTime = 120f
    rotateSpeed = 2f
    warmupSpeed = 0.023f
    shake = 3.6f
    fireWarmupThreshold = 0.92f
    linearWarmup = false
    range = 480f

    targetAir = true
    targetGround = true

    shootEffect = MultiEffect(
      SglFx.shootRail, SglFx.shootRecoilWave
    )
    smokeEffect = Fx.shootSmokeSmite

    shootSound = Sounds.blockExplode1Alt
    shootSoundVolume = 1.4f

    newAmmo(object : MultiTrailBulletType() {
      init {
        damage = 380f
        speed = 8f
        lifetime = 60f

        pierceCap = 4
        pierceBuilding = true

        hitSize = 6f

        knockback = 1.7f

        status = IStatus.结晶化
        statusDuration = 150f

        hittable = false
        despawnHit = true

        hitEffect = MultiEffect(
          Fx.shockwave, SglFx.diamondSpark
        )

        fragOnHit = false
        fragOnAbsorb = true
        fragBullets = 8
        fragBullet = 破碎FEX结晶.copy()
        fragBullet.homingRange = 160f
        fragBullet.homingPower = 0.1f

        trailColor = SglDrawConst.fexCrystal
        trailWidth = 4f
        trailLength = 18
        trailEffect = Fx.colorSparkBig
        trailChance = 0.24f
        trailRotation = true

        hitColor = SglDrawConst.fexCrystal
        val gen = VectorLightningGenerator().apply {
          branchChance = 0.18f
          minBranchStrength = 0.8f
          maxBranchStrength = 1f

          minInterval = 5f
          maxInterval = 15f

          branchMaker = Func2 { vert, strength ->
            branch.maxLength = (40 * strength)
            branch.originAngle = vert.angle + Mathf.random(-90, 90)
            branch
          }
        }

        intervalBullet = lightning(30f, 45f, 4f, SglDrawConst.fexCrystal, true) { b: Bullet? ->
          val e = Units.bestEnemy(b!!.team, b.x, b.y, 80f, { _: Unit -> true }, UnitSorts.farthest)
          if (e == null) {
            gen.vector.rnd(Mathf.random(40f, 80f))
          } else gen.vector.set(e.x - b.x, e.y - b.y).add(Mathf.random(-3f, 3f), Mathf.random(-3f, 3f))

          gen
        }
        bulletInterval = 1f
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.z(Layer.bullet)
        Draw.color(SglDrawConst.fexCrystal)
        val rot = b.fin() * 1800

        SglDraw.drawCrystal(
          b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
        )
      }
    }) { t, b: mindustry.entities.bullet.BulletType? ->
      t.add("[accent]释放闪电${60 / b!!.bulletInterval}/秒[lightgray] ~[accent]${45}[lightgray]伤害[]")
    }
    consume!!.item(IItems.FEX水晶, 1)
    consume!!.time(60f)

    newAmmo(object : MultiTrailBulletType() {
      init {
        damage = 520f
        speed = 6f
        lifetime = 80f

        pierceCap = 4
        pierceBuilding = true

        hitSize = 8f

        knockback = 1.7f

        subTrails = 3

        absorbable = false
        hittable = false
        despawnHit = true

        hitEffect = MultiEffect(
          Fx.shockwave, Fx.bigShockwave, SglFx.crossLight, SglFx.spreadSparkLarge, SglFx.diamondSparkLarge
        )

        fragBullets = 1
        fragBullet = object : LightningBulletType() {
          init {
            damage = 42f
            lifetime = 105f
            speed = 6f

            hitColor = SglDrawConst.fexCrystal

            collides = false
            pierceCap = 42
            hittable = false
            absorbable = false

            despawnEffect = MultiEffect(
              Fx.shockwave, SglFx.diamondSpark
            )

            trailColor = SglDrawConst.fexCrystal
            trailEffect = SglFx.movingCrystalFrag
            trailInterval = 4f
          }

          val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
            minInterval = 6f
            maxInterval = 16f
          }

          override fun continuousDamage(): Float {
            return damage * 20
          }

          override fun init(b: Bullet, container: LightningContainer) {
            super.init(b, container)
            container.lifeTime = 16f
            container.minWidth = 2.5f
            container.maxWidth = 4.5f
            container.lerp = Interp.pow2Out
            container.time = 0f
          }

          override fun update(bullet: Bullet, container: LightningContainer) {
            super.update(bullet, container)

            bullet.vel.x = Mathf.lerpDelta(bullet.vel.x, 0f, 0.05f)
            bullet.vel.y = Mathf.lerpDelta(bullet.vel.y, 0f, 0.05f)

            if (bullet.timer(4, 3f)) {
              var tar: Hitboxc? = null
              var dst = 0f
              for (unit in Groups.unit.intersect(bullet.x - 180, bullet.y - 180, 360f, 360f)) {
                if (unit.team === bullet.team || !unit.hasEffect(IStatus.结晶化)) continue
                val d = unit.dst(bullet)
                if (d > 180) continue

                if (tar == null || d > dst) {
                  tar = unit
                  dst = d
                }
              }

              if (tar == null) {
                dst = 0f
                for (b1 in Groups.bullet.intersect(bullet.x - 180, bullet.y - 180, 360f, 360f)) {

                  if (b1.team !== bullet.team || b1.type !== this) continue
                  val d = b1.dst(bullet)
                  if (d > 180) continue

                  if (tar == null || d > dst) {
                    tar = b1
                    dst = d
                  }
                }
              }

              if (tar == null) return

              gen.vector.set(tar.x() - bullet.x, tar.y() - bullet.y)

              container.create(gen)

              Damage.collideLine(bullet, bullet.team, bullet.x, bullet.y, gen.vector.angle(), gen.vector.len(), false, false)
            }
          }

          override fun draw(b: Bullet, c: LightningContainer) {
            super.draw(b, c)
            val rot = b.fin(Interp.pow2Out) * 1800
            SglDraw.drawCrystal(
              b.x, b.y, 30f, 14f, 9f, 0f, 0f, 0.6f, Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
            )

            Lines.stroke(0.6f * b.fout(), SglDrawConst.fexCrystal)
            SglDraw.dashCircle(b.x, b.y, 180f, 6, 180f, Time.time * 1.6f)
          }

          override fun despawned(b: Bullet) {
            super.despawned(b)

            Damage.damage(b.team, b.x, b.y, 60f, 180f)
          }
        }

        trailColor = SglDrawConst.fexCrystal
        trailWidth = 5f
        trailLength = 22
        trailEffect = Fx.colorSparkBig
        trailChance = 0.24f
        trailRotation = true

        hitColor = SglDrawConst.fexCrystal
        val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
          branchChance = 0.17f
          minBranchStrength = 0.8f
          maxBranchStrength = 1f
          minInterval = 5f
          maxInterval = 15f
          branchMaker = Func2 { vert, strength ->
            branch.maxLength = (40 * strength)
            branch.originAngle = vert.angle + Mathf.random(-90, 90)
            branch
          }
        }
        intervalBullet = lightning(30f, 60f, 4f, SglDrawConst.fexCrystal, true) { b: Bullet? ->
          val e = Units.bestEnemy(b!!.team, b.x, b.y, 80f, Boolf { _: Unit? -> true }, UnitSorts.farthest)
          if (e == null) {
            gen.vector.rnd(Mathf.random(40f, 80f))
          } else gen.vector.set(e.x - b.x, e.y - b.y).add(Mathf.random(-3f, 3f), Mathf.random(-3f, 3f))
          gen
        }
        bulletInterval = 1.5f
      }

      override fun draw(b: Bullet) {
        super.draw(b)

        Draw.z(Layer.bullet)
        Draw.color(SglDrawConst.fexCrystal)
        val rot = b.fin() * 1800

        SglDraw.drawCrystal(
          b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
        )
      }

      override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
        super.hitEntity(b, entity, health)

        if (b.vel.len() > 0.3f) {
          b.time -= b.vel.len()
        }
        b.vel.scl(0.6f)

        if (entity is Unit && entity.hasEffect(IStatus.结晶化)) {
          for (i in 0..4) {
            val len = Mathf.random(1f, 7f)
            val a = b.rotation() + Mathf.range(fragRandomSpread / 2) + fragAngle + ((i - 2) * fragSpread)
            破碎FEX结晶.create(
              b, entity.x + Angles.trnsx(a, len), entity.y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax)
            )
          }
        }
      }
    }, true) { table, b: mindustry.entities.bullet.BulletType? ->
      table!!.add(Core.bundle.format("bullet.damage", b!!.damage))
      table.row()
      table.add(Core.bundle.format("bullet.pierce", b.pierceCap))
      table.row()
      table.add(Core.bundle.format("bullet.frags", b.fragBullets))
      table.row()
      table.table { t ->
        t!!.add(
          Core.bundle.format(
            "infos.mirageLightningDamage", Strings.autoFixed(180f / Vars.tilesize, 1), (b.fragBullet.damage * 20).toString() + StatUnit.perSecond.localized(), IStatus.结晶化.emoji() + IStatus.结晶化.localizedName
          )
        )
      }.left().padLeft(15f)
      table.row()
      table.add(Core.bundle.format("infos.generateLightning", 60 / b.bulletInterval, 60))
    }
    consume!!.item(IItems.充能FEX水晶, 2)
    consume!!.time(120f)

    draw = DrawSglTurret(object : RegionPart("_shooter") {
      init {
        mirror = false
        heatProgress = PartProgress.warmup
        heatColor = SglDrawConst.fexCrystal

        progress = PartProgress.recoil

        moveY = -4f
      }
    }, object : RegionPart("_side") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup

        heatColor = SglDrawConst.fexCrystal
        mirror = true

        moveX = 8f
        moveRot = -35f

        moves.add(PartMove(PartProgress.recoil, 0f, 0f, -10f))
      }
    }, object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup

        heatColor = SglDrawConst.fexCrystal
        mirror = true

        moveX = 2f
        moveY = -4f
        moveRot = 15f

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 5f))
      }
    }, object : RegionPart("_body") {
      init {
        heatProgress = PartProgress.warmup
        heatColor = SglDrawConst.fexCrystal

        mirror = false
      }
    })
  }
}