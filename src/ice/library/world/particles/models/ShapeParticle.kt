package ice.library.world.particles.models

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.util.Time
import ice.library.world.particles.Particle
import ice.library.world.particles.ParticleModel
import mindustry.graphics.Layer

class ShapeParticle : ParticleModel() {
    var circle: Boolean = true
    var polySides: Int = 4
    var outline: Boolean = false
    var outlineStoke: Float = 1.6f
    var rotSpeed: Float = 0f

    var layer: Float = Layer.effect

    override fun draw(p: Particle) {
        val l = Draw.z()
        Draw.z(layer)

        Draw.color(p.color)
        if (circle) {
            if (outline) {
                Lines.stroke(outlineStoke)
                Lines.circle(p.x, p.y, p.size)
            } else Fill.circle(p.x, p.y, p.size)
        } else {
            if (outline) {
                Lines.stroke(outlineStoke)
                Lines.poly(p.x, p.y, polySides, p.size, Time.time * rotSpeed)
            } else Lines.poly(p.x, p.y, polySides, p.size, Time.time * rotSpeed)
        }

        Draw.z(l)
    }
}

