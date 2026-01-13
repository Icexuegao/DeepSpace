package singularity.world.blocks.distribute

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.meta.BlockStatus
import singularity.world.blocks.SglBlock
import singularity.world.components.distnet.DistElementBlockComp
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.modules.DistributeModule

//@Annotations.ImplEntries
open class DistNetBlock(name: String) : SglBlock(name), DistElementBlockComp {
  override  var isNetLinker: Boolean = false
    var topologyUse: Int = 1
    var matrixEnergyCapacity: Float = 0f
    var matrixEnergyUse: Float = 0f

    init {
        solid = true
        update = true
        unloadable = false
        saveConfig = false
        canOverdrive = false
    }

  //  @Annotations.ImplEntries
    open inner class DistNetBuild : SglBuilding(), DistElementBuildComp {
        var distributor: DistributeModule? = null
        var netLinked: Seq<DistElementBuildComp?> = Seq<DistElementBuildComp?>()
        var priority: Int = 0
        var matrixEnergyBuffered: Float = 0f

        public override fun create(block: Block?, team: Team?): Building? {
            super.create(block, team)
            distributor = DistributeModule(this)
            distributor!!.setNet()
            return this
        }

        public override fun status(): BlockStatus? {
            return if (distributor!!.network.netValid()) if (consumer!!.hasConsume()) super.status() else BlockStatus.active else if (distributor!!.network.netStructValid()) BlockStatus.noInput else BlockStatus.noOutput
        }

        public override fun drawStatus() {
            if (this.block.enableDrawStatus) {
                val multiplier = if (block.size > 1) 1.0f else 0.64f
                val brcx = this.tile.drawx() + (this.block.size * 8).toFloat() / 2.0f - 8 * multiplier / 2
                val brcy = this.tile.drawy() - (this.block.size * 8).toFloat() / 2.0f + 8 * multiplier / 2
                Draw.z(71.0f)
                Draw.color(Pal.gray)
                Fill.square(brcx, brcy, 2.5f * multiplier, 45.0f)
                Draw.color(status()!!.color)
                Fill.square(brcx, brcy, 1.5f * multiplier, 45.0f)
                Draw.color()
            }
        }

        override fun updateTile() {
            distributor!!.network.update()
        }

        override fun distributor(): DistributeModule {
            return distributor!!
        }

        override fun priority(priority: Int) {
            this.priority = priority
            distributor!!.network.priorityModified(this)
        }

        public override fun write(write: Writes) {
            super.write(write)
            write.f(matrixEnergyBuffered)
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            matrixEnergyBuffered = read.f()
        }
    }
}