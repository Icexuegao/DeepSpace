package universecore.world.particles.models

import arc.graphics.Color
import arc.math.Interp
import arc.math.Mathf
import universecore.world.particles.Particle
import universecore.world.particles.Particle.Cloud
import universecore.world.particles.ParticleModel

class SizeVelRelatedParticle : ParticleModel() {
    var finalThreshold: Float = 0.25f
    var fadeThreshold: Float = 0.03f
    var sizeInterp: Interp = Interp.linear

    override fun isFinal(particle: Particle): Boolean {
        return particle.speed.len() <= finalThreshold
    }

    override fun trailColor(particle: Particle): Color? {
        return null
    }

    override fun currSize(particle: Particle): Float {
        return particle.defSize * sizeInterp.apply(Mathf.clamp(particle.speed.len() / particle.defSpeed))
    }

    override fun isFaded(particle: Particle?, cloud: Cloud): Boolean {
        return cloud.size < fadeThreshold
    }
}
