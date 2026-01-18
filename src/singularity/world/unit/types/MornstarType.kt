package singularity.world.unit.types

import arc.Core
import arc.func.Boolf
import arc.func.Cons2
import arc.func.Func2
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Vec2
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Interval
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import ice.content.IItems.充能FEX水晶
import ice.content.IItems.强化合金
import ice.content.IItems.气凝胶
import ice.content.IItems.矩阵合金
import ice.content.IItems.铝
import ice.content.IItems.铱
import ice.library.struct.AttachedProperty
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.ContinuousBulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootBarrel
import mindustry.entities.units.WeaponMount
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.graphics.Trail
import mindustry.world.meta.BlockFlag
import singularity.Sgl
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.EmpMultiTrailBulletType
import singularity.world.blocks.turrets.LightLaserBulletType
import singularity.world.unit.AirSeaAmphibiousUnit
import singularity.world.unit.RelatedWeapon
import singularity.world.unit.SglWeapon
import singularity.world.unit.abilities.MirrorFieldAbility
import universecore.world.lightnings.LightningContainer

class MornstarType : AirSeaAmphibiousUnit("mornstar") {
  init {
    requirements(
      Items.silicon, 420, Items.phaseFabric, 360, Items.surgeAlloy, 320, 铝, 380, 气凝胶, 320, 充能FEX水晶, 220, 强化合金, 280, 铱, 200, 矩阵合金, 220
    )

    armor = 19f
    speed = 0.84f
    accel = 0.065f
    drag = 0.03f
    rotateSpeed = 1.8f
    riseSpeed = 0.02f
    boostMultiplier = 1.25f
    faceTarget = true
    health = 42500f
    lowAltitude = true
    hitSize = 64f
    targetFlags = BlockFlag.allLogic

    drawShields = false

    engineOffset = 0f
    engineSize = 0f

    abilities.addAll(object : MirrorFieldAbility() {
      init {
        strength = 340f
        maxShield = 12500f
        recoverSpeed = 6f
        cooldown = 2680f
        minAlbedo = 0.7f
        maxAlbedo = 1f
        rotation = false

        shieldArmor = 10f

        nearRadius = 156f

        val a: Func2<Float?, Float?, ShieldShape?> = Func2 { ofx: Float?, ofy: Float? ->
          object : ShieldShape(6, 0f, 0f, 0f, 48f) {
            init {
              movement = object : ShapeMove() {
                init {
                  x = ofx!!
                  y = ofy!!
                  rotateSpeed = 0.35f

                  childMoving = object : ShapeMove() {
                    init {
                      rotateSpeed = -0.2f
                    }
                  }
                }
              }
            }
          }
        }

        shapes.addAll(
          object : ShieldShape(8, 0f, 0f, 0f, 102f) {
            init {
              movement = object : ShapeMove() {
                init {
                  rotateSpeed = -0.1f
                }
              }
            }
          }, a.get(90f, 0f), a.get(-90f, 0f), a.get(0f, 90f), a.get(0f, -90f)
        )
      }
    })

    setEnginesMirror(object : UnitEngine() {
      init {
        x = 16f
        y = -44f
        radius = 10f
        rotation = 45f
      }
    }, object : UnitEngine() {
      init {
        x = 24f
        y = -52f
        radius = 6f
        rotation = 45f
      }
    }, object : UnitEngine() {
      init {
        x = 34f
        y = -52f
        radius = 8f
        rotation = -45f
      }
    })

    weapons.addAll(object : SglWeapon(Sgl.modName + "-mornstar_cannon") {
      init {
        recoil = 0f
        recoilTime = 120f
        cooldownTime = 120f

        reload = 90f
        rotate = true
        mirror = false

        rotateSpeed = 2.5f

        layerOffset = 1f

        x = 0f
        y = 4f
        shootY = 25f

        shoot = object : ShootBarrel() {
          init {
            barrels = floatArrayOf(
              5.75f, 0f, 0f, -5.75f, 0f, 0f
            )
            shots = 2
            shotDelay = 0f
          }
        }

        bullet = object : EmpMultiTrailBulletType() {
          init {
            trailColor = SglDrawConst.matrixNet
            hitColor = trailColor
            trailLength = 22
            trailWidth = 2f
            trailEffect = MultiEffect(
              SglFx.trailLineLong, SglFx.railShootRecoil, SglFx.movingCrystalFrag
            )
            trailRotation = true
            trailChance = 1f

            lightColor = SglDrawConst.matrixNet
            lightRadius = 120f
            lightOpacity = 0.8f

            shootEffect = MultiEffect(
              SglFx.shootRecoilWave, SglFx.shootRail
            )
            hitEffect = SglFx.lightConeHit
            despawnEffect = MultiEffect(
              SglFx.impactWaveSmall, SglFx.spreadSparkLarge, SglFx.diamondSparkLarge
            )
            smokeEffect = Fx.shootSmokeSmite

            shootSound = Sounds.shootSmite

            damage = 500f
            empDamage = 100f
            lifetime = 45f
            speed = 8f
            pierceCap = 4
            hittable = false

            fragBullet = EdgeFragBullet()
            fragOnHit = true
            fragBullets = 3
            fragRandomSpread = 115f
            fragLifeMin = 0.7f
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(SglDrawConst.matrixNet)
            SglDraw.gapTri(b.x, b.y, 18f, 28f, 16f, b.rotation())
            SglDraw.drawTransform(b.x, b.y, -6f, 0f, b.rotation()) { x: Float, y: Float, r: Float ->
              SglDraw.drawDiamond(x, y, 16f, 8f, r)
            }
          }

          override fun hit(b: Bullet) {
            super.hit(b)
            b.damage -= 125f
          }

          override fun init(b: Bullet) {
            super.init(b)
            b.data = Pools.obtain(TrailMoveLightning::class.java) { TrailMoveLightning() }
          }

          override fun despawned(b: Bullet) {
            super.despawned(b)
            val data = b.data
            if (data is TrailMoveLightning) Pools.free(data)
          }

          override fun updateTrail(b: Bullet) {
            if (!Vars.headless && trailLength > 0) {
              if (b.trail == null) {
                b.trail = Trail(trailLength)
              }
              b.trail.length = trailLength

              val data = b.data
              if (data !is TrailMoveLightning) return
              data.update()
              SglDraw.drawTransform(b.x, b.y, 0f, data.off, b.rotation()) { x: Float, y: Float, r: Float -> b.trail.update(x, y) }
            }
          }
        }

        parts.addAll(object : RegionPart("_blade") {
          init {
            under = true
            progress = PartProgress.recoil
            moveY = -3f
            heatColor = Pal.turretHeat
            heatProgress = PartProgress.heat
          }
        }, object : RegionPart("_body") {
          init {
            under = true
          }
        })
      }
    }, object : SglWeapon(Sgl.modName + "-mornstar_turret") {
      init {
        x = 26f
        y = -28f
        shootY = 0f
        recoil = 5f
        recoilTime = 60f
        reload = 4f

        rotate = true
        rotateSpeed = 8f

        customDisplay = Cons2 { b: BulletType?, t: Table? ->
          t!!.row()
          t.add(Core.bundle.get("infos.damageAttenuationWithDist")).color(Pal.accent)
        }

        bullet = object : BulletType() {
          init {
            speed = 12f
            lifetime = 30f
            damage = 180f

            lightColor = SglDrawConst.matrixNet
            lightRadius = 58f
            lightOpacity = 0.6f

            pierceCap = 1

            despawnHit = true

            shootSound = Sounds.blockExplode1Alt
            trailColor = SglDrawConst.matrixNet
            hitColor = trailColor
            hitEffect = MultiEffect(
              SglFx.spreadDiamondSmall, SglFx.movingCrystalFrag
            )
            smokeEffect = Fx.colorSpark
            shootEffect = MultiEffect(
              SglFx.railShootRecoil, SglFx.crossLightMini
            )
            trailWidth = 3f
            trailLength = 8

            trailEffect = SglFx.glowParticle
            trailChance = 0.12f
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(SglDrawConst.matrixNet)
            Tmp.v1.set(1f, 0f).setAngle(b.rotation())
            SglDraw.gapTri(b.x + Tmp.v1.x * 3 * b.fout(), b.y + Tmp.v1.y * 3 * b.fout(), 15f, 22f, 14f, b.rotation())
            SglDraw.gapTri(b.x - Tmp.v1.x * 3 * b.fout(), b.y - Tmp.v1.y * 3 * b.fout(), 12f, 18f, 10f, b.rotation())
            SglDraw.gapTri(b.x - Tmp.v1.x * 5 * b.fout(), b.y - Tmp.v1.y * 5 * b.fout(), 9f, 12f, 8f, b.rotation())
            SglDraw.drawDiamond(b.x, b.y, 18f, 6f, b.rotation())
          }

          override fun update(b: Bullet) {
            super.update(b)
            b.damage = (b.type.damage + b.type.damage * b.fout()) * 0.5f
          }
        }
      }
    }, object : RelatedWeapon(Sgl.modName + "-lightedge") {
      init {
        useAlternative = isFlying
        mirror = false
        x = 0f
        y = -25f
        shootCone = 180f

        recoil = 0f
        recoilTime = 1f

        alternate = false

        linearWarmup = false
        shootWarmupSpeed = 0.025f
        minWarmup = 0.9f

        reload = 60f

        bullet = object : ContinuousBulletType() {
          init {
            speed = 0f
            lifetime = 180f
            length = 420f

            damage = 80f

            lightColor = SglDrawConst.matrixNet
            lightRadius = 96f
            lightOpacity = 1f

            hitEffect = SglFx.railShootRecoil

            hitColor = SglDrawConst.matrixNet
            trailColor = hitColor
            trailEffect = SglFx.movingCrystalFrag
            trailInterval = 4f

            shootEffect = MultiEffect(
              SglFx.shootCrossLight, SglFx.explodeImpWaveSmall
            )
          }

          override fun init(b: Bullet) {
            super.init(b)

            Sounds.blockExplode1Alt.at(b.x, b.y, 1.25f)

            val owner = b.owner
            if (owner is Unit) {
              b.rotation(b.angleTo(owner.aimX, owner.aimY))
            }
          }

          override fun update(b: Bullet) {
            super.update(b)

            Effect.shake(4f, 4f, b.x, b.y)
            updateTrailEffects(b)
            val owner = b.owner
            if (owner is Unit) {
              b.rotation(Angles.moveToward(b.rotation(), b.angleTo(owner.aimX, owner.aimY), 8 * Time.delta))
            }
          }

          override fun applyDamage(b: Bullet) {
            Damage.collideLaser(b, length, largeHit, laserAbsorb, pierceCap)
          }

          override fun draw(b: Bullet) {
            super.draw(b)

            val realLen = b.fdata
            var lerp = Mathf.clamp(b.time / 40)
            val out = Mathf.clamp((b.type.lifetime - b.time) / 30)
            lerp *= out

            lerp = 1 - Mathf.pow(1 - lerp, 3f)

            Draw.color(SglDrawConst.matrixNet)
            Drawf.tri(b.x, b.y, 12 * lerp, realLen, b.rotation())
            Draw.color(Color.black)
            Drawf.tri(b.x, b.y, 5 * lerp, realLen * 0.8f, b.rotation())
            Draw.color(SglDrawConst.matrixNet)
            Fill.circle(b.x, b.y, 4 * out + 3 * lerp)
            SglDraw.drawDiamond(b.x, b.y, 24f, 10 * lerp, Time.time)
            SglDraw.drawDiamond(b.x, b.y, 28f, 12 * lerp, -Time.time * 1.2f)
            Lines.stroke(0.8f)
            Lines.circle(b.x, b.y, 6f)
            Draw.color(Color.black)
            Fill.circle(b.x, b.y, 4f * lerp)

            Drawf.light(
              b.x, b.y, b.x + Angles.trnsx(b.rotation(), realLen), b.y + Angles.trnsy(b.rotation(), realLen), 46 * lerp, lightColor, lightOpacity
            )

            Draw.color(SglDrawConst.matrixNet)
            SglDraw.gapTri(b.x + Angles.trnsx(Time.time, 8f, 0f), b.y + Angles.trnsy(Time.time, 8f, 0f), 8 * lerp, 12 + 14 * lerp, 8f, Time.time)
            SglDraw.gapTri(b.x + Angles.trnsx(Time.time + 180, 8f, 0f), b.y + Angles.trnsy(Time.time + 180, 8f, 0f), 8 * lerp, 12 + 14 * lerp, 8f, Time.time + 180)
            SglDraw.drawDiamond(b.x + Angles.trnsx(-Time.time * 1.2f, 12f, 0f), b.y + Angles.trnsy(-Time.time * 1.2f, 12f, 0f), 16f, 5 * lerp, -Time.time * 1.2f)
            SglDraw.drawDiamond(b.x + Angles.trnsx(-Time.time * 1.2f + 180, 12f, 0f), b.y + Angles.trnsy(-Time.time * 1.2f + 180, 12f, 0f), 16f, 5 * lerp, -Time.time * 1.2f + 180)

            val out2 = Mathf.pow(1 - out, 3f)
            Tmp.v1.set(35 + 30 * out2, 0f).setAngle(b.rotation())
            Tmp.v2.set(Tmp.v1).setLength(8 + 10 * lerp).rotate90(1)

            val len = 100 + out2 * 80
            val an = Mathf.atan2(len / 2, 8 * lerp) * Mathf.radDeg

            Drawf.tri(b.x + Tmp.v1.x + Tmp.v2.x, b.y + Tmp.v1.y + Tmp.v2.y, len, 8 * lerp, b.rotation() - 90 - an)
            Drawf.tri(b.x + Tmp.v1.x - Tmp.v2.x, b.y + Tmp.v1.y - Tmp.v2.y, len, 8 * lerp, b.rotation() + 90 + an)
          }
        }

        alternativeBullet = object : BulletType() {
          init {
            splashDamage = 380f
            splashDamageRadius = 32f

            speed = 8f
            lifetime = 360f
            rangeOverride = 360f
            homingDelay = 60f

            homingPower = 0.03f
            homingRange = 360f

            lightColor = SglDrawConst.matrixNet
            lightRadius = 75f
            lightOpacity = 0.8f

            despawnShake = 6f

            collides = false
            absorbable = false
            hittable = false

            keepVelocity = false

            hitColor = SglDrawConst.matrixNet
            trailColor = hitColor
            trailLength = 34
            trailWidth = 4.5f

            despawnEffect = MultiEffect(
              SglFx.explodeImpWave, SglFx.crossLightSmall, SglFx.diamondSparkLarge
            )

            trailEffect = SglFx.movingCrystalFrag
            trailInterval = 4f

            fragBullet = object : BulletType() {
              init {
                damage = 60f
                splashDamage = 80f
                splashDamageRadius = 24f
                speed = 4f
                hitSize = 3f
                lifetime = 120f
                despawnHit = true
                hitEffect = SglFx.diamondSpark
                hitColor = SglDrawConst.matrixNet

                collidesTiles = false

                homingRange = 240f
                homingPower = 0.035f

                trailColor = SglDrawConst.matrixNet
                trailLength = 25
                trailWidth = 3f
                trailEffect = SglFx.movingCrystalFrag
                trailInterval = 5f
              }

              override fun draw(b: Bullet) {
                drawTrail(b)
                Draw.color(hitColor)
                Fill.circle(b.x, b.y, 4f)
                Draw.color(Color.black)
                Fill.circle(b.x, b.y, 2.5f)
              }

              override fun updateHoming(b: Bullet) {
                val target: Posc? = Units.closestTarget(b.team, b.x, b.y, homingRange, Boolf { e: Unit? -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) }, Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })

                if (target == null) {
                  b.vel.lerpDelta(Vec2.ZERO, homingPower)
                } else {
                  b.vel.lerpDelta(Tmp.v1.set(target.x() - b.x, target.y() - b.y).setLength(speed * 0.5f), homingPower)
                }
              }
            }
            fragBullets = 5
            fragLifeMin = 0.7f

            intervalBullet = object : LightLaserBulletType() {
              init {
                damage = 150f
                empDamage = 20f
              }

              override fun init(b: Bullet, c: LightningContainer) {
                val target = Units.closestTarget(b.team, b.x, b.y, range, Boolf { e: Unit? -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) }, Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })

                if (target != null) {
                  b.rotation(b.angleTo(target))
                }

                super.init(b, c)
              }
            }
            bulletInterval = 15f
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(SglDrawConst.matrixNet)

            var lerp = Mathf.clamp(b.time / homingDelay)
            lerp = 1 - Mathf.pow(1 - lerp, 2f)

            Fill.circle(b.x, b.y, 4 + 2 * lerp)
            SglDraw.drawDiamond(b.x, b.y, 22f, 10 * lerp, Time.time)
            Lines.stroke(0.8f)
            Lines.circle(b.x, b.y, 6f)
            Draw.color(Color.black)
            Fill.circle(b.x, b.y, 3.75f * lerp)
            Draw.color(SglDrawConst.matrixNet)

            rand.setSeed(b.id.toLong())
            for (i in 0..6) {
              val w = rand.random(1f, 2.5f) * (if (rand.random(1f) > 0.5) 1 else -1)
              val f = rand.random(360f)
              val r = rand.random(12f, 28f)
              val size = rand.random(18f, 26f) * lerp

              val a = f + Time.time * w
              Tmp.v1.set(r, 0f).setAngle(a)

              SglDraw.drawHaloPart(b.x + Tmp.v1.x, b.y + Tmp.v1.y, size, size * 0.5f, a)
            }
          }

          override fun despawned(b: Bullet) {
            super.despawned(b)
            Sounds.shootMalign.at(b, 2f)
          }

          override fun update(b: Bullet) {
            super.update(b)

            if (b.timer(4, 18f)) {
              val target = Units.closestTarget(b.team, b.x, b.y, range, Boolf { e: Unit? -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) }, Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })

              fragBullet.create(b, b.x, b.y, if (target != null) b.angleTo(target) else Mathf.random(0, 360).toFloat())
            }
          }

          override fun updateHoming(b: Bullet) {
            if (homingPower > 0.0001f && b.time >= homingDelay) {
              val realAimX = if (b.aimX < 0) b.x else b.aimX
              val realAimY = if (b.aimY < 0) b.y else b.aimY
              val target = if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team !== b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                b.aimTile.build
              } else {
                Units.closestTarget(b.team, realAimX, realAimY, homingRange, Boolf { e: Unit? -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) }, Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })
              }

              if (target != null) {
                val dst = target.dst(b)
                val v = Mathf.lerpDelta(b.vel.len(), speed * (dst / homingRange), 0.05f)
                b.vel.setLength(v)

                val degA = b.rotation()
                var degB = b.angleTo(target)

                if (degA - degB > 180) {
                  degB += 360f
                } else if (degA - degB < -180) {
                  degB -= 360f
                }

                b.vel.setAngle(Mathf.lerpDelta(degA, degB, homingPower))
              } else {
                b.vel.lerpDelta(0f, 0f, 0.03f)
              }
            }
          }
        }
      }

      var DataWeaponMount.EPHEMERAS: Seq<Ephemera> by AttachedProperty(Seq())
      var DataWeaponMount.TIMER: Interval by AttachedProperty(Interval(3))

      override fun shoot(unit: Unit?, mount: WeaponMount?, shootX: Float, shootY: Float, rotation: Float) {
        val m = mount as DataWeaponMount
        var seq = m.EPHEMERAS
        seq.forEach { ephemera ->
          if (ephemera.alpha > 0.9f) {
            ephemera.shoot(unit, if (useAlternative.alt(unit)) alternativeBullet else bullet)
            ephemera.removed = true;
            return@forEach
          }

        }
      }

      override fun draw(unit: Unit, mount: DataWeaponMount) {
        super.draw(unit, mount)
        Draw.z(Layer.effect)
        SglDraw.drawTransform(unit.x, unit.y, mount.weapon.x, mount.weapon.y, unit.rotation - 90) { x: Float, y: Float, r: Float ->
          Draw.color(SglDrawConst.matrixNet)
          Fill.circle(x, y, 6f)
          Lines.stroke(0.8f)
          SglDraw.dashCircle(x, y, 8f, 4, 180f, Time.time)
          Lines.stroke(0.5f)
          Lines.circle(x, y, 10f)

          Draw.alpha(1f)
          SglDraw.drawDiamond(x, y, 20 + 14 * mount.warmup, 2 + 3 * mount.warmup, Time.time * 1.2f)
          SglDraw.drawDiamond(x, y, 26 + 14 * mount.warmup, 3 + 4 * mount.warmup, -Time.time * 1.2f)
        }
      }

      override fun update(unit: Unit, mount: DataWeaponMount) {
        Tmp.v1.set(mount.weapon.x, mount.weapon.y).rotate(unit.rotation - 90)
        val mx = unit.x + Tmp.v1.x
        val my = unit.y + Tmp.v1.y

        var seq = mount.EPHEMERAS

        if (seq.size < 4) {
          if (mount.TIMER.get(0, 240f)) {
            mount.totalShots++

            var ephemera = Pools.obtain(Ephemera::class.java) { Ephemera() }
            ephemera.x = mx
            ephemera.y = my
            ephemera.move = Mathf.random(0.02f, 0.04f)
            ephemera.angelOff = Mathf.random(15f, 45f) * if (mount.totalShots % 2 == 0) 1f else -1f
            ephemera.bestDst = Mathf.random(18f, 36f)
            ephemera.vel.rnd(Mathf.random(0.6f, 2f))

            seq.add(ephemera)
          }
        }

        if (seq.isEmpty) {
          mount.reload = mount.weapon.reload
        }

        val iterator = seq.iterator()
        while (iterator.hasNext()) {
          val ephemera: Ephemera = iterator.next()!!
          ephemera.alpha = Mathf.lerpDelta(ephemera.alpha, (if (ephemera.removed) 0 else 1).toFloat(), 0.015f)
          ephemera.trail.update(ephemera.x, ephemera.y)
          if (ephemera.removed) {
            if (ephemera.alpha <= 0.05f) {
              iterator.remove()
              Pools.free(ephemera)
            }
            continue
          }

          ephemera.x += ephemera.vel.x * Time.delta
          ephemera.y += ephemera.vel.y * Time.delta

          val dst = (ephemera.bestDst + ephemera.bestDst * mount.warmup) - Mathf.dst(ephemera.x - mx, ephemera.y - my)
          val speed = dst / 30

          ephemera.vel.lerpDelta(Tmp.v1.set(ephemera.x - unit.x, ephemera.y - unit.y).setLength2(1f).scl(speed).rotate(ephemera.angelOff), 0.15f)
          Tmp.v1.set(ephemera.move + ephemera.move * mount.warmup, 0f).rotate(Time.time)
          ephemera.vel.add(Tmp.v1)
        }
      }
    })
  }

  class Ephemera : Poolable {
    var x: Float = 0f
    var y: Float = 0f
    var angelOff: Float = 0f
    var move: Float = 0f
    var bestDst: Float = 0f
    var alpha: Float = 0f
    var removed: Boolean = false
    val vel: Vec2 = Vec2()
    val trail: Trail = Trail(60)

    override fun reset() {
      y = 0f
      x = y
      alpha = 0f
      removed = false
      vel.setZero()
      trail.clear()
      bestDst = 0f
      move = 0f
      angelOff = 0f
    }

    fun shoot(u: Unit?, bullet: BulletType) {
      val b = bullet.create(u, x, y, vel.angle())
      if (b.type.speed > 0.01f) b.vel.set(vel)
      b.set(x, y)
      bullet.shootEffect.at(b.x, b.y, vel.angle(), b.type.hitColor)
      bullet.smokeEffect.at(b.x, b.y, vel.angle(), b.type.hitColor)
    }
  }

  companion object {
    private val rand = Rand()
  }
}