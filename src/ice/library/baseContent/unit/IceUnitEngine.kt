package ice.library.baseContent.unit

import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.type.UnitType.UnitEngine

open class IceUnitEngine(x: Float, y: Float, radius: Float, rotate: Float, var width: Float = 8f) :
    UnitEngine(x, y, radius, rotate) {
    override fun draw(unit: Unit) {
        val type = unit.type
        val scale = if (type.useEngineElevation) unit.elevation else 1f

        if (scale <= 0.0001f) return
        val rot = unit.rotation - 90
        val color = if (type.engineColor == null) unit.team.color else type.engineColor
        val absin = Mathf.absin(Time.time, 3f, 0.2f)

        Tmp.v1.set(x, y - absin * 3).rotate(rot)
        val ex = Tmp.v1.x
        val ey = Tmp.v1.y

        Tmp.v2.set(x, y + 5).rotate(rot)
        val ex2 = Tmp.v2.x
        val ey2 = Tmp.v2.y


        Draw.color(color)



        Drawf.flame(unit.x + ex, unit.y + ey, 20, unit.rotation, 30f + absin * 8, width, 0.5f - absin * 0.8f)

        Draw.color(type.engineColorInner)

        Drawf.flame(unit.x + ex2, unit.y + ey2, 15, unit.rotation, 30f, 3.5f, 0.4f - absin)/* Fill.circle(
             unit.x + ex - Angles.trnsx(rot + rotation, 1f),
             unit.y + ey - Angles.trnsy(rot + rotation, 1f),
             (radius + Mathf.absin(Time.time, 2f, radius / 4f)) / 2f * scale
         )*/
    }
}