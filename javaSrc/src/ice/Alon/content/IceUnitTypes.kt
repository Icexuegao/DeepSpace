package ice.Alon.content

import arc.func.Func
import arc.func.Prov
import ice.Alon.ai.CircleAi
import ice.Alon.ai.TeleportationAI
import ice.Alon.library.UnitTypeTool
import ice.Ice
import mindustry.entities.bullet.MissileBulletType
import mindustry.type.UnitType
import mindustry.type.Weapon

class IceUnitTypes {
    companion object {
        lateinit var ling: UnitType
        fun load() {
            ling = object : UnitType("ling") {
                init {
                    engineOffset = 7f
                    engineSize = 3f
                    rotateSpeed = 7f
                    speed = 3f
                    health = 1000f
                    hitSize = 16f
                    flying = true
                    faceTarget = false
                    circleTarget = true
                    lowAltitude = true
                    controller = Func { TeleportationAI() }
                    aiController = Prov { CircleAi() }
                    constructor = UnitTypeTool.entityConstructor("alpha")
                    weapons.add(Weapon("${Ice.NAME}-ling-weapon").apply {
                        mirror = true
                        rotate = true
                        predictTarget = false
                        rotateSpeed = 9f
                        reload = 15f
                        bullet = object : MissileBulletType(6f, 1f) {
                            init {
                                lifetime = 90f;homingPower = 0.5f;homingRange = 100f
                            }

                        }
                    })
                }
            }
        }
    }
}