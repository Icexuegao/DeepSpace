package ice.entities.bullet

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Position
import arc.util.Tmp
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.gen.Bullet
import mindustry.gen.Unit
import universecore.util.toColor

class BlockHoleBulletType(
  val ranges: Float, val suction: Float, val damages: Float, val percent: Float
) :BasicBulletType() {

  init {
    shrinkY = 0f
    this.damage = 0f
    speed = 0f
    hittable = false
    collides = false
    absorbable = false
    reflectable = false
    ammoMultiplier = 1f
  }

  override fun update(b: Bullet) {
    super.update(b)

    Units.nearbyEnemies(b.team, b.x, b.y, ranges) { u: Unit ->
      val dst = 1f - u.dst(b) / ranges
      u.impulse(Tmp.v3.set(b).sub(u).nor().scl(80f * suction * (dst + 1f)))

      Damage.status(b.team, b.x, b.y, ranges, IStatus.坍缩, 60f, true, true)

      val continuousDamage = (u.type.health * percent / (lifetime / 60f) + damages) * dst / 60f
      u.damageContinuousPierce(continuousDamage)
    }
  }

  override fun draw(b: Bullet) {
    drawTrail(b)

    val (x, y) = Randonge(b.x, b.y, ranges)

    if (Vars.state.isPaused || b.time >= lifetime - 26f) return

    if (Mathf.chance(0.2)) {
      chainLightningFade.at(x, y, Mathf.random(8f, 16f), "FF8663".toColor(), b)
    }

    Draw.reset()
  }

  fun Randonge(rx: Float, ry: Float, range: Float): Pair<Float, Float> {
    return Pair(Mathf.range(range) + rx, Mathf.range(range) + ry)
  }

  val chainLightningFade = Effect(25f, 500f) { e ->
    val data = e.data
    if (data !is Position) return@Effect

    val tx = data.x
    val ty = data.y
    val dst = Mathf.dst(e.x, e.y, tx, ty)

    Tmp.v1.set(data).sub(e.x, e.y).nor()

    val normx = Tmp.v1.x
    val normy = Tmp.v1.y
    val range = e.rotation

    val links = Mathf.ceil(dst / range).toInt()
    val spacing = dst / links

    Lines.stroke(2.5f * Mathf.curve(e.fout(), 0f, 0.7f), e.color)

    Lines.beginLine()
    Lines.linePoint(e.x, e.y)

    IceEffects.rand.setSeed(e.id.toLong())

    val fin = Mathf.curve(e.fin(), 0f, 0.5f)
    for(i in 0 until (links * fin).toInt()) {
      val nx: Float
      val ny: Float

      if (i == links - 1) {
        nx = tx
        ny = ty
      } else {
        val len = (i + 1) * spacing
        Tmp.v1.setToRandomDirection(IceEffects.rand).scl(range / 2f)
        nx = e.x + normx * len + Tmp.v1.x
        ny = e.y + normy * len + Tmp.v1.y
      }

      Lines.linePoint(nx, ny)
    }

    Lines.endLine()
  }.apply {
    followParent = false
  }

}
