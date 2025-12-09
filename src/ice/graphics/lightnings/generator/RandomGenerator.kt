package ice.graphics.lightnings.generator

import arc.math.geom.Vec2
import arc.util.Tmp
import ice.graphics.lightnings.LightningVertex
import kotlin.math.max

/**随机路径的闪电生成器，给出起点路径总长度生成随机闪电路径
 *
 * @since 1.5
 * @author EBwilson
 *  移动到图形模块中*/

open class RandomGenerator : LightningGenerator() {
    var maxLength: Float = 80f
    var maxDeflect: Float = 70f
    var originAngle: Float = Float.MIN_VALUE

    var currLength: Float = 0f
    var curr: Vec2 = Vec2()

    var first: Boolean = false
    var maxDistance: Float = 0f

    override fun reset() {
        super.reset()
        currLength = 0f
        maxDistance = 0f
        first = true
        if (originAngle == Float.MIN_VALUE) {
            curr.rnd(0.001f)
        } else {
            curr.set(0.001f, 0f).setAngle(originAngle)
        }
    }

    override fun handleVertex(vertex: LightningVertex) {
        if (first) {
            vertex.isStart = true
            vertex.valid = true
            first = false
        } else {
            val distance = seed.random(minInterval, maxInterval)
            if (currLength + distance > maxLength) {
                vertex.isEnd = true
            }

            currLength += distance
            Tmp.v1.setLength(distance).setAngle(curr.angle() + seed.random(-maxDeflect, maxDeflect))
            curr.add(Tmp.v1)
            maxDistance = max(maxDistance, curr.len())
        }

        vertex.x = curr.x
        vertex.y = curr.y
    }

    override fun clipSize(): Float {
        return maxDistance
    }

    override fun hasNext(): Boolean {
        return super.hasNext() && currLength < maxLength
    }
}
