package ice.type.unit

import ice.Ice
import mindustry.type.Weapon

open class IceWeapon(name: String) : Weapon("${Ice.NAME}-$name")