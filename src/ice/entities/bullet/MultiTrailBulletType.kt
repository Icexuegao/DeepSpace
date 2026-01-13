package ice.entities.bullet

import arc.math.Mathf
import arc.math.Rand
import arc.util.Time
import arc.util.Tmp
import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.gen.Bullet
import mindustry.graphics.Trail
import singularity.graphic.SglDraw

open class MultiTrailBulletType : BulletType() {
    var subTrails: Int = 2
    var subTrailWidth: Float = 2f
    var subRotSpeed: Float = 8f
    var offset: Float = 8f
    var offsetMove: Boolean = true

    override fun init(b: Bullet) {
        super.init(b)
        val sub = arrayOfNulls<Trail>(subTrails)
        b.data = sub

        for (i in sub.indices) {
            sub[i] = Trail(trailLength / 2)
        }
    }

    override fun removed(b: Bullet) {
        super.removed(b)
        val data = b.data
        if (data is Array<*> && data.isArrayOf<Trail>()) {
            for (trail in data) {
                Fx.trailFade.at(b.x, b.y, 2f, trailColor, (trail as Trail).copy())
            }
        }
    }

    override fun update(b: Bullet) {
        super.update(b)
        val data = b.data
        if (data is Array<*> && data.isArrayOf<Trail>()) {
            val step: Float = 360f / data.size
            Tmp.v1.set(4 + offset * (if (offsetMove) b.fslope() else 1f), 0f).setAngle(b.rotation() + 90)
            for (i in data.indices) {
                rand.setSeed(b.id.toLong())
                val lerp = Mathf.sinDeg(rand.random(0f, 360f) + Time.time * subRotSpeed + step * i)
                (data[i] as Trail).update(b.x + Tmp.v1.x * lerp, b.y + Tmp.v1.y * lerp)
            }
        }
    }

    override fun drawTrail(b: Bullet) {
        super.drawTrail(b)
        val data = b.data
        if (data is Array<*> && data.isArrayOf<Trail>()) {
            val step: Float = 360f / data.size
            Tmp.v1.set(4 + offset * (if (offsetMove) b.fslope() else 1f), 0f).setAngle(b.rotation() + 90)
            for (i in data.indices) {
                rand.setSeed(b.id.toLong())
                val lerp = Mathf.sinDeg(rand.random(0f, 360f) + Time.time * subRotSpeed + step * i)
                SglDraw.drawDiamond(
                    b.x + Tmp.v1.x * lerp, b.y + Tmp.v1.y * lerp, 8f, 4f, b.rotation() + Tmp.v2.set(b.vel.len(), -Mathf.sinDeg(Time.time * subRotSpeed + step * i) * 2 * b.fslope()).angle()
                )
                (data[i] as Trail).draw(trailColor, subTrailWidth)
            }
        }
    }

    companion object {
        private val rand = Rand()
    }
}