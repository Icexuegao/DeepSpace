package singularity.world.blocks.distribute

import arc.func.Prov
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

open class DistNetBlock(name: String) : SglBlock(name), DistElementBlockComp {
  override var isNetLinker: Boolean = false
  override var topologyUse: Int = 1
  override var matrixEnergyCapacity: Float = 0.0f
  override var matrixEnergyUse: Float = 0.0f

  init {
    this.solid = true
    this.update = true
    this.unloadable = false
    this.saveConfig = false
    this.canOverdrive = false
    buildType = Prov(::DistNetBuild)
  }

  public override fun setStats() {
    super.setStats()
    this.setDistNetStats(this.stats)
  }

  open inner class DistNetBuild : SglBuilding(), DistElementBuildComp {
    override var distributor = DistributeModule(this)
    override var netLinked = Seq<DistElementBuildComp>()
    override var priority: Int = 0
    override var matrixEnergyBuffered: Float = 0.0f

    public override fun create(block: Block, team: Team): Building {
      super.create(block, team)
      this.distributor!!.setNet()
      return this
    }

    public override fun status(): BlockStatus? {
      return if (this.distributor!!.network.netValid()) (if (this.consumer.hasConsume()) super.status() else BlockStatus.active) else (if (this.distributor!!.network.netStructValid()) BlockStatus.noInput else BlockStatus.noOutput)
    }

    public override fun drawStatus() {
      if (this.block.enableDrawStatus) {
        val multiplier = if (this.block.size > 1) 1.0f else 0.64f
        val brcx = this.tile.drawx() + (this.block.size * 8).toFloat() / 2.0f - 8.0f * multiplier / 2.0f
        val brcy = this.tile.drawy() - (this.block.size * 8).toFloat() / 2.0f + 8.0f * multiplier / 2.0f
        Draw.z(71.0f)
        Draw.color(Pal.gray)
        Fill.square(brcx, brcy, 2.5f * multiplier, 45.0f)
        Draw.color(this.status()!!.color)
        Fill.square(brcx, brcy, 1.5f * multiplier, 45.0f)
        Draw.color()
      }
    }

    override fun updateTile() {
      this.distributor!!.network.update()
    }

    fun distributor(): DistributeModule {
      return this.distributor!!
    }

    open fun priority(priority: Int) {
      this.priority = priority
      this.distributor!!.network.priorityModified(this)
    }

    public override fun write(write: Writes) {
      super.write(write)
      write.f(this.matrixEnergyBuffered)
    }

    public override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.matrixEnergyBuffered = read.f()
    }

    fun priority(): Int {
      return this.priority
    }

    fun netLinked(): Seq<DistElementBuildComp> {
      return this.netLinked
    }

    fun matrixEnergyBuffered(): Float {
      return this.matrixEnergyBuffered
    }

    fun matrixEnergyBuffered(set: Float) {
      this.matrixEnergyBuffered = set
    }

    override fun onProximityAdded() {
      super.onProximityAdded()
      this.distNetAdd()
    }

    override fun onProximityRemoved() {
      super.onProximityRemoved()
      this.distNetRemove()
    }
  }
}