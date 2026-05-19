package universecore.world.particles.models

import arc.graphics.Color
import arc.util.Tmp
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel
import kotlin.math.min

/**多粒子模型组合器
 *
 * 将多个粒子模型组合在一起,按顺序调用每个模型的方法
 * 适用于需要多种粒子行为叠加的场景
 *
 * 你不应当重写该类的任何方法
 * @see universecore.world.particles.ParticleModel
 * @see universecore.world.particles.Particle
 */
class MultiParticleModel(vararg var models: ParticleModel) :ParticleModel() {

  override fun draw(particle: Particle) {
    for(model in models) model.draw(particle)
  }

  override fun drawTrail(particle: Particle) {
    for(model in models) model.drawTrail(particle)
  }

  override fun reset(particle: Particle) {
    for(model in models) model.reset(particle)
  }

  override fun updateTrail(particle: Particle, c: Particle.Cloud) {
    for(model in models) model.updateTrail(particle, c)
  }

  override fun remove(particle: Particle) {
    for(model in models) model.remove(particle)
  }

  override fun update(particle: Particle) {
    for(model in models) model.update(particle)
  }

  override fun init(particle: Particle) {
    for(model in models) model.init(particle)
  }

  override fun trailColor(particle: Particle): Color {
    Tmp.c1.set(particle.color)
    for(model in models) {
      val c = model.trailColor(particle) ?: continue
      Tmp.c1.mul(c)
    }
    return Tmp.c1
  }

  override fun deflect(particle: Particle) {
    for(model in models) model.deflect(particle)
  }

  override fun isFinal(particle: Particle): Boolean {
    for(model in models) if (model.isFinal(particle)) return true
    return false
  }

  override fun isFaded(particle: Particle?, cloud: Particle.Cloud): Boolean {
    for(model in models) if (model.isFaded(particle, cloud)) return true
    return false
  }

  override fun currSize(particle: Particle): Float {
    var res = Float.MAX_VALUE

    for(model in models) {
      res = min(model.currSize(particle), res)
    }

    return res
  }
}