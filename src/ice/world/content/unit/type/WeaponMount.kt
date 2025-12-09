package ice.world.content.unit.type

import mindustry.entities.units.WeaponMount
import mindustry.type.Weapon

class WeaponMount(weapon: Weapon) : WeaponMount(weapon) {
    var warmups = 0f
    var status: MayflyStatus? = null
    var phase: Float = 0f
    var init = false
}