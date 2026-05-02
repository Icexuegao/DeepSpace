package ice.world.content.blocks.distribution.digitalStorage

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Eachable
import universecore.struct.texture.LazyTextureArrayDelegate
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.blocks.production.GenericCrafter

class HubConduit(name: String) : LogisticsBlock(name) {
    val texture: Array<TextureRegion> by LazyTextureArrayDelegate(this.name, 11)
    val textureLookup = listOf(
        10,
        0,
        1, 4,
        0, 0, 2, 8,
        1, 5, 1, 7, 3, 6, 9, 10
    )

    override val blockType = Type.CONDUIT

    init {
        size = 1
        health = 50
        update = false
        hasPower = true
        hasItems = false
        unloadable = false
        destructible = true
        acceptsItems = false
        conductivePower = true
        buildType = Prov(::DigitalConduitBuild)
    }

    override fun getPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan?>) = texture[10]

    override fun canPlaceOn(tile: Tile?, team: Team, rotation: Int): Boolean {
        tile ?: return false

        val x = tile.x
        val y = tile.y
        val edges = Edges.getEdges(size)
        var hub: LogisticsHub.DigitalStorageBuild? = null
        for (point in edges) {
            val other = Vars.world.tile(x + point.x, y + point.y).build
            if (other is LogisticsBuild) {
                val otherHub = LogisticsGraph.getHub(other)
                if (hub == null) hub = otherHub
                else if (otherHub != null && otherHub != hub)
                    return false
            }
        }

        return true
    }

    fun mapIndex(b0: Boolean, b1: Boolean, b2: Boolean, b3: Boolean) =
        textureLookup[b0.int or (b1.int shl 1) or (b2.int shl 2) or (b3.int shl 3)]

    private val Boolean.int: Int
        get() = if (this) 1 else 0

    inner class DigitalConduitBuild : LogisticsBuild() {
        val links = arrayOfNulls<Building>(size * 4)
        val linked = HashSet<LogisticsBuild>(4)
        val crafterLinked = HashSet<GenericCrafter.GenericCrafterBuild>()
        val nearby = Edges.getEdges(size)!!
        var drawIndex = 0

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            updateIndex()

            linked.clear()
            crafterLinked.clear()
            nearby.forEachIndexed { i, point ->
                val old = links[i]
                val new = Vars.world.build(tile.x + point.x, tile.y + point.y)
                if (old == new || new in linked || new in crafterLinked) return@forEachIndexed
                when(old) {
                    is LogisticsBuild -> LogisticsGraph.cut(this, old)
                    is GenericCrafter.GenericCrafterBuild -> LogisticsGraph.cut(this, old)
                }
                when (new) {
                    is LogisticsBuild -> {
                        links[i] = new
                        linked += new
                        LogisticsGraph.link(this, new)
                    }

                    is GenericCrafter.GenericCrafterBuild -> {
                        links[i] = new
                        crafterLinked += new
                        LogisticsGraph.link(this, new)
                    }

                    else -> links[i] = null
                }
            }

            proximity.each {
                if (it is GenericCrafter.GenericCrafterBuild) crafterLinked += it
            }
        }

        override fun onProximityRemoved() {
            super.onProximityRemoved()

            links.forEach {
                when(it) {
                    is LogisticsBuild -> LogisticsGraph.cut(this, it)
                    is GenericCrafter.GenericCrafterBuild -> LogisticsGraph.cut(this, it)
                }
            }
        }

        override fun draw() {
            Draw.rect(texture[drawIndex], x, y)
        }

        private fun updateIndex() {
            drawIndex = mapIndex(
                validNearby(0),
                validNearby(1),
                validNearby(2),
                validNearby(3),
            )
        }

        private fun validNearby(rotation: Int): Boolean {
            val nearby = nearby(rotation)
            return nearby is LogisticsBuild || nearby is GenericCrafter.GenericCrafterBuild
        }
    }
}