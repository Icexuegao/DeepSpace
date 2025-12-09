package ice.world.content.unit.ability

import arc.Core
import arc.func.Cons
import arc.func.Floatf
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Intersector
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Nullable
import arc.util.Time
import ice.graphics.SglDraw
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.gen.Bullet
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.type.UnitType
import kotlin.math.max

class MirrorFieldAbility : MirrorShieldBase() {
    override var localizedName = "mirrorShield"
    var rotation = false
    var shapes = Seq<ShieldShape>(ShieldShape::class.java)

    /**搜索子弹的范围，若不设置则为默认值，若护盾的一部分会运动则须手动将此数据设置为覆盖子部分的最大范围 */
    var nearRadius: Float = -1f

    init {
        bundle {
            desc(zh_CN, "镜面护盾")
        }
    }

    override fun localized() = localizedName

    override fun init(type: UnitType?) {
        super.init(type)
        if (nearRadius < 0) {
            for (shape in shapes) {
                nearRadius = max(nearRadius, Mathf.dst(shape.x, shape.y) + shape.radius)
            }
        }
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Any {
        val res = super.clone() as MirrorFieldAbility
        for (i in 0..<shapes.size) {
            if (res.shapes.get(i) != null) res.shapes.set(i, shapes.get(i).clone())
        }
        return res
    }
    override fun shouldReflect(unit: Unit, bullet: Bullet): Boolean {
        for (shape in shapes) {
            if ((rotation && shape.inlerp(unit, unit.rotation() - 90, bullet, radScl)) || (!rotation && shape.inlerp(
                    unit, 0f, bullet, radScl))
            ) return true
        }
        return false
    }

    override fun eachNearBullets(unit: Unit, cons: Cons<Bullet>) {
        Groups.bullet.intersect(unit.x - nearRadius, unit.y - nearRadius, nearRadius * 2, nearRadius * 2, Cons { b ->
            if (unit.team !== b!!.team) cons.get(b)
        })
    }

    override fun update(unit: Unit) {
        super.update(unit)

        for (shape in shapes) {
            shape.flushMoves(unit)
        }
    }

    override fun draw(unit: Unit) {
        if (unit.shield > 0) {
            Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha))
            Draw.z(SglDraw.mirrorField + 0.001f * alpha)

            for (shape in shapes) {
                if (rotation) shape.draw(unit, unit.rotation() - 90, alpha, radScl)
                else shape.draw(unit, 0f, alpha, radScl)
            }
        }
    }


    class ShieldShape(var sides: Int, var x: Float, var y: Float, var angle: Float, var radius: Float) : Cloneable {
        @Nullable
        var movement: ShapeMove? = null

        private var moveOffsetX = 0f
        private var moveOffsetY = 0f
        private var moveOffsetRot = 0f

        fun inlerp(unit: Unit, rotation: Float, bullet: Bullet, scl: Float): Boolean {
            return Intersector.isInRegularPolygon(sides,
                unit.x + moveOffsetX + Angles.trnsx(rotation, x * scl, y * scl),
                unit.y + moveOffsetY + Angles.trnsy(rotation, x * scl, y * scl), radius * scl,
                rotation + angle + moveOffsetRot, bullet.x(), bullet.y())
        }

        fun draw(unit: Unit, rotation: Float, alpha: Float, scl: Float) {
            val drawX = unit.x + moveOffsetX + Angles.trnsx(rotation, x * scl, y * scl)
            val drawY = unit.y + moveOffsetY + Angles.trnsy(rotation, x * scl, y * scl)

            Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha))

            if (Core.settings.getBool("animatedshields")) {
                Draw.z(SglDraw.mirrorField + 0.001f * alpha)
                Fill.poly(drawX, drawY, sides, radius * scl, rotation + angle + moveOffsetRot)
            } else {
                Draw.z(SglDraw.mirrorField + 1)
                Lines.stroke(1.5f)
                Draw.alpha(0.09f)
                Fill.poly(drawX, drawY, sides, radius * scl, rotation + angle + moveOffsetRot)

                for (i in 1..4) {
                    Draw.alpha(i / 4f)
                    Lines.poly(drawX, drawY, sides, radius * scl * (i / 4f), rotation + angle + moveOffsetRot)
                }
            }
        }

        fun flushMoves(unit: Unit?) {
            if (movement == null) return

            val off = movement!!.offset(unit)
            moveOffsetX = off.x
            moveOffsetY = off.y
            moveOffsetRot = movement!!.rotation(unit)
        }

        public override fun clone(): ShieldShape {
            try {
                //fuck java
                return super.clone() as ShieldShape
            } catch (_: CloneNotSupportedException) {
                throw AssertionError()
            }
        }
    }

    class ShapeMove {
        var x: Float = 0f
        var y: Float = 0f
        var angle: Float = 0f
        var moveX: Float = 0f
        var moveY: Float = 0f
        var moveRot: Float = 0f
        var rotateSpeed: Float = 0f
        var interp: Interp = Interp.linear
        var lerp: Floatf<Unit> = Floatf { e: Unit -> 1F }

        private val vec2 = Vec2()

        @Nullable
        var childMoving: ShapeMove? = null

        private fun lerp(unit: Unit?): Float {
            return interp.apply(lerp.get(unit))
        }

        fun offset(unit: Unit?): Vec2 {
            vec2.set(x, y).add(tmp.set(moveX, moveY).scl(lerp(unit)))
                .rotate(if (rotateSpeed == 0f) moveRot * lerp(unit) else Time.time * rotateSpeed)

            return if (childMoving != null) vec2.add(childMoving!!.offset(unit)) else vec2
        }

        fun rotation(unit: Unit?): Float {
            return angle + (if (childMoving != null) childMoving!!.rotation(
                unit) else if (rotateSpeed == 0f) moveRot * lerp(unit) else Time.time * rotateSpeed)
        }

        companion object {
            private val tmp = Vec2()
        }
    }
}
