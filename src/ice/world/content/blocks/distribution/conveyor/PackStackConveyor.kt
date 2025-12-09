package ice.world.content.blocks.distribution.conveyor

import arc.func.Prov
import arc.math.geom.Geometry
import mindustry.Vars
import mindustry.content.Blocks

class PackStackConveyor(name: String) : StackConveyor(name) {
    init {
        buildType = Prov(::PackStackConveyorBuild)
    }

    inner class PackStackConveyorBuild : StackConveyorBuild() {
        override fun stateUnload() {
            val nearby1 = Vars.world.tile(tile.x + Geometry.d4(this.rotation).x, tile.y + Geometry.d4(this.rotation).y)
            val nearby = if (nearby1 == null) false else nearby1.block() == Blocks.air || nearby1.block() == null
            if (front() == null && nearby && items.any()) {
                val packStack = PackStack()
                packStack.items.set(items)
                items.clear()
                packStack.shepos = pos()
                packStack.vel.set(5f, 0f).setAngle(rotation * 90f)
                packStack.set(x, y)
                packStack.add()
                poofOut()
                lastItem = null
                return
            }
            while (lastItem != null && (if (!outputRouter) moveForward(lastItem) else dump(lastItem))) {
                if (!outputRouter) {
                    items.remove(lastItem, 1)
                }

                if (items.empty()) {
                    poofOut()
                    lastItem = null
                }
                if (lastItem !== items.first()) break
            }

        }
    }
}