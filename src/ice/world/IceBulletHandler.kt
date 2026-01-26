package ice.world

import mindustry.entities.pattern.ShootPattern

fun interface IceBulletHandler : ShootPattern.BulletHandler {
  override fun shoot(x: Float, y: Float, rotation: Float, delay: Float) {
    shoot(x, y, rotation, delay, null)
  }
}