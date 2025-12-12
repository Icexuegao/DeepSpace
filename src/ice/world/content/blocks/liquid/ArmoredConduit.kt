package ice.world.content.blocks.liquid

import arc.func.Prov
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.distribution.DirectionLiquidBridge
import mindustry.world.blocks.liquid.Conduit
import mindustry.world.blocks.liquid.LiquidJunction

class ArmoredConduit(name: String?) : Conduit(name) {
    init {
        leaks = false
        buildType = Prov(::ArmoredConduitBuild)
    }

    override fun blends(tile: Tile?, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
        return (otherblock.outputsLiquid && blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock)) ||
                (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasLiquids) || otherblock is LiquidJunction
    }

    inner class ArmoredConduitBuild : ConduitBuild() {
        override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            //TODO接近检查是一种超级黑客解决方案，用于通过路口从块到管道......
            return super.acceptLiquid(source, liquid) && (tile == null || source.block is Conduit || source.block is DirectionLiquidBridge || source.block is LiquidJunction || source.tile.absoluteRelativeTo(tile.x.toInt(), tile.y.toInt()).toInt() == rotation || !source.proximity.contains(this))
        }
    }
}