package ice.world.draw

import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import mindustry.gen.Building
import mindustry.world.draw.DrawBlock
import kotlin.math.abs

class DrawArcSmelt : DrawBlock() {
    var flameColor: Color = Color.valueOf("f58349")
    var midColor: Color = Color.valueOf("f2d585")
    var flameRad: Float = 1f
    var circleSpace: Float = 2f
    var flameRadiusScl: Float = 3f
    var flameRadiusMag: Float = 0.3f
    var circleStroke: Float = 1f
    var x: Float = 0f
    var y: Float = 0f
    var alpha: Float = 0.68f
    var particles: Int = 25
    var particleLife: Float = 40f
    var particleRad: Float = 7f
    var particleStroke: Float = 1.1f
    var particleLen: Float = 3f
    var drawCenter: Boolean = true
    var blending: Blending = Blending.additive
    var startAngle = 0f
    var endAngle = 360f
    override fun draw(build: Building) {
        if (build.warmup() > 0f && flameColor.a > 0.001f) {
            Lines.stroke(circleStroke * build.warmup())
            val si = Mathf.absin(flameRadiusScl, flameRadiusMag)
            val a = alpha * build.warmup()
            Draw.blend(blending)

            Draw.color(midColor, a)
            if (drawCenter) Fill.circle(build.x + x, build.y + y, flameRad + si)

            Draw.color(flameColor, a)
            if (drawCenter) Lines.circle(build.x + x, build.y + y, (flameRad + circleSpace + si) * build.warmup())

            Lines.stroke(particleStroke * build.warmup())
            val base = Time.time / particleLife
            rand.setSeed(build.id.toLong())
            for (i in 0 until particles) {
                val fin = (rand.random(1f) + base) % 1f
                val fout = 1f - fin
                var angle: Float
                if (startAngle < 0) {
                    val ran = rand.random(0f, endAngle)
                    val fl = 360 - abs(startAngle)
                    val ran1 = rand.random(fl, 360f)
                    val random = rand.random(0, 1)
                    angle = if (random == 0) ran else ran1
                } else {
                    angle = rand.random(startAngle, endAngle)
                }
                val len = particleRad * Interp.pow2Out.apply(fin)
                Lines.lineAngle(build.x + Angles.trnsx(angle, len) + x, build.y + Angles.trnsy(angle, len) + y, angle,
                                particleLen * fout * build.warmup())
            }

            Draw.blend()
            Draw.reset()
        }
    }
}