package ice.library.baseContent.blocks.distribution

import arc.func.Prov
import ice.library.baseContent.blocks.abstractBlocks.IceBlock
import mindustry.content.Blocks
import mindustry.gen.Building
import mindustry.gen.Teamc
import mindustry.type.Item
import mindustry.world.Tile
import mindustry.world.meta.BlockGroup

class IceRouter(name: String) : IceBlock(name) {
    var speed: Float = 8f

    init {
        solid = false
        update = true
        hasItems = true
        unloadable = false
        itemCapacity = 1
        underBullets = true
        group = BlockGroup.transportation
        buildType = Prov(::IceRouterBuild)
    }

    inner class IceRouterBuild : IceBuild() {
        var lastItem: Item? = null
        var lastInput: Tile? = null
        var time: Float = 0f
        override fun updateTile() {
            if (lastItem == null && items.any()) {
                lastItem = items.first()
            }

            if (lastItem != null) {
                time += 1f / speed * delta()
                val target = getTileTarget(lastItem, lastInput, false)

                if (target != null && (time >= 1f || !(target.block is IceRouter || target.block.instantTransfer))) {
                    getTileTarget(lastItem, lastInput, true)
                    target.handleItem(this, lastItem)
                    items.remove(lastItem, 1)
                    lastItem = null
                }
            }
        }

        override fun acceptStack(item: Item, amount: Int, source: Teamc): Int {
            return 0
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return team === source.team && lastItem == null && items.total() == 0
        }

        override fun handleItem(source: Building, item: Item) {
            items.add(item, 1)
            lastItem = item
            time = 0f
            lastInput = source.tileOn()
        }

        override fun removeStack(item: Item, amount: Int): Int {
            val result = super.removeStack(item, amount)
            if (result != 0 && item === lastItem) {
                lastItem = null
            }
            return result
        }

        fun getTileTarget(item: Item?, from: Tile?, set: Boolean): Building? {
            val counter = rotation
            for (i in 0 until proximity.size) {
                val other = proximity[(i + counter) % proximity.size]
                if (set) rotation = (((rotation + 1) % proximity.size).toByte()).toInt()
                if (other.tile === from && from.block() === Blocks.overflowGate) continue
                if (other.acceptItem(this, item)) {
                    return other
                }
            }
            return null
        }
    }
}
