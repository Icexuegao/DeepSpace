package ice.world.content.unit.ability

import mindustry.gen.Hitboxc
import mindustry.gen.Unit

interface ICollideBlockerAbility {
    fun blockedCollides(unit: Unit, other: Hitboxc): Boolean
}
