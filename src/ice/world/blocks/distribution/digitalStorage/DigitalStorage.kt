package ice.world.blocks.distribution.digitalStorage

import arc.Events
import arc.func.Prov
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Log
import mindustry.Vars
import mindustry.content.Items
import mindustry.game.EventType
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.consumers.ConsumeItems


class DigitalStorage(name: String) : Block(name) {
    fun interface HandleBlock<out T> {
        fun cons(original: DigitalStorageBuild, build: @UnsafeVariance T)
    }

    val classPar = ObjectMap<Class<out Any>, HandleBlock<Building>>()
    private inline fun <reified T : Building> puts(value: HandleBlock<T>) {
        classPar.put(T::class.java, value)
    }

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
        requirements(Category.distribution, ItemStack.with(Items.copper, 1))
        puts<DigitalOutput.DigitalUnloaderBuild> { original, build ->
            build.proximity.forEach {
                if (build.sortItem != null && original.items.get(build.sortItem) > 0 && it.acceptItem(build,
                        build.sortItem)
                ) {
                    it.handleItem(build, build.sortItem)
                    original.items.remove(build.sortItem, 1)
                }
            }
        }
        puts<GenericCrafter.GenericCrafterBuild> { original, build ->
            //输入请求
            val block: GenericCrafter = build.block as GenericCrafter
            Log.info("1")
            block.outputItems?.forEach { outputItems ->
                Log.info("2")
                val get = build.items.get(outputItems.item)
                if (get>0&& original.acceptItem(build, outputItems.item)) {
                    Log.info("3")
                    build.items.remove(outputItems.item, 1)
                    original.handleItem(build, outputItems.item)
                }
            }
            //输出
            block.findConsumer<ConsumeItems> { cons -> cons is ConsumeItems }.items.forEach items@{ consitem ->
                if (!original.items.has(consitem.item)) return@items
                if (build.acceptItem(build, consitem.item)) {
                    original.items.remove(consitem.item, 1)
                    build.handleItem(build, consitem.item)
                }
            }
        }
        puts<DigitalInput.DigitalInputBuild> { s, b ->
            b.building = s
        }
    }

    override fun outputsItems(): Boolean {
        return false
    }

    inner class DigitalStorageBuild : Building() {
        private val buildings = Seq<Building>()
        private val conduitBuilds = Seq<DigitalConduit.DigitalConduitBuild>()

        override fun init(tile: Tile, team: Team, shouldAdd: Boolean, rotation: Int): Building {
            Events.on(EventType.TileChangeEvent::class.java) { updateProximity() }
            return super.init(tile, team, shouldAdd, rotation)
        }

        override fun acceptItem(source: Building, i: Item): Boolean {
            return items.get(i) < getMaximumAccepted(i)// && !proximity.contains(source)
        }

        override fun updateProximity() {
            buildings.clear()
            conduitBuilds.clear()
            proximity.forEach {
                fg(it.tile)
            }
            super.updateProximity()
        }

        override fun updateTile() {
            Log.info(buildings.size)
            buildings.forEach { building ->
                classPar.get(building::class.java)?.cons(this, building)
            }
        }


        private fun fg(tile: Tile) {
            val build1 = tile.build

            if (build1 is DigitalConduit.DigitalConduitBuild) {
                build1.building = this
                if (conduitBuilds.contains(build1)) return
                conduitBuilds.addUnique(build1)
                each(tile)
            } else if (build1 != null&&build1!=this) {
                buildings.addUnique(build1)
            }
        }

        fun each(tile: Tile) {
            val build = Vars.world.tile(tile.x + 1, tile.y.toInt())
            if (build != null) {
                fg(build)
            }
            val build1 = Vars.world.tile(tile.x - 1, tile.y.toInt())
            if (build != null) {
                fg(build1)
            }
            val build2 = Vars.world.tile(tile.x.toInt(), tile.y + 1)
            if (build != null) {
                fg(build2)
            }
            val build3 = Vars.world.tile(tile.x.toInt(), tile.y - 1)
            if (build != null) {
                fg(build3)
            }
        }

    }


}