package universecore.world.particles.models

import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.util.Interval
import arc.util.Time
import ice.library.struct.AttachedProperty
import mindustry.entities.Units
import mindustry.gen.Bullet
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import singularity.world.particles.SglParticleModels.hitrect
import singularity.world.particles.SglParticleModels.rect
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel

class HeatBulletParticleModel :ParticleModel() {
  companion object {
    var Particle.owner: Bullet? by AttachedProperty(null)
    var Particle.bullet: Bullet? by AttachedProperty(null)
    var Particle.timer: Interval by AttachedProperty(Interval(6))
  }

  val tmp1: Particle.Cloud = Particle.Cloud()
  val tmp2: Particle.Cloud = Particle.Cloud()
  override fun reset(particle: Particle) {
    super.reset(particle)
    particle.owner = null
    particle.bullet = null
  }

  override fun drawTrail(particle: Particle) {
    for(cloud in particle) {
      tmp1.color.set(Color.black)
      tmp1.x = cloud.x
      tmp1.y = cloud.y
      tmp1.size = cloud.size / 2
      tmp1.perCloud = cloud.perCloud
      tmp1.nextCloud = tmp2

      tmp2.x = cloud.nextCloud!!.x
      tmp2.y = cloud.nextCloud!!.y
      tmp2.size = cloud.nextCloud!!.size / 2

      Draw.z(Layer.bullet - 1)
      tmp1.draw()
      Draw.z(Layer.effect)
      cloud.draw()
      tmp1.draw()
    }
  }

  override fun draw(particle: Particle) {
    Draw.z(Layer.bullet - 1)
    Fill.circle(particle.x, particle.y, particle.size)
    Draw.z(Layer.effect)
    Lines.stroke(1f, Pal.lighterOrange)
    Lines.circle(particle.x, particle.y, particle.size)
  }

  override fun currSize(particle: Particle): Float {
    val owner = particle.owner
    return if (owner != null && owner.isAdded) particle.defSize else Mathf.approachDelta(particle.size, 0f, 0.04f)
  }

  override fun update(particle: Particle) {
    particle.owner?.let {
      if (it.isAdded) {
        particle.set(it.x, it.y)
      } else particle.owner = null
    }
    particle.bullet?.let {
      if (it.isAdded) {
        it.keepAlive = true
      }
    }

  }

  override fun updateTrail(particle: Particle, c: Particle.Cloud) {
    c.size -= 0.03f * Time.delta

    particle.bullet?.let { b->
      if ( b.isAdded && particle.timer.get(5f) && c.nextCloud != null) {
        val dx = c.nextCloud!!.x - c.x
        val dy = c.nextCloud!!.y - c.y
        val expand = 3f

        rect.set(c.x, c.y, dx, dy).normalize().grow(expand * 2f)
        b.hitbox(hitrect)

        Units.nearbyEnemies(b.team, rect, Cons { u: Unit? ->
          if (u!!.checkTarget(b.type.collidesAir, b.type.collidesGround) && u.hittable()) {
            u.hitbox(hitrect)
            val vec = Geometry.raycastRect(c.x, c.y, c.nextCloud!!.x, c.nextCloud!!.y, hitrect.grow(expand * 2))

            if (vec != null) {
              if (!b.collided.contains(u.id)) {
                b.collision(u, u.x, u.y)
              }
            }
          }
        })
      }
    }
  }

  override fun isFaded(particle: Particle?, cloud: Particle.Cloud): Boolean {
    return cloud.size <= 0.03f
  }

  override fun trailColor(particle: Particle): Color? {
    return particle.color
  }

  override fun isFinal(particle: Particle): Boolean {
    return !(particle.owner != null && particle.owner!!.isAdded) && particle.size <= 0.04f
  }
}