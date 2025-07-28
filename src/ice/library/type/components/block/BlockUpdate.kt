package ice.library.type.components.block

import mindustry.world.Tile

interface BlockUpdate {
    fun update(tile:Tile)
}