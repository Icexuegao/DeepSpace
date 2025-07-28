package ice.library.type.baseContent.blocks.environment

import arc.Core
import arc.struct.Seq
import ice.library.util.accessField
import mindustry.Vars
import mindustry.graphics.BlockRenderer
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor

open class IceFloor(name: String, variants: Int) : Floor(name, variants) {
    var BlockRenderer.updateFloors: Seq<UpdateRenderState> by accessField("updateFloors")
    var updateFloor = false

    init {
        hasColor = true
    }

    override fun init() {
        super.init()
        Vars.content.block("${this.name}Wall")?.let {
            wall = it
        }
    }

    override fun updateRender(tile: Tile) = updateFloor
    override fun icons() = if (variants == 0) arrayOf(Core.atlas.find(name)) else arrayOf(Core.atlas.find(name + "1"))

}