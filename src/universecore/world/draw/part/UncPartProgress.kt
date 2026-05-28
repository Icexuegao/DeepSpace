package universecore.world.draw.part

import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import mindustry.entities.part.DrawPart.PartFunc
import mindustry.entities.part.DrawPart.PartParams
import kotlin.math.max

fun interface UncPartProgress {
  companion object {
    fun constant(value: Float): UncPartProgress {
      return UncPartProgress { p -> value }
    }

    /** Reload of the weapon - 1 right after shooting, 0 when ready to fire */
    val reload: UncPartProgress = UncPartProgress { p -> p.reload }
    /** Reload, but smoothed out, so there is no sudden jump between 0-1.  */
    val smoothReload: UncPartProgress = UncPartProgress { p -> p.smoothReload }
    /** Weapon warmup, 0 when not firing, 1 when actively shooting. Not equivalent to heat.  */
    val warmup: UncPartProgress = UncPartProgress { p -> p.warmup }
    /** Weapon charge, 0 when beginning to charge, 1 when finished  */
    val charge: UncPartProgress = UncPartProgress { p -> p.charge }
    /** Weapon recoil with no curve applied.  */
    val recoil: UncPartProgress = UncPartProgress { p -> p.recoil }
    /** Weapon heat, 1 when just fired, 0, when it has cooled down (duration depends on weapon)  */
    val heat: UncPartProgress = UncPartProgress { p -> p.heat }
    /** Lifetime fraction, 0 to 1. Only for missiles.  */
    val life: UncPartProgress = UncPartProgress { p -> p.life }
    /** Current unscaled value of Time.time.  */
    val time: UncPartProgress = UncPartProgress { p -> Time.time }
  }

  fun get(p: PartParams): Float

  fun getClamp(p: PartParams): Float {
    return getClamp(p, true)
  }

  fun getClamp(p: PartParams, clamp: Boolean): Float {
    return if (clamp) Mathf.clamp(get(p)) else get(p)
  }

  fun inv(): UncPartProgress {
    return UncPartProgress { p -> 1f - get(p) }
  }

  fun slope(): UncPartProgress {
    return UncPartProgress { p -> Mathf.slope(get(p)) }
  }

  fun clamp(): UncPartProgress {
    return UncPartProgress { p -> Mathf.clamp(get(p)) }
  }

  fun add(amount: Float): UncPartProgress {
    return UncPartProgress { p -> get(p) + amount }
  }

  fun add(other: UncPartProgress): UncPartProgress {
    return UncPartProgress { p -> get(p) + other.get(p) }
  }

  fun delay(amount: Float): UncPartProgress {
    return UncPartProgress { p -> (get(p) - amount) / (1f - amount) }
  }

  fun curve(offset: Float, duration: Float): UncPartProgress {
    return UncPartProgress { p -> (get(p) - offset) / duration }
  }

  fun sustain(offset: Float, grow: Float, sustain: Float): UncPartProgress {
    return UncPartProgress { p ->
      val `val` = get(p) - offset
      kotlin.math.min(max(`val`, 0f) / grow, (grow + sustain + grow - `val`) / grow)
    }
  }

  fun shorten(amount: Float): UncPartProgress {
    return UncPartProgress { p -> get(p) / (1f - amount) }
  }

  fun compress(start: Float, end: Float): UncPartProgress {
    return UncPartProgress { p -> Mathf.curve(get(p), start, end) }
  }

  fun blend(other: UncPartProgress, amount: Float): UncPartProgress {
    return UncPartProgress { p -> Mathf.lerp(get(p), other.get(p), amount) }
  }

  fun mul(other: UncPartProgress): UncPartProgress {
    return UncPartProgress { p -> get(p) * other.get(p) }
  }

  fun mul(amount: Float): UncPartProgress {
    return UncPartProgress { p -> get(p) * amount }
  }

  fun min(other: UncPartProgress): UncPartProgress {
    return UncPartProgress { p -> kotlin.math.min(get(p), other.get(p)) }
  }

  fun sin(offset: Float, scl: Float, mag: Float): UncPartProgress {
    return UncPartProgress { p -> get(p) + Mathf.sin(Time.time + offset, scl, mag) }
  }

  fun sin(scl: Float, mag: Float): UncPartProgress {
    return UncPartProgress { p -> get(p) + Mathf.sin(scl, mag) }
  }

  fun absin(scl: Float, mag: Float): UncPartProgress {
    return UncPartProgress { p -> get(p) + Mathf.absin(scl, mag) }
  }

  fun mod(amount: Float): UncPartProgress {
    return UncPartProgress { p -> Mathf.mod(get(p), amount) }
  }

  fun loop(time: Float): UncPartProgress {
    return UncPartProgress { p -> Mathf.mod(get(p) / time, 1f) }
  }

  fun apply(other: UncPartProgress, func: PartFunc): UncPartProgress {
    return UncPartProgress { p -> func.get(get(p), other.get(p)) }
  }

  fun curve(interp: Interp): UncPartProgress {
    return UncPartProgress { p -> interp.apply(get(p)) }
  }

}