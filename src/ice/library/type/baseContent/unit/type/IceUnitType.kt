package ice.library.type.baseContent.unit.type

import mindustry.type.UnitType
import mindustry.type.Weapon

open class IceUnitType(name: String) : UnitType(name) {
    open inner class IceWeapon(name: String = "") : Weapon(name) {
        init {
            this.name = this@IceUnitType.name + name
        }
    }
}
