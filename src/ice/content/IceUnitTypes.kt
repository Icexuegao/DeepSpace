package ice.content

import arc.func.Func
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import ice.Ice
import ice.ai.CircleAi
import ice.type.unit.IceWeapon
import ice.type.unit.PointDefenseWeapon
import ice.ui.tex.Colors
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.content.UnitTypes
import mindustry.entities.Effect
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.UnitEntityLegacyAlpha
import mindustry.graphics.Layer
import mindustry.type.UnitType
import mindustry.type.Weapon

object IceUnitTypes {
    private lateinit var ling: UnitType
    lateinit var missionary: UnitType
    lateinit var footman: UnitType
    fun load() {
        ling = object : UnitType("ling") {
            init {
                armor = 1f
                itemCapacity = 5
                engineColor = Color.valueOf("c45f5f")
                engineOffset = 8f
                engineSize = 3f
                rotateSpeed = 7f
                speed = 3f
                health = 150f
                hitSize = 16f
                flying = true
                faceTarget = false
                circleTarget = true
                lowAltitude = true
                controller = Func { CircleAi() }
                constructor = Prov(UnitEntityLegacyAlpha::create)
                weapons.add(Weapon("${Ice.NAME}-${name}-weapon").apply {
                    mirror = true
                    rotate = true
                    predictTarget = false
                    rotateSpeed = 9f
                    reload = 15f
                    bullet = object : BasicBulletType(5f, 14f) {
                        init {
                            shootY += 1
                            lifetime = 60f
                            homingPower = 0.05f;homingRange = 50f
                            shootEffect = Effect(30f) { e ->
                                val color1 = Color.valueOf("ea8878")
                                val color2 = Color.valueOf("c45f5f")
                                Draw.color(color1, color2, e.fin())
                                Fx.rand.setSeed(e.id.toLong())
                                for (i in 0..5) {
                                    val rot: Float = e.rotation + Fx.rand.range(26f)
                                    Fx.v.trns(rot, Fx.rand.random(e.finpow() * 10f))
                                    Fill.poly(
                                        e.x + Fx.v.x, e.y + Fx.v.y, 4, e.fout() * 2f + 0.2f, Fx.rand.random(360f)
                                    )
                                }
                            }

                        }
                    }
                })
            }
        }
        missionary = object : UnitType("missionary") {}.apply {
            constructor = Prov(UnitEntityLegacyAlpha::create)
            flying = true
            speed = 0.9f
            rotateSpeed = 0.6f
            hitSize = 90f
        }
        footman = object : UnitType("footman") {}.apply {
            constructor =Prov(UnitEntityLegacyAlpha::create)
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
                    pierceBuilding=true
                    statusDuration = 60f * 10
                    shootEffect = IceEffects.changeFlame(lifetime * speed)
                    hitEffect = Fx.hitFlameSmall
                    despawnEffect = Fx.none
                    status = StatusEffects.burning
                    shootSound = Sounds.fire
                    keepVelocity = false
                    hittable = false
                }
            }, Weapon().apply {
                x = -2f
                y = 8f
                bullet = BulletType(6.7f, 17f).apply {
                    inaccuracy = 32f
                    pierceBuilding=true
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
            }, IceWeapon("footman-wea1").apply {
                controllable = false
                autoTarget = true
                rotate = true
                x = 0f
                y = -16f
                mirror = false
                top = false
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
            }, PointDefenseWeapon("footman-wea2").apply {
                reload = 0f
                top = false
                range = 100f
                rotate = true
                bullet.damage = 100f
                bullet.range = 80f
                ignoreRotation = true
            })
        }
    }
}