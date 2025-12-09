package ice.library.world.particles.models

import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import arc.util.noise.Noise
import ice.library.world.particles.Particle
import ice.library.world.particles.ParticleModel

open class RandDeflectParticle : ParticleModel() {
    var strength: Float = 1f
    var deflectAngle: Float = 45f

    override fun deflect(p: Particle) {
        p.deflectAngle = deflectAngle
        p.strength = strength
        val angle = Tmp.v1.set(p.speed).scl(-1.0f).angle()
        val scl = Mathf.clamp(p.speed.len() / p.defSpeed * Time.delta * p.strength)
        Tmp.v2.set(p.speed).setAngle(angle + Noise.noise(p.x, p.y, 0.01f, 6.7f) * p.deflectAngle).scl(scl)
        p.speed.add(Tmp.v2)
    }
}
