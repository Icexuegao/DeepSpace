package ice.entities.effect

import arc.graphics.Color
import mindustry.entities.Effect

class MultiEffect(vararg var effects: Effect) : Effect() {
    constructor(effect: Effect, size: Int) : this(*Array(size) { effect })

    override fun create(x: Float, y: Float, rotation: Float, color: Color?, data: Any?) {
        if (!shouldCreate()) return

        for (effect in effects) {
            effect.create(x, y, rotation, color, data)
        }
    }
}
