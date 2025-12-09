package ice.library.world.particles.models

import ice.library.world.particles.Particle
import ice.library.world.particles.ParticleModel

class DrawDefaultTrailParticle : ParticleModel() {
    override fun drawTrail(c: Particle) {
        var n = 0f
        for (ps in c) {
            ps.draw(1 - n / c.cloudCount(), 1 - (n + 1) / c.cloudCount())
            n++
        }
    }
}