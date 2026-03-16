package ice.graphics.windField

import arc.util.Time
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class WindField {
  var seed = 12345

  /**
   * 可移动的平滑噪声值（适用于动态风场、流动效果）
   *
   * @param x X 坐标
   * @param y Y 坐标
   * @param time 时间戳或帧数，用于控制移动
   * @param angle 移动方向角度（0-360 度），0 度向右，90 度向上
   * @param speed 移动速度，控制单位时间内移动的距离
   * @param scale 缩放系数，控制变化的频率（默认 0.05）
   * @param seed 随机种子
   * @return 0-1 之间的平滑值
   */
  fun getMovingNoiseValue(
    x: Int, y: Int, time: Float = Time.time / 20, angle: Float = 0f, speed: Float = 0.5f, scale: Float = 0.05f
  ): Float {
    // 将角度转换为弧度，并计算移动方向的偏移量
    val radians = Math.toRadians(angle.toDouble())
    val offsetX = cos(radians) * speed * time
    val offsetY = sin(radians) * speed * time

    // 应用偏移量到坐标上
    val nx = (x + offsetX).toFloat() * scale
    val ny = (y + offsetY).toFloat() * scale

    // 找到网格的四个角点
    val x0 = floor(nx).toInt()
    val x1 = x0 + 1
    val y0 = floor(ny).toInt()
    val y1 = y0 + 1

    // 计算相对位置
    val dx = nx - x0
    val dy = ny - y0

    // 平滑插值
    val u = smoothStep(dx)
    val v = smoothStep(dy)

    // 获取梯度值
    val g00 = gradient(x0, y0)
    val g10 = gradient(x1, y0)
    val g01 = gradient(x0, y1)
    val g11 = gradient(x1, y1)

    // 线性插值
    val x1Interp = lerp(g00, g10, u)
    val x2Interp = lerp(g01, g11, u)

    return lerp(x1Interp, x2Interp, v)
  }

  /**
   * 向量版本的移动噪声（质量更高，推荐）
   */
  fun getMovingVectorNoiseValue(
    x: Int, y: Int, time: Float, angle: Float = 0f, speed: Float = 0.5f, scale: Float = 0.05f
  ): Float {
    val radians = Math.toRadians(angle.toDouble())
    val offsetX = cos(radians) * speed * time
    val offsetY = sin(radians) * speed * time

    val nx = (x + offsetX).toFloat() * scale
    val ny = (y + offsetY).toFloat() * scale

    val x0 = floor(nx).toInt()
    val x1 = x0 + 1
    val y0 = floor(ny).toInt()
    val y1 = y0 + 1

    val dx = nx - x0
    val dy = ny - y0

    val u = smoothStep(dx)
    val v = smoothStep(dy)

    val dot00 = dotGradient(x0, y0, dx, dy, seed)
    val dot10 = dotGradient(x1, y0, dx - 1, dy, seed)
    val dot01 = dotGradient(x0, y1, dx, dy - 1, seed)
    val dot11 = dotGradient(x1, y1, dx - 1, dy - 1, seed)

    val x1Interp = lerp(dot00, dot10, u)
    val x2Interp = lerp(dot01, dot11, u)

    return (lerp(x1Interp, x2Interp, v) + 1.0f) * 0.5f
  }

// ... existing code ...
// Smoothstep、lerp、gradient、dotGradient 函数保持不变
// ... existing code ...

  /**
   * Smoothstep 平滑函数，确保在边界处导数为 0，过渡更自然
   */
  private fun smoothStep(t: Float): Float {
    return t * t * (3.0f - 2.0f * t)
  }

  /**
   * 线性插值
   */
  private fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
  }

  /**
   * 伪随机梯度生成器
   * 使用哈希函数确保相同坐标总是返回相同的值
   */
  private fun gradient(x: Int, y: Int): Float {
    var n = x + y * 57 + seed * 131
    n = (n shl 13) xor n
    val hash = ((n * (n * n * 15731 + 789221) + 1376312589) and 0x7fffffff)
    return (hash.toFloat() / Int.MAX_VALUE)
  }

  /**
   * 简化的向量点积版本（更平滑）
   */
  fun getVectorNoiseValue(x: Int, y: Int, scale: Float = 0.05f, seed: Int = 12345): Float {
    val nx = x * scale
    val ny = y * scale

    val x0 = floor(nx).toInt()
    val x1 = x0 + 1
    val y0 = floor(ny).toInt()
    val y1 = y0 + 1

    val dx = nx - x0
    val dy = ny - y0

    val u = smoothStep(dx)
    val v = smoothStep(dy)

    // 获取梯度向量并计算点积
    val dot00 = dotGradient(x0, y0, dx, dy, seed)
    val dot10 = dotGradient(x1, y0, dx - 1, dy, seed)
    val dot01 = dotGradient(x0, y1, dx, dy - 1, seed)
    val dot11 = dotGradient(x1, y1, dx - 1, dy - 1, seed)

    val x1Interp = lerp(dot00, dot10, u)
    val x2Interp = lerp(dot01, dot11, u)

    // 将结果映射到 0-1 范围
    return (lerp(x1Interp, x2Interp, v) + 1.0f) * 0.5f
  }

  /**
   * 生成梯度向量并计算点积
   */
  private fun dotGradient(x: Int, y: Int, dx: Float, dy: Float, seed: Int): Float {
    var n = x + y * 57 + seed * 131
    n = (n shl 13) xor n
    val hash = ((n * (n * n * 15731 + 789221) + 1376312589) and 0x7fffffff)

    // 根据哈希值选择梯度方向
    val gx = when (hash % 12) {
      0, 1 -> 1.0f
      2, 3 -> -1.0f
      4, 5 -> 0.0f
      6, 7 -> 1.0f
      8, 9 -> -1.0f
      else -> 0.0f
    }

    val gy = when (hash % 12) {
      0, 1 -> 0.0f
      2, 3 -> 0.0f
      4, 5 -> 1.0f
      6, 7 -> -1.0f
      8, 9 -> 1.0f
      else -> -1.0f
    }

    return gx * dx + gy * dy
  }
}
