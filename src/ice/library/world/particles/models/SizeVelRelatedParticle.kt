package ice.library.world.particles.models

import arc.graphics.Color
import arc.math.Interp
import arc.math.Mathf
import ice.library.world.particles.Particle
import ice.library.world.particles.Particle.Cloud
import ice.library.world.particles.ParticleModel

class SizeVelRelatedParticle : ParticleModel() {
    var finalThreshold: Float = 0.25f
    var fadeThreshold: Float = 0.03f
    var sizeInterp: Interp = Interp.linear

    override fun isFinal(p: Particle): Boolean {
        return p.speed.len() <= finalThreshold
    }

    override fun trailColor(p: Particle): Color? {
        return null
    }

    override fun currSize(p: Particle): Float {
        return p.defSize * sizeInterp.apply(Mathf.clamp(p.speed.len() / p.defSpeed))
    }

    override fun isFaded(p: Particle, cloud: Cloud): Boolean {
        return cloud.size < fadeThreshold
    }
}
