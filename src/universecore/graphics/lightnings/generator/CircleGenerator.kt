package universecore.graphics.lightnings.generator

import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Tmp
import universecore.graphics.lightnings.LightningVertex

/**环形闪电的生成器，通过指定的圆心和半径生成闪电顶点
 *
 * @since 2.3
 * @author EBwilson
 */
class CircleGenerator : LightningGenerator() {
  /**闪电基于的圆的半径 */
  var radius: Float = 16f

  /**圆的原始起点角度，这会影响time不为0时生成圆的闪电蔓延起点 */
  var originAngle: Float = 0f

  /**圆的旋转方向，大于0为逆时针，否则为顺时针 */
  var directory: Int = 1

  /**这个圆是否闭合，这会决定闪电的头部和尾部是连接的还是断开的 */
  var enclosed: Boolean = true

  var rad: Vec2 = Vec2()
  var currentRotated: Float = 0f

  var first: Boolean = false
  var firstOne: LightningVertex? = null

  override fun reset() {
    super.reset()
    rad.set(1f, 0f).setLength(radius).setAngle(originAngle)
    currentRotated = 0f
    first = true
    firstOne = null
  }

  override fun hasNext(): Boolean {
    return super.hasNext() && currentRotated < 360
  }

  override fun handleVertex(vertex: LightningVertex) {
    val step = seed.random(minInterval, maxInterval)
    val rotated = step / (Mathf.pi * radius / 180) * (if (directory >= 0) 1 else -1)

    if (rotated + currentRotated >= 360) {
      if (firstOne == null) {
        currentRotated = 361f
        return
      }

      vertex.isEnd = !enclosed
      if (enclosed) {
        vertex.x = firstOne!!.x
        vertex.y = firstOne!!.y
      }
      currentRotated = 360f
    } else {
      currentRotated += rotated

      val offset = seed.random(-maxSpread, maxSpread)
      Tmp.v2.set(Tmp.v1.set(rad.rotate(rotated))).setLength(offset).scl((if (offset < 0) -1 else 1).toFloat())
      Tmp.v1.add(Tmp.v2)

      vertex.x = Tmp.v1.x
      vertex.y = Tmp.v1.y
    }

    if (first) {
      vertex.valid = true
      vertex.isStart = !enclosed
      if (enclosed) {
        firstOne = vertex
      }
      first = false
    }
  }

  override fun clipSize(): Float {
    return radius + maxSpread
  }
}