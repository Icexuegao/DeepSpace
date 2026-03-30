@file:Suppress("unused")

package ice.library.math

import arc.math.Interp
import arc.math.Mathf
import kotlin.math.min


operator fun Interp.invoke(x: Float) = this.apply(x)

/** @return 从接收者到 [target] 的插值数 */
fun Float.lerp(target: Float, progress: Progress) =
  Mathf.lerp(this, target, progress)
/** @return 自身 */
fun FloatArray.lerp(target: FloatArray, progress: Progress) = this.apply {
  for (i in 0 until min(target.size, target.size)) {
    this[i] = this[i].lerp(target[i], progress)
  }
}

fun Float.approach(to: Float, speed: Float) =
  Mathf.approach(this, to, speed)

fun Float.approachDelta(to: Float, speed: Float) =
  Mathf.approachDelta(this, to, speed)
/**返回平滑插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f]*/
val Progress.smooth: Progress
  get() = Interp.smooth(this.coerceIn(0f, 1f))
/**返回更平滑的插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f] */
val Progress.smoother: Progress
  get() = Interp.smoother(this.coerceIn(0f, 1f))
/**返回斜坡插值。
 * @receiver 任意数字，最终被限制在 [0f,1f] -> [1f,0f]
 * @return [0f,1f]*/
val Progress.slope: Progress
  get() = Interp.slope(this.coerceIn(0f, 1f))
/** 返回二次幂插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f] */
val Progress.pow2Intrp: Progress
  get() = Interp.pow2(this.coerceIn(0f, 1f))
/** 返回三次幂插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f] */
val Progress.pow3Intrp: Progress
  get() = Interp.pow3(this.coerceIn(0f, 1f))
/** 返回二次幂 Out 插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f] */
val Progress.pow2OutIntrp: Progress
  get() = Interp.pow2Out(this.coerceIn(0f, 1f))
/** 返回三次幂 Out 插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f] */
val Progress.pow3OutIntrp: Progress
  get() = Interp.pow3Out(this.coerceIn(0f, 1f))
/** 返回二次幂 In 插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f] */
val Progress.pow2InIntrp: Progress
  get() = Interp.pow2In(this.coerceIn(0f, 1f))
/** 返回三次幂 In 插值。
 * @receiver 任意数字，最终被限制在 [0f,1f]
 * @return [0f,1f] */
val Progress.pow3InIntrp: Progress
  get() = Interp.pow3In(this.coerceIn(0f, 1f))
/** @param growingTime 递增的数值
 * @param maxTime 最大边界
 * @return [0f,1f]，递增 */
fun progressTime(growingTime: Float, maxTime: Float): Progress =
  (growingTime / maxTime).coerceIn(0f, 1f)

fun dweep(time: Float) {
  progressTime(1f, 2f).smooth
}
/** 返回 1f - this */
val Progress.reverseProgress: Progress
  get() = 1f - this
/** 示例
 * ```kotlin
 * 10.inProgress(0.22f) == 2
 * ```
 * @receiver 最大值
 * @return 一个在 [0,max) 范围内四舍五入的数字 */
fun Int.inProgress(progress: Progress): Int {
  val p = progress.coerceIn(0f, 1f)
  return Mathf.round(p * this).coerceAtMost(this - 1)
}
/** @receiver [0f,1f] 范围内的进度
 * @param from 起始值
 * @param to 结束值
 * @return 一个根据 [inProgress] 从 [from] 到 [to] 的值 */
fun Progress.between(from: Float, to: Float): Float {
  if (from > to) {
    return from - (from - to) * this
  } else if (from < to) {
    return from + (to - from) * this
  }
  return from
}
