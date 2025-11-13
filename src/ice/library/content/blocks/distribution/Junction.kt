package ice.library.content.blocks.distribution

import arc.func.Prov
import ice.library.content.blocks.abstractBlocks.IceBlock
import mindustry.gen.Building
import mindustry.gen.Teamc
import mindustry.type.Item
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

class Junction(name: String) : IceBlock(name) {
    var speed: Float = 26f
    var capacity: Int = 6
    var displayedSpeed: Float = 13f

    init {
        solid = false
        update = true
        unloadable = false
        underBullets = true
        noUpdateDisabled = true
        group = BlockGroup.transportation
        buildType = Prov(::JunctionBuild)
    }

    override fun setStats() {
        super.setStats()
        //(60f / speed * capacity) returns 13.84 which is not the actual value (non linear, depends on fps)
        stats.add(Stat.itemsMoved, displayedSpeed, StatUnit.itemsSecond)
        stats.add(Stat.itemCapacity, capacity.toFloat(), StatUnit.items)
    }

    override fun outputsItems(): Boolean {
        return true
    }

    inner class JunctionBuild : IceBuild() {
        var time = Array(4) { 0f }
        var next = Array(4) { true }
        override fun updateTile() {
            super.updateTile()

            for (i in 0 until 4) {
                time[i] += displayedSpeed / 60 * delta()
                if (time[i] > 1f) {
                    time[i] = 0f
                    next[i] = true
                }
            }
        }

        override fun acceptStack(item: Item?, amount: Int, source: Teamc?): Int {
            return 0
        }

        override fun acceptItem(source: Building, item: Item?): Boolean {
            val relative = source.relativeTo(tile).toInt()
            if (relative == -1) return false
            val to = nearby(relative)
            return to != null && to.team === team && next[relative] && to.acceptItem(this, item)
        }

        override fun handleItem(source: Building, item: Item?) {
            val relative = source.relativeTo(tile).toInt()
            if (relative == -1) return
            next[relative] = false
            val to = nearby(relative)
            to?.handleItem(this, item)
        }

    }
}