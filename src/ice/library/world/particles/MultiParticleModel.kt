package ice.library.world.particles


import arc.graphics.Color
import arc.util.Tmp
import ice.library.world.particles.Particle.Cloud
import kotlin.math.min

class MultiParticleModel(vararg var models: ParticleModel) : ParticleModel() {

    override fun draw(p: Particle) {
        for (model in models) {
            model.draw(p)
        }
    }

    override fun drawTrail(c: Particle) {
        for (model in models) {
            model.drawTrail(c)
        }
    }

    override fun updateTrail(p: Particle, c: Cloud) {
        for (model in models) {
            model.updateTrail(p, c)
        }
    }

    override fun update(p: Particle) {
        for (model in models) {
            // if (model == null) break
            model.update(p)
        }
    }

    override fun init(particle: Particle) {
        for (model in models) {
            model.init(particle)
        }
    }

    override fun trailColor(p: Particle): Color {
        Tmp.c1.set(p.color)
        for (model in models) {
            val c = model.trailColor(p)
            if (c == null) continue
            Tmp.c1.mul(c)
        }
        return Tmp.c1
    }

    override fun deflect(p: Particle) {
        for (model in models) {
            model.deflect(p)
        }
    }

    override fun isFinal(p: Particle): Boolean {
        for (model in models) {
            if (model.isFinal(p)) return true
        }
        return false
    }

    override fun isFaded(p: Particle, cloud: Cloud): Boolean {
        for (model in models) {
            if (model.isFaded(p, cloud)) return true
        }
        return false
    }

    override fun currSize(p: Particle): Float {
        var res = Float.MAX_VALUE

        for (model in models) {
            res = min(model.currSize(p), res)
        }

        return res
    }
}
