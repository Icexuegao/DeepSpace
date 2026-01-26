package ice.world.meta

import arc.func.Cons
import arc.func.Cons2
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.Lines.lineAngle
import arc.graphics.g2d.TextureRegion
import arc.math.Angles.*
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.util.Tmp
import ice.content.ILiquids
import ice.entities.effect.IEffect
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.graphics.IceColor.s1
import ice.graphics.IceDraw
import ice.ui.fragment.DeBugFragment
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.effect.MultiEffect
import mindustry.game.Team
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType
import mindustry.type.Weapon
import kotlin.math.max
import kotlin.math.min

object IceEffects {
    val rand = Rand()
    val prismaticSpikes =  Effect(45f, 100f){ e ->
        Lines.stroke(e.fout() * 2, e.color)
        val rad = 4 + e.finpow() * e.rotation
        val  x = trnsx(0f, rad)
        val  y = trnsy(0f, rad)
        Lines.quad(e.x, e.y + x, e.x + x * 2, e.y, e.x, e.y - x, e.x - x * 2, e.y)
        (0..3).forEach { i->
            Drawf.tri(e.x, e.y, 6f, e.rotation * 1.5f * e.fout(), i * 90f)
        }

        Draw.color()
        (0..3).forEach { i->
            Drawf.tri(e.x, e.y, 3f, e.rotation * 0.5f * e.fout(), i * 90f)
        }

        Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout())
    }
    val bloodNeoplasma = Effect(23f) { e ->
        val scl = max(e.rotation, 1f)
        Draw.color(Tmp.c1.set(ILiquids.浓稠血浆.color).mul(1.1f))
        randLenVectors(e.id.toLong(), 6, 19f * e.finpow() * scl) { x: Float, y: Float ->
            Fill.circle(e.x + x, e.y + y, e.fout() * 3.5f * scl + 0.3f)
        }
    }
    val lancerLaserShoot: Effect = Effect(21f) { e: EffectContainer ->
        Draw.color(IceColor.b4)
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
        randLenVectors(e.id.toLong(), 8, e.finpow() * 17f) { x: Float, y: Float ->
            Draw.color(s1)
            val ang = Mathf.angle(x, y)
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 3f)
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
        Draw.color(IceColor.b4)
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

            if (angleDist(engine.rotation, -90f) > 75) return@Cons
            val ang = Mathf.slerp(engine.rotation, -90f, 0.75f)

            Tmp.v1.trns(e.rotation, engine.y, -engine.x)

            e.scaled(80f) { i: EffectContainer ->
                drawFunc(
                    i.x + Tmp.v1.x,
                    i.y + Tmp.v1.y,
                    engine.radius * 1.5f * i.fout(Interp.slowFast),
                    3000 * engine.radius / (type.engineSize + 4),
                    i.rotation + ang - 90
                )
                Fill.circle(i.x + Tmp.v1.x, i.y + Tmp.v1.y, engine.radius * 1.5f * i.fout(Interp.slowFast))
            }

            randLenVectors(
                (e.id + index).toLong(), 22, 400 * engine.radius / (type.engineSize + 4), e.rotation + ang - 90, 0f
            ) { x: Float, y: Float ->
                lineAngle(
                    e.x + x + Tmp.v1.x, e.y + y + Tmp.v1.y, Mathf.angle(x, y), e.fout() * 60
                )
            }
        }

        Draw.color()
        Draw.mixcol(e.color, 1f)
        Draw.alpha(e.fin() + 0.3f)
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

        randLenVectors(e.id.toLong(), 10, e.finpow() * 10f, e.rotation, 22f) { x: Float, y: Float ->
            Draw.color(
                //  Tmp.c1.set(powder.color).mul(rand.random(1f, 1.1f)),
                //  Tmp.c2.set(powder.color).mul(rand.random(0.4f, 0.6f)),
                e.finpow() / 2f
            )
            Fill.circle(e.x + x, e.y + y, 0.1f + e.fout() * 2f)
        }
    }

    fun drawFunc(x: Float, y: Float, width: Float, length: Float, angle: Float) {
        val wx = trnsx(angle + 90, width)
        val wy = trnsy(angle + 90, width)
        Fill.tri(x + wx, y + wy, x - wx, y - wy, trnsx(angle, length) + x, trnsy(angle, length) + y)
    }

    fun unitMountSXY(unit: Any, weapon: Weapon, cons: Cons2<Float, Float>) {
        if (unit is Unit) {
            val mount = unit.mounts.find {
                it.weapon.name.equals(weapon.name)
            } ?: return
            val weapon = mount.weapon
            val mountX = unit.x + trnsx(unit.rotation - 90, weapon.x, weapon.y)
            val mountY = unit.y + trnsy(unit.rotation - 90, weapon.x, weapon.y)
            val weaponRotation = unit.rotation - 90 + (if (weapon.rotate) mount.rotation else weapon.baseRotation)
            val bulletX = mountX + trnsx(weaponRotation, weapon.shootX, weapon.shootY)
            val bulletY = mountY + trnsy(weaponRotation, weapon.shootX, weapon.shootY)
            cons.get(bulletX, bulletY)
        }
    }

    /*** @param lengthSize 该值决定火焰最终长度 子弹的话一般是速度*时间 */
    fun changeFlame(lengthSize: Float): Effect {
        return Effect(32f, 80f) { e ->
            Draw.color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin())
            randLenVectors(e.id.toLong(), 8, e.finpow() * lengthSize, e.rotation, 10f) { x: Float, y: Float ->
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f)
            }
        }
    }

    //生成多个小方块特效
    fun square(color: Color, life: Float = 15f, length: Float = 4f, size: Float = 2f): Effect {
        return Effect(life) { e ->
            randLenVectors(e.id.toLong(), 6, length + e.fin() * 5f) { x: Float, y: Float ->
                Draw.color(color, Color.white, e.fin())
                Fill.square(e.x + x, e.y + y, 0.5f + e.fout() * size, 45f)
            }
        }
    }

  fun shieldWave(unit: Unit, color: Color =IceColor.b4, range: Float = 50f): Effect {
    return Effect(30f) { e: EffectContainer ->
      val color = color.cpy().a(e.fout())
      IceDraw.light(
        unit.x, unit.y, Lines.circleVertices(70f), range, e.rotation,
        Color.clear,
        color, e::fout
      )
    }
  }

    val arcend: IEffect = IEffect {
        set(120f) { e ->
            Draw.color(e.color)
            Draw.scl(3f, 3f)
            Draw.alpha(e.fout())
            Draw.rect(e.data as TextureRegion, e.x, e.y)
            Draw.scl()
            Lines.stroke(4f)
            rand.setSeed(e.id.toLong())
            IceDraw.arc(e.x, e.y, 64f * (2 * e.fin()), 2f, 360f, 0f)
            IceDraw.arc(e.x, e.y, 48f * (2 * e.fin()), 2f, 360f, 0f)
            val edge = 16f
            val width = 4f

            Drawf.tri(e.x + edge, e.y, width, 80f, 0f)
            Drawf.tri(e.x, e.y + edge, width, 80f, 90f)
            Drawf.tri(e.x - edge, e.y, width, 80f, 180f)
            Drawf.tri(e.x, e.y - edge, width, 80f, 270f)
        }
    }
    fun getPhaseJump(x: Float, y: Float, rotate: Float, color: Color, team: Team, unit: UnitType, text: TextureRegion){
         IEffect{
            setRun(0.9f) { e ->
                DeBugFragment.spawnAction(unit, e.x, e.y, rotate, color,team)
                arcend.at(e.x, e.y,0f,color,text)
            }
            set(5 * 60f) { e ->
                Draw.color(color)
                Draw.alpha(Interp.pow4Out.apply(e.fin()))
                Draw.scl(3f, 3f)
                Draw.rect(text, e.x, e.y)
                Draw.scl()
                Lines.stroke(4f)
                rand.setSeed(e.id.toLong())
                IceDraw.arc(e.x, e.y, 64f, 2f, 360 * e.fin(), e.fin() * max(100f, rand.random(360f)
                ) * 2.5f)
                IceDraw.arc(e.x, e.y, 48f, 2f, 360 * e.fin(), -e.fin() * max(100f, rand.random(360f)
                ) * 2.5f)
                val edge = 16f
                val width = 4f

                Drawf.tri(e.x + edge, e.y, width, 80f, 0f)
                Drawf.tri(e.x, e.y + edge, width, 80f, 90f)
                Drawf.tri(e.x - edge, e.y, width, 80f, 180f)
                Drawf.tri(e.x, e.y - edge, width, 80f, 270f)
                val len = 80 / 4f
                fun drawtri(x: Float, y: Float, width: Float, length: Float) {
                    (1..4).forEach {
                        Drawf.tri(x + e.x, y + e.y, width, length, it * 90 + 720f * e.fin())
                    }
                }

                (1..4).forEach { index ->
                    rand.setSeed(e.id + index.toLong())
                    val random = rand.random(80f, 100f)
                    Tmp.v2.set(random, random).scl(e.fout())
                    Tmp.v2.setAngle(rand.random(360f, 720f) * e.fin())
                    val x = (when (index) {
                        1 -> 1
                        2 -> -1
                        3 -> 1
                        4 -> -1
                        else -> 0
                    })
                    val y = (when (index) {
                        1 -> 1
                        2 -> 1
                        3 -> -1
                        4 -> -1
                        else -> 0
                    })
                    drawtri(x * Tmp.v2.x, y * Tmp.v2.y, 4f, len)
                }
            }
        }.at(x,y)
    }

    val arc = IEffect {
        setRun(0.9f) { e ->
            DeBugFragment.spawnAction(e.data as UnitType, e.x, e.y, 90f, IceColor.b4,Vars.player.team())
            arcend.at(e.x, e.y,0f,IceColor.b4,IStyles.afehs)
        }
        set(5 * 60f) { e ->
            Draw.color(IceColor.b4)
            Draw.alpha(Interp.pow4Out.apply(e.fin()))
            Draw.scl(3f, 3f)
            Draw.rect(IStyles.afehs, e.x, e.y)
            Draw.scl()
            Lines.stroke(4f)
            rand.setSeed(e.id.toLong())
            IceDraw.arc(e.x, e.y, 64f, 2f, 360 * e.fin(), e.fin() * max(100f, rand.random(360f)
            ) * 2.5f)
            IceDraw.arc(e.x, e.y, 48f, 2f, 360 * e.fin(), -e.fin() * max(100f, rand.random(360f)
            ) * 2.5f)
            val edge = 16f
            val width = 4f

            Drawf.tri(e.x + edge, e.y, width, 80f, 0f)
            Drawf.tri(e.x, e.y + edge, width, 80f, 90f)
            Drawf.tri(e.x - edge, e.y, width, 80f, 180f)
            Drawf.tri(e.x, e.y - edge, width, 80f, 270f)
            val len = 80 / 4f
            fun drawtri(x: Float, y: Float, width: Float, length: Float) {
                (1..4).forEach {
                    Drawf.tri(x + e.x, y + e.y, width, length, it * 90 + 720f * e.fin())
                }
            }

            (1..4).forEach { index ->
                rand.setSeed(e.id + index.toLong())
                val random = rand.random(80f, 100f)
                Tmp.v2.set(random, random).scl(e.fout())
                Tmp.v2.setAngle(rand.random(360f, 720f) * e.fin())
                val x = (when (index) {
                    1 -> 1
                    2 -> -1
                    3 -> 1
                    4 -> -1
                    else -> 0
                })
                val y = (when (index) {
                    1 -> 1
                    2 -> 1
                    3 -> -1
                    4 -> -1
                    else -> 0
                })
                drawtri(x * Tmp.v2.x, y * Tmp.v2.y, 4f, len)
            }
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
            (0..amount).forEach { _ ->
                val rot: Float = e.rotation + Fx.rand.range(range)
                Fx.v.trns(rot, Fx.rand.random(e.finpow() * 10f))
                Fill.poly(
                    e.x + Fx.v.x, e.y + Fx.v.y, 4, e.fout() * 2f + 0.2f, Fx.rand.random(360f)
                )
            }
        }
    }

    val baseHitEffect = Effect(14f) { e ->
        Draw.color(Color.white, IceColor.b4, e.fin())
        e.scaled(7f) { s ->
            Lines.stroke(0.5f + s.fout())
            Lines.circle(e.x, e.y, s.fin() * 5f)
        }
        Lines.stroke(0.5f + e.fout())
        randLenVectors(e.id.toLong(), 5, e.fin() * 15f) { x: Float, y: Float ->
            val ang = Mathf.angle(x, y)
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f)
        }
        Drawf.light(e.x, e.y, 20f, IceColor.b4, 0.6f * e.fout())
    }

    fun blastExplosion(color: Color = Pal.missileYellow): Effect {
        return Effect(22f) { e: EffectContainer ->
            Draw.color(color)
            e.scaled(6f) { i: EffectContainer ->
                Lines.stroke(3f * i.fout())
                Lines.circle(e.x, e.y, 3f + i.fin() * 15f)
            }
            Draw.color(Color.gray)
            randLenVectors(
                e.id.toLong(), 5, 2f + 23f * e.finpow()) { x: Float, y: Float ->
                Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f)
            }
            Draw.color(color)
            Lines.stroke(e.fout())
            randLenVectors((e.id + 1).toLong(), 4, 1f + 23f * e.finpow()) { x: Float, y: Float ->
                lineAngle(
                    e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f)
            }
            Drawf.light(e.x, e.y, 45f, Pal.missileYellowBack, 0.8f * e.fout())
        }
    }

    fun lightningShoot(
        life: Float = 12f,
        color: Color = IceColor.b4,
        amount: Int = 8,
        length: Float = 21f,
        range: Float = 30f
    ): Effect {
        return Effect(life) { e ->
            Draw.color(Color.white, color, e.fin())
            Lines.stroke(e.fout() * 1.2f + 0.5f)
            randLenVectors(e.id.toLong(), amount, length * e.finpow(), e.rotation, range
            ) { x: Float, y: Float ->
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5f + 2f)
            }
        }
    }

    fun baseShootEffect(color: Color = IceColor.b4): Effect {
        val effect = Effect(8f) { e ->
            Draw.color(color, Color.white, e.fin())
            val w = 1f + 5 * e.fout()
            Drawf.tri(e.x, e.y, w, 15f * e.fout(), e.rotation)
            Drawf.tri(e.x, e.y, w, 3f * e.fout(), e.rotation + 180f)
        }
        val effect2 = Effect(16f) { e ->
            Draw.color(color, Color.white, e.fin())
            Lines.stroke(0.5f + e.fout())
            randLenVectors(e.id.toLong(), 5, e.fin() * 15f, e.rotation, 30f) { x, y ->
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3 + 1f)
            }
        }
        return MultiEffect(effect, effect2)
    }
}
