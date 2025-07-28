package ice.library.entities.ability

import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.util.Time
import mindustry.entities.abilities.Ability
import mindustry.gen.Unit

class RotatorAbility(val name: String, val x: Float, val y: Float, val speed: Float, val mirror: Boolean): Ability() {
    init {
        display=false
    }

    override fun draw(unit: Unit) {
        val rot = unit.rotation - 90
        val trnsx1 = Angles.trnsx(rot, x, y)
        val trnsy1 = Angles.trnsy(rot, x, y)
        val trnsx2 = Angles.trnsx(rot, -x, y)
        val trnsy2 = Angles.trnsy(rot, -x, y)

        val speed = Time.time * speed * 6
        val ux = unit.x + trnsx1
        val uy = unit.y + trnsy1
        val nx = unit.x +trnsx2
        val ny = unit.y + trnsy2
        Draw.rect(unit.type.name + "-" + name, ux, uy, speed)
        if (mirror) Draw.rect(unit.type.name + "-" + name, nx, ny, -speed)
    }
}