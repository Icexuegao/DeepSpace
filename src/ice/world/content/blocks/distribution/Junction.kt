package ice.world.content.blocks.distribution

import arc.func.Prov
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.gen.Building
import mindustry.gen.Teamc
import mindustry.type.Item
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

class Junction(name: String) : IceBlock(name) {
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
        hasItems=false
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.itemsMoved, displayedSpeed, StatUnit.itemsSecond)
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
      private val checkingPath = ThreadLocal<MutableSet<Building>>()

      override fun acceptItem(source: Building, item: Item?): Boolean {
        val relative = source.relativeTo(tile).toInt()
        if (relative == -1) return false
        val to = nearby(relative)
        if (to == null || to.team !== team || !next[relative]) return false

        val path = checkingPath.get() ?: run {
          val newSet = mutableSetOf<Building>()
          checkingPath.set(newSet)
          newSet
        }

        if (path.contains(this)) return false

        path.add(this)
        val result = to.acceptItem(this, item)
        path.remove(this)

        return result
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