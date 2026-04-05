
package ice

import arc.math.geom.Mat3D
import arc.math.geom.Vec2
import arc.math.geom.Vec3

class CubeCalculator {
  private val vertices = arrayOf(
    Vec3(-1f, -1f, -1f),
    Vec3(1f, -1f, -1f),
    Vec3(1f, 1f, -1f),
    Vec3(-1f, 1f, -1f),
    Vec3(-1f, -1f, 1f),
    Vec3(1f, -1f, 1f),
    Vec3(1f, 1f, 1f),
    Vec3(-1f, 1f, 1f)
  )

  // 12 条边 (顶点索引对)
  val edges = arrayOf(
    Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 0), // 后面 4 条
    Pair(4, 5), Pair(5, 6), Pair(6, 7), Pair(7, 4), // 前面 4 条
    Pair(0, 4), Pair(1, 5), Pair(2, 6), Pair(3, 7)  // 连接前后 4 条
  )

  val projectedPoints = Array(8) { Vec2() }

  private val matrix = Mat3D()
  private val tempVec = Vec3()

  var angleY = 0f
  var angleX = 0f
  var angleZ = 0f
  var size = 1f

  fun update(delta: Float) {
    angleY += delta * 45f
    angleX += delta * 30f
    angleZ += delta * 30f
    if (angleY > 360f) angleY -= 360f
    if (angleX > 360f) angleX -= 360f

    matrix.idt()
    matrix.rotate(Vec3.Y, angleY)
    matrix.rotate(Vec3.X, angleX)
    matrix.rotate(Vec3.Z, angleZ)

    for (i in vertices.indices) {
      val v = vertices[i]

      tempVec.x = v.x * matrix.`val`[0] + v.y * matrix.`val`[4] + v.z * matrix.`val`[8]
      tempVec.y = v.x * matrix.`val`[1] + v.y * matrix.`val`[5] + v.z * matrix.`val`[9]
      tempVec.z = v.x * matrix.`val`[2] + v.y * matrix.`val`[6] + v.z * matrix.`val`[10]

      projectedPoints[i].set(tempVec.x * size, -tempVec.y * size)
    }
  }
}