package universecore.world.particles.models

import arc.math.Mathf
import arc.util.Time
import ice.library.struct.AttachedProperty
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel

open class TimeParticle : ParticleModel() {
    var defLifeMin: Float = 180f
    var defLifeMax: Float = 180f
    var speedRelated: Boolean = false

    var Particle.begion: Float by AttachedProperty(0f)
    var Particle.lifeTime: Float by AttachedProperty(Mathf.random(defLifeMin, defLifeMax))
    var Particle.progress: Float by AttachedProperty(0f)

    override fun init(particle: Particle) {
        particle.begion=Time.time
    }

    override fun update(particle: Particle) {
        val lifeTime: Float = particle.lifeTime
        val time: Float = Time.time - particle.begion
        val prog = 1 - Mathf.clamp(time / lifeTime)
        particle.progress=prog

        if (speedRelated) {
            particle.speed.setLength(particle.defSpeed * prog)
        }
    }

    override fun currSize(particle: Particle): Float {
        return particle.defSize * particle.progress
    }

    override fun isFinal(particle: Particle): Boolean {
        return particle.progress <= 0f
    }


}