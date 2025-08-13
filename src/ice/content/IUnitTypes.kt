package ice.content

import arc.func.Func
import arc.func.Prov
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Rect
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import ice.Ice
import ice.ai.CircleAi
import ice.library.IFiles
import ice.library.entities.IceRegister
import ice.library.entities.ability.RotatorAbility
import ice.library.entities.bullet.AngleBulletType
import ice.library.entities.bullet.ChainBulletType
import ice.library.entities.bullet.RandomDamageBulletType
import ice.library.scene.tex.Colors
import ice.library.struct.addP
import ice.library.baseContent.BaseContentSeq
import ice.library.baseContent.unit.IceUnitEngine
import ice.library.baseContent.unit.ability.BarAbility
import ice.library.baseContent.unit.type.IceTankUnitType
import ice.library.baseContent.unit.type.IceUnitType
import ice.library.baseContent.unit.unitEntity.ClusterLobesUnit
import ice.library.baseContent.unit.unitEntity.WitchUnit
import ice.library.meta.IceEffects
import ice.library.util.toStringi
import ice.music.IceSounds
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.content.UnitTypes
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.bullet.*
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.FlarePart
import mindustry.entities.part.HaloPart
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Weapon
import mindustry.type.weapons.PointDefenseBulletWeapon
import mindustry.ui.Bar
import kotlin.math.min
import kotlin.random.Random

object IUnitTypes {
    fun load() {
        Vars.content.units().forEach {
            if (it.minfo.mod == Ice.ice) BaseContentSeq.units.add(it)
        }
    }

    val 路西法 = object : IceUnitType("lucifer") {
        override fun update(unit: Unit) {
            super.update(unit)
            val d = 16f
            val intersect = Groups.bullet.intersect(unit.x - d, unit.y - d, d * 2f, d * 2f)
            intersect.forEach {
                if (it.team == unit.team()) return
                val angle = Angles.angle(unit.x, unit.y, it.x, it.y)
                IceEffects.shieldWave.at(it.x, it.y, angle, unit)
                it.type.hit(it, it.x, it.y)
                it.remove()
            }
        }
    }.apply {
        armor = 1f
        speed = 3f
        flying = true
        health = 150f
        hitSize = 16f
        engineSize = 3f
        rotateSpeed = 7f
        itemCapacity = 5
        lowAltitude = true
        faceTarget = false
        engineOffset = 8f
        circleTarget = true
        engineColor = Color.valueOf("c45f5f")
        controller = Func { CircleAi() }
        constructor = Prov(UnitEntityLegacyAlpha::create)
        weapons.add(IceWeapon("-weapon").apply {
            mirror = true
            rotate = true
            predictTarget = false
            rotateSpeed = 9f
            reload = 15f
            bullet = BasicBulletType(5f, 14f).apply {
                shootY += 1
                lifetime = 60f
                homingPower = 0.05f
                homingRange = 50f
                shootEffect = IceEffects.squareAngle()
            }
            bullet = RandomDamageBulletType {
                IceEffects.rand.random(1f, 10f)
            }.apply {
                speed = 3f
            }
        })
    }
    val 仆从 = IceUnitType("footman").apply {
        constructor = Prov(UnitEntityLegacyAlpha::create)
        health = 2000f
        flying = true
        forceMultiTarget = true
        hitSize = 30f
        aiController = UnitTypes.flare.aiController
        rotateSpeed = 5.2f
        speed = 3.2f
        engineSize = 6f
        engineOffset = 19f
        engineColor = Colors.b4

        weapons.add(Weapon().apply {
            x = -6f
            y = 10f
            bullet = BulletType(6.7f, 17f).apply {
                inaccuracy = 32f
                ammoMultiplier = 3f
                hitSize = 7f
                lifetime = 18f
                pierce = true
                pierceBuilding = true
                statusDuration = 60f * 10
                shootEffect = IceEffects.changeFlame(lifetime * speed)
                hitEffect = Fx.hitFlameSmall
                despawnEffect = Fx.none
                status = IStatus.圣火
                shootSound = Sounds.fire
                keepVelocity = false
                hittable = false
            }
        }, Weapon().apply {
            x = -2f
            y = 8f
            bullet = BulletType(6.7f, 17f).apply {
                inaccuracy = 32f
                pierceBuilding = true
                ammoMultiplier = 3f
                hitSize = 7f
                lifetime = 18f
                pierce = true
                shootSound = Sounds.fire
                statusDuration = 60f * 10
                shootEffect = IceEffects.changeFlame(lifetime * speed)
                hitEffect = Fx.hitFlameSmall
                despawnEffect = Fx.none
                status = StatusEffects.burning
                keepVelocity = false
                hittable = false
            }
        }, IceWeapon("wea1").apply {
            controllable = false
            autoTarget = true
            rotate = true
            x = 0f
            y = -16f
            mirror = false
            top = true
            bullet = object : BasicBulletType(2f, 2f, "ice-sphalerite") {
                override fun draw(b: Bullet?) {
                    Draw.z(Layer.bullet + 16)
                    super.draw(b)
                }

                override fun drawLight(b: Bullet?) {
                    Draw.z(Layer.bullet + 16)
                    super.drawLight(b)
                }
            }.apply {
                layer = 91f
            }
            ignoreRotation = true
        }, PointDefenseBulletWeapon().apply {
            range = 10 * 8f
        })
    }
    val 传教者 = IceUnitType("missionary").apply {
        speed = 0.9f
        flying = true
        hitSize = 90f
        health = 40000f
        targetAir = true
        faceTarget = false
        lowAltitude = true
        rotateSpeed = 0.6f
        targetGround = true
        forceMultiTarget = true
        constructor = Prov(UnitEntityLegacyAlpha::create)
        engines.add(IceUnitEngine(30f, -65f, 8f, -90f, 6f))
        engines.add(IceUnitEngine(0f, -80f, 8f, -90f))
        engines.add(IceUnitEngine(-30f, -65f, 8f, -90f, 6f))
        weapons.addP {
            IceWeapon("-weapon1").apply {
                x = -34.75f
                y = -20.75f
                top = true
                rotate = true
                mirror = true
                shootY = 8f
                reload = 30f
                rotateSpeed = 10f
                shootSound = Sounds.shootSmite
                shoot = ShootPattern().apply {
                    shotDelay = 30f
                    shots = 3
                    shotDelay = 10f
                }
                bullet = object : BasicBulletType(7f, 250f) {
                    init {
                        sprite = "${Ice.name}-largeOrb"
                        width = 17f
                        height = 21f
                        hitSize = 8f
                        shootEffect = MultiEffect(Fx.shootTitan, Fx.colorSparkBig, object : WaveEffect() {
                            init {
                                colorTo = Pal.accent
                                colorFrom = colorTo
                                lifetime = 12f
                                sizeTo = 20f
                                strokeFrom = 3f
                                strokeTo = 0.3f
                            }
                        })
                        smokeEffect = Fx.shootSmokeSmite
                        ammoMultiplier = 1f
                        pierceCap = 4
                        pierce = true
                        pierceBuilding = true
                        trailColor = Pal.accent
                        backColor = trailColor
                        hitColor = backColor
                        frontColor = Color.white
                        trailWidth = 2.8f
                        trailLength = 9
                        hitEffect = Fx.hitBulletColor
                        buildingDamageMultiplier = 0.3f

                        despawnEffect = MultiEffect(Fx.hitBulletColor, object : WaveEffect() {
                            init {
                                sizeTo = 30f
                                colorTo = Pal.accent
                                colorFrom = colorTo
                                lifetime = 12f
                            }
                        })

                        trailRotation = true
                        trailEffect = Fx.disperseTrail
                        trailInterval = 3f

                        bulletInterval = 3f
                        intervalBullet = object : LightningBulletType() {
                            init {
                                damage = 30f
                                collidesAir = false
                                lightningColor = Pal.accent
                                lightningLength = 5
                                lightningLengthRand = 10
                                buildingDamageMultiplier = 0.5f
                                lightningType = object : BulletType(0.0001f, 0f) {
                                    init {
                                        lifetime = Fx.lightning.lifetime
                                        hitEffect = Fx.hitLancer
                                        despawnEffect = Fx.none
                                        status = StatusEffects.shocked
                                        statusDuration = 10f
                                        hittable = false
                                        lightColor = Color.white
                                        buildingDamageMultiplier = 0.25f
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        weapons.addP {
            IceWeapon("-weapon2").apply {
                x = -20.5f
                y = 4f
                mirror = true
                rotate = true
                top = true
                reload = 60f
                shootSound = Sounds.malignShoot
                shoot = ShootPattern()

                bullet = object : BombBulletType(500f, 64f, "${Ice.name}-ad") {
                    var i = 0f
                    override fun update(b: Bullet) {
                        super.update(b)
                        i += b.time()
                    }

                    override fun draw(b: Bullet) {
                        super.draw(b)
                        Draw.color(Pal.accent)

                        Lines.stroke(1 - Interp.pow3Out.apply(b.fin()) * 3)

                        Lines.poly(b.x, b.y, 3, Interp.pow3Out.apply(1 - b.fin()) * 24, i)
                    }
                }.apply {
                    width = 10f
                    height = 10f
                    speed = 12f
                    lifetime = 120f
                    despawnEffect = WaveEffect().apply {
                        colorTo = Pal.accent
                        colorFrom = Pal.accent
                        sizeFrom = 0f
                        sizeTo = 8 * 5f
                        lifetime = 30f
                    }
                    shootEffect = MultiEffect(Fx.shootTitan, ParticleEffect().apply {
                        lifetime = 20f
                        colorFrom = Pal.accent
                        sizeFrom = 2f
                        sizeTo = 8f
                        length = 30f
                        cone = 40f
                        line = true
                    })

                    intervalSpread = 300f
                    bulletInterval = 10f
                    intervalBullet = BasicBulletType(2f, 30f, "mine-bullet").apply {
                        pierce = true
                        pierceBuilding = true
                        status = IStatus.破甲I
                        trailChance = 0.2f
                        trailEffect = WaveEffect().apply {
                            lifetime = 10f
                            sizeTo = 24f
                            sides = 3
                            colorFrom = Pal.accent
                        }
                    }
                }
            }
        }
        weapons.addP {
            IceWeapon("-weapon3").apply {
                x = -16f
                rotate = true
                y = 29f
                mirror = true
                top = true
                reload = 6f
                shoot = ShootPattern().apply {
                    shotDelay = 15f
                }
                bullet = BasicBulletType(8f, 23f).apply {
                    trailChance = 0.25f
                    trailLength = 12
                    trailWidth = 3.2f
                    trailColor = Pal.accent
                    hitEffect = WaveEffect().apply {
                        lifetime = 15f
                        sizeTo = 30f
                        strokeFrom = 4f
                        colorFrom = Pal.accent
                        colorTo = Pal.accent
                    }
                    despawnEffect = hitEffect
                    trailEffect = ParticleEffect().apply {
                        particles = 1
                        lifetime = 25f
                        sizeFrom = 3f
                        sizeTo = 0f
                        cone = 360f
                        length = 23f
                        sizeInterp = Interp.pow10In
                        colorFrom = Pal.accent
                        colorTo = Pal.accent
                    }
                }
            }
        }
        weapons.addP {
            weapons.last().copy().apply {
                x = -13f
                rotate = true
                y = 45f
            }
        }
    }
    val 裂片集群 = object : IceUnitType("clusterLobes") {
        init {
            weapons.addP {
                IceWeapon().apply {
                    shoot = ShootPattern().apply {
                        shots = 4
                        shotDelay = 6f
                    }
                    mirror = false
                    baseRotation = 90f
                    shake = 3f
                    shootCone = 360f
                    rotate = false
                    rotateSpeed = 0f
                    reload = 60 * 3f
                    inaccuracy = 60f
                    bullet = object : BasicBulletType(8f, 40f) {
                        init {
                            smokeEffect = Fx.none
                            shootSound = Sounds.malignShoot
                            shootEffect = Fx.none
                            lifetime = 60 * 8f / speed + 60
                            trailLength = 24
                            trailWidth = 3f
                            trailColor = Colors.b4
                            homingRange = 100 * 8f
                            homingPower = 0.2f
                            homingDelay = 10f
                            hitEffect = Effect(60f) { e ->
                                Draw.color(Colors.b4)
                                Lines.stroke(Interp.pow3Out.apply(e.fout()) * 3)
                                Lines.poly(e.x, e.y, 8, Interp.pow3Out.apply(e.fin()) * 36 + 36, e.rotation)
                            }
                            despawnEffect = hitEffect
                            despawnHit = true
                        }

                        override fun removed(b: Bullet) {
                            val bc = object : BombBulletType(15f, 40f) {
                                init {
                                    collidesGround = true
                                    collides = true
                                    splashDamage = 15f
                                    collidesTiles = true
                                    speed = 0f
                                    collidesAir = true
                                    drag = 0f
                                    lifetime = 15f
                                    despawnEffect = WaveEffect().apply {
                                        lifetime = 15f
                                        sizeTo = 15f
                                        strokeFrom = 4f
                                        colorFrom = Colors.b4
                                        colorTo = Colors.b4
                                    }
                                    hitEffect = despawnEffect
                                }

                                override fun draw(b: Bullet) {
                                    Draw.color(Colors.b4)
                                    b.vel.set(0f, 0f)
                                    Drawf.tri(b.x, b.y, 8f, 8f, b.data as Float)
                                    //  super.draw(b)
                                }
                            }
                            for (i in 0 until 15) {
                                val x = IceEffects.rand.random(-36, 36)
                                val y = IceEffects.rand.random(-36, 36)
                                bc.create(
                                    b,
                                    b.team,
                                    b.x + x,
                                    b.y + y,
                                    Random.nextInt(360).toFloat(),
                                    -1f,
                                    1f,
                                    1f,
                                    Random.nextInt(360).toFloat()
                                )
                            }
                            super.removed(b)
                        }

                        override fun update(b: Bullet) {
                            super.update(b)
                            if (Mathf.chanceDelta(1f.toDouble())) {
                                val x = IceEffects.rand.random(-8f, 8f)
                                val y = IceEffects.rand.random(-8f, 8f)
                                IceEffects.layerBullet.at(b.x + x, b.y + y, 0f, Colors.b4, Random.nextInt(360))
                            }
                        }

                        override fun draw(b: Bullet) {
                            drawTrail(b)
                            drawParts(b)
                            val shrink = shrinkInterp.apply(b.fout())
                            val height = this.height * ((1f - shrinkY) + shrinkY * shrink)
                            val width = this.width * ((1f - shrinkX) + shrinkX * shrink)
                            val mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin())

                            Draw.mixcol(mix, mix.a)
                            Draw.color(trailColor)
                            Drawf.tri(b.x, b.y, width + 2, height, b.rotation())

                            Draw.reset()
                        }
                    }
                }
            }
            weapons.addP {
                weapons.first().copy().apply {
                    baseRotation = 270f
                }
            }
            abilities.add(BarAbility<ClusterLobesUnit>().cons { unit, bars ->
                bars.add(Bar({ "格挡 ${unit.resistCont}" }, { Colors.b4 }) { 1f }).row()
                bars.add(Bar({ "护盾 ${unit.armor().toInt()}" }, { Colors.b4 }) { unit.armor() / 100 }).row()
                bars.add(Bar({ "免伤 ${(unit.immunity() * 100).toStringi(2)}%" }, { Colors.b4 }) {
                    unit.immunity()
                }).row()
            })
            rotateSpeed = 2f
            drag = 0.05f
            constructor = IceRegister.getPutUnit<ClusterLobesUnit>()
            flying = true
            hidden = false
            faceTarget = false
            lowAltitude = true
            drawBody = false
            drawCell = false
            health = 96000f
            armor = 0f
            hitSize = 32f
            speed = 18 / 7.5f
            rotateSpeed = 4f
            range = 8 * 60f
            engineSize = 0f
            itemCapacity = 0
            Vars.content.statusEffects().forEach {
                if (it.speedMultiplier == 1f) return@forEach
                immunities.add(it)
            }
            parts.add(HaloPart().apply {
                mirror = false
                shapes = 4
                radius = 6f
                triLength = 4f
                haloRadius = 16f
                haloRotateSpeed = -1f
            })
        }
    }
    val 女巫 = object : IceUnitType("witch") {
        var lobesMin: Int = 7
        var lobesMax: Int = 7
        var botAngle: Float = 60f
        var origin = 0.1f
        var sclMin: Float = 30f
        var sclMax = 50f
        var magMin = 5f
        var magMax = 15f
        var timeRange = 40f
        var spread = 0f
        val tentacle = Array(27) {
            IFiles.findPng("witch (${28 - it})")
        }

        init {
            flying = true
            hitSize = 100f
            constructor = IceRegister.getPutUnit<WitchUnit>()
        }

        override fun draw(unit: Unit) {
            for (tex in tentacle) {
                Fx.rand.setSeed(tentacle.indexOf(tex).toLong())
                val offset = Fx.rand.random(180f)
                val lobes = Fx.rand.random(lobesMin, lobesMax)
                for (i in 0 until lobes) {
                    val ba = i / lobes.toFloat() + offset + Fx.rand.range(spread)
                    val angle = ba + Mathf.sin(
                        Time.time + Fx.rand.random(0f, timeRange),
                        Fx.rand.random(sclMin, sclMax),
                        Fx.rand.random(magMin, magMax)
                    )
                    val w = region.width * region.scl()
                    val h = region.height * region.scl()


                    Draw.rect(
                        tex,
                        unit.x - Angles.trnsx(angle, origin) + w * 0.5f,
                        unit.y - Angles.trnsy(angle, origin),
                        w,
                        h,
                        origin * 4f,
                        h / 2f,
                        angle - 90
                    )
                }
            }

            super.draw(unit)
        }
    }
    val 断业 = IceTankUnitType("breakUp").apply {
        speed = 0.48f
        armor = 26f
        health = 22000f
        hitSize = 75f
        crushDamage = 25f / 5f
        rotateSpeed = 0.8f
        treadPullOffset = 1
        constructor = Prov(TankUnit::create)
        outlineColor = Color.valueOf("24222B")
        treadRects = arrayOf(Rect(70f - (400 / 2), 53f - (500 / 2), 83f, 394f))
        weapons.addP {
            Weapon("$name-weapon1").apply {
                x = 0f
                y = 11f
                shootY += 16f
                mirror = false
                rotate = true
                rotateSpeed = 0.5f
                shootCone = 0f
                cooldownTime = 38f * 2
                recoil = 4f
                reload = 240f
                shake = 4f
                shoot.firstShotDelay = 80f
                shootSound = IceSounds.laser2
                val bullet1 = LaserBulletType(1200f).apply {
                    colors = arrayOf(Colors.b4.cpy().a(0.4f), Colors.b4, Color.white)
                    chargeSound = IceSounds.forceHoldingLaser2
                    buildingDamageMultiplier = 1.25f
                    hitEffect = Fx.hitLancer
                    shootEffect = IceEffects.lancerLaserShoot
                    hitSize = 16f
                    lifetime = 30f
                    drawSize = 400f
                    collidesAir = true
                    length = 250f * 2
                    width = 30f * 1.5f
                    ammoMultiplier = 1f
                    pierceBuilding = true
                }
                val lightSky = Colors.b4
                val bullet2 = object : ChainBulletType(12f) {
                    override fun init(b: Bullet?) {
                        for (i in 1..3) {
                            super.init(b)
                        }
                    }
                }.apply {
                    collidesGround = false
                    length = 250f * 2
                    hitColor = lightSky.cpy().a(0.4f).also { lightningColor = it }.also { lightColor = it }
                    //shootEffect = NHFx.hitSparkLarge
                    //// hitEffect = NHFx.lightningHitSmall
                    //  smokeEffect = NHFx.hugeSmokeGray
                }
                bullet = MultiBulletType(bullet1, bullet2).apply {
                    speed = 0f
                    keepVelocity = true
                    chargeEffect = MultiEffect(
                        Effect(38f * 2) { e: EffectContainer ->
                            val data = e.data
                            if (data is Unit) {
                                val mount = data.mounts[0]
                                val weapon = mount.weapon
                                var mountX = data.x + Angles.trnsx(data.rotation - 90, x, y)
                                var mountY = data.y + Angles.trnsy(data.rotation - 90, x, y)
                                var weaponRotation = data.rotation - 90 + (if (rotate) mount.rotation else baseRotation)
                                var bulletX = mountX + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY)
                                var bulletY = mountY + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY)

                                Draw.color(Colors.b4)
                                Angles.randLenVectors(e.id.toLong(), 20, 1f + 40f * e.fout(), e.rotation, 120f
                                ) { x: Float, y: Float ->
                                    Lines.lineAngle(bulletX + x, bulletY + y, Mathf.angle(x, y), e.fslope() * 3f + 1f)
                                }

                            }
                        },
                        Effect(45f * 2) { e: EffectContainer ->
                            val data = e.data
                            if (data is Unit) {
                                val mount = data.mounts[0]
                                val weapon = mount.weapon
                                var mountX = data.x + Angles.trnsx(data.rotation - 90, x, y)
                                var mountY = data.y + Angles.trnsy(data.rotation - 90, x, y)
                                var weaponRotation = data.rotation - 90 + (if (rotate) mount.rotation else baseRotation)
                                var bulletX = mountX + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY)
                                var bulletY = mountY + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY)
                                val margin = 1f - Mathf.curve(e.fin(), 0.9f)
                                val fin = min(margin, e.fin())

                                Draw.color(Colors.b4)
                                Fill.circle(bulletX, bulletY, fin * 6f)

                                Draw.color()
                                Fill.circle(bulletX, bulletY, fin * 4f)
                            }
                        },
                    )
                }
            }
        }
        weapons.addP {
            Weapon("$name-weapon2").apply {
                shoot.apply {
                    shots = 3
                    shotDelay = 10f
                }
                x = 0f
                y = -30f
                shootY = 8f
                mirror = false
                rotate = true
                rotateSpeed = 3f
                reload = 150f
                recoil = 4f
                shootSound = IceSounds.highExplosiveShell
                bullet = AngleBulletType(4f, 2f, 180f, 2f).apply {
                    width = 8f
                    height = 8f
                    knockback = 0.5f
                    shootEffect = Effect(32f) { e: EffectContainer ->
                        Draw.color(Color.white, Colors.b4, e.fin())
                        Fx.rand.setSeed(e.id.toLong())
                        for (i in 0..8) {
                            val rot = e.rotation + Fx.rand.range(26f)
                            Fx.v.trns(rot, Fx.rand.random(e.finpow() * 30f))
                            Fill.poly(e.x + Fx.v.x, e.y + Fx.v.y, 4, e.fout() * 4f + 0.2f, Fx.rand.random(360f))
                        }
                    }
                    parts = Seq.with(FlarePart().apply {
                        followRotation = true
                        rotMove = 180f
                        progress = DrawPart.PartProgress.life
                        color1 = Colors.b4
                        stroke = 6f
                        radius = 5f
                        radiusTo = 30f
                    })
                }
                bullet = BasicBulletType().apply {
                    val rand = Rand()
                    fun tri(x: Float, y: Float, width: Float, length: Float, angle: Float) {
                        val wx = Angles.trnsx(angle + 90, width)
                        val wy = Angles.trnsy(angle + 90, width)
                        Fill.tri(x + wx, y + wy, x - wx, y - wy, Angles.trnsx(angle, length) + x,
                            Angles.trnsy(angle, length) + y)
                    }
                    speed = 13f
                    lifetime = 60 * 2f
                    val layer1 = Effect(300f, 1600f) { e: EffectContainer ->
                        val rad = 150f
                        rand.setSeed(e.id.toLong())

                        Draw.color(Color.white, e.color, e.fin() + 0.6f)
                        val circleRad = e.fin(Interp.circleOut) * rad * 4f
                        Lines.stroke(12 * e.fout())
                        Lines.circle(e.x, e.y, circleRad)
                        for (i in 0..23) {
                            Tmp.v1.set(1f, 0f).setToRandomDirection(rand).scl(circleRad)
                            tri(
                                e.x + Tmp.v1.x, e.y + Tmp.v1.y,
                                rand.random(circleRad / 16, circleRad / 12) * e.fout(),
                                rand.random(circleRad / 4,
                                    circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180)
                        }

                        Draw.blend(Blending.additive)
                        Draw.z(Layer.effect + 0.1f)
                        Fill.light(e.x, e.y, Lines.circleVertices(circleRad), circleRad, Color.clear,
                            Tmp.c1.set(Draw.getColor()).a(e.fout(Interp.pow10Out)))
                        Draw.blend()
                        Draw.z(Layer.effect)
                        Drawf.light(e.x, e.y, rad * e.fout(Interp.circleOut) * 4f, e.color, 0.7f)
                    }
                    despawnEffect = layer1
                }
            }
        }
        weapons.addP {
            Weapon("$name-weapon3").apply {
                x = 28f
                y = -25f
            }
        }
        weapons.addP {
            Weapon("$name-weapon4").apply {
                x = 25.5f
                y = 10f
            }
        }
        weapons.addP {
            Weapon("$name-weapon4").apply {
                x = 25.5f
                y = 32f
            }
        }
    }
    val 焚棘 = object : IceUnitType("ardenThorn") {
        val value = RotatorAbility("propeller", 13f, 10f, 5f, true)
        override fun drawShadow(unit: Unit) {
            val e = Mathf.clamp(unit.elevation, shadowElevation, 1f) * shadowElevationScl * (1f - unit.drownTime)
            val x = unit.x + shadowTX * e
            val y = unit.y + shadowTY * e
            val floor = Vars.world.floorWorld(x, y)
            val dest = if (floor.canShadow) 1f else 0f
            //是的，这更新了 draw（） 中的状态......这不是问题，因为无论如何我都不想让它变得明显
            unit.shadowAlpha = if (unit.shadowAlpha < 0) dest else Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f)
            Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha)


            val rot = unit.rotation - 90
            val trnsx1 = Angles.trnsx(rot, value.x, value.y)
            val trnsy1 = Angles.trnsy(rot, value.x, value.y)
            val trnsx2 = Angles.trnsx(rot, -value.x, value.y)
            val trnsy2 = Angles.trnsy(rot, -value.x, value.y)

            val speed = Time.time * value.speed * 6
            val ux = unit.x + trnsx1
            val uy = unit.y + trnsy1
            val nx = unit.x +trnsx2
            val ny = unit.y + trnsy2
            Draw.rect(name+"-"+"propeller", ux+ shadowTX * e, uy+ shadowTY * e, speed)
            if (true) Draw.rect(name+"-"+"propeller", nx+ shadowTX * e, ny+ shadowTY * e, -speed)
            Draw.rect(name, unit.x + shadowTX * e, unit.y + shadowTY * e, unit.rotation - 90)
            Draw.color()
        }
    }.apply {
        constructor = Prov(UnitEntity::create)
        flying = true
        speed = 2f
        hitSize = 40f
        abilities.add(value)
    }

}
/*
bullet = object : LightningLinkerBulletType(2.5f, 250f) {
    init {
        rangeOverride = 480f
        trailWidth = 8f
        trailLength = 40
        lightningColor = lightSkyBack
        lightColor = lightningColor
        trailColor = lightColor
        backColor = trailColor
        frontColor = Color.white
        randomGenerateRange = 280f
        randomLightningNum = 5
        linkRange = 280f
        scaleLife = true
        hitModifier
        size /= 1.5f
        drag = 0.0065f
        fragLifeMin = 0.125f
        fragLifeMax = 0.45f
        fragVelocityMax = 0.75f
        fragVelocityMin = 0.25f
        fragBullets = 13
        fragBullet = BulletType()
        hitSound = Sounds.explosionbig
        drawSize = 40f
        splashDamageRadius = 240f
        splashDamage = 80f
        lifetime = 300f
        despawnEffect = Fx.none
        hitEffect = Effect(50f) { e: EffectContainer ->
            Draw.color(lightSkyBack)
            Fill.circle(e.x, e.y, e.fout() * 44)
            Lines.stroke(e.fout() * 3.2f)
            Lines.circle(e.x, e.y, e.fin() * 80)
            Lines.stroke(e.fout() * 2.5f)
            Lines.circle(e.x, e.y, e.fin() * 50)
            Angles.randLenVectors(e.id.toLong(), 30, 18 + 80 * e.fin()) { x: Float, y: Float ->
                Lines.stroke(e.fout() * 3.2f)
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 14 + 5)
            }
            Draw.color(Color.white)
            Fill.circle(e.x, e.y, e.fout() * 30)
        }
        shootEffect = Effect(30f) { e: EffectContainer ->
            Draw.color(lightSkyBack)
            Fill.circle(e.x, e.y, e.fout() * 32)
            Draw.color(Color.white)
            Fill.circle(e.x, e.y, e.fout() * 20)
        }
        smokeEffect = Effect(40f, 100f) { e: EffectContainer ->
            Draw.color(lightSkyBack)
            Lines.stroke(e.fout() * 3.7f)
            Lines.circle(e.x, e.y, e.fin() * 100 + 15)
            Lines.stroke(e.fout() * 2.5f)
            Lines.circle(e.x, e.y, e.fin() * 60 + 15)
            Angles.randLenVectors(e.id.toLong(), 15, 7f + 60f * e.finpow()
            ) { x: Float, y: Float ->
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 4f + e.fout() * 16f)
            }
        }
    }
}*/
