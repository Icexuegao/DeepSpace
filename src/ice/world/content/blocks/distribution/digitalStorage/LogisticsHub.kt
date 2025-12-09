package ice.world.content.blocks.distribution.digitalStorage

import arc.Events
import arc.func.Cons2
import arc.func.Prov
import arc.struct.Queue
import arc.struct.Seq
import ice.library.EventType
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.game.EventType.BlockBuildBeginEvent
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.world.Tile
import mindustry.world.blocks.production.GenericCrafter

class LogisticsHub(name: String) : IceBlock(name) {
    val processor = HashMap<Class<*>, (Building, DigitalStorageBuild) -> Unit>()
    override fun blockChanged(tile: Tile) {
        Events.fire(EventType.LogisticsHubFire())
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
        put<LogisticsOutput.DigitalUnloaderBuild> { build, ori ->
            build.original = ori
        }
        put<LogisticsInput.DigitalInputBuild> { build, ori ->
            build.original = ori
        }
        put<GenericCrafter.GenericCrafterBuild> { build, ori ->
            val crafter = build.block as GenericCrafter
            crafter.outputItems.forEach { items ->
                if (build.items.get(items.item) > 0 && ori.acceptItem(build, items.item)) {
                    ori.handleItem(build, items.item)
                    build.items.remove(items.item, 1)
                }
            }
            ori.items.each { item, amount ->
                if (crafter.consumesItem(item) && build.acceptItem(ori, item)) {
                    build.handleItem(ori, item)
                    ori.items.remove(item, 1)
                }
            }
        }
    }

    inline fun <reified T : Building> put(cons: Cons2<T, DigitalStorageBuild>) {
        processor[T::class.java] = { building, storage ->
            cons.get(building as T, storage)
        }
    }

    override fun outputsItems(): Boolean {
        return false
    }

    inner class DigitalStorageBuild : IceBuild() {
        override fun afterReadAll() {
            super.afterReadAll()
            breadthFirstSearch(this)
        }
        init {
            Events.on(EventType.LogisticsHubFire::class.java) { event ->
                breadthFirstSearch(this)
            }
            Events.on(BlockBuildBeginEvent::class.java) {
                if (it.breaking){
                    breadthFirstSearch(this)
                }
            }
        }

        val conduits = Seq<HubConduit.DigitalConduitBuild>()
        val builds = Seq<Building>()
        override fun acceptItem(source: Building, i: Item): Boolean {
            return items.get(i) < getMaximumAccepted(i)
        }

        override fun updateTile() {
            conduits.remove { it.dead }
            builds.remove { it.dead }
            builds.forEach {
                processor.get(it::class.java)?.invoke(it, this)
            }
        }


        fun breadthFirstSearch(startBuilding: Building) {
            conduits.clear()
            builds.forEach {
                if (it is LogisticsOutput.DigitalUnloaderBuild) {
                    it.original = null
                }
                if (it is LogisticsInput.DigitalInputBuild) {
                    it.original = null
                }
            }
            builds.clear()
            // 使用队列实现BFS
            val queue = Queue<Building>()
            val visited = HashSet<Building>()
            // 从起始方块开始
            queue.addFirst(startBuilding)
            visited.add(startBuilding)

            while (queue.size > 0) {
                val current = queue.removeFirst()
                // 处理当前方块（这里只是示例，你可以根据需要实现具体逻辑）
                onBlockVisited(current)
                // 遍历当前方块的邻近方块
                for (neighbor in current.proximity) {
                    if (neighbor != null && !visited.contains(neighbor)) {
                        // 可以根据需要添加筛选条件，例如检查方块类型、团队等
                        if (isValidNeighbor(neighbor)) {
                            visited.add(neighbor)
                            queue.addFirst(neighbor)
                        }
                    }
                }
            }
        }

        // 示例：检查邻居是否有效（根据你的需求自定义）
        private fun isValidNeighbor(build: Building): Boolean {
            // 例如：只处理相同团队的方块
            return build.team == team && build is HubConduit.DigitalConduitBuild
            // 或者：只处理特定类型的方块
            // return block.block == YourModBlocks.yourBlock;
        }

        // 当访问每个方块时执行的操作
        private fun onBlockVisited(build: Building) {
            if (build is HubConduit.DigitalConduitBuild) {
                conduits.addUnique(build)
                build.building = this
                build.proximity.select { it !is HubConduit.DigitalConduitBuild }.forEach(builds::addUnique)
            }
            // 示例：给方块添加效果或更新状态
            // block.someProperty = someValue;
            // 或者：记录方块信息
            // visitedBlocks.add(block);
        }
    }

}