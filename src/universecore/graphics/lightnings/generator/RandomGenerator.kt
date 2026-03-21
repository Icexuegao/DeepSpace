package universecore.graphics.lightnings.generator

import arc.math.geom.Vec2
import arc.util.Tmp
import universecore.graphics.lightnings.LightningVertex
import kotlin.math.max

/**随机路径的闪电生成器，给出起点路径总长度生成随机闪电路径
 *
 * @since 2.3
 * @author EBwilson
 */
open class RandomGenerator : LightningGenerator() {
  var maxLength: Float = 80f
  var maxDeflect: Float = 70f
  var originAngle: Float = Float.MIN_VALUE

  var currLength: Float = 0f
  var currVec2: Vec2 = Vec2()

  var first: Boolean = false
  var maxDistance: Float = 0f

  override fun reset() {
    super.reset()
    currLength = 0f
    maxDistance = 0f
    first = true
    if (originAngle == Float.MIN_VALUE) {
      currVec2.rnd(0.001f)
    } else {
      currVec2.set(0.001f, 0f).setAngle(originAngle)
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
      Tmp.v1.setLength(distance).setAngle(currVec2.angle() + seed.random(-maxDeflect, maxDeflect))
      currVec2.add(Tmp.v1)
      maxDistance = max(maxDistance, currVec2.len())
    }

    vertex.x = currVec2.x
    vertex.y = currVec2.y
  }

  override fun clipSize(): Float {
    return maxDistance
  }

  override fun hasNext(): Boolean {
    return super.hasNext() && currLength < maxLength
  }
}