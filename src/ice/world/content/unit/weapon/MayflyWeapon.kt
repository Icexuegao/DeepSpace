package ice.world.content.unit.weapon

import arc.func.Boolf
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Mathf
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Table
import arc.util.Time
import arc.util.Tmp
import ice.entities.bullet.LightLaserBulletType
import ice.entities.bullet.base.BulletType
import ice.graphics.IceColor
import ice.graphics.SglDraw
import ice.graphics.lightnings.LightningContainer
import ice.world.SglFx
import ice.world.content.unit.type.MayflyStatus
import ice.world.content.unit.type.WeaponMount
import ice.world.meta.IceStats
import mindustry.content.Fx
import mindustry.entities.Units
import mindustry.entities.effect.MultiEffect
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.type.UnitType
import mindustry.ui.Styles

open class MayflyWeapon(name: String) : IceWeapon(name) {
    var delay: Float = 0f

    val subBullet: BulletType = object : BulletType() {
        init {
            damage = 80f
            splashDamage = 120f
            splashDamageRadius = 24f
            speed = 3f
            hitShake = 5f
            rangeOverride = 550f
            fragBullet = mindustry.entities.bullet.LightningBulletType().apply {
                lightningLength = 8
                lightningLengthRand = 8
                damage = 24f
            }

            fragBullets = 3

            hitSize = 3f
            lifetime = 90f
            homingDelay = 20f
            despawnHit = true
            hitEffect = MultiEffect(SglFx.explodeImpWave, SglFx.diamondSpark)
            hitColor = IceColor.matrixNet

            homingRange = 620f
            homingPower = 0.05f

            trailColor = IceColor.matrixNet
            trailLength = 20
            trailWidth = 2.4f
            trailEffect = SglFx.trailParticle
            trailChance = 0.16f
        }

        override fun draw(b: Bullet) {
            drawTrail(b)
            Draw.color(hitColor)
            Fill.circle(b.x, b.y, 6f)
            Draw.color(Color.black)
            Fill.circle(b.x, b.y, 3f)
        }

        override fun updateHoming(b: Bullet) {
            if (b.time < homingDelay) return
            val target: Posc? = Units.closestTarget(b.team, b.x, b.y, homingRange,
                Boolf { e: Unit? -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) },
                Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })

            if (target != null) {
                b.vel.lerpDelta(Tmp.v1.set(target.x() - b.x, target.y() - b.y).setLength(10f), homingPower)
            }
        }
    }

    init {
        mirror = true
        alternate = false
        rotate = false
        shootCone = 180f
        reload = 60f
        shootWarmupSpeed = 0.03f
        linearWarmup = false
        minWarmup = 0.9f
        shootSound = Sounds.lasershoot
        bullet = object : BulletType() {
            init {
                damage = 320f
                pierceCap = 3
                pierceBuilding = true
                fragBullets = 1
                fragRandomSpread = 0f
                fragAngle = 0f
                fragBullet = object : LightLaserBulletType() {
                    init {
                        length = 140f
                        damage = 160f
                        empDamage = 41f
                    }

                    override fun init(b: Bullet, c: LightningContainer) {
                        val target = Units.closestTarget(b.team, b.x, b.y, range, Boolf { e: Unit? ->
                            e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id)
                        }, Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })

                        if (target != null) {
                            b.rotation(b.angleTo(target))
                        }

                        super.init(b, c)
                    }
                }

                hitShake = 4f

                shootEffect = MultiEffect(SglFx.impactBubbleSmall, Fx.colorSparkBig)

                trailColor = IceColor.matrixNet
                hitColor = trailColor
                hitEffect = SglFx.diamondSparkLarge

                trailEffect = MultiEffect(SglFx.movingCrystalFrag)
                trailChance = 0.4f

                despawnHit = true

                speed = 10f
                lifetime = 120f
                homingDelay = 30f
                homingPower = 0.15f
                rangeOverride = 500f
                homingRange = 600f

                trailLength = 28
                trailWidth = 4f
            }

            override fun draw(b: Bullet) {
                super.draw(b)

                val delay = 1 - Mathf.pow(1 - Mathf.clamp(b.time / homingDelay), 2f)

                Draw.color(IceColor.matrixNet)
                Fill.circle(b.x, b.y, 6 + 2 * delay)
                SglDraw.drawDiamond(b.x, b.y, 24f, 8 * delay, b.rotation())

                SglDraw.drawTransform(b.x, b.y, 4 * delay, 0f, b.rotation()) { x: Float, y: Float, r: Float ->
                    SglDraw.gapTri(x, y, 12 * delay, 16 + 16 * delay, 14f, r)
                }

                Draw.color(Color.black)
                Fill.circle(b.x, b.y, 5 * delay)
            }

            override fun updateHoming(b: Bullet) {
                if (b.time < homingDelay) {
                    b.vel.lerpDelta(0f, 0f, 0.06f)
                }

                if (Mathf.chanceDelta((0.3f * b.vel.len() / speed).toDouble())) {
                    Fx.colorSparkBig.at(b.x, b.y, b.rotation(), b.type.hitColor)
                }

                if (b.time >= homingDelay) {
                    val realAimX = if (b.aimX < 0) b.x else b.aimX
                    val realAimY = if (b.aimY < 0) b.y else b.aimY
                    val target =
                        if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team !== b.team && !b.hasCollided(
                                b.aimTile.build.id)
                        ) {
                            b.aimTile.build
                        } else {
                            Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                                Boolf { e: Unit? -> e != null && !b.hasCollided(e.id) },
                                Boolf { t: Building? -> t != null && !b.hasCollided(t.id) })
                        }

                    if (target != null) {
                        val v = Mathf.lerpDelta(b.vel.len(), speed, 0.08f)
                        b.vel.setLength(v)
                        b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target),
                            homingPower * (v / speed) * Time.delta * 50f))
                    } else {
                        b.vel.lerpDelta(0f, 0f, 0.06f)
                    }

                    if (b.vel.len() >= speed * 0.8f) {
                        if (b.timer(3, 3f)) SglFx.weaveTrail.at(b.x, b.y, b.rotation(), hitColor)
                    }
                }
            }
        }
    }

    override fun addStats(u: UnitType, t: Table) {
        super.addStats(u, t)

        val ic = Table()
        subBullet.setStats(ic)
        val coll = Collapser(ic, true)
        coll.setDuration(0.1f)

        t.table { ft ->
            ft.left().defaults().left()
            ft.add("[lightgray]${IceStats.发射数量.localized()}[]: 2x")
            ft.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }.update { i ->
                i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen)
            }.size(8f).padLeft(16f).expandX()
        }
        t.row()
        t.add<Collapser?>(coll).padLeft(16f)
    }

    override fun init(unit: Unit, mount: WeaponMount) {
        super.init(unit, mount)
        val status = MayflyStatus()
        status.x = unit.x + Angles.trnsx(unit.rotation() - 90, mount.weapon.x, mount.weapon.y)
        status.y = unit.y + Angles.trnsy(unit.rotation() - 90, mount.weapon.x, mount.weapon.y)

        status.rot.set(1f, 0f).setAngle(unit.rotation() + mount.rotation)
        mount.status = status
        mount.phase = Mathf.random(0f, 360f)
    }

    override fun update(unit: Unit, mount: mindustry.entities.units.WeaponMount) {
        super.update(unit, mount)
        if (mount is WeaponMount) mount.status?.update(unit, mount)

    }

    override fun draw(unit: Unit, mount: mindustry.entities.units.WeaponMount) {
        if (mount is WeaponMount) {
            mount.status?.draw(unit, mount)
        }
    }

    override fun shoot(unit: Unit,
                       mount: mindustry.entities.units.WeaponMount,
                       shootX: Float,
                       shootY: Float,
                       rotation: Float
    ) {
        if (mount is WeaponMount) {
            Time.run(delay) {
                mount.status?.let { stat ->
                    bullet.create(unit, stat.x, stat.y, stat.rot.angle())
                    bullet.shootEffect.at(stat.x, stat.y, stat.rot.angle(), bullet.hitColor)
                    Time.run(12f) {
                        for (sign in Mathf.signs) {
                            subBullet.create(unit, stat.x, stat.y, stat.rot.angle() + 25 * sign)
                        }
                    }
                }
            }
        }
    }
}