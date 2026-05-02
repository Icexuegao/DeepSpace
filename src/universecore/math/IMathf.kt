package universecore.math

import arc.util.Time
import kotlin.math.sin

object IMathf {
  /**
   * 计算基于当前时间的正弦波数值。
   * 适用于随时间变化的周期性动画或波动效果。
   *
   * @param amplitude       振幅，即波动的最大偏移量
   * @param angularVelocity 角速度，决定波动快慢，单位通常为弧度/秒
   * @param initialPhase    初相位，即时间零点时的相位偏移
   * @param offset          纵向偏移量，决定波形的中心位置
   * @return 当前时刻的正弦波计算结果
   */
  fun sint(amplitude: Float = 1f, angularVelocity: Float = 1f, initialPhase: Float = 0f, offset: Float = 0f): Float {
    return amplitude * sin(angularVelocity * Time.time + initialPhase) + offset
  }
}