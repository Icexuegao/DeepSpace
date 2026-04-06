package ice.entities.bullet

import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.gen.Bullet
import kotlin.math.max

/** Basic continuous (line) bullet type that does not draw itself. Essentially abstract.  */
open class ContinuousBulletType :BulletType() {
  var length: Float = 220f
  var shake: Float = 0f
  var damageInterval: Float = 5f
  var largeHit: Boolean = false
  var continuous: Boolean = true
  /** If a building fired this, whether to multiply damage by its timescale.  */
  var timescaleDamage: Boolean = false

  init {
    removeAfterPierce = false
    pierceCap = -1
    speed = 0f
    despawnEffect = Fx.none
    shootEffect = Fx.none
    lifetime = 16f
    impact = true
    keepVelocity = false
    collides = false
    pierce = true
    hittable = false
    absorbable = false
  }

  override fun continuousDamage(): Float {
    if (!continuous) return -1f
    return damage / damageInterval * 60f
  }

  override fun estimateDPS(): Float {
    if (!continuous) return super.estimateDPS()
    //assume firing duration is about 100 by default, may not be accurate there's no way of knowing in this method
    //assume it pierces 3 blocks/units
    return damage * 100f / damageInterval * 3f
  }

  override fun calculateRange(): Float {
    return max(length+rangeChange, maxRange)
  }

  override fun init() {
    super.init()

    drawSize = max(drawSize, length * 2f)
  }

  override fun init(b: Bullet) {
    super.init(b)

    if (!continuous) {
      applyDamage(b)
    }
  }

  override fun update(b: Bullet) {
    if (!continuous) return

    //damage every 5 ticks
    if (b.timer(1, damageInterval)) {
      applyDamage(b)
    }

    if (shake > 0) {
      Effect.shake(shake, shake, b)
    }

    updateBulletInterval(b)
  }

  open fun applyDamage(b: Bullet) {
    val damage = b.damage
    val owner = b.owner
    if (timescaleDamage && owner is Building) {
      b.damage *= owner.timeScale()
    }
    Damage.collideLine(b, b.team, b.x, b.y, b.rotation(), currentLength(b), largeHit, laserAbsorb, pierceCap)
    b.damage = damage
  }

  open fun currentLength(b: Bullet): Float {
    return length
  }
}