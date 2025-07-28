package ice.library.util

import arc.util.Time

object Mathf{
    /**
    A：「振幅（Amplitude）」，曲线最高点与最低点的差值，表现为曲线的整体高度
    ω：「角速度（Angular Velocity）」，控制曲线的周期，表现为曲线的紧密程度
    φ：「初相（Initial Phase）」，即当 x = 0 时的相位，表现为曲线在坐标系上的水平位置
    k：「偏距（Offset）」，表现为曲线在坐标系上的垂直位置
     */
    fun sint(amplitude: Float, angularVelocity: Float, initialPhase: Float, offset: Float): Float {
        return amplitude * kotlin.math.sin(angularVelocity * Time.time + initialPhase) + offset
    }
}

