package universecore.world.particles.models

import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import arc.util.noise.Noise
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel

open class RandDeflectParticle : ParticleModel() {
  var strength: Float = 1f
  var deflectAngle: Float = 45f

  override fun deflect(particle: Particle) {
    particle.deflectAngle = deflectAngle
    particle.strength = strength
    val angle = Tmp.v1.set(particle.speed).scl(-1.0f).angle()
    val scl = Mathf.clamp(particle.speed.len() / particle.defSpeed * Time.delta * particle.strength)
    Tmp.v2.set(particle.speed).setAngle(angle + Noise.noise(particle.x, particle.y, 0.01f, 6.7f) * particle.deflectAngle).scl(scl)
    particle.speed.add(Tmp.v2)
  }
}
