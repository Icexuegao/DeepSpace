package ice.entities.bullet

import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Position
import arc.util.Tmp
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.world.meta.IceEffects
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import universecore.util.toColor

class RandomLightningBulletType(
  private val damages: Float,
  private val range: Float,
  private val radius: Float,
  private val reload: Float,
  private val num: Int,
  private val effect: mindustry.entities.Effect
) : BasicBulletType() {

  init {
    hittable = false
    absorbable = false
    reflectable = false
    ammoMultiplier = 1f
  }
  fun Randonge(rx: Float, ry: Float, range: Float): Pair<Float, Float> {
    return Pair(Mathf.range(range) + rx, Mathf.range(range) + ry)
  }

  override fun update(b: Bullet) {
    super.update(b)

    if (b.time >= b.lifetime - 26f) return

    if (b.timer.get(reload)) {
      repeat(num) {
        val xy = Randonge(b.x, b.y, range)
        val x = xy.first
        val y = xy.second

        Damage.damage(b.team, x, y, radius, damages)
        Damage.status(null, x, y, radius, IStatus.坍缩, 60f, true, true)

        chainLightningFadeReversed.at(x, y, Mathf.random(16f, 24f), "FF8663".toColor(), b)
        Sounds.explosionPlasmaSmall.at(x, y)

        Effect.shake(4f, 30f, x, y)
        effect.at(x, y)
      }
    }
  }

  val chainLightningFadeReversed = Effect(25f, 500f) { e ->
    val data = e.data
    if (data !is Position) return@Effect

    val tx = e.x
    val ty = e.y
    val dst = Mathf.dst(data.x, data.y, tx, ty)

    Tmp.v1.set(e.x, e.y).sub(data).nor()

    val normx = Tmp.v1.x
    val normy = Tmp.v1.y
    val range = e.rotation

    val links = Mathf.ceil(dst / range).toInt()
    val spacing = dst / links

    Lines.stroke(2.5f * Mathf.curve(e.fout(), 0f, 0.7f), e.color)

    Lines.beginLine()
    Lines.linePoint(data.x, data.y)

    IceEffects.rand.setSeed(e.id.toLong())

    val fin = Mathf.curve(e.fin(), 0f, 0.5f)
    for (i in 0 until (links * fin).toInt()) {
      val nx: Float
      val ny: Float

      if (i == links - 1) {
        nx = tx
        ny = ty
      } else {
        val len = (i + 1) * spacing
        Tmp.v1.setToRandomDirection(IceEffects.rand).scl(range / 2f)
        nx = data.x + normx * len + Tmp.v1.x
        ny = data.y + normy * len + Tmp.v1.y
      }

      Lines.linePoint(nx, ny)
    }

    Lines.endLine()
  }.apply {
    followParent = false
  }

}
