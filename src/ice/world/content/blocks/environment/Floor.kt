package ice.world.content.blocks.environment

import arc.Core
import arc.struct.Seq
import ice.library.IFiles
import ice.library.util.accessField
import mindustry.Vars
import mindustry.graphics.BlockRenderer
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor

open class Floor(name: String) : Floor(name) {
    var BlockRenderer.updateFloors: Seq<UpdateRenderState> by accessField("updateFloors")
    var updateFloor = false
    var setInit:Runnable = Runnable{}
    init {
        hasColor = true
        var variants = 0
        while (IFiles.hasPng("$name${variants + 1}")) {
            variants++
        }
        this.variants = variants
    }
    fun setInit(init:Runnable){
        setInit=init
    }
    override fun init() {
        super.init()
        setInit.run()
        Vars.content.block("${name}Wall")?.let {
            wall = it
        }
        Vars.content.block("${name}Stone")?.let {
            decoration=it
        }
    }

    override fun updateRender(tile: Tile) = updateFloor
    override fun icons() = if (variants == 0) arrayOf(Core.atlas.find(name)) else arrayOf(Core.atlas.find(name + "1"))

}