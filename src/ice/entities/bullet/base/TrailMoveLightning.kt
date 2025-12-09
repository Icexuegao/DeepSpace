package ice.entities.bullet.base

import arc.math.Mathf
import arc.util.Time
import arc.util.pooling.Pool.Poolable

class TrailMoveLightning : Poolable {
    var off: Float = 0f
    var offDelta: Float = 0f

    var chance: Float = 0.3f
    var maxOff: Float = 4f
    var range: Float = 4f

    init {
        flushDelta(0)
    }

    private fun flushDelta(i: Int) {
        offDelta = Mathf.random(if (i <= 0) -range else 0f, if (i >= 0) range else 0f)
    }

    fun update() {
        if (Mathf.chanceDelta(chance.toDouble()) || off >= maxOff || off <= -maxOff) flushDelta(if (off >= maxOff) -1 else if (off <= -maxOff) 1 else 0)
        off += offDelta * Time.delta
    }

    override fun reset() {
        off = 0f
        offDelta = 0f
        maxOff = 4f
        range = 4f
    }
}
