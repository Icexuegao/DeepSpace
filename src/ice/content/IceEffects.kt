package ice.content

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import ice.ui.tex.Colors.紫色
import mindustry.entities.Effect
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import kotlin.math.min


object IceEffects {
    /*** @param lengthSize 该值决定火焰最终长度 子弹的话一般是速度*时间 */
    fun changeFlame(lengthSize: Float): Effect {
        return Effect(32f, 80f) { e ->
            Draw.color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin())
            Angles.randLenVectors(e.id.toLong(), 8, e.finpow() * lengthSize, e.rotation, 10f) { x: Float, y: Float ->
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f)
            }
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
        Draw.color(紫色)
        Drawf.tri(x, y, width, length, e.rotation)
        Drawf.tri(x, y, width, length, 180 + e.rotation)
    }

    /** 紫色渐变圆球  */
    val lancerLaserChargeBegin = Effect(60f) { e ->
        val margin = 1f - Mathf.curve(e.fin(), 0.9f)
        val fin = min(margin.toDouble(), e.fin().toDouble()).toFloat()
        Draw.color(Pal.spore)
        Fill.circle(e.x, e.y, fin * 4f)
        Draw.color(紫色)
        Fill.circle(e.x, e.y, fin * 2f)
    }

    /**紫色粒子效果*/
    val hitLaserBlast = Effect(12f) { e ->
        Lines.stroke(e.fout() * 1.5f)
        Angles.randLenVectors(e.id.toLong(), 8, e.finpow() * 17f) { x: Float, y: Float ->
            Draw.color(紫色)
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 3f)
        }
    }
}
