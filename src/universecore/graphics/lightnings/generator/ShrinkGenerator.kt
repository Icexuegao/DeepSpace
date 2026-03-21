package universecore.graphics.lightnings.generator

import arc.math.geom.Vec2
import arc.util.Tmp
import universecore.graphics.lightnings.LightningVertex

/**收缩闪电的生成器，这会生成一定范围内向中心蔓延的闪电
 *
 * @since 2.3
 * @author EBwilson
 */
class ShrinkGenerator : LightningGenerator() {
  var minRange: Float = 0f
  var maxRange: Float = 0f

  var vec: Vec2 = Vec2()
  var distance: Float = 0f
  var currentDistance: Float = 0f
  var first: Boolean = false

  override fun reset() {  
    super.reset()
    vec.rnd(seed.random(minRange, maxRange).also {distance = it})
    currentDistance = distance
    first = true
  }

  override fun hasNext(): Boolean {
    return super.hasNext() && currentDistance > 0
  }

  override fun handleVertex(vertex: LightningVertex) {
    currentDistance -= seed.random(minInterval, maxInterval)

    if (currentDistance > minInterval) {
      if (first) {
        Tmp.v2.set(vec)
      } else {
        val offset = seed.random(-maxSpread, maxSpread)
        Tmp.v2.set(vec).setLength(currentDistance).add(Tmp.v1.set(vec).rotate90(1).setLength(offset).scl((if (offset < 0) -1 else 1).toFloat()))
      }
    } else {
      currentDistance = 0f
      Tmp.v2.setZero()
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
    return 0f
  }
}