package universecore.world.particles.models

import arc.graphics.Color
import arc.math.Mathf
import universecore.world.particles.Particle
import universecore.world.particles.Particle.Cloud
import universecore.world.particles.ParticleModel

open class TrailFadeParticle : ParticleModel() {
    var trailFade: Float = 0.075f
    var fadeColor: Color? = null
    var colorLerpSpeed: Float = 0.03f
    var linear: Boolean = false

    override fun updateTrail(particle: Particle, c: Cloud) {
        c.size = if (linear) Mathf.approachDelta(c.size, 0f, trailFade) else Mathf.lerpDelta(c.size, 0f, trailFade)
        if (fadeColor != null) c.color.lerp(fadeColor, colorLerpSpeed)
    }
}
