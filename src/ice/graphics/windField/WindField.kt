package ice.graphics.windField

import arc.math.Mathf

/**
 * 风场模拟器 - 每个格子存储一个二维风力向量
 */
class WindField(
  val width: Int,      // 横向格子数
  val height: Int    // 纵向格子数
) {
  // 存储每个格子的风力向量 (x, y)
  private val windX = Array(width) {FloatArray(height)}
  private val windY = Array(width) {FloatArray(height)}

  // 用于 Perlin noise 计算的种子
  private var timeSeed = 0f

  /**
   * 获取指定格子的风力向量大小
   */
  fun getWindMagnitude(gridX: Int, gridY: Int): Float {
    if (gridX !in 0 until width || gridY !in 0 until height) return 0f
    return Mathf.len(windX[gridX][gridY], windY[gridX][gridY])
  }

  /**
   * 获取指定格子的风力向量
   */
  fun getWindVector(gridX: Int, gridY: Int): WindVector {
    if (gridX !in 0 until width || gridY !in 0 until height)
      return WindVector(0f, 0f, 0f)

    val x = windX[gridX][gridY]
    val y = windY[gridX][gridY]
    val magnitude = Mathf.len(x, y)
    val angle = Mathf.atan2(y, x) * Mathf.radiansToDegrees

    return WindVector(x, y, magnitude, angle)
  }

  /**
   * 更新整个风场 - 基于时间和 Perlin noise
   */
  fun update(deltaTime: Float) {
    timeSeed += deltaTime * 0.1f // 降低时间流速，从 0.5 改为 0.1

    for (x in 0 until width) {
      for (y in 0 until height) {
        // 使用多个噪声层叠加产生自然的风场效果
        val nx = x / (width * 0.3f)
        val ny = y / (height * 0.3f)

        // 基础风向 - 使用噪声（降低时间变化率）
        val baseAngle = noise(nx, ny, timeSeed * 0.3f) * Mathf.PI2 * 2
        val baseStrength = (noise(nx + 100, ny + 100, timeSeed * 0.2f) * 0.5f + 0.5f)

        // 第二层噪声增加变化（降低频率和时间变化）
        val detailAngle = noise(nx * 1.5f, ny * 1.5f, timeSeed * 0.25f) * Mathf.PI2

        // 合成风向（减少细节层的权重）
        val finalAngle = baseAngle + detailAngle * 0.15f
        val finalStrength = baseStrength * (0.8f + Mathf.sin(timeSeed * 0.5f + x * 0.1f) * 0.2f)

        windX[x][y] = Mathf.cos(finalAngle) * finalStrength
        windY[x][y] = Mathf.sin(finalAngle) * finalStrength
      }
    }
  }

  /**
   * 简单的 Perlin noise 实现
   */
  private fun noise(x1: Float, y1: Float, z1: Float): Float {
    var x = x1
    var y = y1
    var z = z1

    val X = Mathf.floor(x) and 255
    val Y = Mathf.floor(y) and 255
    val Z = Mathf.floor(z) and 255

    x -= Mathf.floor(x)
    y -= Mathf.floor(y)
    z -= Mathf.floor(z)

    val u = fade(x)
    val v = fade(y)
    val w = fade(z)

    val A = perm[X] + Y
    val AA = perm[A] + Z
    val AB = perm[A + 1] + Z
    val B = perm[X + 1] + Y
    val BA = perm[B] + Z
    val BB = perm[B + 1] + Z

    return lerp(
      w,
      lerp(
        v,
        lerp(u, grad(perm[AA], x, y, z), grad(perm[BA], x - 1, y, z)),
        lerp(u, grad(perm[AB], x, y - 1, z), grad(perm[BB], x - 1, y - 1, z))
      ),
      lerp(
        v,
        lerp(u, grad(perm[AA + 1], x, y, z - 1), grad(perm[BA + 1], x - 1, y, z - 1)),
        lerp(u, grad(perm[AB + 1], x, y - 1, z - 1), grad(perm[BB + 1], x - 1, y - 1, z - 1))
      )
    )
  }

  private fun fade(t: Float) = t * t * t * (t * (t * 6 - 15) + 10)
  private fun lerp(t: Float, a: Float, b: Float) = a + t * (b - a)

  private fun grad(hash: Int, x: Float, y: Float, z: Float): Float {
    val h = hash and 15
    val u = if (h < 8) x else y
    val v = if (h < 4) y else if (h == 12 || h == 14) x else z
    return ((if ((h and 1) == 0) u else -u) + (if ((h and 2) == 0) v else -v))
  }

  companion object {
    private val perm = IntArray(512) {
      val p = intArrayOf(
        151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
        190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168,
        68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
        102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198,
        173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
        223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224,
        232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
        49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
      )
      if (it < 256) p[it] else p[it - 256]
    }.let {p -> IntArray(512) {i -> p[i and 255]}}
  }
}

/**
 * 风力向量数据类
 */
data class WindVector(
  val x: Float,
  val y: Float,
  val magnitude: Float,
  val angle: Float = 0f
)
