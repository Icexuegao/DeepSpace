package ice.world.content.blocks.distribution

import arc.func.Prov
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.gen.Building
import mindustry.gen.Teamc
import mindustry.type.Item
import mindustry.world.meta.BlockGroup

class Router(name: String) : IceBlock(name) {
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
        var rotate = 0
        override fun updateTile() {
            val item = items.first()
            item?.let {
                nearby(rotate % 4)?.let {
                    if (it.acceptItem(this, item)) {
                        it.handleItem(this, item)
                        items.remove(item, 1)
                    }
                }
                rotate++
            }
        }

        override fun acceptStack(item: Item, amount: Int, source: Teamc): Int {
            return 0
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return team == source.team && items.total() == 0
        }

        override fun handleItem(source: Building, item: Item) {
            items.add(item, 1)
        }
    }
}
