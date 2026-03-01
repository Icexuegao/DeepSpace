package universecore.world.particles.models

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.util.Time
import mindustry.graphics.Layer
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel

open class ShapeParticle : ParticleModel() {
    var circle: Boolean = true
    var polySides: Int = 4
    var outline: Boolean = false
    var outlineStoke: Float = 1.6f
    var rotSpeed: Float = 0f
    var layer: Float = Layer.effect

    override fun draw(particle: Particle) {
        val l = Draw.z()
        Draw.z(layer)

        Draw.color(particle.color)
        if (circle) {
            if (outline) {
                Lines.stroke(outlineStoke)
                Lines.circle(particle.x, particle.y, particle.size)
            } else Fill.circle(particle.x, particle.y, particle.size)
        } else {
            if (outline) {
                Lines.stroke(outlineStoke)
                Lines.poly(particle.x, particle.y, polySides, particle.size, Time.time * rotSpeed)
            } else Lines.poly(particle.x, particle.y, polySides, particle.size, Time.time * rotSpeed)
        }

        Draw.z(l)
    }
}

