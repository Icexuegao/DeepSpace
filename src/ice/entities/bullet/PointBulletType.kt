package ice.entities.bullet

import arc.func.Cons
import arc.func.Floatc2
import arc.math.geom.Geometry
import arc.util.Tmp
import ice.entities.bullet.base.BulletType
import mindustry.Vars
import mindustry.entities.Units
import mindustry.gen.Bullet
import mindustry.gen.Unit

class PointBulletType : BulletType() {
  var trailSpacing: Float = 10f

  init {
    scaleLife = true
    lifetime = 100f
    collides = false
    reflectable = false
    keepVelocity = false
  }

  override fun init(b: Bullet) {
    super.init(b)

    val px = b.x + b.lifetime * b.vel.x
    val py = b.y + b.lifetime * b.vel.y
    val rot = b.rotation()

    Geometry.iterateLine(0f, b.x, b.y, px, py, trailSpacing) { x: Float, y: Float ->
      trailEffect.at(x, y, rot)
    }

    b.time = b.lifetime
    b.set(px, py)

    //calculate hit entity
    cdist = 0f
    result = null
    val range = 1f

    Units.nearbyEnemies(b.team, px - range, py - range, range * 2f, range * 2f, Cons { e: Unit? ->
      if (e!!.dead() || !e.checkTarget(collidesAir, collidesGround) || !e.hittable()) return@Cons
      e.hitbox(Tmp.r1)
      if (!Tmp.r1.contains(px, py)) return@Cons

      val dst = e.dst(px, py) - e.hitSize
      if ((result == null || dst < cdist)) {
        result = e
        cdist = dst
      }
    })

    if (result != null) {
      b.collision(result, px, py)
    } else if (collidesTiles) {
      val build = Vars.world.buildWorld(px, py)
      if (build != null && build.team !== b.team) {
        build.collision(b)
        hit(b, px, py)
        b.hit = true
      }
    }

    b.remove()

    b.vel.setZero()
  }

  companion object {
    private var cdist = 0f
    private var result: Unit? = null
  }
}
