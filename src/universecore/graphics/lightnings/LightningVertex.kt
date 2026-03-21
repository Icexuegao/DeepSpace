package universecore.graphics.lightnings

import arc.util.pooling.Pool
import arc.util.pooling.Pools

/**闪电的顶点容器，保存了一个顶点的必要信息和绘制进度计时器
 * 此类实例大量，应当复用
 *
 * @since 2.3
 * @author EBwilson
 */
class LightningVertex : Pool.Poolable {
  var x: Float = 0f
  var y: Float = 0f
  var angle: Float = 0f

  var isStart: Boolean = false
  var isEnd: Boolean = false

  var valid: Boolean = false
  var progress: Float = 0f

  var branchOther: Lightning? = null

   fun draw(x: Float, y: Float) {
    if (branchOther != null) branchOther!!.draw(x, y)
  }

  fun update() {
    if (branchOther != null) branchOther!!.update()
  }

  override fun reset() {
    if (branchOther != null) Pools.free(branchOther)

    valid = false
    progress = 0f
    y = 0f
    x = y
    angle = 0f
    branchOther = null
    isStart = false
    isEnd = false
  }
}