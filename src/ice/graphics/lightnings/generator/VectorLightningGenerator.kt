package ice.graphics.lightnings.generator

import arc.math.geom.Vec2
import arc.util.Tmp
import ice.graphics.lightnings.LightningVertex

/**
矢量闪电生成器，生成由将沿着指定的向量创建一条直线蔓延的闪电
@author EBwilson
@since 1.5 */

class VectorLightningGenerator : LightningGenerator() {
    var vector: Vec2 = Vec2()
    var distance: Float = 0f
    var currentDistance: Float = 0f
    var first: Boolean = false

    override fun reset() {
        super.reset()
        currentDistance = 0f
        first = true
        distance = vector.len()
    }

    override fun hasNext(): Boolean {
        return super.hasNext() && currentDistance < distance
    }

    override fun handleVertex(vertex: LightningVertex) {
        currentDistance += seed.random(minInterval, maxInterval)

        if (currentDistance < distance - minInterval) {
            if (first) {
                Tmp.v2.setZero()
            } else {
                val offset = seed.random(-maxSpread, maxSpread)
                Tmp.v2.set(vector).setLength(currentDistance).add(Tmp.v1.set(vector).rotate90(1).setLength(offset).scl((if (offset < 0) -1 else 1).toFloat()))
            }
        } else {
            currentDistance = distance
            Tmp.v2.set(vector)
            vertex.isEnd = true
        }

        vertex.x = Tmp.v2.x
        vertex.y = Tmp.v2.y

        if (first) {
            vertex.isStart = true
            vertex.valid = true
            first = false
        }
    }

    override fun clipSize(): Float {
        return distance
    }
}
