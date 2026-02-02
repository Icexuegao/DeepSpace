package singularity.world.unit.types

import arc.func.Boolf
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.content.IItems.FEX水晶
import ice.content.IItems.充能FEX水晶
import ice.content.IItems.强化合金
import ice.content.IItems.气凝胶
import ice.content.IItems.矩阵合金
import ice.content.IItems.简并态中子聚合物
import ice.content.IItems.铝
import ice.content.IItems.铱锭
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Damage
import mindustry.entities.Mover
import mindustry.entities.Units
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.ContinuousLaserBulletType
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.part.HaloPart
import mindustry.entities.pattern.ShootPattern
import mindustry.entities.units.WeaponMount
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Trail
import mindustry.type.Weapon
import mindustry.world.meta.BlockFlag
import singularity.Sgl
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.EmpBulletType
import singularity.world.blocks.turrets.LightLaserBulletType
import singularity.world.draw.part.CustomPart
import singularity.world.unit.AirSeaAmphibiousUnit
import singularity.world.unit.RelatedWeapon
import singularity.world.unit.SglWeapon
import singularity.world.unit.abilities.MirrorArmorAbility
import kotlin.math.max
import kotlin.math.min

class AuroraType : AirSeaAmphibiousUnit("aurora") {
  init {
    requirements(
      Items.silicon, 360, Items.phaseFabric, 380, Items.surgeAlloy, 390, 铝, 400, 气凝胶, 430, FEX水晶, 280, 充能FEX水晶, 280, 强化合金, 340, 铱锭, 320, 矩阵合金, 380, 简并态中子聚合物, 200
    )

    armor = 10f
    speed = 0.65f
    accel = 0.06f
    drag = 0.04f
    rotateSpeed = 1.25f
    riseSpeed = 0.02f
    boostMultiplier = 1.2f
    faceTarget = true
    health = 52500f
    lowAltitude = true
    hitSize = 75f
    targetFlags = BlockFlag.allLogic
    drawShields = false

    engineOffset = 50f
    engineSize = 16f

    abilities.addAll(object : MirrorArmorAbility() {
      init {
        strength = 380f
        maxShield = 9500f
        recoverSpeed = 4f
        cooldown = 6050f
        minAlbedo = 0.6f
        maxAlbedo = 0.9f

        shieldArmor = 12f
      }
    })

    setEnginesMirror(object : UnitEngine() {
      init {
        x = 38f
        y = -12f
        radius = 8f
        rotation = -45f
      }
    }, object : UnitEngine() {
      init {
        x = 40f
        y = -54f
        radius = 10f
        rotation = -45f
      }
    })

    weapons.addAll(object : SglWeapon(Sgl.modName + "-aurora_lightcone") {
      init {
        shake = 5f
        shootSound = Sounds.blockExplode1
        x = 29f
        y = -30f
        shootY = 8f
        rotate = true
        rotateSpeed = 3f
        recoil = 6f
        recoilTime = 60f
        cooldownTime = 60f
        reload = 60f
        shadow = 45f
        linearWarmup = false
        shootWarmupSpeed = 0.03f
        minWarmup = 0.8f

        layerOffset = 1f

        parts.addAll(object : CustomPart() {
          init {
            x = 0f
            y = -16f
            layer = Layer.effect
            progress = PartProgress.warmup

            draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
              Lines.stroke(p * 1.6f, SglDrawConst.matrixNet)
              Lines.circle(x, y, 3 * p)

              Tmp.v1.set(0f, 9 * p).setAngle(r + 180 + 40 * p)
              Tmp.v2.set(0f, 9 * p).setAngle(r + 180 - 40 * p)

              SglDraw.drawDiamond(x + Tmp.v1.x, y + Tmp.v1.y, 9 * p, 7f * p, Tmp.v1.angle())
              SglDraw.drawDiamond(x + Tmp.v2.x, y + Tmp.v2.y, 9 * p, 7f * p, Tmp.v2.angle())

              Tmp.v1.set(0f, 9 * p).setAngle(r + 180 + 100 * p)
              Tmp.v2.set(0f, 9 * p).setAngle(r + 180 - 100 * p)

              SglDraw.drawDiamond(x + Tmp.v1.x, y + Tmp.v1.y, 9 * p, 7f * p, Tmp.v1.angle())
              SglDraw.drawDiamond(x + Tmp.v2.x, y + Tmp.v2.y, 9 * p, 7f * p, Tmp.v2.angle())
            }
          }
        }, object : CustomPart() {
          init {
            x = 0f
            y = -16f
            layer = Layer.effect
            progress = PartProgress.warmup.delay(0.7f)

            draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
              Tmp.v1.set(0f, 14 * p).setAngle(r + 195)
              Tmp.v2.set(0f, 14 * p).setAngle(r + 165)

              SglDraw.gapTri(x + Tmp.v1.x, y + Tmp.v1.y, 3 * p, 10f, -3f, Tmp.v1.angle())
              SglDraw.gapTri(x + Tmp.v2.x, y + Tmp.v2.y, 3 * p, 10f, -3f, Tmp.v2.angle())

              Tmp.v1.set(0f, 14 * p).setAngle(r + 250)
              Tmp.v2.set(0f, 14 * p).setAngle(r + 110)

              SglDraw.gapTri(x + Tmp.v1.x, y + Tmp.v1.y, 4 * p, 12f, -4f, Tmp.v1.angle())
              SglDraw.gapTri(x + Tmp.v2.x, y + Tmp.v2.y, 4 * p, 12f, -4f, Tmp.v2.angle())

              Tmp.v1.set(0f, 14 * p).setAngle(r + 305)
              Tmp.v2.set(0f, 14 * p).setAngle(r + 55)

              SglDraw.gapTri(x + Tmp.v1.x, y + Tmp.v1.y, 3 * p, 10f, -3f, Tmp.v1.angle())
              SglDraw.gapTri(x + Tmp.v2.x, y + Tmp.v2.y, 3 * p, 10f, -3f, Tmp.v2.angle())
            }
          }
        })

        bullet = object : EmpBulletType() {
          init {
            trailLength = 36
            trailWidth = 2.2f
            trailColor = SglDrawConst.matrixNet
            trailRotation = true
            trailChance = 1f
            hitSize = 8f
            speed = 12f
            lifetime = 40f
            damage = 620f
            range = 480f

            empDamage = 26f

            pierce = true
            hittable = false
            reflectable = false
            pierceArmor = true
            pierceBuilding = true
            absorbable = false

            trailEffect = MultiEffect(
              SglFx.lightConeTrail, SglFx.lightCone, SglFx.trailLineLong
            )
            hitEffect = SglFx.lightConeHit
            hitColor = SglDrawConst.matrixNet

            intervalBullet = object : BulletType() {
              init {
                damage = 132f
                speed = 8f
                hitSize = 3f
                keepVelocity = false
                lifetime = 45f
                hitColor = SglDrawConst.matrixNet
                hitEffect = SglFx.circleSparkMini

                despawnHit = true

                trailColor = SglDrawConst.matrixNet
                trailWidth = 3f
                trailLength = 23
              }

              override fun init(b: Bullet?) {
                super.init(b)
                b?.trail(Trail(trailLength))
              }
              override fun draw(b: Bullet) {
                super.draw(b)
                Draw.color(hitColor)
                SglDraw.drawDiamond(b.x, b.y, 12 * b.fout(), 6 * b.fout(), b.rotation())
              }

              override fun drawTrail(b: Bullet) {
                val z = Draw.z()
                Draw.z(z - 0.0001f)
                b.trail.draw(trailColor, trailWidth * b.fout())
                Draw.z(z)
              }

              override fun removed(b: Bullet) {
                if (trailLength > 0 && b.trail != null && b.trail.size() > 0) {
                  Fx.trailFade.at(b.x, b.y, trailWidth * b.fout(), trailColor, b.trail.copy())
                }
              }
            }
            bulletInterval = 4f
          }

          override fun init(b: Bullet) {
            super.init(b)
            val l = Pools.obtain(TrailMoveLightning::class.java) {TrailMoveLightning()}
            l.range = 8f
            l.maxOff = 7.5f
            l.chance = 0.4f
            b.data = l
          }

          override fun updateBulletInterval(b: Bullet) {
            if (b.timer.get(2, bulletInterval)) {
              val bull = intervalBullet.create(b, b.x, b.y, b.rotation())
              bull.vel.scl(b.fout())
              rand.setSeed(bull.id.toLong())
              val scl = rand.random(3.65f, 5.25f) * (if (rand.random(1f) > 0.5f) 1 else -1)
              val mag = rand.random(2.8f, 5.6f) * b.fout()
              bull.mover = Mover { e: Bullet? -> e!!.moveRelative(0f, Mathf.cos(e.time, scl, mag)) }
            }
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

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(SglDrawConst.matrixNet)
            Drawf.tri(b.x, b.y, 8f, 18f, b.rotation())
            for (i in Mathf.signs) {
              Drawf.tri(b.x, b.y, 8f, 26f, b.rotation() + 156f * i)
            }
          }

          override fun update(b: Bullet) {
            super.update(b)
            Damage.damage(b.team, b.x, b.y, hitSize, damage * Time.delta)
          }
        }
      }
    }, object : SglWeapon(Sgl.modName + "-aurora_turret") {
      init {
        shake = 4f
        shootSound = Sounds.shootLaser
        x = 22f
        y = 20f
        shootY = 6f
        rotate = true
        rotateSpeed = 6f
        recoil = 4f
        recoilTime = 20f
        cooldownTime = 60f
        reload = 20f
        shadow = 25f

        bullet = object : LightLaserBulletType() {
          init {
            damage = 425f
            empDamage = 96f
            lifetime = 24f
            width = 16f
            length = 480f
            shootEffect = MultiEffect(
              SglFx.crossLightSmall, SglFx.shootRecoilWave
            )
            colors = arrayOf<Color?>(SglDrawConst.matrixNetDark, SglDrawConst.matrixNet, Color.white)
            hitColor = colors[0]

            generator.maxSpread = 11.25f
            generator.minInterval = 6f
            generator.maxInterval = 15f

            lightningMinWidth = 2.2f
            lightningMaxWidth = 3.8f
          }
        }
      }
    }, object : RelatedWeapon(Sgl.modName + "-lightedge") {
      init {
        x = 0f
        y = -22f
        shootY = 0f
        reload = 600f
        mirror = false
        rotateSpeed = 0f
        shootCone = 0.5f
        rotate = true
        shootSound = Sounds.blockExplode1Alt
        ejectEffect = SglFx.railShootRecoil
        recoilTime = 30f
        shake = 4f

        minWarmup = 0.9f
        shootWarmupSpeed = 0.03f

        shoot.firstShotDelay = 80f

        alternativeShoot = object : ShootPattern() {
          override fun shoot(totalShots: Int, handler: BulletHandler) {
            for (i in 0..<shots) {
              handler.shoot(0f, 0f, Mathf.random(0f, 360f), firstShotDelay + i * shotDelay)
            }
          }
        }
        alternativeShoot.shots = 12
        alternativeShoot.shotDelay = 3f
        alternativeShoot.firstShotDelay = 0f
        useAlternative = isFlying
        parentizeEffects = true

        parts.addAll(object : HaloPart() {
          init {
            progress = PartProgress.warmup
            color = SglDrawConst.matrixNet
            layer = Layer.effect
            haloRotateSpeed = -1f
            shapes = 2
            triLength = 0f
            triLengthTo = 26f
            haloRadius = 0f
            haloRadiusTo = 14f
            tri = true
            radius = 6f
          }
        }, object : HaloPart() {
          init {
            progress = PartProgress.warmup
            color = SglDrawConst.matrixNet
            layer = Layer.effect
            haloRotateSpeed = -1f
            shapes = 2
            triLength = 0f
            triLengthTo = 8f
            haloRadius = 0f
            haloRadiusTo = 14f
            tri = true
            radius = 6f
            shapeRotation = 180f
          }
        }, object : HaloPart() {
          init {
            progress = PartProgress.warmup
            color = SglDrawConst.matrixNet
            layer = Layer.effect
            haloRotateSpeed = 1f
            shapes = 2
            triLength = 0f
            triLengthTo = 12f
            haloRadius = 8f
            tri = true
            radius = 8f
          }
        }, object : HaloPart() {
          init {
            progress = PartProgress.warmup
            color = SglDrawConst.matrixNet
            layer = Layer.effect
            haloRotateSpeed = 1f
            shapes = 2
            triLength = 0f
            triLengthTo = 8f
            haloRadius = 8f
            tri = true
            radius = 8f
            shapeRotation = 180f
          }
        }, object : CustomPart() {
          init {
            layer = Layer.effect
            progress = PartProgress.warmup

            draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
              Draw.color(SglDrawConst.matrixNet)
              SglDraw.gapTri(x + Angles.trnsx(r + Time.time, 16f, 0f), y + Angles.trnsy(r + Time.time, 16f, 0f), 12 * p, 42f, 12f, r + Time.time)
              SglDraw.gapTri(x + Angles.trnsx(r + Time.time + 180, 16f, 0f), y + Angles.trnsy(r + Time.time + 180, 16f, 0f), 12 * p, 42f, 12f, r + Time.time + 180)
            }
          }
        })

        val s: Weapon = this
        bullet = object : ContinuousLaserBulletType() {
          init {
            damage = 210f
            lifetime = 180f
            fadeTime = 30f
            length = 720f
            width = 6f
            hitColor = SglDrawConst.matrixNet
            shootEffect = SglFx.explodeImpWave
            chargeEffect = SglFx.auroraCoreCharging
            chargeSound = Sounds.shootLaser
            fragBullets = 2
            fragSpread = 10f
            fragOnHit = true
            fragRandomSpread = 60f
            fragLifeMin = 0.7f
            shake = 5f
            incendAmount = 0
            incendChance = 0f

            drawSize = 620f
            pointyScaling = 0.7f
            oscMag = 0.85f
            oscScl = 1.1f
            frontLength = 70f
            lightColor = SglDrawConst.matrixNet
            colors = arrayOf<Color?>(
              Color.valueOf("8FFFF0").a(0.6f), Color.valueOf("8FFFF0").a(0.85f), Color.valueOf("B6FFF7"), Color.valueOf("D3FDFF")
            )
          }

          override fun update(b: Bullet) {
            super.update(b)
            val owner = b.owner
            if (owner is Unit) {
              owner.vel.lerp(0f, 0f, 0.1f)

              val bulletX: Float = owner.x + Angles.trnsx(owner.rotation - 90, x + shootX, y + shootY)
              val bulletY: Float = owner.y + Angles.trnsy(owner.rotation - 90, x + shootX, y + shootY)
              val angle: Float = owner.rotation

              b.rotation(angle)
              b.set(bulletX, bulletY)

              for (mount in owner.mounts) {
                mount.reload = mount.weapon.reload
                if (mount.weapon === s) {
                  mount.recoil = 1f
                }
              }

              if (ejectEffect != null) ejectEffect.at(bulletX, bulletY, angle, b.type.hitColor)
            }
          }

          override fun draw(b: Bullet) {
            val realLength = Damage.findLaserLength(b, length)
            val fout = Mathf.clamp(if (b.time > b.lifetime - fadeTime) 1f - (b.time - (lifetime - fadeTime)) / fadeTime else 1f)
            val baseLen = realLength * fout
            val rot = b.rotation()

            for (i in colors.indices) {
              Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)))

              val colorFin = i / (colors.size - 1).toFloat()
              val baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin)
              val stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * baseStroke
              val ellipseLenScl = Mathf.lerp(1 - i / (colors.size).toFloat(), 1f, pointyScaling)

              Lines.stroke(stroke)
              Lines.lineAngle(b.x, b.y, rot, baseLen - frontLength, false)

              //back ellipse
              Drawf.flameFront(b.x, b.y, divisions, rot + 180f, backLength, stroke / 2f)

              //front ellipse
              Tmp.v1.trnsExact(rot, baseLen - frontLength)
              Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, divisions, rot, frontLength * ellipseLenScl, stroke / 2f)
            }

            Tmp.v1.trns(b.rotation(), baseLen * 1.1f)

            Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f)

            Draw.color(SglDrawConst.matrixNet)

            val step = 1 / 45f
            Tmp.v1.set(length, 0f).setAngle(b.rotation())
            val dx = Tmp.v1.x
            val dy = Tmp.v1.y
            for (i in 0..44) {
              if (i * step * length > realLength) break

              val lerp = Mathf.clamp(b.time / (fadeTime * step * i)) * Mathf.sin(Time.time / 2 - i * step * Mathf.pi * 6)
              Draw.alpha(0.4f + 0.6f * lerp)
              SglDraw.drawDiamond(b.x + dx * step * i, b.y + dy * step * i, 8 * fout, 16 + 20 * lerp + 80 * (1 - fout), b.rotation())
            }
            Draw.reset()
          }
        }

        alternativeBullet = object : BulletType() {
          init {
            pierceArmor = true
            hitShake = 6f
            damage = 280f
            splashDamage = 420f
            splashDamageRadius = 32f
            absorbable = false
            hittable = true
            speed = 10f
            lifetime = 120f
            homingRange = 450f
            homingPower = 0.25f
            hitColor = SglDrawConst.matrixNet
            hitEffect = MultiEffect(
              SglFx.explodeImpWave, SglFx.diamondSpark
            )

            trailLength = 32
            trailWidth = 3f
            trailColor = SglDrawConst.matrixNet
            trailEffect = MultiEffect(
              SglFx.movingCrystalFrag, Fx.colorSparkBig
            )
            trailRotation = true
            trailInterval = 4f

            despawnHit = true

            homingDelay = 30f

            fragBullet = object : BulletType() {
              init {
                collides = false
                absorbable = false

                splashDamage = 260f
                splashDamageRadius = 24f
                speed = 1.2f
                lifetime = 64f

                hitShake = 4f
                hitSize = 3f

                despawnHit = true
                hitEffect = MultiEffect(
                  SglFx.explodeImpWaveSmall, SglFx.diamondSpark
                )
                hitColor = SglDrawConst.matrixNet

                trailColor = SglDrawConst.matrixNet
                trailEffect = SglFx.glowParticle
                trailRotation = true
                trailInterval = 15f

                fragBullet = object : LightningBulletType() {
                  init {
                    lightningLength = 14
                    lightningLengthRand = 4
                    damage = 24f
                  }
                }
                fragBullets = 1
              }

              override fun draw(b: Bullet) {
                Draw.color(hitColor)
                val fout = b.fout(Interp.pow3Out)
                Fill.circle(b.x, b.y, 5f * fout)
                Draw.color(Color.black)
                Fill.circle(b.x, b.y, 2.6f * fout)
              }
            }
            fragBullets = 3
            fragLifeMin = 0.7f
          }

          override fun updateHoming(b: Bullet) {
            if (Mathf.chanceDelta((0.3f * b.vel.len() / speed).toDouble())) {
              Fx.colorSpark.at(b.x, b.y, b.rotation(), b.type.hitColor)
            }

            if (b.time < homingDelay) {
              b.vel.lerpDelta(0f, 0f, 0.06f)
            } else if (homingPower > 0.0001f && b.time >= homingDelay) {
              val realAimX = if (b.aimX < 0) b.x else b.aimX
              val realAimY = if (b.aimY < 0) b.y else b.aimY
              val target = if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team !== b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                b.aimTile.build
              } else {
                Units.closestTarget(b.team, realAimX, realAimY, homingRange, Boolf { e: Unit? -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) }, Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })
              }

              if (target != null) {
                val v = Mathf.lerpDelta(b.vel.len(), speed, 0.08f)
                b.vel.setLength(v)
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * (v / speed) * Time.delta * 50f))
              } else {
                b.vel.lerpDelta(0f, 0f, 0.06f)
              }
            }
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(SglDrawConst.matrixNet)
            Drawf.tri(b.x, b.y, 8f, 24f, b.rotation())
            Drawf.tri(b.x, b.y, 8f, 10f, b.rotation() + 180)

            Tmp.v1.set(1f, 0f).setAngle(b.rotation())
            SglDraw.gapTri(b.x + Tmp.v1.x * 8 * b.fout(), b.y + Tmp.v1.y * 3 * b.fout(), 16f, 24f, 18f, b.rotation())
            SglDraw.gapTri(b.x + Tmp.v1.x * 2 * b.fout(), b.y - Tmp.v1.y * 3 * b.fout(), 12f, 20f, 16f, b.rotation())
            SglDraw.gapTri(b.x - Tmp.v1.x * 2 * b.fout(), b.y - Tmp.v1.y * 5 * b.fout(), 8f, 14f, 10f, b.rotation())
          }

          override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
            if (entity is Unit && entity.shield > 0) {
              val damageShield = min(max(entity.shield, 0f), b.type.damage * 1.25f)
              entity.shield -= damageShield
              Fx.colorSparkBig.at(b.x, b.y, b.rotation(), SglDrawConst.matrixNet)
            }
            super.hitEntity(b, entity, health)
          }
        }
      }

      override fun draw(unit: Unit, mount: WeaponMount) {
        super.draw(unit, mount)
        Tmp.v1.set(0f, y).rotate(unit.rotation - 90)
        val dx = unit.x + Tmp.v1.x
        val dy = unit.y + Tmp.v1.y

        Lines.stroke(1.6f * (if (mount.charging) 1f else mount.warmup * (1 - mount.recoil)), SglDrawConst.matrixNet)
        Draw.alpha(0.7f * mount.warmup * (1 - unit.elevation))
        val disX = Angles.trnsx(unit.rotation - 90, 3 * mount.warmup, 0f)
        val disY = Angles.trnsy(unit.rotation - 90, 3 * mount.warmup, 0f)

        Tmp.v1.set(0f, 720f).rotate(unit.rotation - 90)
        val angle = Tmp.v1.angle()
        val distX = Tmp.v1.x
        val distY = Tmp.v1.y

        Lines.line(dx + disX, dy + disY, dx + distX + disX, dy + distY + disY)
        Lines.line(dx - disX, dy - disY, dx + distX - disX, dy + distY - disY)
        val step = 1 / 30f
        val rel = (1 - mount.reload / reload) * mount.warmup * (1 - unit.elevation)
        var i = 0.001f
        while (i <= 1) {
          Draw.alpha(if (rel > i) 1f else Mathf.maxZero(rel - (i - step)) / step)
          Drawf.tri(dx + distX * i, dy + distY * i, 3f, 2.598f, angle)
          i += step
        }

        Draw.reset()

        Draw.color(SglDrawConst.matrixNet)
        val relLerp = if (mount.charging) 1f else 1 - mount.reload / reload
        val edge = max(relLerp, mount.recoil * 1.25f)
        Lines.stroke(0.8f * edge)
        Draw.z(Layer.bullet)
        SglDraw.dashCircle(dx, dy, 10f, 4, 240f, Time.time * 0.8f)
        Lines.stroke(edge)
        Lines.circle(dx, dy, 8f)
        Fill.circle(dx, dy, 5 * relLerp)

        SglDraw.drawDiamond(dx, dy, 6 + 12 * relLerp, 3 * relLerp, Time.time)
        SglDraw.drawDiamond(dx, dy, 5 + 10 * relLerp, 2.5f * relLerp, -Time.time * 0.87f)
      }

      override fun update(unit: Unit, mount: WeaponMount) {
        val axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y)
        val axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y)

        if (mount.charging) mount.reload = mount.weapon.reload

        if (unit.isFlying) {
          mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation
          mount.rotation = mount.targetRotation
        } else {
          mount.rotation = 0f
        }

        if (mount.warmup < 0.01f) {
          mount.reload = max(mount.reload - 0.2f * Time.delta, 0f)
        }

        super.update(unit, mount)
      }
    })
  }

  override fun init() {
    super.init()

    omniMovement = true
  }

  companion object {
    private val rand = Rand()
  }
}