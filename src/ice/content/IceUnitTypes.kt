package ice.content

import arc.func.Func
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import ice.Ice
import ice.ai.CircleAi
import ice.library.UnitTypeTool
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.bullet.BasicBulletType
import mindustry.type.UnitType
import mindustry.type.Weapon

object IceUnitTypes {
    private lateinit var ling: UnitType
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
                constructor = UnitTypeTool.entityConstructor("alpha")
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
    }
}