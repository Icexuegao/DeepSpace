package universecore.world.particles.models

import arc.func.Floatf
import arc.func.Func
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel


class TargetMoveParticle : ParticleModel() {
    var deflection: Floatf<Particle> = Floatf { e: Particle -> 0.2f }
    var dest: Func<Particle, Vec2>? = null

    override fun deflect(particle: Particle) {
        val from = particle.speed.angle()
        val dest = this.dest!!.get(particle)
        val to = Tmp.v1.set(dest.x, dest.y).sub(particle.x, particle.y).angle()
        var r = to - from
        r = if (r > 180) r - 360 else if (r < -180) r + 360 else r
        particle.speed.rotate(r * deflection.get(particle) * Time.delta)
    }

    override fun isFinal(particle: Particle): Boolean {
        val dest = this.dest!!.get(particle)
        return Mathf.len(particle.x - dest.x, particle.y - dest.y) <= 2f
    }
}