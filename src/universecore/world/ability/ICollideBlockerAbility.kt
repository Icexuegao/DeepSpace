package universecore.world.ability

import mindustry.gen.Hitboxc
import mindustry.gen.Unit

interface ICollideBlockerAbility {
    fun blockedCollides(unit: Unit, other: Hitboxc): Boolean
}
