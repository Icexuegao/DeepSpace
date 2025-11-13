package ice.library.entities.bullet

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Vec2
import arc.util.Tmp
import ice.library.struct.Vec2Seq
import mindustry.gen.Bullet

@Suppress("UNCHECKED_CAST") open class TrailFadeBulletType(
    speed: Float,
    damage: Float,
    bulletSprite: String = "bullet"
) : IceBasicBulletType(speed, damage, bulletSprite) {
    companion object {
        private val v1: Vec2 = Vec2()
        protected val v2: Vec2 = Vec2()
        protected val v3: Vec2 = Vec2()
        protected val rand: Rand = Rand()
    }

    var tracers: Int = 1
    var tracerStroke: Float = 3f
    var tracerFadeOffset = 10f
    var tracerStrokeOffset: Int = 15
    var tracerSpacing: Float = 8f
    var tracerRandX: Float = 6f
    var tracerUpdateSpacing: Float = 0.3f

    /** 是否将子弹的生成点添加到跟踪序列中。 */
    var addBeginPoint: Boolean = false

    init {
        impact = true
    }

    /* override fun despawned(b: Bullet) {
         val data = b.data
         if (data is Array<*> && data.isArrayOf<Vec2Seq>()) {
             val pointsArr = data as Array<Vec2Seq>
             for (points in pointsArr) {
                 points.add(b.x, b.y)
                 if (despawnBlinkTrail || (b.absorbed && hitBlinkTrail)) {
                     PosLightning.createBoltEffect(hitColor, tracerStroke * 2f, points)
                     val v = points.firstTmp()
                     //NHFx.lightningHitSmall.at(v.x, v.y, hitColor)
                 } else {
                     points.add(tracerStroke, tracerFadeOffset)
                     // NHFx.lightningFade.at(b.x, b.y, tracerStrokeOffset, hitColor, points)
                 }
             }
             b.data = null
         }
         super.despawned(b)
     }*/
    override fun init(b: Bullet) {
        super.init(b)
        b.data = Array(tracers) { i ->
            Vec2Seq().also { if (addBeginPoint) it.add(b.x, b.y) }
        }
    }

    override fun update(b: Bullet) {
        super.update(b)
        if (b.timer(2, tracerUpdateSpacing)) {
            val data = b.data
            if (!(data is Array<*> && data.isArrayOf<Vec2Seq>())) return
            val points = data as Array<Vec2Seq>
            for (seq in points) {
                v2.trns(b.rotation(), 0f, rand.range(tracerRandX))
                v1.setToRandomDirection(rand).scl(tracerSpacing)
                seq.add(v3.set(b.x, b.y).add(v1).add(v2))
            }
        }
    }

    override fun drawTrail(b: Bullet) {
        super.drawTrail(b)
        val data = b.data
        if ((data is Array<*> && data.isArrayOf<Vec2Seq>())) {
            val pointsArr = b.data() as Array<Vec2Seq>
            for (points in pointsArr) {
                if (points.size() < 2) return
                Draw.color(hitColor)
                for (i in 1..<points.size()) {
                    Lines.stroke(Mathf.clamp(
                        (i + tracerFadeOffset / 2f) / points.size() * (tracerStrokeOffset - (points.size() - i)) / tracerStrokeOffset) * tracerStroke)
                    val from = points.setVec2(i - 1, Tmp.v1)
                    val to = points.setVec2(i, Tmp.v2)
                    Lines.line(from.x, from.y, to.x, to.y, false)
                    Fill.circle(from.x, from.y, Lines.getStroke() / 2)
                }

                Fill.circle(points.peekTmp().x, points.peekTmp().y, tracerStroke)
            }
        }
    }

}