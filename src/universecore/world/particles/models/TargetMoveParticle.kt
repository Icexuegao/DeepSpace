package universecore.world.particles.models

import arc.func.Floatf
import arc.func.Func
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import universecore.struct.AttachedProperty
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel

/** 目标移动粒子模型
 *
 * 粒子会逐渐转向并飞向指定目标位置,
 * 到达目标附近时判定为终态 */
open class TargetMoveParticle :ParticleModel() {
  companion object {
    var Particle.dest: Vec2 by AttachedProperty { Vec2() }
    var Particle.eff: Float by AttachedProperty { 0f }
  }

  var deflection: Floatf<Particle> = Floatf { _ -> 0.2f }
  var target: Func<Particle, Vec2>? = null

  override fun reset(particle: Particle) {
    super.reset(particle)
    particle.dest = Vec2()
    particle.eff = 0f
  }

  override fun deflect(particle: Particle) {
    val from = particle.speed.angle()
    val dest = this.target!!.get(particle)
    val to = Tmp.v1.set(dest.x, dest.y).sub(particle.x, particle.y).angle()
    var r = to - from
    r = if (r > 180) r - 360 else if (r < -180) r + 360 else r
    particle.speed.rotate(r * deflection.get(particle) * Time.delta)
  }

  override fun isFinal(particle: Particle): Boolean {
    val dest = this.target!!.get(particle)
    return Mathf.len(particle.x - dest.x, particle.y - dest.y) <= 2f
  }
}