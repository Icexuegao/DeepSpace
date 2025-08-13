package ice.library.meta.consumers

import ice.library.baseContent.blocks.crafting.multipleCrafter.MultipleCrafter.MultipleCrafterBuilding
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.consumers.ConsumePower
import mindustry.world.meta.Stats

class ConsumePowerMultiple(var cons: Array<ConsumePower>) : ConsumePower() {
    override fun apply(block: Block) {
    }

    override fun ignore(): Boolean {
        return true
    }

    override fun display(stats: Stats) {
    }

    override fun requestedPower(entity: Building): Float {
        if (entity is MultipleCrafterBuilding) {
            return if (entity.consPower != null) entity.consPower!!.requestedPower(entity) else 0f
        }
        return 0f
    }
}
