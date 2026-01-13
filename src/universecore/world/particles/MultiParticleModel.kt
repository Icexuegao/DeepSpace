package universecore.world.particles


import arc.graphics.Color
import arc.util.Tmp
import universecore.world.particles.Particle.Cloud
import kotlin.math.min

class MultiParticleModel(vararg var models: ParticleModel) : ParticleModel() {

    override fun draw(particle: Particle) {
        for (model in models) {
            model.draw(particle)
        }
    }

    override fun drawTrail(particle: Particle) {
        for (model in models) {
            model.drawTrail(particle)
        }
    }

    override fun updateTrail(particle: Particle, c: Cloud) {
        for (model in models) {
            model.updateTrail(particle, c)
        }
    }

    override fun update(particle: Particle) {
        for (model in models) {
            // if (model == null) break
            model.update(particle)
        }
    }

    override fun init(particle: Particle) {
        for (model in models) {
            model.init(particle)
        }
    }

    override fun trailColor(particle: Particle): Color {
        Tmp.c1.set(particle.color)
        for (model in models) {
            val c = model.trailColor(particle) ?: continue
            Tmp.c1.mul(c)
        }
        return Tmp.c1
    }

    override fun deflect(particle: Particle) {
        for (model in models) {
            model.deflect(particle)
        }
    }

    override fun isFinal(particle: Particle): Boolean {
        for (model in models) {
            if (model.isFinal(particle)) return true
        }
        return false
    }

    override fun isFaded(particle: Particle?, cloud: Cloud): Boolean {
        for (model in models) {
            if (model.isFaded(particle, cloud)) return true
        }
        return false
    }

    override fun currSize(particle: Particle): Float {
        var res = Float.MAX_VALUE

        for (model in models) {
            res = min(model.currSize(particle), res)
        }

        return res
    }
}
