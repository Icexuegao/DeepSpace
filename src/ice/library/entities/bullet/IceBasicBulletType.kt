package ice.library.entities.bullet

import ice.library.IFiles
import mindustry.entities.bullet.BasicBulletType

open class IceBasicBulletType : BasicBulletType {
    constructor() : super()
    constructor(speed: Float, damage: Float) : super(speed, damage)
    constructor(speed: Float, damage: Float, bulletSprite: String) : super(speed, damage, bulletSprite)

    init {
        sprite = "circle-bullet"
        backRegion = IFiles.findIcePng("$sprite-back")
        frontRegion = IFiles.findIcePng(sprite)
    }
}