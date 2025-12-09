package ice.graphics.lightnings

import arc.func.Cons2
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import ice.graphics.lightnings.generator.LightningGenerator
import mindustry.graphics.Drawf
import kotlin.math.min

/**
单条闪电的存储容器，保存了闪电的起始时间还有闪电的顶点信息
此类实例大量，应当复用
@author EBwilson
移动到图形模块中
@since 1.5 */
class Lightning private constructor() : Poolable {
    val vertices: Seq<LightningVertex> = Seq<LightningVertex>()

    /** 闪电的持续时间  */
    var lifeTime: Float = 0f

    /** 闪电消逝的过渡时间  */
    var fadeTime: Float = 0f

    /** 闪电是否随淡出过程从起点开始消失  */
    var backFade: Boolean = false

    /** 闪电整体的宽度是否随闪电的持续时间淡出  */
    var fade: Boolean = true

    /** 闪电被创建时的时间  */
    var startTime: Float = 0f

    /** 这道闪电的剪切尺寸，用于绘制时的画面裁切  */
    var clipSize: Float = 0f

    /** 闪电的宽度  */
    var width: Float = 0f

    /** 闪电的宽度插值函数  */
    var lerp: Interp? = Interp.linear

    /** 闪电的每一段的触发器，在任意一段闪电的部分生成完成时会各自调用一次，传入当前顶点和前一个顶点  */
    var trigger: Cons2<LightningVertex?, LightningVertex?>? = null

    /** 闪电由产生到完全显现的时间，在[Lightning. speed]未设置的情况下有效  */
    var time: Float = 0f
    var counter: Float = 0f
    var lengthMargin: Float = 0f

    var headClose: Boolean = false
    var endClose: Boolean = false

    var totalLength: Float = 0f

    var cursor: Int = 0

    var enclosed: Boolean = false

    /** 更新一次闪电状态  */
    fun update() {
        if (time == 0f && cursor < vertices.size) {
            var per: LightningVertex? = null
            for (vertex in vertices) {
                if (per != null) {
                    per.progress = 1f
                    vertex.valid = true
                    if (trigger != null) trigger!!.get(per, vertex)
                }
                per = vertex
            }
            cursor = vertices.size
        } else {
            var increase = vertices.size / time * Time.delta

            while (increase > 0) {
                if (cursor == 0) {
                    cursor++
                }

                if (cursor >= vertices.size) break

                val per = vertices.get(cursor - 1)
                val curr = vertices.get(cursor)
                val delta = min(increase, 1 - per.progress)
                per.progress += delta
                increase -= delta

                if (per.progress >= 1) {
                    curr.valid = true
                    if (trigger != null) trigger!!.get(per, curr)
                    cursor++
                }
            }
        }

        for (vertex in vertices) {
            if (!vertex.isEnd && !vertex.isStart && vertex.valid) vertex.update()
        }
    }

    /**
     * 绘制这道闪电
     * @param x 绘制闪电的原点x坐标
     * @param y 绘制闪电的原点y坐标
     */
    fun draw(x: Float, y: Float) {
        var lerp = Mathf.clamp(this.lerp!!.apply(Mathf.clamp((lifeTime - (Time.time - startTime)) / fadeTime)))
        var del = if (backFade) (1 - lerp) * vertices.size else 0f

        if (!fade) lerp = 1f

        for (i in 2..vertices.size) {
            val v1 = if (i - 3 >= 0) vertices.get(i - 3) else if (enclosed) vertices.get(Mathf.mod(i - 3, vertices.size)) else null
            val v2 = vertices.get(i - 2)
            val v3 = vertices.get(i - 1)
            val v4 = if (i < vertices.size) vertices.get(i) else if (enclosed) vertices.get(Mathf.mod(i, vertices.size)) else null

            var lastOffX: Float
            var lastOffY: Float
            var nextOffX: Float
            var nextOffY: Float

            val fade = min(del, 1f)
            del -= fade
            if (!v2!!.valid) break

            self.set(v3!!.x, v3.y).sub(v2.x, v2.y)

            if (v1 != null) {
                last.set(v2.x, v2.y).sub(v1.x, v1.y)

                val aveAngle = (last.angle() + self.angle()) / 2
                val off = width / 2 * lerp / Mathf.cosDeg(aveAngle - last.angle())

                lastOffX = Angles.trnsx(aveAngle + 90, off)
                lastOffY = Angles.trnsy(aveAngle + 90, off)
            } else {
                Tmp.v1.set(self).rotate90(1).setLength(width / 2 * lerp)
                lastOffX = Tmp.v1.x
                lastOffY = Tmp.v1.y
            }

            if (v4 != null) {
                next.set(v4.x, v4.y).sub(v3.x, v3.y)
                val aveAngle = (self.angle() + next.angle()) / 2
                val off = width / 2 * lerp / Mathf.cosDeg(aveAngle - self.angle())

                nextOffX = Angles.trnsx(aveAngle + 90, off)
                nextOffY = Angles.trnsy(aveAngle + 90, off)
            } else {
                Tmp.v1.set(self).rotate90(1).setLength(width / 2 * lerp)
                nextOffX = Tmp.v1.x
                nextOffY = Tmp.v1.y
            }

            lastOffX *= lerp
            lastOffY *= lerp
            nextOffX *= lerp
            nextOffY *= lerp

            val orgX = x + v2.x
            val orgY = y + v2.y
            val fadX = Tmp.v1.x * fade
            val fadY = Tmp.v1.y * fade

            Tmp.v1.set(self).scl(v2.progress)
            if ((v2.isStart && !headClose) || (v3.isEnd && !endClose)) {
                val l = if (v2.isStart) v2.progress else 1 - v2.progress
                val f = if (v2.isStart) fade else 1 - fade
                Fill.quad(orgX + fadX + lastOffX * f, orgY + fadY + lastOffY * f, orgX + fadX - lastOffX * f, orgY + fadY - lastOffY * f, orgX + Tmp.v1.x - nextOffX * l, orgY + Tmp.v1.y - nextOffY * l, orgX + Tmp.v1.x + nextOffX * l, orgY + Tmp.v1.y + nextOffY * l)
            } else {
                Fill.quad(orgX + fadX + lastOffX, orgY + fadY + lastOffY, orgX + fadX - lastOffX, orgY + fadY - lastOffY, orgX + Tmp.v1.x - nextOffX, orgY + Tmp.v1.y - nextOffY, orgX + Tmp.v1.x + nextOffX, orgY + Tmp.v1.y + nextOffY)
            }

            Drawf.light(orgX, orgY, orgX + Tmp.v1.x, orgY + Tmp.v1.y, width * 32, Draw.getColor(), Draw.getColor().a)

            v2.draw(x, y)
        }
    }

    override fun reset() {
        for (vertex in vertices) {
            Pools.free(vertex)
        }
        vertices.clear()
        counter = 0f
        width = 0f
        time = 0f
        cursor = 0
        lifeTime = 0f
        enclosed = false
        lerp = null
        lengthMargin = 0f
        startTime = 0f
        clipSize = 0f
        trigger = null
    }

    companion object {
        private val last = Vec2()
        private val self = Vec2()
        private val next = Vec2()

        fun create(generator: LightningGenerator, width: Float, lifeTime: Float, lerp: Interp, time: Float, trigger: Cons2<LightningVertex?, LightningVertex?>?): Lightning {
            return create(generator, width, lifeTime, lifeTime, lerp, time, fade = true, backFade = false, trigger = trigger)
        }

        fun create(generator: LightningGenerator, width: Float, lifeTime: Float, fadeTime: Float, lerp: Interp?, time: Float, fade: Boolean, backFade: Boolean, trigger: Cons2<LightningVertex?, LightningVertex?>?): Lightning {
            val result = Pools.obtain(Lightning::class.java) { Lightning() }
            result.width = width
            result.time = time
            result.startTime = Time.time
            result.lifeTime = lifeTime
            result.fadeTime = fadeTime
            result.lerp = lerp
            result.fade = fade
            result.backFade = backFade
            result.trigger = trigger

            generator.setCurrentGen(result)

            var last: LightningVertex? = null
            for (vertex in generator) {
                result.vertices.add(vertex)
                if (last != null) {
                    result.totalLength += Mathf.len(vertex.x - last.x, vertex.y - last.y)
                }
                last = vertex
            }
            result.enclosed = generator.isEnclosed()
            result.clipSize = generator.clipSize()

            return result
        }
    }
}
