package ice.world.draw.part

import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import mindustry.entities.part.DrawPart
import mindustry.entities.part.DrawPart.PartFunc
import mindustry.entities.part.DrawPart.PartParams
import kotlin.math.max

fun interface IcePartProgress : DrawPart.PartProgress {

  override fun getClamp(p: PartParams?): Float {
    return getClamp(p, true)
  }

  override fun getClamp(p: PartParams?, clamp: Boolean): Float {
    return if (clamp) Mathf.clamp(get(p)) else get(p)
  }

  override fun inv(): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> 1f - get(p) }
  }

  override fun slope(): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> Mathf.slope(get(p)) }
  }

  override fun clamp(): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> Mathf.clamp(get(p)) }
  }

  override fun add(amount: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) + amount }
  }

  override fun add(other: DrawPart.PartProgress): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) + other.get(p) }
  }

  override fun delay(amount: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> (get(p) - amount) / (1f - amount) }
  }

  override fun curve(offset: Float, duration: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> (get(p) - offset) / duration }
  }

  override fun sustain(offset: Float, grow: Float, sustain: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? ->
      val `val` = get(p) - offset
      kotlin.math.min(max(`val`, 0f) / grow, (grow + sustain + grow - `val`) / grow)
    }
  }

  override fun shorten(amount: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) / (1f - amount) }
  }

  override fun compress(start: Float, end: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> Mathf.curve(get(p), start, end) }
  }

  override fun blend(other: DrawPart.PartProgress, amount: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> Mathf.lerp(get(p), other.get(p), amount) }
  }

  override fun mul(other: DrawPart.PartProgress): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) * other.get(p) }
  }

  override fun mul(amount: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) * amount }
  }

  override fun min(other: DrawPart.PartProgress): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> kotlin.math.min(get(p), other.get(p)) }
  }

  override fun sin(offset: Float, scl: Float, mag: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) + Mathf.sin(Time.time + offset, scl, mag) }
  }

  override fun sin(scl: Float, mag: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) + Mathf.sin(scl, mag) }
  }

  override fun absin(scl: Float, mag: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> get(p) + Mathf.absin(scl, mag) }
  }

  override fun mod(amount: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> Mathf.mod(get(p), amount) }
  }

  override fun loop(time: Float): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> Mathf.mod(get(p) / time, 1f) }
  }

  override fun apply(other: DrawPart.PartProgress, func: PartFunc): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> func.get(get(p), other.get(p)) }
  }

  override fun curve(interp: Interp): DrawPart.PartProgress {
    return DrawPart.PartProgress { p: PartParams? -> interp.apply(get(p)) }
  }
}