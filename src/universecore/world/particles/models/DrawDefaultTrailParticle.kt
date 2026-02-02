package universecore.world.particles.models

import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel

open class DrawDefaultTrailParticle : ParticleModel() {
    override fun drawTrail(particle: Particle) {
        var n = 0f
        for (ps in particle) {
            ps.draw(1 - n / particle.cloudCount(), 1 - (n + 1) / particle.cloudCount())
            n++
        }
    }
}