package ice.ai

import ice.library.baseContent.blocks.distribution.droneNetwork.DroneDeliveryTerminal.DroneDeliveryTerminalBuild
import mindustry.entities.units.AIController
import mindustry.gen.Building
import mindustry.gen.Call
import mindustry.type.Item

class CarryTaskAI : AIController() {
    class ItemTask(val source: DroneDeliveryTerminalBuild, val target: Building, val item: Item, val amount: Int)

    var source: DroneDeliveryTerminalBuild? = null
    var task: ItemTask? = null
    fun addTask(task: ItemTask) {
        source = task.source
        this.task = task
    }

    var heath = 0
    override fun updateMovement() {
        heath += 1
        task?.also {
            val stack = unit.stack
            if (stack.item == it.item && stack.amount == it.amount) {
                moveTo(it.target, 0f)
                unit.lookAt(it.target)
                if (it.target.within(unit, 20f)) {
                    Call.transferItemTo(unit, it.item, it.amount, it.target.x, it.target.y, it.target)
                    task = null
                }
            } else {
                moveTo(it.source, 0f)
                unit.lookAt(it.source)
                if (it.source.within(unit, 20f)) {
                    Call.takeItems(it.source, it.item, it.amount, unit)
                }
            }
            heath = 0
            return
        }
        source?.also {
            heath = 0
            moveTo(it, 40f)
            unit.lookAt(it)
        }
        if (heath > 60 * 4) unit.kill()
    }
}