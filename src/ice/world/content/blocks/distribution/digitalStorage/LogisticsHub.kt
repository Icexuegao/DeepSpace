package ice.world.content.blocks.distribution.digitalStorage

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.blocks.production.GenericCrafter

class LogisticsHub(name: String) : LogisticsBlock(name) {
//    val processor = HashMap<Class<*>, (Building, DigitalStorageBuild) -> Unit>()

    override val blockType = Type.HUB

    init {
        size = 3
        solid = true
        health = 400
        update = true
        hasItems = true
        hasPower = true
        unloadable = false
        acceptsItems = false
        destructible = true
        itemCapacity = 500
        conductivePower = true
        conveyorPlacement = true
        buildType = Prov(::DigitalStorageBuild)
    }

    override fun canPlaceOn(tile: Tile?, team: Team, rotation: Int): Boolean {
        tile ?: return false

        val x = tile.x
        val y = tile.y
        val edges = Edges.getEdges(size)
        for (point in edges) {
            val other = Vars.world.tile(x + point.x, y + point.y).build
            if (other is LogisticsBuild && LogisticsGraph.getHub(other) != null)
                return false
        }

        return true
    }

    override fun outputsItems(): Boolean {
        return false
    }

    inner class DigitalStorageBuild : LogisticsBuild() {
        override fun acceptItem(source: Building, i: Item): Boolean {
            return items.get(i) < getMaximumAccepted(i)
        }

        override fun drawSelect() {
            super.drawSelect()

            Draw.z(Layer.blockOver)
            Draw.color(Pal.accent, 0.3f)
            LogisticsGraph.eachCrafter(this) {
                Fill.circle(it.x, it.y, 4f)
            }
            Draw.reset()
        }

        override fun updateTile() {
            LogisticsGraph.eachCrafter(this) { crafter ->
                val block = crafter.block as GenericCrafter
                block.outputItems.forEach { items ->
                    if (crafter.items.get(items.item) > 0 && acceptItem(crafter, items.item)) {
                        handleItem(crafter, items.item)
                        crafter.items.remove(items.item, 1)
                    }
                }
                items.each { item, _ ->
                    if (block.consumesItem(item) && crafter.acceptItem(this, item)) {
                        crafter.handleItem(this, item)
                        items.remove(item, 1)
                    }
                }
            }
        }
    }
}