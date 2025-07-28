package ice.library.type.meta

import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.util.Tmp
import ice.library.draw.IDraws
import ice.library.scene.texs.Colors
import ice.library.scene.texs.Colors.s1
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType
import kotlin.math.min

object IceEffects {
    val rand = Rand()
    val lancerLaserShoot: Effect = Effect(21f) { e: EffectContainer ->
        Draw.color(Colors.y2)
        for (i in Mathf.signs) {
            Drawf.tri(e.x, e.y, 8f * e.fout(), 35f, e.rotation + 90f * i)
        }
    }
    val lancerLaserShoot1 = Effect(21f) { e ->
        val x = e.x
        val y = e.y
        val width = 8f * e.fout()
        val length = 16f
        if (e.rotation == 0f) {
            e.rotation = 45f
        }
        Draw.color(s1)
        Drawf.tri(x, y, width, length, e.rotation)
        Drawf.tri(x, y, width, length, 180 + e.rotation)
    }
    val lancerLaserChargeBegin = Effect(60f) { e ->
        val margin = 1f - Mathf.curve(e.fin(), 0.9f)
        val fin = min(margin, e.fin())
        Draw.color(Pal.spore)
        Fill.circle(e.x, e.y, fin * 4f)
        Draw.color(s1)
        Fill.circle(e.x, e.y, fin * 2f)
    }
    val hitLaserBlast = Effect(12f) { e ->
        Lines.stroke(e.fout() * 1.5f)
        Angles.randLenVectors(e.id.toLong(), 8, e.finpow() * 17f) { x: Float, y: Float ->
            Draw.color(s1)
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 3f)
        }
    }
    val layerBullet = Effect(45f) { e ->
        Draw.color(e.color)
        Draw.z(Layer.effect)
        e.x += rand.random(-1f, 1f)
        e.y += rand.random(-1f, 1f)
        val fl = 8f * Interp.pow3Out.apply(e.fout())
        Drawf.tri(e.x, e.y, fl, fl, (e.data as Int).toFloat())
    }
    val wave = Effect(300f) {
        Draw.z(Layer.effect)
        Draw.color(Colors.b4)
        Draw.alpha(it.fout())
        Lines.circle(it.x + it.fin() * 40, it.y + it.fin() * 40, 1f + 5 * 1.5f * it.fin())
    }
    val jumpTrail: Effect = Effect(120f, 5000f, Cons { e: EffectContainer ->
        val type = e.data<UnitType>()
        Draw.color(if (type.engineColor == null) e.color else type.engineColor)
        if (type.engineLayer > 0) Draw.z(type.engineLayer)
        else Draw.z((if (type.lowAltitude) Layer.flyingUnitLow else Layer.flyingUnit) - 0.001f)
        Draw.alpha(e.fin())
        for (index in 0 until type.engines.size) {
            val engine = type.engines[index]

            if (Angles.angleDist(engine.rotation, -90f) > 75) return@Cons
            val ang = Mathf.slerp(engine.rotation, -90f, 0.75f)

            Tmp.v1.trns(e.rotation, engine.y, -engine.x)

            e.scaled(80f) { i: EffectContainer ->
                DrawFunc(
                    i.x + Tmp.v1.x,
                    i.y + Tmp.v1.y,
                    engine.radius * 1.5f * i.fout(Interp.slowFast),
                    3000 * engine.radius / (type.engineSize + 4),
                    i.rotation + ang - 90
                )
                Fill.circle(i.x + Tmp.v1.x, i.y + Tmp.v1.y, engine.radius * 1.5f * i.fout(Interp.slowFast))
            }

            Angles.randLenVectors(
                (e.id + index).toLong(), 22, 400 * engine.radius / (type.engineSize + 4), e.rotation + ang - 90, 0f
            ) { x: Float, y: Float ->
                Lines.lineAngle(
                    e.x + x + Tmp.v1.x, e.y + y + Tmp.v1.y, Mathf.angle(x, y), e.fout() * 60
                )
            }
        }

        Draw.color()
        Draw.mixcol(e.color, 1f)
        Draw.alpha(e.fin())
        Draw.rect(
            type.fullIcon,
            e.x,
            e.y,
            type.fullIcon.width * e.fout(Interp.pow2Out) * Draw.scl * 1.2f,
            type.fullIcon.height * e.fout(Interp.pow2Out) * Draw.scl * 1.5f,
            e.rotation - 90f
        )
        Draw.reset()
    })
    val powderLeak = Effect(46f, 20f) { e ->
        // if (e.data !is Powder) return@Effect
        // val powder = e.data as Powder
        Draw.z(Mathf.lerpDelta(Layer.block - 0.001f, Layer.blockUnder, e.finpow()))
        rand.setSeed(e.id.toLong())

        Angles.randLenVectors(e.id.toLong(), 10, e.finpow() * 10f, e.rotation, 22f) { x: Float, y: Float ->
            Draw.color(
                //  Tmp.c1.set(powder.color).mul(rand.random(1f, 1.1f)),
                //  Tmp.c2.set(powder.color).mul(rand.random(0.4f, 0.6f)),
                e.finpow() / 2f
            )
            Fill.circle(e.x + x, e.y + y, 0.1f + e.fout() * 2f)
        }
    }

    fun DrawFunc(x: Float, y: Float, width: Float, length: Float, angle: Float) {
        val wx = Angles.trnsx(angle + 90, width)
        val wy = Angles.trnsy(angle + 90, width)
        Fill.tri(x + wx, y + wy, x - wx, y - wy, Angles.trnsx(angle, length) + x, Angles.trnsy(angle, length) + y)
    }

    /*** @param lengthSize 该值决定火焰最终长度 子弹的话一般是速度*时间 */
    fun changeFlame(lengthSize: Float): Effect {
        return Effect(32f, 80f) { e ->
            Draw.color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin())
            Angles.randLenVectors(e.id.toLong(), 8, e.finpow() * lengthSize, e.rotation, 10f) { x: Float, y: Float ->
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f)
            }
        }
    }

    //生成多个小方块特效
    fun square(color: Color): Effect {
        return Effect(15f) { e: EffectContainer ->
            Angles.randLenVectors(e.id.toLong(), 6, 4f + e.fin() * 5f) { x: Float, y: Float ->
                Draw.color(color, Color.white, e.fin())
                Fill.square(e.x + x, e.y + y, 0.5f + e.fout() * 2f, 45f)
            }
        }
    }

    val shieldWave = Effect(30f) { e: EffectContainer ->
        val data = e.data
        if (data is Unit) {
            val color = Colors.b4.cpy().a(e.fout())
            IDraws.light(data.x, data.y, Lines.circleVertices(50f), 20f, e.rotation, Color.clear,
                color, e::fout)
        }
    }

    fun squareAngle(
        life: Float = 30f,
        amount: Int = 5,
        range: Float = 26f,
        color1: Color = Color.valueOf("ea8878"),
        color2: Color = Color.valueOf("c45f5f")
    ): Effect {
        return Effect(life) { e ->
            Draw.color(color1, color2, e.fin())
            Fx.rand.setSeed(e.id.toLong())
            for (i in 0..amount) {
                val rot: Float = e.rotation + Fx.rand.range(range)
                Fx.v.trns(rot, Fx.rand.random(e.finpow() * 10f))
                Fill.poly(
                    e.x + Fx.v.x, e.y + Fx.v.y, 4, e.fout() * 2f + 0.2f, Fx.rand.random(360f)
                )
            }
        }
    }

    val baseBulletBoom = Effect(14f) { e: EffectContainer ->
        Draw.color(Color.white, Colors.b4, e.fin())
        e.scaled(7f) { s: EffectContainer ->
            Lines.stroke(0.5f + s.fout())
            Lines.circle(e.x, e.y, s.fin() * 5f)
        }

        Lines.stroke(0.5f + e.fout())

        Angles.randLenVectors(e.id.toLong(), 5, e.fin() * 15f) { x: Float, y: Float ->
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f)
        }
        Drawf.light(e.x, e.y, 20f, e.color, 0.6f * e.fout())
    }

    fun blastExplosion(color: Color=Pal.missileYellow) : Effect{
      return  Effect(22f) { e: EffectContainer ->
            Draw.color(color)
            e.scaled(6f) { i: EffectContainer ->
                Lines.stroke(3f * i.fout())
                Lines.circle(e.x, e.y, 3f + i.fin() * 15f)
            }

            Draw.color(Color.gray)

            Angles.randLenVectors(
                e.id.toLong(), 5, 2f + 23f * e.finpow()) { x: Float, y: Float ->
                Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f)
            }

            Draw.color(color)
            Lines.stroke(e.fout())

            Angles.randLenVectors((e.id + 1).toLong(), 4, 1f + 23f * e.finpow()) { x: Float, y: Float ->
                Lines.lineAngle(
                    e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f)
            }
            Drawf.light(e.x, e.y, 45f, Pal.missileYellowBack, 0.8f * e.fout())
        }
    }
}
