package ice.world.content.blocks.distribution.conveyor

import arc.func.Prov
import arc.math.geom.Geometry
import arc.math.geom.Point2
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.world.Block
import mindustry.world.Edges
import mindustry.world.Tile



class ArmoredConveyor(name: String) : Conveyor(name) {
    init {
        noSideBlend = true
        buildType= Prov(::ArmoredConveyorBuild)
    }

    override fun blends(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
        return (otherblock.outputsItems() && blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock)) ||
                (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems)
    }

    override fun blendsArmored(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
        return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery)
                || ((!otherblock.rotatedOutput(otherx, othery, tile) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null && Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile).toInt() == rotation) ||
                (otherblock is Conveyor && otherblock.rotatedOutput(otherx, othery, tile) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x.toInt(), tile.y.toInt())))
    }

    inner class ArmoredConveyorBuild : ConveyorBuild() {
        override fun acceptItem(source: Building, item: Item?): Boolean {
            return super.acceptItem(source, item) && (source.block is Conveyor || Edges.getFacingEdge(source.tile, tile).relativeTo(tile).toInt() == rotation)
        }
    }
}