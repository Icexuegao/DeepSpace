package universecore.world.draw.part

import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import mindustry.entities.part.DrawPart.PartFunc
import mindustry.entities.part.DrawPart.PartParams
import kotlin.math.max
import kotlin.math.min

/** 部件进度计算函数接口，用于根据[PartParams]计算一个浮点进度值 */
fun interface UncPartProgress {

  companion object {
    /** 固定常量 */
    fun constant(value: Float) = UncPartProgress { value }
    /** 武器装填进度:刚射击完为1,装填完毕为0 */
    val reload = UncPartProgress { p -> p.reload }
    /** 平滑装填进度(无突变) */
    val smoothReload = UncPartProgress { p -> p.smoothReload }
    /** 武器预热:未射击0,连续射击1 */
    val warmup = UncPartProgress { p -> p.warmup }
    /** 蓄力进度:开始蓄力0,完成1 */
    val charge = UncPartProgress { p -> p.charge }
    /** 后坐力原始值 */
    val recoil = UncPartProgress { p -> p.recoil }
    /** 热量:刚开火1,冷却后0 */
    val heat = UncPartProgress { p -> p.heat }
    /** 导弹生命周期比例[0,1] */
    val life = UncPartProgress { p -> p.life }
    /** 全局时间(未缩放) */
    val time = UncPartProgress { Time.time }
  }

  /** 根据参数计算进度值 */
  fun get(p: PartParams): Float

  /** 获取进度值,可选择是否钳位到[0,1]区间 @param clamp 是否钳位,默认为true */
  fun getClamp(p: PartParams, clamp: Boolean = true) = if (clamp) Mathf.clamp(get(p)) else get(p)

  /** 取反:1-原值 */
  fun inv() = UncPartProgress { p -> 1f - get(p) }

  /** 应用slope曲线(Mathf.slope) */
  fun slope() = UncPartProgress { p -> Mathf.slope(get(p)) }

  /** 钳位到[0,1] */
  fun clamp() = UncPartProgress { p -> Mathf.clamp(get(p)) }

  /** 加上一个固定值 */
  fun add(amount: Float) = UncPartProgress { p -> get(p) + amount }

  /** 加上另一个进度值 */
  fun add(other: UncPartProgress) = UncPartProgress { p -> get(p) + other.get(p) }

  /** 乘以一个固定值 */
  fun mul(amount: Float) = UncPartProgress { p -> get(p) * amount }

  /** 乘以另一个进度值 */
  fun mul(other: UncPartProgress) = UncPartProgress { p -> get(p) * other.get(p) }

  /** 取两个进度值的最小值 */
  fun min(other: UncPartProgress) = UncPartProgress { p -> min(get(p), other.get(p)) }

  /** 线性插值混合另一个进度值 */
  fun blend(other: UncPartProgress, amount: Float) = UncPartProgress { p -> Mathf.lerp(get(p), other.get(p), amount) }

  /** 延迟效果:将[0,1]映射到[amount,1]后再拉伸回[0,1] */
  fun delay(amount: Float) = UncPartProgress { p -> (get(p) - amount) / (1f - amount) }

  /** 截取并缩放:将[offset,offset+duration]映射到[0,1] */
  fun curve(offset: Float, duration: Float) = UncPartProgress { p -> (get(p) - offset) / duration }

  /** 分段线性:上升,保持,下降(对称梯形) */
  fun sustain(offset: Float, grow: Float, sustain: Float) = UncPartProgress { p ->
    val value = get(p) - offset
    min(max(value, 0f) / grow, (grow + sustain + grow - value) / grow)
  }

  /** 缩短有效范围:将[0,1-amount]拉伸到[0,1] */
  fun shorten(amount: Float) = UncPartProgress { p -> get(p) / (1f - amount) }

  /** 压缩区间:将[start,end]映射到[0,1],外部为0/1常量 */
  fun compress(start: Float, end: Float) = UncPartProgress { p -> Mathf.curve(get(p), start, end) }

  /** 应用插值曲线(Interp) */
  fun curve(interp: Interp) = UncPartProgress { p -> interp.apply(get(p)) }

  /** 添加正弦波动(相对时间,带相位偏移) */
  fun sin(offset: Float, scl: Float, mag: Float) = UncPartProgress { p -> get(p) + Mathf.sin(Time.time + offset, scl, mag) }

  /** 添加正弦波动(全局时间) */
  fun sin(scl: Float, mag: Float) = UncPartProgress { p -> get(p) + Mathf.sin(scl, mag) }

  /** 添加绝对值正弦波动 */
  fun absin(scl: Float, mag: Float) = UncPartProgress { p -> get(p) + Mathf.absin(scl, mag) }

  /** 取模运算,结果在[0,amount)之间 */
  fun mod(amount: Float) = UncPartProgress { p -> Mathf.mod(get(p), amount) }

  /** 循环播放:将原值按周期time映射到[0,1]循环 */
  fun loop(time: Float) = UncPartProgress { p -> Mathf.mod(get(p) / time, 1f) }

  /** 应用双参数函数进行变换 */
  fun apply(other: UncPartProgress, func: PartFunc) = UncPartProgress { p -> func.get(get(p), other.get(p)) }
}