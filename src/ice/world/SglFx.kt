package ice.world

import arc.func.Cons
import arc.func.Func2
import arc.graphics.Color
import arc.graphics.g2d.Bloom
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Draw.color
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Geometry
import arc.math.geom.Position
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.graphics.IceColor
import ice.graphics.lightnings.LightningContainer
import ice.graphics.lightnings.LightningContainer.PoolLightningContainer
import ice.graphics.lightnings.LightningVertex
import ice.graphics.lightnings.generator.RandomGenerator
import ice.library.util.Functions
import ice.world.content.unit.ability.MirrorFieldAbility
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Effect
import mindustry.entities.effect.MultiEffect
import mindustry.gen.Building
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import singularity.graphic.SglDraw
import kotlin.math.ceil
import kotlin.math.min

@Suppress("unused")
object SglFx {
    val rand: Rand = Rand()/*  public final static Effect cellScan = new Effect(45, e -> {
        Draw.color(e.color, 0.6f);

        if(e.data instanceof GameOfLife b){
          Fill.square(e.x, e.y, b.cellSize/2*e.fslope(), e.rotation);
        }
      });*/

    /* public final static Effect cellDeath = new Effect(45, e -> {
       Draw.color(e.color);

       if(e.data instanceof GameOfLife b){
         Lines.stroke(b.cellSize/2*e.fout());

         Lines.square(e.x, e.y, b.gridSize*2*e.fin(Interp.pow2Out), e.rotation);
       }
     });

     *//*  public final  Effect cellScan = new Effect(45, e -> {
    Draw.color(e.color, 0.6f);

    if(e.data instanceof GameOfLife b){
      Fill.square(e.x, e.y, b.cellSize/2*e.fslope(), e.rotation);
    }
  });*//*  public final  Effect cellScan = new Effect(45, e -> {
    Draw.color(e.color, 0.6f);

    if(e.data instanceof GameOfLife b){
      Fill.square(e.x, e.y, b.cellSize/2*e.fslope(), e.rotation);
    }
  });*/

    val lightningCont: Effect = Effect(200f) { e ->
        val data = e!!.data
        if (data is LightningContainer) {
            data.update()

            color(e.color)
            data.draw(e.x, e.y)
        }
    }

    val colorLaserCharge: Effect = Effect(38f) { e ->
        color(e!!.color)
        Angles.randLenVectors(e.id.toLong(), 14, 1f + 20f * e.fout(), e.rotation, 120f) { x: Float, y: Float ->
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f)
        }
    }

    val colorLaserChargeBegin: Effect = Effect(60f) { e ->
        val margin = 1f - Mathf.curve(e!!.fin(), 0.9f)
        val fin = min(margin, e.fin())

        color(e.color)
        Fill.circle(e.x, e.y, fin * 3f)

        color()
        Fill.circle(e.x, e.y, fin * 2f)
    }

    var mirrorShieldBreak: Effect = Effect(40f) { e ->
        Lines.stroke(1.4f * e!!.fout())
        var radius = 130f
        val data = e.data
        if (data is MirrorFieldAbility) {
            radius = data.nearRadius
        }

        Mathf.rand.setSeed(e.id.toLong())
        Angles.randLenVectors(e.id.toLong(), Mathf.rand.random(radius.toInt() / 5, radius.toInt() / 3), 0f, radius) { x: Float, y: Float ->
            val offX = Mathf.rand.random(-16f, 16f) * e.fout(Interp.pow2Out)
            val offY = Mathf.rand.random(-16f, 16f) * e.fout(Interp.pow2Out)
            color(e.color, e.color.a * 0.4f)
            Fill.poly(e.x + x + offX, e.y + y + offY, 6, 10 * e.fout(Interp.pow4))
            Draw.alpha(1f)
            Lines.poly(e.x + x + offX, e.y + y + offY, 6, 10f)
        }
    }.followParent(true)
    val spreadSizedDiamond: Effect = Effect(42f) { e ->
        color(e!!.color)
        Lines.stroke(12f * e.fout())
        Lines.square(e.x, e.y, e.rotation * e.fin(Interp.pow2Out), 45f)
    }

    val spreadDiamond: Effect = Effect(35f) { e ->
        color(e!!.color)
        Lines.stroke(12f * e.fout())
        Lines.square(e.x, e.y, 32 * e.fin(Interp.pow2Out), 45f)
    }

    val spreadDiamondSmall: Effect = Effect(25f) { e ->
        color(e!!.color)
        Lines.stroke(8f * e.fout())
        Lines.square(e.x, e.y, 18 * e.fin(Interp.pow2Out), 45f)
    }
    val gasLeak: Effect = Effect(90f, Cons { e ->
        if (e!!.data<Any?>() !is Number) return@Cons
        val param = (e.data<Any?>() as Number).toFloat()

        color(e.color, Color.lightGray, e.fin())
        Draw.alpha(0.75f * param * e.fout())
        Angles.randLenVectors(e.id.toLong(), 1, 8f + e.fin() * (param + 3)) { x: Float, y: Float ->
            Fill.circle(e.x + x, e.y + y, 0.55f + e.fslope() * 4.5f)
        }
    })

    val moveParticle: Effect = Effect(90f) { e ->
        color(e!!.color)
        Tmp.v1.setZero()
        val data = e.data
        if (data is Number) {
            Tmp.v1.set(data.toFloat(), 0f).setAngle(e.rotation)
        }

        val rad = Mathf.randomSeed(e.id.toLong(), 1f, 3f) * e.fout(Interp.pow2Out)
        Fill.circle(e.x + Tmp.v1.x * e.fin(), e.y + Tmp.v1.y * e.fin(), rad)
    }

    val moveDiamondParticle: Effect = Effect(90f) { e ->
        color(e!!.color)
        Tmp.v1.setZero()
        val data = e.data
        if (data is Number) {
            Tmp.v1.set(data.toFloat(), 0f).setAngle(e.rotation)
        }

        val rad = Mathf.randomSeed(e.id.toLong(), 1.6f, 3.4f) * e.fout(Interp.pow2Out)
        if (Mathf.randomSeed(e.id.toLong()) > 0.5f) {
            Lines.stroke(rad / 2f)
            Lines.square(e.x + Tmp.v1.x * e.fin(), e.y + Tmp.v1.y * e.fin(), rad, e.fin() * Mathf.randomSeed(e.id.toLong(), 180f, 480f))
        } else {
            Fill.square(e.x + Tmp.v1.x * e.fin(), e.y + Tmp.v1.y * e.fin(), rad, e.fin() * Mathf.randomSeed(e.id.toLong(), 180f, 480f))
        }
    }

    val cloudGradient: Effect = Effect(45f) { e ->
        color(e!!.color, 0f)
        Draw.z(Layer.flyingUnit + 1)
        SglDraw.gradientCircle(e.x, e.y, 14 * e.fout(), 0.6f)
    }

    val shootRecoilWave: Effect = Effect(40f) { e ->
        color(e!!.color)
        for (i in Mathf.signs) {
            Drawf.tri(e.x, e.y, 15f * e.fout(), 50f, e.rotation + 40f * i)
        }
    }

    val impactWaveSmall: Effect = Effect(18f) { e ->
        color(e!!.color)
        Lines.stroke(5 * e.fout())
        Lines.circle(e.x, e.y, 36 * e.fin(Interp.pow3Out))
    }

    val impactWave: Effect = Effect(24f) { e ->
        color(e!!.color)
        Lines.stroke(6 * e.fout())
        Lines.circle(e.x, e.y, 48 * e.fin(Interp.pow3Out))
    }

    val impactWaveBig: Effect = Effect(30f) { e ->
        color(e!!.color)
        Lines.stroke(6.5f * e.fout())
        Lines.circle(e.x, e.y, 55 * e.fin(Interp.pow3Out))
    }

    val impactWaveLarge: Effect = Effect(38f) { e ->
        color(e!!.color)
        Lines.stroke(7.3f * e.fout())
        Lines.circle(e.x, e.y, 80 * e.fin(Interp.pow3Out))
    }

    val polyParticle: Effect = Effect(150f) { e ->  //这段代码很特殊，闪光线的代码并不是我写的，这来自一个bug，大概是来自Lines的闭合线问题，意料之外，但效果还不错，就留着了
        Angles.randLenVectors(e!!.id.toLong(), 1, 24f, e.rotation + 180, 20f) { x: Float, y: Float ->
            val vertices = Mathf.randomSeed((e.id + x).toInt().toLong(), 3, 6)
            val step = 360f / vertices

            Fill.polyBegin()
            Lines.beginLine()

            for (i in 0..<vertices) {
                val radius = Mathf.randomSeed((e.id + i).toLong(), 1.5f, 4f) * e.fout(Interp.pow3Out)
                val lerp = e.fin(Interp.pow2Out)
                val rot = Mathf.randomSeed((e.id + i).toLong(), -360, 360).toFloat()
                val off = Mathf.randomSeed((e.id + i + 1).toLong(), -step / 2, step / 2)
                val angle = step * i + rot * lerp + off
                val dx = Angles.trnsx(angle, radius) + x * lerp
                val dy = Angles.trnsy(angle, radius) + y * lerp

                Fill.polyPoint(e.x + dx, e.y + dy)
                Lines.linePoint(e.x + dx, e.y + dy)
            }

            Draw.z(Layer.bullet - 5f)
            color(e.color, 0.5f)
            Fill.polyEnd()

            Draw.z(Layer.effect)
            Lines.stroke(0.4f * e.fout(), e.color)
            Lines.endLine(true)
        }
    }

    val impactBubbleSmall: Effect = Effect(40f) { e ->
        color(e!!.color)
        Angles.randLenVectors(e.id.toLong(), 9, 20f) { x: Float, y: Float ->
            val s = Mathf.randomSeed((e.id + x).toInt().toLong(), 3f, 6f)
            Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), s * e.fout())
        }
    }

    val impactBubble: Effect = Effect(60f) { e ->
        color(e.color)
        Angles.randLenVectors(e.id.toLong(), 12, 26f) { x: Float, y: Float ->
            val s = Mathf.randomSeed((e.id + x).toInt().toLong(), 4f, 8f)
            Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), s * e.fout())
        }
    }

    val impactBubbleBig = Effect(79f) { e ->
        color(e.color)
        Angles.randLenVectors(e.id.toLong(), 15, 45f) { x: Float, y: Float ->
            val s = Mathf.randomSeed((e.id + x).toInt().toLong(), 5f, 10f)
            Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), s * e.fout())
        }
    }

    val crystalConstructed = Effect(60f) { e ->
        color(e.color)
        Lines.stroke(4 * e.fout())

        Draw.z(Layer.effect)
        Lines.square(e.x, e.y, 12 * e.fin(), 45f)
    }

    val hadronReconstruct = Effect(60f) { e ->
        color(Pal.reactorPurple)
        Lines.stroke(3f * e.fout())
        Draw.z(Layer.effect)
        Angles.randLenVectors(e.id.toLong(), 3, 12f) { x: Float, y: Float ->
            Lines.square(e.x + x, e.y + y, (14 + Mathf.randomSeed((e.id + (x * y).toInt()).toLong(), -2, 2)) * e.fin(), e.fin() * Mathf.randomSeed((e.id + (x * y).toInt()).toLong(), -90, 90))
        }
    }

    val polymerConstructed = Effect(60f) { e ->
        color(Pal.reactorPurple)
        Lines.stroke(6 * e.fout())

        Lines.square(e.x, e.y, 30 * e.fin())
        Lines.square(e.x, e.y, 30 * e.fin(), 45f)
    }

    val spreadField = Effect(60f) { e ->
        color(e.color)
        Lines.stroke(8 * e.fout())

        Lines.square(e.x, e.y, 38 * e.fin(Interp.pow2Out))
        Lines.square(e.x, e.y, 38 * e.fin(Interp.pow2Out), 45f)
    }

    val forceField = Effect(45f) { e ->
        color(e.color)
        val data = e.data
        if (data is Float) {
            Draw.alpha(data)
        }
        val endRot = ((ceil((e.rotation / 45).toDouble()).toInt() + 1) * 45).toFloat()

        Draw.z(Layer.effect)
        Lines.stroke(Mathf.lerp(1.5f, 0.4f, e.fin()))
        Lines.square(e.x, e.y, Mathf.lerp(35f, 3f, e.fin()), Mathf.lerp(e.rotation, endRot, e.fin()))
    }

    val FEXsmoke = Effect(80f) { e ->
        val move = Mathf.clamp(e!!.fin() / 0.35f)
        val size = 1 - Mathf.clamp((e.fin() - 0.65f) / 0.35f)

        Draw.z(Layer.effect)
        Angles.randLenVectors(e.id.toLong(), 6, 4f + Functions.lerp(0.0, 9.0, 0.1, (move * 40).toDouble()).toFloat()) { x: Float, y: Float ->
            color(IceColor.fexCrystal, Color.lightGray, Mathf.clamp(e.fin() + Mathf.random(-0.1f, 0.1f)))
            Fill.square(e.x + x, e.y + y, 0.2f + size * 2f + Mathf.random(-0.15f, 0.15f), 45f)
        }
    }

    val shootSmokeMissileSmall = Effect(130f, 300f) { e ->
        color(e!!.color)
        Draw.alpha(0.5f)
        Mathf.rand.setSeed(e.id.toLong())
        for (i in 0..17) {
            Tmp.v1.trns(e.rotation + 180f + Mathf.rand.range(19f), Mathf.rand.random(e.finpow() * 60f)).add(Mathf.rand.range(2f), Mathf.rand.range(2f))
            e.scaled(e.lifetime * Mathf.rand.random(0.2f, 1f)) { b ->
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, b!!.fout() * 3.5f + 0.3f)
            }
        }
    }

    val impWave = Effect(10f) { e ->
        color(Color.white)
        Lines.stroke(e!!.fout())
        Lines.circle(e.x, e.y, Mathf.randomSeed(e.id.toLong(), 8, 10) * e.fin())
    }

    val glowParticle = Effect(45f) { e ->
        color(e.color, Color.white, e.fin())
        Angles.randLenVectors(e.id.toLong(), 1, 3.5f, e.rotation, 5f) { x: Float, y: Float ->
            Fill.circle(e.x + x * e.fin(Interp.pow2Out), e.y + y * e.fin(Interp.pow2Out), 1.6f * e.fout(Interp.pow2Out))
        }
    }

    val freezingBreakDown = Effect(180f, Cons { e ->
        val data = e.data
        if (data !is Unit) return@Cons
        val size: Float = data.hitSize * 1.2f

        val intensity = size / 32 - 2.2f
        val baseLifetime = 25f + intensity * 11f

        e.scaled(baseLifetime) { b ->
            color()
            b!!.scaled(5 + intensity * 2f) { i ->
                Lines.stroke((3.1f + intensity / 5f) * i!!.fout())
                Lines.circle(b.x, b.y, (3f + i.fin() * 14f) * intensity)
                Drawf.light(b.x, b.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * b.fout())
            }

            color(IceColor.winter, IceColor.frost, b.fin())
            Lines.stroke((2f * b.fout()))

            Draw.z(Layer.effect + 0.001f)
            Angles.randLenVectors((b.id + 1).toLong(), b.finpow() + 0.001f, (8 * intensity).toInt(), 28f * intensity) { x: Float, y: Float, `in`: Float, out: Float ->
                Lines.lineAngle(b.x + x, b.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity))
                Drawf.light(b.x + x, b.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f)
            }
        }

        val rate = e.fout(Interp.pow2In)
        val l = size * rate * 1.2f
        val w = size * rate * 0.2f

        val x = e.x
        val y = e.y
        val fout = e.fout()
        val fin = e.fin()
        Drawf.light(x, y, fout * size, IceColor.winter, 0.7f)

        val lerp = e.fin(Interp.pow3Out)
        val id = e.id

        SglDraw.drawBloomUponFlyUnit<Any?>(null) { n: Any? ->
            color(IceColor.winter)
            SglDraw.drawLightEdge(x, y, l, w, l, w)
            Lines.stroke(5f * fout)
            Lines.circle(x, y, 55 * fout)

            SglDraw.gradientCircle(x, y, size * lerp, -size * lerp * fout, 1f)
            Draw.reset()
        }

        Draw.z(Layer.flyingUnit + 1)
        Angles.randLenVectors(id.toLong(), Mathf.randomSeed(id.toLong(), size / 6, size / 3).toInt(), size / 2, size * 2) { dx: Float, dy: Float ->
            val s = Mathf.randomSeed((id + dx).toInt().toLong(), size / 4, size / 2)
            val le = 1 - Mathf.pow(fin, 4f)
            SglDraw.drawCrystal(
                x + dx * lerp, y + dy * lerp, s, s * le * 0.35f, s * le * 0.24f, 0f, 0f, 0.8f * le, Layer.effect, Layer.bullet - 1, Time.time * Mathf.randomSeed(id.toLong(), -3.5f, 3.35f) + Mathf.randomSeed((id + dx).toLong(), 360f), Mathf.angle(dx, dy), Tmp.c1.set(IceColor.frost).a(0.65f), IceColor.winter
            )
        }
    })

    val crossLightMini = Effect(22f) { e ->
        color(e.color)
        for (i in Mathf.signs) {
            SglDraw.drawDiamond(e.x, e.y, 12 + 64 * e.fin(Interp.pow3Out), 5 * e.fout(Interp.pow3Out), e.rotation + 45 + i * 45)
        }
    }

    val crossLightSmall = Effect(26f) { e ->
        color(e.color)
        for (i in Mathf.signs) {
            SglDraw.drawDiamond(e.x, e.y, 22 + 74 * e.fin(Interp.pow3Out), 8 * e.fout(Interp.pow3Out), e.rotation + 45 + i * 45)
        }
    }

    val crossLight = Effect(30f) { e ->
        color(e.color)
        for (i in Mathf.signs) {
            SglDraw.drawDiamond(e.x, e.y, 32 + 128 * e.fin(Interp.pow3Out), 12 * e.fout(Interp.pow3Out), e.rotation + 45 + i * 45)
        }
    }

    val shootCrossLight = Effect(120f) { e ->
        color(e!!.color)
        val l = e.fout(Interp.pow3Out)
        SglDraw.drawLightEdge(e.x, e.y, 140f, 5.5f * l, 140f, 5.5f * l, e.rotation + 220 * e.fin(Interp.pow3Out))
    }

    val shootCrossLightLarge = Effect(140f) { e ->
        color(e!!.color)
        val l = e.fout(Interp.pow3Out)
        SglDraw.drawLightEdge(e.x, e.y, 240f, 12.5f * l, 240f, 12.5f * l, e.rotation + 237 * e.fin(Interp.pow3Out))
    }

    val ploymerGravityField: Effect = Effect(32f) { e ->
        color(e!!.color)
        val data = e.data
        if (data is Building) {
            val fout = e.fout()
            val x = e.x
            val y = e.y
            val r = e.rotation

            SglDraw.drawBloomUponFlyUnit<Building?>(data) { bu: Building? ->
                Lines.stroke(2.6f * bu!!.efficiency * fout)
                Lines.circle(x, y, r * fout)
                Draw.reset()
            }
        }
    }

    val weaveTrail: Effect = Effect(12f) { e ->
        color(e!!.color, Color.white, e.fin())
        SglDraw.drawDiamond(e.x, e.y, 15 + 45 * e.fin(), 8 * e.fout(), e.rotation + 90)
    }

    val steam: Effect = Effect(90f) { e ->
        val motion = if (e!!.data<Any?>() is Vec2) e.data() else Vec2(0f, 0f)
        val len = motion.len()
        color(Color.white)
        Draw.alpha(0.75f * e.fout())
        for (i in 0..4) {
            val curr = motion.cpy().rotate(Mathf.randomSeed(e.id.toLong(), -20, 20).toFloat()).setLength(len * e.finpow())
            Fill.circle(e.x + curr.x, e.y + curr.y, Mathf.randomSeed(e.id.toLong(), 3.5f, 5f) * (0.3f + 0.7f * e.fslope()))
        }
    }

    val steamBreakOut: Effect = Effect(24f) { e ->
        val data = if (e!!.data is FloatArray) e.data() else floatArrayOf(18f, 24f, 0.3f)
        val leng = Mathf.random(data[0], data[1])
        for (i in 0..3) {
            if (Mathf.chanceDelta(data[2].toDouble())) steam.at(e.x, e.y, 0f, Vec2(leng * Geometry.d8(i * 2 + 1).x, leng * Geometry.d8(i * 2 + 1).y))
        }
    }

    val lightCone: Effect = Effect(16f) { e ->
        color(e!!.color)
        SglDraw.drawDiamond(e.x, e.y, 8f, 26 * e.fout(), e.rotation)
    }

    val lightConeHit: Effect = Effect(30f) { e ->
        color(e!!.color)
        val fout = e.fout(Interp.pow2Out)
        val fin = e.fin(Interp.pow2Out)
        Angles.randLenVectors(e.id.toLong(), Mathf.randomSeed((e.id + 1).toLong(), 3, 4), 30f, e.rotation, 60f) { dx: Float, dy: Float ->
            Drawf.tri(e.x - dx * fin, e.y - dy * fin, 6f * fout, 6 + 15 * fout, Mathf.angle(dx, dy) + 180)
            Drawf.tri(e.x - dx * fin, e.y - dy * fin, 6f * fout, 6f * fout, Mathf.angle(dx, dy))
        }
    }

    val matrixDrill: Effect = Effect(45f) { e ->
        color(e!!.color)
        Lines.stroke(1.8f * e.fout())
        Lines.square(e.x, e.y, 3f + 12 * e.fin(), 45f)
        Fill.square(e.x, e.y, 3 * e.fout(), 45 + 360 * e.fin(Interp.pow3Out))
    }

    val lightConeTrail: Effect = Effect(20f) { e ->
        color(e!!.color)
        val i = if (Mathf.randomSeed(e.id.toLong()) > 0.5f) 1 else -1
        val off = Mathf.randomSeed(e.id.toLong(), -10, 10).toFloat()
        val fout = e.fout(Interp.pow2Out)

        val rot = e.rotation + 156f * i + off
        val dx = Angles.trnsx(rot, 24f, 0f) * e.fin(Interp.pow2Out)
        val dy = Angles.trnsy(rot, 24f, 0f) * e.fin(Interp.pow2Out)

        Drawf.tri(e.x + dx, e.y + dy, 8f * fout, 8 + 24 * fout, rot)
        Drawf.tri(e.x + dx, e.y + dy, 8f * fout, 8f * fout, rot + 180)
    }

    val trailLine: Effect = Effect(24f) { e ->
        color(e!!.color)
        Drawf.tri(e.x, e.y, 2f * e.fout(), 2 + 6 * e.fout(), e.rotation)
        Drawf.tri(e.x, e.y, 2f * e.fout(), 8 + 10 * e.fout(), e.rotation + 180)
    }

    val trailLineLong: Effect = Effect(30f) { e ->
        color(e.color)
        Drawf.tri(e.x, e.y, 4 * e.fout(), 6 + 8 * e.fout(), e.rotation)
        Drawf.tri(e.x, e.y, 4 * e.fout(), 10 + 16 * e.fout(), e.rotation + 180)
    }

    val spreadSparkLarge: Effect = Effect(28f) { e ->
        color(Color.white, e!!.color, e.fin())
        Lines.stroke(e.fout() * 1.2f + 0.5f)
        Angles.randLenVectors(e.id.toLong(), 20, 10f * e.fin(), 27f) { x: Float, y: Float ->
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 5f + 0.5f)
        }
    }

    val circleSparkMini: Effect = Effect(32f) { e ->
        color(Color.white, e!!.color, e.fin())
        Lines.stroke(e.fout() * 0.8f + 0.2f)
        Angles.randLenVectors(e.id.toLong(), 22, 4f * e.fin(), 12f) { x: Float, y: Float ->
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 2.2f)
        }
    }

    val constructSpark: Effect = Effect(24f) { e ->
        val c = e!!.color
        val fin = e.fin()
        val fs = e.fslope()
        val ex = e.x
        val ey = e.y
        val id = e.id
        SglDraw.drawBloomUponFlyUnit {
            color(Color.white, c, fin)
            Lines.stroke((1 - fin) * 0.8f + 0.2f)

            Angles.randLenVectors(id.toLong(), 22, 4f * fin, 12f) { x: Float, y: Float ->
                Lines.lineAngle(ex + x, ey + y, Mathf.angle(x, y), fs * 2.2f)
            }
            Draw.reset()
        }
    }

    val circleSparkLarge: Effect = Effect(65f) { e ->
        color(Color.white, e!!.color, e.fin())
        Lines.stroke(e.fout() * 1.4f + 0.5f)
        Angles.randLenVectors(e.id.toLong(), 37, 28f * e.fin(), 49f) { x: Float, y: Float ->
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 9f)
        }
    }

    val diamondSpark: Effect = Effect(30f) { e ->
        color(Color.white, e!!.color, e.fin())
        Lines.stroke(e.fout() * 1.2f + 0.5f)
        Angles.randLenVectors(e.id.toLong(), 7, 6f * e.fin(), 20f) { x: Float, y: Float ->
            SglDraw.drawDiamond(e.x + x, e.y + y, 10f, e.fout(Interp.pow2Out) * 4f, Mathf.angle(x, y))
        }
    }

    val diamondSparkLarge: Effect = Effect(30f) { e ->
        color(Color.white, e!!.color, e.fin())
        Lines.stroke(e.fout() * 1.2f + 0.5f)
        Angles.randLenVectors(e.id.toLong(), 9, 8f * e.fin(), 24f) { x: Float, y: Float ->
            SglDraw.drawDiamond(e.x + x, e.y + y, 12f, e.fout(Interp.pow2Out) * 5f, Mathf.angle(x, y))
        }
    }

    val railShootRecoil: Effect = Effect(12f) { e ->
        color(e!!.color)
        Angles.randLenVectors(e.id.toLong(), Mathf.randomSeed(e.id.toLong(), 2, 4), 24f, e.rotation + 180, 60f) { x: Float, y: Float ->
            val size = Mathf.randomSeed((e.id + x).toInt().toLong(), 6, 12).toFloat()
            val lerp = e.fin(Interp.pow2Out)
            SglDraw.drawDiamond(e.x + x * lerp, e.y + y * lerp, size, size / 2f * e.fout(), Mathf.angle(x, y))
        }
    }

    val trailParticle: Effect = Effect(95f) { e ->
        color(e!!.color)
        Angles.randLenVectors(e.id.toLong(), 3, 35f) { x: Float, y: Float ->
            Fill.circle(e.x + x * e.fin(Interp.pow2In), e.y + y * e.fin(Interp.pow2In), 1.2f * e.fout())
        }
    }

    val iceParticle: Effect = Effect(124f) { e ->
        color(e!!.color)
        val amo = Mathf.randomSeed(e.id.toLong(), 2, 5)
        for (i in 0..<amo) {
            val len = Mathf.randomSeed(e.id + i * 2L, 40f) * e.fin()
            val off = Mathf.randomSeed((e.id + i).toLong(), -8, 8).toFloat()
            val x = Angles.trnsx(e.rotation, len) + Angles.trnsx(e.rotation + 90, off)
            val y = Angles.trnsy(e.rotation, len) + Angles.trnsy(e.rotation + 90, off)
            Fill.circle(e.x + x, e.y + y, 0.9f * e.fout(Interp.pow2Out))
        }
    }

    val auroraCoreCharging: Effect? = Effect(80f, 100f) { e ->
        color(IceColor.matrixNet)
        Lines.stroke(e!!.fin() * 2f)
        Lines.circle(e.x, e.y, 4f + e.fout() * 100f)

        Fill.circle(e.x, e.y, e.fin() * 10)

        Angles.randLenVectors(e.id.toLong(), 20, 40f * e.fout()) { x: Float, y: Float ->
            Fill.circle(e.x + x, e.y + y, e.fin() * 5f)
            Drawf.light(e.x + x, e.y + y, e.fin() * 15f, Pal.heal, 0.7f)
        }

        color()

        Fill.circle(e.x, e.y, e.fin() * 8)
        Drawf.light(e.x, e.y, e.fin() * 16f, Pal.heal, 0.7f)
    }.rotWithParent(true).followParent(true)

    val explodeImpWaveMini: Effect = impactExplode(16f, 36f)

    val explodeImpWaveSmall: Effect = impactExplode(22f, 40f)

    val explodeImpWave: Effect = impactExplode(32f, 50f)

    val explodeImpWaveBig: Effect = impactExplode(40f, 65f)

    val explodeImpWaveLarge: Effect = impactExplode(60f, 95f)

    val explodeImpWaveLaserBlase: Effect = impactExplode(86f, 200f)

    val reactorExplode: Effect = MultiEffect(Fx.reactorExplosion, Effect(180f) { e ->
        val size = if (e.data<Any?>() is Float) e.data() else 120f
        val fin1 = Mathf.clamp(e.fin() / 0.1f)
        val fin2 = Mathf.clamp((e.fin() - 0.1f) / 0.3f)

        color(Pal.reactorPurple)
        Lines.stroke(6 * e.fout())
        val radius = size * (1 - Mathf.pow(e.fout(), 3f))
        Lines.circle(e.x, e.y, radius)

        Draw.z(Layer.effect + 10)
        SglDraw.gradientCircle(e.x, e.y, radius - 3 * e.fout(), -(size / 6) * (1 - e.fin(Interp.pow3)), Draw.getColor().cpy().a(0f))
        Draw.z(Layer.effect)

        val h: Float
        val rate = if (e.fin() > 0.1f) 1 - fin2 else fin1
        h = size / 2 * rate
        val w: Float = h / 5

        Lines.stroke(3f * rate)
        Lines.circle(e.x, e.y, h / 2)

        Fill.quad(
            e.x + h, e.y, e.x, e.y + w, e.x - h, e.y, e.x, e.y - w
        )
        Fill.quad(
            e.x + w, e.y, e.x, e.y + h, e.x - w, e.y, e.x, e.y - h
        )

        val intensity = size / 32 - 2.2f
        val baseLifetime = 25f + intensity * 11f

        color(Pal.reactorPurple2)
        Draw.alpha(0.7f)
        for (i in 0..3) {
            Mathf.rand.setSeed(e.id * 2L + i)
            val lenScl = Mathf.rand.random(0.4f, 1f)
            val fi = i
            e.scaled(e.lifetime * lenScl) { b ->
                Angles.randLenVectors((b!!.id + fi - 1).toLong(), b.fin(Interp.pow10Out), (2.9f * intensity).toInt(), 22f * intensity) { x: Float, y: Float, `in`: Float, out: Float ->
                    val fout = b.fout(Interp.pow5Out) * Mathf.rand.random(0.5f, 1f)
                    val rad = fout * ((2f + intensity) * 2.35f)

                    Fill.circle(b.x + x, b.y + y, rad)
                    Drawf.light(b.x + x, b.y + y, rad * 2.5f, Pal.reactorPurple, 0.5f)
                }
            }
        }
        e.scaled(baseLifetime) { b ->
            color()
            b!!.scaled(5 + intensity * 2f) { i ->
                Lines.stroke((3.1f + intensity / 5f) * i!!.fout())
                Lines.circle(b.x, b.y, (3f + i.fin() * 14f) * intensity)
                Drawf.light(b.x, b.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * b.fout())
            }

            color(Pal.lighterOrange, Pal.reactorPurple, b.fin())
            Lines.stroke((2f * b.fout()))

            Draw.z(Layer.effect + 0.001f)
            Angles.randLenVectors((b.id + 1).toLong(), b.finpow() + 0.001f, (8 * intensity).toInt(), 28f * intensity) { x: Float, y: Float, `in`: Float, out: Float ->
                Lines.lineAngle(b.x + x, b.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity))
                Drawf.light(b.x + x, b.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f)
            }
        }
    })

    val iceExplode: Effect = Effect(128f) { e ->
        val rate = e!!.fout(Interp.pow2In)
        val l = 176 * rate
        val w = 38 * rate

        val x = e.x
        val y = e.y
        val fout = e.fout()
        val fin = e.fin()
        Drawf.light(x, y, fout * 192, IceColor.winter, 0.7f)

        Draw.z(Layer.flyingUnit + 1)

        val lerp = e.fin(Interp.pow3Out)
        val id = e.id
        SglDraw.drawBloomUponFlyUnit<Any?>(null) { n: Any? ->
            color(IceColor.winter)
            SglDraw.drawLightEdge(x, y, l, w, l, w)
            Lines.stroke(5f * fout)
            Lines.circle(x, y, 55 * fout)
            Lines.stroke(8f * fout)
            Lines.circle(x, y, 116 * lerp)

            Angles.randLenVectors(id.toLong(), Mathf.randomSeed(id.toLong(), 16, 22), 128f) { dx: Float, dy: Float ->
                val size = Mathf.randomSeed((id + dx).toInt().toLong(), 14, 24).toFloat()
                SglDraw.drawDiamond(x + dx * lerp, y + dy * lerp, size, size * (1 - Mathf.pow(fin, 2f)) * 0.35f, Mathf.angle(dx, dy))
            }
            Draw.reset()
        }
    }

    val particleSpread: Effect = Effect(125f) { e ->
        color(e!!.color)
        Angles.randLenVectors(e.id.toLong(), 3, 32f) { x: Float, y: Float ->
            Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), 0.9f * e.fout(Interp.pow2Out))
        }
    }

    val movingCrystalFrag: Effect = Effect(45f) { e ->
        val size = Mathf.randomSeed(e!!.id.toLong(), 4, 6) * e.fout()
        color(e.color)
        Angles.randLenVectors(e.id.toLong(), 1, 3f, 6f, e.rotation, 20f) { x: Float, y: Float ->
            SglDraw.drawDiamond(e.x + x * e.fin(Interp.pow2Out), e.y + y * e.fin(Interp.pow2Out), size * 2.5f, size, Angles.angle(x, y))
        }
    }

    val crystalFrag: Effect = Effect(26f) { e ->
        val size = Mathf.randomSeed(e!!.id.toLong(), 2, 4) * e.fout()
        color(e.color)
        SglDraw.drawDiamond(e.x, e.y, size * 2.5f, size, Mathf.randomSeed(e.id.toLong(), 0f, 360f))
    }

    val crystalFragFex: Effect = Effect(26f) { e ->
        val size = Mathf.randomSeed(e!!.id.toLong(), 2, 4) * e.fout()
        color(IceColor.fexCrystal, 0.7f)
        SglDraw.drawDiamond(e.x, e.y, size * 2.5f, size, Mathf.randomSeed(e.id.toLong(), 0f, 360f))
    }

    val iceCrystal: Effect = Effect(120f) { e ->
        val size = Mathf.randomSeed(e!!.id.toLong(), 2, 6) * e.fslope()
        color(e.color)
        val rot = Mathf.randomSeed((e.id + 1).toLong(), 360f)
        val blingX = Angles.trnsx(rot, size * 2)
        val blingY = Angles.trnsy(rot, size * 2)
        SglDraw.drawDiamond(e.x, e.y, size * 2, size / 2, rot)
        e.scaled(45f) { ec ->
            SglDraw.drawDiamond(
                ec!!.x + blingX, ec.y + blingY, 85 * ec.fslope(), 1.2f * ec.fslope(), Mathf.randomSeed((ec.id + 2).toLong(), 360f) + Mathf.randomSeed((ec.id + 3).toLong(), -15, 15) * ec.fin()
            )
        }
    }

    val shootRail: Effect = Effect(60f) { e ->
        e!!.scaled(12f) { b ->
            Lines.stroke(b!!.fout() * 4f + 0.2f, e.color)
            Lines.circle(b.x, b.y, b.fin() * 70f)
            Lines.stroke(b.fout() * 2.3f + 0.15f)
            Lines.circle(b.x, b.y, b.fin() * 62f)
        }
        val lerp = e.fout(Interp.pow2Out)
        color(e.color)
        SglDraw.drawLightEdge(e.x, e.y, 64 + 64 * lerp, 10 * lerp, 60 + 80 * lerp, 6 * lerp, e.rotation + 90)
    }

    val winterShooting: Effect = Effect(60f) { e ->
        e!!.scaled(12f) { b ->
            Lines.stroke(b!!.fout() * 4f + 0.2f, IceColor.winter)
            Lines.circle(b.x, b.y, b.fin() * 75f)
        }
        val lerp = e.fout(Interp.pow2Out)
        color(IceColor.winter)
        SglDraw.drawLightEdge(e.x, e.y, 64 + 64 * lerp, 12 * lerp, 60 + 80 * lerp, 6 * lerp, e.rotation + 90)

        val l = e.fin(Interp.pow2Out)
        Angles.randLenVectors(e.id.toLong(), Mathf.randomSeed(e.id.toLong(), 8, 16), 48f, e.rotation + 180, 60f) { x: Float, y: Float ->
            val size = Mathf.randomSeed((e.id + x).toInt().toLong(), 12, 20).toFloat()
            SglDraw.drawDiamond(e.x + x * l, e.y + y * l, size, size / 2f * e.fout(), Mathf.angle(x, y))
        }
    }

    val laserBlastWeaveLarge: Effect = Effect(280f, 200f) { e ->
        val size = 140f
        val fin1 = Mathf.clamp(e!!.fin() / 0.1f)
        val fin2 = Mathf.clamp((e.fin() - 0.1f) / 0.3f)

        color(e.color)
        val radius = size * e.fin(Interp.pow4Out)

        Draw.alpha(0.6f)
        SglDraw.gradientCircle(e.x, e.y, radius, -radius * e.fout(Interp.pow2Out), 0f)
        Draw.alpha(1f)
        Lines.stroke(6 * e.fout(Interp.pow2Out))
        Lines.circle(e.x, e.y, radius)

        val h: Float
        val rate = if (e.fin() > 0.1f) 1 - fin2 else fin1
        h = size * 1.26f * rate
        val w: Float = h / 4

        Lines.stroke(3f * rate)
        Lines.circle(e.x, e.y, h / 2)

        SglDraw.drawLightEdge(e.x, e.y, h, w, h, w)

        Mathf.rand.setSeed(e.id.toLong())
        for (i in 0..13) {
            val rot = Mathf.rand.random(0f, 360f)
            val wi = Mathf.rand.random(12, 18).toFloat()
            val le = Mathf.rand.random(wi * 2f, wi * 4f)

            SglDraw.drawTransform(e.x, e.y, radius, 0f, rot) { x: Float, y: Float, ro: Float ->
                Drawf.tri(x, y, wi * e.fout(), le, ro - 180)
            }
        }

        e.scaled(100f) { ef ->
            Angles.randLenVectors(e.id.toLong(), 9, 45f, 164f) { x: Float, y: Float ->
                val lerp = ef!!.fin(Interp.pow4Out)
                val si = Mathf.len(x, y) * Mathf.randomSeed((x + y).toLong(), 0.6f, 0.8f)
                SglDraw.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90)
            }
        }

        e.scaled(120f) { ef ->
            Angles.randLenVectors(e.id * 2L, 9, 40f, 154f) { x: Float, y: Float ->
                val lerp = Mathf.clamp((ef!!.fin(Interp.pow4Out) - 0.2f) / 0.8f)
                val si = Mathf.len(x, y) * Mathf.randomSeed((x + y).toLong(), 0.7f, 0.9f)
                SglDraw.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90)
            }
        }

        e.scaled(140f) { ef ->
            Angles.randLenVectors(e.id * 2L, 10, 36f, 150f) { x: Float, y: Float ->
                val lerp = Mathf.clamp((ef!!.fin(Interp.pow4Out) - 0.4f) / 0.6f)
                val si = Mathf.len(x, y) * Mathf.randomSeed((x + y).toLong(), 0.7f, 0.9f)
                SglDraw.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90)
            }
        }
        e.scaled(160f) { ef ->
            Angles.randLenVectors(e.id * 3L, 12, 32f, 144f) { x: Float, y: Float ->
                val lerp = Mathf.clamp((ef!!.fin(Interp.pow4Out) - 0.5f) / 0.5f)
                val si = Mathf.len(x, y) * Mathf.randomSeed((x + y).toLong(), 0.9f, 1f)
                SglDraw.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90)
            }
            Lines.stroke(4 * ef!!.fout())
            Angles.randLenVectors(e.id * 4L, ef.finpow() + 0.001f, 58, size * 1.2f) { dx: Float, dy: Float, `in`: Float, out: Float ->
                Lines.lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 8 + out * 64f)
                Drawf.light(e.x + dx, e.y + dy, out * size / 2, Draw.getColor(), 0.8f)
            }
        }
    }

    val randomLightning: Effect = object : LightningEffect() {
        val branch: RandomGenerator = RandomGenerator()

        val generator: RandomGenerator = object : RandomGenerator() {

            init {
                branchChance = 0.15f
                branchMaker = Func2 { vert: LightningVertex?, str: Float? ->
                    branch.originAngle = vert!!.angle + Mathf.random(-90, 90)
                    branch.maxLength = 60 * str!!
                    branch
                }
            }
        }

        init {
            branch.maxDeflect = 60f
            lifetime = 60f
        }

        override fun render(e: EffectContainer) {
            if (e.data == null) return
            val con: LightningContainer = e.data()
            color(e.color)
            Draw.z(Layer.effect)
            if (!Vars.state.isPaused) con.update()
            con.draw(e.x, e.y)
        }

        override fun createLightning(x: Float, y: Float): LightningContainer {
            if (data !is Float) data = 90f
            val lightning = PoolLightningContainer.create(lifetime, 1.4f, 2.5f)

            lightning.lerp = Interp.pow2Out
            lightning.time = lifetime / 2
            generator.maxLength = Mathf.random((data as Float) / 2, data as Float)
            lightning.create(generator)

            Time.run(lifetime + 5) { Pools.free(lightning) }
            return lightning
        }
    }

    val spreadLightning: Effect = object : LightningEffect() {
        val branch: RandomGenerator = RandomGenerator().apply {
            maxDeflect = 50f
        }

        val generator: RandomGenerator = object : RandomGenerator() {
            init {
                maxDeflect = 60f
                branchChance = 0.15f
                branchMaker = Func2 { vert: LightningVertex?, str: Float? ->
                    branch.originAngle = vert!!.angle + Mathf.random(-90, 90)
                    branch.maxLength = 60 * str!!
                    branch
                }
            }
        }

        init {
            lifetime = 45f
        }

        override fun render(e: EffectContainer) {
            if (e.data == null) return
            val con: LightningContainer = e.data()
            color(e.color)
            Draw.z(Layer.effect)
            Fill.circle(e.x, e.y, 2.5f * e.fout())
            Lines.stroke(1 * e.fout())
            Lines.circle(e.x, e.y, 6 * e.fout())
            if (!Vars.state.isPaused) con.update()
            con.draw(e.x, e.y)
        }

        override fun createLightning(x: Float, y: Float): LightningContainer {
            val lightning = PoolLightningContainer.create(lifetime, 1.5f, 2.6f)

            lightning.lerp = Interp.pow2Out
            lightning.time = lifetime / 2
            val amount = Mathf.random(4, 6)
            for (i in 0..<amount) {
                generator.maxLength = Mathf.random(50, 75).toFloat()
                lightning.create(generator)
            }

            Time.run(lifetime + 5) { Pools.free(lightning) }
            return lightning
        }
    }

    val shrinkIceParticleSmall: Effect = Effect(120f) { e ->
        color(IceColor.winter)
        Angles.randLenVectors(e!!.id.toLong(), Mathf.randomSeed(e.id.toLong(), 6, 12), 32f) { x: Float, y: Float ->
            val size = Mathf.randomSeed((e.id + x).toInt().toLong(), 8, 16).toFloat()
            val lerp = e.fout(Interp.pow3Out)
            SglDraw.drawDiamond(e.x + x * lerp, e.y + y * lerp, size, size / 2f * e.fin(), Mathf.angle(x, y))
        }
    }

    val shrinkParticleSmall: Effect = shrinkParticle(12f, 2f, 120f, null)

    val blingSmall: Effect = Effect(320f) { e ->
        Draw.z(Layer.effect)
        color(e!!.color)
        var size = Mathf.randomSeed(e.id.toLong(), 6, 10).toFloat()
        size *= e.fout(Interp.pow4In)
        size += Mathf.absin(Time.time + Mathf.randomSeed(e.id.toLong(), 2 * Mathf.pi), 3.5f, 2f)
        val i = e.fin(Interp.pow3Out)
        val dx = Mathf.randomSeed(e.id.toLong(), 16f)
        val dy = Mathf.randomSeed((e.id + 1).toLong(), 16f)
        SglDraw.drawLightEdge(e.x + dx * i, e.y + dy * i, size, size * 0.15f, size, size * 0.15f)
    }

    val bling: Effect = Effect(320f) { e ->
        Draw.z(Layer.effect)
        color(e!!.color)
        var size = Mathf.randomSeed(e.id.toLong(), 6, 10).toFloat()
        size *= e.fout(Interp.pow4In)
        size += Mathf.absin(Time.time + Mathf.randomSeed(e.id.toLong(), 2 * Mathf.pi), 3.5f, 2f)
        SglDraw.drawLightEdge(e.x, e.y, size, size * 0.15f, size, size * 0.15f)
    }

    val lightningBoltWave: Effect = Effect(90f) { e ->
        color(e!!.color)
        val rate = e.fout(Interp.pow2In)
        val l = 168 * rate
        val w = 36 * rate

        Drawf.light(e.x, e.y, e.fout() * 96, e.color, 0.7f)

        val lerp = e.fin(Interp.pow3Out)
        SglDraw.drawLightEdge(e.x, e.y, l, w, l, w)
        Lines.stroke(5f * e.fout())
        Lines.circle(e.x, e.y, 45 * e.fout())
        Lines.stroke(8f * e.fout())
        Lines.circle(e.x, e.y, 84 * lerp)

        Angles.randLenVectors(e.id.toLong(), Mathf.randomSeed(e.id.toLong(), 15, 20), 92f) { x: Float, y: Float ->
            val size = Mathf.randomSeed((e.id + x).toInt().toLong(), 18, 26).toFloat()
            SglDraw.drawDiamond(e.x + x * lerp, e.y + y * lerp, size, size * 0.23f * e.fout(), Mathf.angle(x, y))
        }

        e.scaled(45f) { ef ->
            Angles.randLenVectors(e.id.toLong(), 8, 25f, 94f) { x: Float, y: Float ->
                val le = ef!!.fin(Interp.pow4Out)
                val si = Mathf.len(x, y) * Mathf.randomSeed((x + y).toLong(), 0.6f, 0.8f)
                SglDraw.drawDiamond(e.x + x * le, e.y + y * le, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90)
            }
        }

        e.scaled(56f) { ef ->
            Angles.randLenVectors(e.id * 2L, 8, 20f, 82f) { x: Float, y: Float ->
                val le = Mathf.clamp((ef!!.fin(Interp.pow4Out) - 0.3f) / 0.7f)
                val si = Mathf.len(x, y) * Mathf.randomSeed((x + y).toLong(), 0.7f, 0.9f)
                SglDraw.drawDiamond(e.x + x * le, e.y + y * le, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90)
            }
        }
        e.scaled(75f) { ef ->
            Angles.randLenVectors(e.id * 3L, 9, 14f, 69f) { x: Float, y: Float ->
                val le = Mathf.clamp((ef!!.fin(Interp.pow4Out) - 0.5f) / 0.5f)
                val si = Mathf.len(x, y) * Mathf.randomSeed((x + y).toLong(), 0.9f, 1f)
                SglDraw.drawDiamond(e.x + x * le, e.y + y * le, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90)
            }
            Lines.stroke(3 * ef!!.fout())
            Angles.randLenVectors(e.id * 4L, ef.finpow() + 0.001f, 48, 102f) { dx: Float, dy: Float, `in`: Float, out: Float ->
                Lines.lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 4 + out * 34f)
                Drawf.light(e.x + dx, e.y + dy, out * 96, Draw.getColor(), 0.8f)
            }
        }
    }

    val neutronWeaveMicro: Effect = Effect(45f) { e ->
        color(e!!.color)
        Lines.stroke(e.fout())
        Lines.square(e.x, e.y, 2.2f + 6.8f * e.fin(), 45f)
        Fill.square(e.x, e.y, 2.2f * e.fout(), 45f)
    }

    val neutronWeaveMini: Effect = Effect(45f) { e ->
        color(e!!.color)
        Lines.stroke(1.5f * e.fout())
        Lines.square(e.x, e.y, 3f + 9f * e.fin(), 45f)
        Fill.square(e.x, e.y, 3f * e.fout(), 45f)
    }

    val neutronWeave: Effect = Effect(45f) { e ->
        color(e!!.color)
        Lines.stroke(1.8f * e.fout())
        Lines.square(e.x, e.y, 4f + 12 * e.fin(), 45f)
        Fill.square(e.x, e.y, 4 * e.fout(), 45f)
    }

    val neutronWeaveBig: Effect = Effect(45f) { e ->
        color(e.color)
        Lines.stroke(2f * e.fout())
        Lines.square(e.x, e.y, 5f + 18 * e.fin(), 45f)
        Fill.square(e.x, e.y, 5 * e.fout(), 45f)
    }

    fun impactExplode(size: Float, lifeTime: Float): Effect {
        return impactExplode(size, lifeTime, false)
    }

    fun impactExplode(size: Float, lifeTime: Float, heightBloom: Boolean): Effect {
        return Effect(lifeTime) { e ->
            val rate = e!!.fout(Interp.pow2In)
            val l = size * 1.16f * rate
            val w = size * 0.1f * rate

            val fout = e.fout()
            val fin = e.fin()
            Drawf.light(e.x, e.y, fout * size * 1.15f, e.color, 0.7f)

            val x = e.x
            val y = e.y
            val id = e.id
            val draw = SglDraw.DrawAcceptor { n: Bloom? ->
                color(e.color)
                SglDraw.drawLightEdge(x, y, l, w, l, w)
                Lines.stroke(size * 0.08f * fout)
                Lines.circle(x, y, size * 0.55f * fout)
                Lines.stroke(size * 0.175f * fout)
                Lines.circle(x, y, size * 1.25f * (1 - Mathf.pow(fout, 3f)))

                Angles.randLenVectors(id.toLong(), 12, 26f) { dx: Float, dy: Float ->
                    val s = Mathf.randomSeed((id + dx).toInt().toLong(), 4f, 8f)
                    Fill.circle(x + dx * fin, y + dy * fin, s * fout)
                }
                Draw.reset()
            }

            if (heightBloom) {
                Draw.z(Layer.flyingUnit + 1)
                SglDraw.drawBloomUponFlyUnit<Bloom?>(null, draw)
            } else draw.draw(null)

            Draw.z(Layer.effect + 0.001f)
            Lines.stroke((size * 0.065f * fout))
            Angles.randLenVectors((e.id + 1).toLong(), e.finpow() + 0.001f, (size / 2.25f).toInt(), size * 1.2f) { dx: Float, dy: Float, `in`: Float, out: Float ->
                Lines.lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 3 + out * size * 0.7f)
                Drawf.light(e.x + dx, e.y + dy, out * size / 2, Draw.getColor(), 0.8f)
            }
        }
    }

    fun shrinkParticle(radius: Float, maxSize: Float, lifeTime: Float, color: Color?): Effect {
        return Effect(lifeTime) { e ->
            Draw.z(Layer.effect)
            color(color ?: e!!.color)
            Draw.alpha(1 - Mathf.clamp((e!!.fin() - 0.75f) / 0.25f))
            Angles.randLenVectors(e.id.toLong(), 2, radius) { x: Float, y: Float ->
                val size = Mathf.randomSeed(e.id.toLong(), maxSize)
                val le = e.fout(Interp.pow3Out)
                Fill.square(
                    e.x + x * le, e.y + y * le, size * e.fin(), Mathf.lerp(Mathf.randomSeed(e.id.toLong(), 360f), Mathf.randomSeed(e.id.toLong(), 360f), e.fin())
                )
            }
        }
    }

    fun graphiteCloud(radius: Float, density: Int): Effect {
        return Effect(360f) { e ->
            Draw.z(Layer.bullet - 5)
            color(Pal.stoneGray)
            Draw.alpha(0.6f)
            Angles.randLenVectors(e!!.id.toLong(), density, radius) { x: Float, y: Float ->
                val size = Mathf.randomSeed((e.id + x).toInt().toLong(), 14, 18).toFloat()
                val i = e.fin(Interp.pow3Out)
                Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out))
            }
            Draw.z(Layer.effect)
            color(Items.graphite.color)
            Angles.randLenVectors((e.id + 1).toLong(), (density * 0.65f).toInt(), radius) { x: Float, y: Float ->
                var size = Mathf.randomSeed((e.id + x).toInt().toLong(), 7, 10).toFloat()
                size *= e.fout(Interp.pow4In)
                size += Mathf.absin(Time.time + Mathf.randomSeed((e.id + x).toInt().toLong(), 2 * Mathf.pi), 3.5f, 2f)
                val i = e.fin(Interp.pow3Out)
                SglDraw.drawLightEdge(e.x + x * i, e.y + y * i, size, size * 0.15f, size, size * 0.15f)
            }
        }
    }

    abstract class LightningEffect : Effect() {
        protected var data: Any? = null

        override fun at(pos: Position) {
            create(pos.x, pos.y, 0f, Color.white, createLightning(pos.x, pos.y))
        }

        override fun at(pos: Position, parentize: Boolean) {
            create(pos.x, pos.y, 0f, Color.white, createLightning(pos.x, pos.y))
        }

        override fun at(pos: Position, rotation: Float) {
            create(pos.x, pos.y, rotation, Color.white, createLightning(pos.x, pos.y))
        }

        override fun at(x: Float, y: Float) {
            create(x, y, 0f, Color.white, createLightning(x, y))
        }

        override fun at(x: Float, y: Float, rotation: Float) {
            create(x, y, rotation, Color.white, createLightning(x, y))
        }

        override fun at(x: Float, y: Float, rotation: Float, color: Color?) {
            create(x, y, rotation, color, createLightning(x, y))
        }

        override fun at(x: Float, y: Float, color: Color?) {
            create(x, y, 0f, color, createLightning(x, y))
        }

        override fun at(x: Float, y: Float, rotation: Float, color: Color?, data: Any?) {
            this.data = data
            create(x, y, rotation, color, createLightning(x, y))
        }

        abstract fun createLightning(x: Float, y: Float): LightningContainer?
    }
}