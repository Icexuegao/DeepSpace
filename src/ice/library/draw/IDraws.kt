package ice.library.draw

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Tmp
import kotlin.math.abs
import kotlin.math.min

object IDraws {
    /** 绘制⚪  */
    fun arc(x: Float, y: Float, radius: Float, scaleFactor: Float, innerAnge: Float, rotate: Float) {
        var innerAngel = innerAnge
        val sides = 40 + (radius * scaleFactor).toInt()

        val step = 360f / sides
        val sing = if (innerAngel > 0) 1 else -1
        innerAngel = min(abs(innerAngel), 360.0f)

        Lines.beginLine()

        var overed = 0f
        var ang = 0f
        while (ang <= innerAngel - step) {
            overed += step
            Tmp.v1.set(radius, 0f).setAngle(ang * sing + rotate)
            Lines.linePoint(x + Tmp.v1.x, y + Tmp.v1.y)
            ang += step
        }

        if (innerAngel >= 360 - 0.01f) {
            Lines.endLine(true)
            return
        }

        if (overed < innerAngel) {
            Tmp.v1.set(radius, 0f).setAngle(innerAngel * sing + rotate)
            Lines.linePoint(x + Tmp.v1.x, y + Tmp.v1.y)
        }
        Lines.endLine()
    }
    fun light(x: Float, y: Float, sides: Int, radius: Float, rotation: Float = 0f, center: Color, edge: Color,func: Prov<Float>) {
        var sides = sides
        sides = Mathf.ceil(sides / 2f) * 2
        val space = 360 / sides
        var i = 0
        while (i < sides) {
            val px = Angles.trnsx(space * i + rotation, radius)
            val py = Angles.trnsy(space * i + rotation, radius)
            val px2 = Angles.trnsx(space * (i + 1) + rotation, radius)
            val py2 = Angles.trnsy(space * (i + 1) + rotation, radius)
            val centerf = run {
                center.toFloatBits()
            }
            val edgef = run {
                val get = func.get()

                val a = abs(180 - space * i) / 180f
                edge.a( Interp.pow3In.apply(a)*get) .toFloatBits()

            }
            Fill.quad(x, y, centerf, x + px, y + py, edgef, x + px2, y + py2, edgef, x + px2, y + py2, edgef)
            if (i == sides - 1) {
                val px = Angles.trnsx( rotation, radius)
                val py = Angles.trnsy( rotation, radius)
                Fill.quad(x, y, centerf, x + px2, y + py2, edgef, x + px, y + py, edgef, x + px, y + py, edgef)
            }
            i += 1

        }
    }
}
