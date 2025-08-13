package ice.library.baseContent.unit.type

class IceTankUnitType(name: String) : IceUnitType(name) {
    init {
        squareShape = true
        omniMovement = false
        rotateMoveFirst = true
        rotateSpeed = 1.3f
        envDisabled = 0
        speed = 0.8f
    }
}