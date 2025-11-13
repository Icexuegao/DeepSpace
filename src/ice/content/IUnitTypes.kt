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
import arc.util.Nullable
import arc.util.Tmp
import ice.ai.AIController
import ice.ai.CarryTaskAI
import ice.library.content.blocks.abstractBlocks.IceBlock.Companion.desc
import ice.library.content.unit.ability.BarAbility
import ice.library.content.unit.entity.*
import ice.library.content.unit.type.IceUnitType
import ice.library.entities.IcePuddle
import ice.library.entities.IceRegister
import ice.library.entities.bullet.*
import ice.library.entities.effect.MultiEffect
import ice.library.meta.IceEffects
import ice.library.meta.stat.IceStats
import ice.library.scene.tex.IceColor
import ice.library.util.toStringi
import ice.music.ISounds
import ice.ui.BaseBundle.Companion.bundle
import mindustry.Vars
import mindustry.ai.UnitCommand
import mindustry.ai.types.MinerAI
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.content.UnitTypes
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.Mover
import mindustry.entities.Puddles
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.LaserBulletType
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.FlarePart
import mindustry.entities.part.HaloPart
import mindustry.entities.pattern.ShootHelix
import mindustry.entities.pattern.ShootPattern
import mindustry.game.Team
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.ui.Bar
import kotlin.math.min
import kotlin.random.Random
@Suppress("unused")
object IUnitTypes {
    fun load() = kotlin.Unit
    val 收割 = IceUnitType("harvester") {
        speed = 2f
        flying = true
        hitSize = 10f
        isEnemy = false
        mineTier = 2
        mineSpeed = 3f
        engineColor = IceColor.b4
        defaultCommand = UnitCommand.mineCommand
        aiController = Prov(::MinerAI)
        bundle {
            desc(zh_CN, "收割", "全新设计的采矿单位,搭载了高效的激光共振钻头")
        }
    }
    val 和弦 = IceUnitType("chord") {
        drag = 0.017f
        accel = 0.05f
        armor = 8f
        speed = 3.5f
        flying = true
        health = 60f
        hitSize = 12f
        isEnemy = false
        useUnitCap = false
        rotateSpeed = 3f
        lowAltitude = false
        itemCapacity = 30
        allowedInPayloads = false
        logicControllable = false
        playerControllable = false
        controller = Func { CarryTaskAI() }
        constructor = Prov(UnitEntity::create)
        bundle {
            desc(zh_CN, "和弦")
        }
    }
    val 突刺 = IceUnitType("barbProtrusion") {
        armor = 8f
        speed = 0.7f
        health = 700f
        hitSize = 14f
        rotateSpeed = 3.3f
        squareShape = true
        omniMovement = false
        rotateMoveFirst = true
        treadRects = arrayOf(Rect(11f - (64 / 2), 5f - (64 / 2), 16f, 53f))
        setWeapon("weapon") {
            x = 0f
            shootY += 2f
            reload = 60f
            mirror = false
            rotate = true
            rotateSpeed = 3f
            bullet = IceBasicBulletType(4f, 80f) {
                height = 8f
                width = 4f
                drag = 0f
                trailColor = Pal.accent
                trailWidth = 1.7f
                trailLength = 4
                lifetime = 40f
                shootEffect = IceEffects.baseShootEffect(Pal.accent)
            }
        }
        val ms = 0.2f
        setUnitDamageEvent { u: Unit, b: Bullet ->
            if (b.type.pierceArmor) return@setUnitDamageEvent
            val bulletDir = b.rotation()
            val unitDir = u.rotation()
            // 计算相对角度（0-360）
            val relativeAngle = (bulletDir - unitDir + 360) % 360
            // 身后范围：90度到270度之间
            if (relativeAngle !in 90.0..270.0) {
                //后面
            } else {
                b.damage -= b.damage * 0.2f
            }
        }
        statsFun {
            stats.addPercent(IceStats.正面免伤, ms)
        }
        bundle {
            desc(zh_CN, "突刺")
        }
    }
    val 路西法 = IceUnitType("lucifer") {
        armor = 1f
        speed = 3.5f
        flying = true
        health = 150f
        hitSize = 16f
        mineTier=2
        mineSpeed=2f
        engineSize = 3f
        faceTarget = false
        lowAltitude = true
        rotateSpeed = 7f
        itemCapacity = 5
        engineOffset = 8f
        circleTarget = true
        engineColor = IceColor.b4
        itemCapacity = 30
        buildSpeed = 0.75f
        buildRange = 8 * 40f
        setWeapon("weapon") {
            mirror = true
            rotate = true
            reload = 15f
            rotateSpeed = 9f
            predictTarget = false
            bullet = RandomDamageBulletType(8, 14, 3f) {
                backColor= IceColor.b4
                frontColor= IceColor.b4
                lightColor= IceColor.b4
                shootY += 1
                lifetime = 60f
                homingPower = 0.05f
                homingRange = 50f
                shootEffect = IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b5)
            }
        }
        abilities
        bundle {
            desc(zh_CN, "路西法")
        }
    }
    val 仆从 = IceUnitType("footman") {
        speed = 3.2f
        flying = true
        health = 2000f
        hitSize = 30f
        engineSize = 6f
        rotateSpeed = 5.2f
        engineOffset = 19f
        engineColor = IceColor.b4
        forceMultiTarget = true
        aiController = UnitTypes.flare.aiController
        constructor = Prov(UnitEntityLegacyAlpha::create)
        setWeapon {
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
        }
        bundle {
            desc(zh_CN, "仆从",
                "传教者的专属防空护卫,仆从搭载了双联装净化之焰喷射器,能够喷射高温火焰,专门克制无人机集群",
                "确定是护卫不是火刑柱?")
        }
    }
    val 传教者 = IceUnitType("missionary") {
        speed = 0.9f
        flying = true
        hitSize = 90f
        health = 40000f
        targetAir = true
        faceTarget = true
        lowAltitude = true
        rotateSpeed = 0.6f
        targetGround = true
        forceMultiTarget = true
        constructor = Prov(UnitEntity::create)
        engines.add(IceUnitType.IUnitEngine(30f, -65f, 8f, -90f, 6f))
        engines.add(IceUnitType.IUnitEngine(0f, -80f, 8f, -90f))
        engines.add(IceUnitType.IUnitEngine(-30f, -65f, 8f, -90f, 6f))
        setWeapon("weapon1") {
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
            bullet = IceBasicBulletType(7f, 250f, "large-orb") {
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
        setWeapon("weapon2") {
            x = -20.5f
            y = 4f
            rotate = true
            reload = 60f
            shootSound = Sounds.malignShoot
            bullet = BombBulletType(500f, 64f) {
                width = 10f
                height = 10f
                speed = 12f
                drag = 0.05f
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
                var i = 0f
                addUpdate {
                    i += it.time()
                }
                addDraw {
                    Draw.color(Pal.accent)
                    Lines.stroke(1 - Interp.pow3Out.apply(it.fin()) * 3)
                    Lines.poly(it.x, it.y, 3, Interp.pow3Out.apply(1 - it.fin()) * 24, i)
                }
            }
        }
        setWeapon("weapon3") {
            x = -16f
            y = 29f
            top = true
            rotate = true
            mirror = true
            reload = 6f
            shoot.apply {
                shotDelay = 15f
            }
            bullet = IceBasicBulletType(8f, 23f) {
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
        }.copyAdd {
            x = -13f
            y = 45f
        }
        bundle {
            desc(zh_CN, "传教者",
                "重型空中火力平台,枢机教廷[净化之翼]军团,搭载4门防空拦截的磁轨速射炮,2门圣裁等离子爆裂炮,以及2门对地穿甲的粒子冲击炮形成全方位立体火力网",
                "枢机的例行祷告")
        }
    }
    val 裂片集群 = IceUnitType("clusterLobes") {
        setWeapon {
            shoot.apply {
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
                    trailColor = IceColor.b4
                    homingRange = 100 * 8f
                    homingPower = 0.2f
                    homingDelay = 10f
                    hitEffect = Effect(60f) { e ->
                        Draw.color(IceColor.b4)
                        Lines.stroke(Interp.pow3Out.apply(e.fout()) * 3)
                        Lines.poly(e.x, e.y, 8, Interp.pow3Out.apply(e.fin()) * 36 + 36, e.rotation)
                    }
                    despawnEffect = hitEffect
                    despawnHit = true
                }

                override fun removed(b: Bullet) {
                    /* val bc = object : BombBulletType(15f, 40f) {
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
                                 colorFrom = IceColor.b4
                                 colorTo = IceColor.b4
                             }
                             hitEffect = despawnEffect
                         }

                         override fun draw(b: Bullet) {
                             Draw.color(IceColor.b4)
                             b.vel.set(0f, 0f)
                             Drawf.tri(b.x, b.y, 8f, 8f, b.data as Float)
                             //  super.draw(b)
                         }
                     }
                     (0 until 15).forEach { i ->
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
                     }*/
                    super.removed(b)
                }

                override fun update(b: Bullet) {
                    super.update(b)
                    if (Mathf.chanceDelta(1f.toDouble())) {
                        val x = IceEffects.rand.random(-8f, 8f)
                        val y = IceEffects.rand.random(-8f, 8f)
                        IceEffects.layerBullet.at(b.x + x, b.y + y, 0f, IceColor.b4, Random.nextInt(360))
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
        }.copyAdd {
            baseRotation = 270f
        }
        abilities.add(BarAbility<ClusterLobesUnit>().cons { unit, bars ->
            bars.add(Bar({ "格挡 ${unit.resistCont}" }, { IceColor.b4 }) { 1f }).row()
            bars.add(Bar({ "护盾 ${unit.armor().toInt()}" }, { IceColor.b4 }) { unit.armor() / 100 }).row()
            bars.add(Bar({ "免伤 ${(unit.immunity() * 100).toStringi(2)}%" }, { IceColor.b4 }) {
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
        bundle {
            desc(zh_CN, "裂片集群")
        }
    }
    val 断业 = IceUnitType("breakUp") {
        speed = 0.48f
        armor = 26f
        health = 22000f
        hitSize = 75f
        crushDamage = 25f / 5f
        rotateSpeed = 0.8f
        treadPullOffset = 1
        squareShape = true
        omniMovement = false
        rotateMoveFirst = true
        outlineColor = Color.valueOf("24222B")
        treadRects = arrayOf(Rect(70f - (400 / 2), 53f - (500 / 2), 83f, 394f))
        setWeapon("weapon1") {
            x = 0f
            y = 11f
            shake = 4f
            shootY += 16f
            mirror = false
            rotate = true
            recoil = 4f
            reload = 240f
            shootCone = 0f
            rotateSpeed = 0.5f
            cooldownTime = 38f * 2
            shoot.firstShotDelay = 80f
            shootSound = ISounds.laser2
            parentizeEffects = true
            val laserBulletTypelength = 400f
            val bullet2 = object : ChainBulletType(12f) {
                override fun init(b: Bullet) {
                    (1..3).forEach { _ ->
                        super.init(b)
                    }
                }
            }.apply {
                collidesGround = false
                length = laserBulletTypelength
                hitColor = IceColor.b4.cpy().a(0.4f).also { lightningColor = it }.also { lightColor = it }
            }
            val bullet1 = object : LaserBulletType(1200f) {
                override fun create(
                    owner: Entityc?,
                    shooter: Entityc?,
                    team: Team?,
                    x: Float,
                    y: Float,
                    angle: Float,
                    damage: Float,
                    velocityScl: Float,
                    lifetimeScl: Float,
                    data: Any?,
                    mover: Mover?,
                    aimX: Float,
                    aimY: Float,
                    target: Teamc?
                ): Bullet? {
                    bullet2.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data,
                        mover, aimX, aimY, target)
                    return super.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data,
                        mover, aimX, aimY, target)
                }
            }.apply {
                colors = arrayOf(IceColor.b4.cpy().a(0.4f), IceColor.b4, Color.white)

                width = 45f
                lifetime = 30f
                sideAngle = 60f
                sideLength = 35f
                collidesAir = true
                collidesGround = true
                hitEffect = Fx.hitLancer
                length = laserBulletTypelength
                buildingDamageMultiplier = 1.25f
                chargeSound = ISounds.forceHoldingLaser2
                shootEffect = IceEffects.lancerLaserShoot
                chargeEffect = MultiEffect(
                    Effect(38f * 2) { e ->
                        IceEffects.unitMountSXY(e.data, this@setWeapon) { bulletX, bulletY ->
                            Draw.color(IceColor.b4)
                            Angles.randLenVectors(e.id.toLong(), 20, 1f + 40f * e.fout(), e.rotation, 120f
                            ) { x: Float, y: Float ->
                                Lines.lineAngle(bulletX + x, bulletY + y, Mathf.angle(x, y), e.fslope() * 3f + 1f)
                            }
                        }
                    },
                    Effect(45f * 2) { e: EffectContainer ->
                        IceEffects.unitMountSXY(e.data, this@setWeapon) { bulletX, bulletY ->
                            val margin = 1f - Mathf.curve(e.fin(), 0.9f)
                            val fin = min(margin, e.fin())
                            Draw.color(IceColor.b4)
                            Fill.circle(bulletX, bulletY, fin * 6f)
                            Draw.color()
                            Fill.circle(bulletX, bulletY, fin * 4f)
                        }
                    },
                )
            }


            bullet = bullet1
        }
        setWeapon("weapon2") {
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
            shootSound = ISounds.highExplosiveShell
            bullet = AngleBulletType(4f, 2f, 180f, 2f).apply {
                width = 8f
                height = 8f
                knockback = 0.5f
                shootEffect = Effect(32f) { e: EffectContainer ->
                    Draw.color(Color.white, IceColor.b4, e.fin())
                    Fx.rand.setSeed(e.id.toLong())
                    (0..8).forEach { i ->
                        val rot = e.rotation + Fx.rand.range(26f)
                        Fx.v.trns(rot, Fx.rand.random(e.finpow() * 30f))
                        Fill.poly(e.x + Fx.v.x, e.y + Fx.v.y, 4, e.fout() * 4f + 0.2f, Fx.rand.random(360f))
                    }
                }
                parts = Seq.with(FlarePart().apply {
                    followRotation = true
                    rotMove = 180f
                    progress = DrawPart.PartProgress.life
                    color1 = IceColor.b4
                    stroke = 6f
                    radius = 5f
                    radiusTo = 30f
                })
            }
            bullet = BasicBulletType().apply {
                val rand = Rand()

                speed = 13f
                lifetime = 60 * 2f
                val layer1 = Effect(300f, 1600f) { e: EffectContainer ->
                    val rad = 150f
                    rand.setSeed(e.id.toLong())
                    Draw.color(Color.white, e.color, e.fin() + 0.6f)
                    val circleRad = e.fin(Interp.circleOut) * rad * 4f
                    Lines.stroke(12 * e.fout())
                    Lines.circle(e.x, e.y, circleRad)
                    (0..23).forEach { i ->
                        Tmp.v1.set(1f, 0f).setToRandomDirection(rand).scl(circleRad)
                        IceEffects.drawFunc(
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
        setWeapon("weapon3") {
            x = 28f
            y = -25f
            rotate = true
            reload = 30f
            recoils = 3
            rotateSpeed = 3f
            shootSound = Sounds.shootBig
            bullet = TrailFadeBulletType(19f, 200f).apply {
                lifetime = 15f
                trailLength = 10
                trailWidth = 1.6f
                tracerStroke -= 0.3f
                keepVelocity = true
                tracerSpacing = 10f
                tracerUpdateSpacing *= 1.25f
                lightningColor = IceColor.b4
                lightColor = lightningColor
                backColor = lightColor
                hitColor = backColor
                trailColor = IceColor.b4
                frontColor = IceColor.b4
                width = 9f
                height = 9f
                hitSound = Sounds.plasmaboom
                hitShake = 5f
                despawnShake = hitShake
                pierceArmor = true
                pierceCap = 4

                splashDamage = 20f
                splashDamageRadius = 24f
                splashDamagePierce = true
                collidesAir = true
                collidesGround = true
                shootEffect = MultiEffect(IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b4),
                    IceEffects.lightningShoot())
                hitEffect = MultiEffect(IceEffects.square(IceColor.b4, length = 16f, size = 4f), Effect(15f) { e ->
                    val rad = 5f
                    e.color = IceColor.b4
                    IceEffects.rand.setSeed(e.id.toLong())
                    Draw.color(Color.white, e.color, e.fin() + 0.6f)
                    val circleRad = e.fin(Interp.circleOut) * rad * 4f
                    Lines.stroke(3 * e.fout())
                    Lines.circle(e.x, e.y, circleRad)
                })
                despawnEffect = hitEffect
            }
            shootY += 3f
        }
        setWeapons("weapon4" to {
            x = 25.5f
            y = 10f
        }, "weapon4" to {
            x = 25.5f
            y = 32f
        })
        bundle {
            desc(zh_CN, "断业",
                "断业是神殿[净罪计划]的产物,其装甲内层熔铸了经神祝圣的净化合金,主炮对建筑与重甲单位造成毁灭性伤害,能撕裂红雾中的畸变体集群,弹头内藏的圣水可延缓红雾再生",
                "帝国腐朽的装甲部队节节败退,唯有枢机的神术能短暂驱散腐化,帝国残部讥讽其为[伪神的铁棺材],但无人能否认——当它的履带碾过焦土时,连红雾都会为之退散")
        }
    }
    val 焚棘 = IceUnitType("ardenThorn") {
        speed = 1.3f
        accel = 0.5f
        drag = 0.05f
        flying = true
        health = 2000f
        hitSize = 40f
        drawCell = false
        faceTarget = false
        rotateSpeed = 1.9f
        constructor = IceRegister.getPutUnit<ArdenThorn>()
        setWeapon("weapon1") {
            x = 11f
            y = 27f
            shootY += 1f
            reload = 30f
            recoil = 2f
            rotate = true
            layerOffset = -1f
            rotateSpeed = 4f
            shootSound = Sounds.missile
            shoot = object : ShootHelix() {
                var bl = true
                override fun shoot(totalShots: Int, handler: BulletHandler, @Nullable barrelIncrementer: Runnable?) {
                    for (i in 0..<shots) {
                        bl = !bl
                        handler.shoot(0f, 0f, 0f, firstShotDelay + shotDelay * i
                        ) { b ->
                            b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * if (bl) 1 else -1))
                        }

                    }
                }

                init {
                    scl = 4f
                    mag = 2f
                }
            }
            bullet = BombBulletType(80f, 8 * 3f, "mine-bullet") {
                width *= 2
                height *= 2
                collidesAir = true
                splashDamagePierce = true
                collidesTiles = true
                collides = true
                var sec = 30f
                speed = (sec * 8f) / 60f
                lifetime = (25f / sec) * 60f
                trailWidth = 2f
                trailLength = 13
                trailColor = IceColor.b4
                frontColor = IceColor.b5
                lightColor = IceColor.b5
                backColor = IceColor.b5
                hitEffect = MultiEffect(Effect(16f) { e ->
                    IceEffects.rand.setSeed(e.id.toLong())
                    val rad = splashDamageRadius
                    Draw.color(Color.white, backColor, e.fin())
                    val circleRad = e.fin(Interp.circleOut) * rad
                    Lines.stroke(5 * e.fout())
                    Lines.circle(e.x, e.y, circleRad)
                    (0..3).forEach { i ->
                        Tmp.v1.set(1f, 0f).setToRandomDirection(IceEffects.rand).scl(circleRad)
                        IceEffects.drawFunc(
                            e.x + Tmp.v1.x, e.y + Tmp.v1.y,
                            IceEffects.rand.random(circleRad / 16, circleRad / 12) * e.fout(),
                            IceEffects.rand.random(circleRad / 4,
                                circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180)
                    }

                    Draw.blend(Blending.additive)
                    Draw.z(Layer.effect + 0.1f)
                    Fill.light(e.x, e.y, Lines.circleVertices(circleRad), circleRad, Color.clear,
                        Tmp.c1.set(Draw.getColor()).a(e.fout()))
                    Draw.blend()
                    Draw.z(Layer.effect)
                    Drawf.light(e.x, e.y, rad * e.fout(Interp.circleOut) * 4f, e.color, 0.7f)
                }, IceEffects.baseHitEffect)
                despawnEffect = hitEffect
                shootEffect = IceEffects.squareAngle(color1 = IceColor.b5, color2 = IceColor.b4)
            }
        }
        setWeapon("weapon2") {
            x = -17f
            y = -8f
            rotate = true
            shootY += 3f
            reload = 4f
            recoil = 1f
            layerOffset = -2f
            rotateSpeed = 2.5f
            reloadInterp = Interp.linear
            shootSound = ISounds.laserGun
            bullet = RandomDamageBulletType(20, 30, 7f) {
                pierceCap = 3
                frontColor = IceColor.b5
                lightColor = IceColor.b5
                backColor = IceColor.b5
                width = 4f
                height = 9f
                hitSize = 4f
                shootEffect = Effect(8f) { e ->
                    Draw.color(IceColor.b5, IceColor.b4, e.fin())
                    val w = 1f + 2 * e.fout()
                    Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation + 30f)
                    Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation + 15f)
                    Drawf.tri(e.x, e.y, w, 4 * e.fout(), e.rotation)
                    Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation - 15f)
                    Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation - 30f)
                }
                hitEffect = IceEffects.baseHitEffect
                despawnEffect = hitEffect
            }
        }
        bundle {
            desc(zh_CN, "焚棘",
                "轻型侦察攻击机,配备独特的渐速式火力系统,机身尾部的两挺转管重机枪在开火时可持续提升射速,形成愈演愈烈的压制弹幕.头部则搭载了两门锁定式导弹发射器,用于精准打击轻型防御目标.虽定位为侦察单位,但其出色的滞空能力与双重火力配置,使其能在探查敌情的同時实施骚扰性攻击,成为战场上空难以驱离的刺眼存在")
        }
    }
    val 青壤 = IceUnitType("schizovegeta") {
        speed = 0.3f
        health = 200f
        hitSize = 12f
        drawCell = false
        rotateSpeed = 1f
        outlineRadius = 3
        outlineColor = IceColor.r2
        createScorch = false
        deathSound = ISounds.chizovegeta

        legPhysicsLayer = false
        allowLegStep = true
        legStraightness = 0.3f
        stepShake = 0f
        legCount = 8
        legLength = 8f
        legGroupSize = 4
        lockLegBase = true
        legBaseUnder = true
        legContinuousMove = true
        legExtension = -2f
        legBaseOffset = 3f
        legMaxLength = 1.1f
        legMinLength = 0.2f
        legLengthScl = 0.96f
        legForwardScl = 1.1f
        rippleScale = 0.2f
        legMoveSpace = 1f

        deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
        constructor = IceRegister.getPutUnit<Schizovegeta>()
        bundle {
            desc(zh_CN, "青壤",
                "由血肉喷口缓慢孕育的活体培养囊,本身不具备攻击性,只会笨拙地蠕行移动.当其外膜在环境中自然破裂或被外力摧毁时,会释放出数颗至数十颗不等的肿瘤")
        }
    }
    val 丰穰之瘤 = IceUnitType("richTumor") {
        speed = 0f
        accel = -3f
        range = 0f
        health = 30f
        hitSize = 4f
        drawCell = false
        targetAir = false
        useUnitCap = false
        targetable = false
        itemCapacity = 0
        targetGround = false
        createScorch = false
        outlineRadius = 1
        playerControllable = false
        deathSound = Sounds.plantBreak
        deathExplosionEffect = IceEffects.bloodNeoplasma
        constructor = IceRegister.getPutUnit<RichTumor>()
        bundle {
            desc(zh_CN, "丰穰之瘤",
                "无法移动的特殊组织体,不会被任何单位视为目标.落地后进入短暂的潜伏期,随后开始将下方地表同化为活性肿瘤地,为血肉网络提供持续的生长基础")
        }
    }
    val 蚀虻 = IceUnitType("corrodfly-head") {
        rotateMoveFirst = true

        legStraightness = 0.3f
        stepShake = 0f
        legCount = 2
        legLength = 18f
        legGroupSize = 4
        lockLegBase = true
        legBaseUnder = true
        legContinuousMove = true
        legExtension = -2f
        legBaseOffset = 3f
        legMaxLength = 1.1f
        legMinLength = 0.2f
        legLengthScl = 0.96f
        legForwardScl = 1.1f
        rippleScale = 0.2f
        legMoveSpace = 1f

        hitSize = 8f
        rotateSpeed = 2.5f
        speed = 0.8f
        createScorch = false
        drawCell = false
        outlineRadius = 3
        outlineColor = IceColor.r2
        deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
        constructor = IceRegister.getPutUnit<CorrodflyHead>()
        bundle {
            desc(zh_CN, "蚀虻")
        }
    }
    val 蚀虻Middle = IceUnitType("corrodfly-middle") {
        hitSize = 5f
        drawCell = false
        outlineRadius = 3
        outlineColor = IceColor.r2
        hidden = true
        playerControllable = false
        createScorch = false
        deathSound = ISounds.chizovegeta
        aiController = Prov(::AIController)
        deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
        constructor = IceRegister.getPutUnit<CorrodflyMiddle>()
    }
    val 蚀虻End = IceUnitType("corrodfly-end") {
        legStraightness = 0.3f
        stepShake = 0f
        legCount = 2
        legLength = 18f
        legGroupSize = 4
        lockLegBase = true
        legBaseUnder = true
        legContinuousMove = true
        legExtension = -2f
        legBaseOffset = 3f
        legMaxLength = 1.1f
        legMinLength = 0.2f
        legLengthScl = 0.96f
        legForwardScl = 1.1f
        rippleScale = 0.2f
        legMoveSpace = 1f

        hitSize = 8f
        outlineRadius = 3
        outlineColor = IceColor.r2
        drawCell = false
        createScorch = false
        hidden = true
        constructor = IceRegister.getPutUnit<CorrodflyEnd>()
        faceTarget = false
        playerControllable = false
        deathSound = ISounds.chizovegeta
        aiController = Prov(::AIController)
        deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
        setWeapon("weapon") {
            x = 0f
            y = -4f
            shootX += 1
            recoil = 1f
            mirror = false
            rotate = true
            reload = 50f
            shootY += 2f
            shoot.shots = 2
            shoot.shotDelay = 15f
            shootSound = ISounds.flblSquirt
            bullet = MultiBasicBulletType("flesh") {
                width = 7f
                height = width
                shrinkInterp = Interp.one
                status = IStatus.流血
                statusDuration = 2 * 60f
                lightColor = IceColor.r3
                backColor = IceColor.r3
                frontColor = IceColor.r3
                lightOpacity = 0.2f
                shootEffect = Fx.none
                hitEffect = Effect(14f) { e ->
                    Draw.color(IceColor.r3, IceColor.r1, e.fin())
                    e.scaled(7f) { s ->
                        Lines.stroke(0.5f + s.fout())
                        Lines.circle(e.x, e.y, s.fin() * 5f)
                    }
                    Lines.stroke(0.5f + e.fout())
                    Angles.randLenVectors(e.id.toLong(), 5, e.fin() * 15f) { x: Float, y: Float ->
                        val ang = Mathf.angle(x, y)
                        Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f)
                    }
                    Drawf.light(e.x, e.y, 20f, IceColor.r3, 0.6f * e.fout())
                }
                despawnEffect = hitEffect
                smokeEffect = Effect(20f) { e ->
                    Draw.color(IceColor.r1, IceColor.r2, e.fin())
                    Angles.randLenVectors(e.id.toLong(), 5, e.finpow() * 6f, e.rotation, 20f
                    ) { x: Float, y: Float ->
                        Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f)
                    }
                }
                addRemoved { b ->
                    val puddle = IcePuddle.create()
                    puddle.team = b.team
                    puddle.tile = b.tileOn()
                    puddle.liquid = ILiquids.浓稠血浆
                    puddle.amount = IceEffects.rand.random((height + width) / 2, height * width / 2)
                    puddle.set(b.x, b.y)
                    Puddles.register(puddle)
                    puddle.add()
                }
            }
        }
    }
}