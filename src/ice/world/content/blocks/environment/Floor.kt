package ice.world.content.blocks.environment

import arc.Core
import arc.struct.Seq
import ice.library.EventType
import ice.library.EventType.addContentInitEvent
import ice.library.util.accessField
import ice.world.content.blocks.abstractBlocks.Variants
import mindustry.Vars
import mindustry.graphics.BlockRenderer
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor

open class Floor(name: String) : Floor(name) {
    var BlockRenderer.updateFloors: Seq<UpdateRenderState> by accessField("updateFloors")
    var updateFloor = false

    init {
        hasColor = true

        EventType.addAtlasPackEvent {
            Variants.setBlockVariants(this)
        }
        addContentInitEvent {
            Vars.content.block("${this.name}Wall")?.let {
                wall = it
            }
            val block = Vars.content.block("${name}Stone")
            if (block is Prop) {
                decoration = block
            }
        }
    }

    override fun updateRender(tile: Tile) = updateFloor
    override fun icons() = arrayOf(Core.atlas.find("${name}1"))
}