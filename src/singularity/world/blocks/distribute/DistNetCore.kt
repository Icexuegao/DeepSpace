package singularity.world.blocks.distribute

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.geom.Geometry
import arc.struct.*
import arc.util.Strings
import mindustry.ctype.ContentType
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.StatUnit
import singularity.world.blocks.distribute.matrixGrid.RequestHandlers
import singularity.world.blocks.distribute.netcomponents.CoreNeighbourComponent.CoreNeighbourComponentBuild
import singularity.world.blocks.distribute.netcomponents.NetPluginComp
import singularity.world.components.distnet.DistMatrixUnitComp
import singularity.world.components.distnet.DistNetworkCoreComp
import singularity.world.components.distnet.IOPointComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import singularity.world.distribution.MatrixGrid
import singularity.world.distribution.buffers.BaseBuffer
import singularity.world.distribution.buffers.ItemsBuffer
import singularity.world.distribution.buffers.LiquidsBuffer
import singularity.world.distribution.request.DistRequestBase
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import singularity.world.modules.DistCoreModule
import universecore.util.NumberStrify
import universecore.util.colletion.TreeSeq
import kotlin.math.max

open class DistNetCore(name: String) : NetPluginComp(name), DistMatrixUnitComp {
  var requestEnergyCost: Float = 0.1f

  init {
    this.topologyUse = 0
    this.isNetLinker = true
    this.computingPower = 8
    this.topologyCapacity = 8
    this.bufferSize = ObjectMap.of(DistBufferType.itemBuffer, 256, DistBufferType.liquidBuffer, 256)
    buildType= Prov(::DistNetCoreBuild)
  }

  override fun setStats() {
    super.setStats()
    this.stats.add(SglStat.computingPower, (this.computingPower * 60).toFloat(), StatUnit.perSecond)
    this.stats.add(SglStat.topologyCapacity, this.topologyCapacity.toFloat())
    this.stats.remove(SglStat.matrixEnergyUse)
    this.stats.add(SglStat.matrixEnergyUse, Strings.autoFixed(this.matrixEnergyUse * 60.0f, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perSecond") + " + " + Strings.autoFixed(this.requestEnergyCost * 60.0f, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perRequest") + Core.bundle.get("misc.perSecond"), arrayOfNulls<Any>(0))
    this.stats.add(SglStat.bufferSize, { t ->
      t.defaults().left().fillX().padLeft(10.0f)
      t.row()
      val var2: ObjectMap.Entries<*, *> = this.bufferSize.iterator()
      while (var2.hasNext()) {
        val entry: ObjectMap.Entry<DistBufferType<*>, Int> = var2.next() as ObjectMap.Entry<DistBufferType<*>, Int>
        if (entry.value > 0) {
          t.add(Core.bundle.get("content." + (entry.key as DistBufferType<*>).targetType().name + ".name") + ": " + NumberStrify.toByteFix(entry.value.toDouble(), 2))
          t.row()
        }
      }
    })
  }

  override var bufferCapacity: Int=0

  inner class DistNetCoreBuild : NetPluginCompBuild(), DistNetworkCoreComp {
    override var ioPoints = ObjectSet<IOPointComp>()
    override var requestHandlerMap = ObjectMap<DistRequestBase, RequestHandlers.RequestHandler<*>>()
    override var buffers  = OrderedMap<DistBufferType<*>, BaseBuffer<*, *, *>>()
    override var configs = TreeSeq<TargetConfigure> { a, b -> b.priority - a.priority }
    override var matrixGrid: MatrixGrid = MatrixGrid(this)
    override var requests = OrderedSet<DistRequestBase>()
    override var tempFactories = ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>>()
   override var distCore = DistCoreModule(this)
    var proximityComps = Seq<CoreNeighbourComponentBuild>()
    override var priority=0

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      this.netLinked.removeAll(this.proximityComps)
      this.proximityComps.clear()

      for (building in this.proximity) {
        if (building is CoreNeighbourComponentBuild) {
          val comp = building as CoreNeighbourComponentBuild?
          this.proximityComps.add(comp)
        }
      }

      this.netLinked.addAll(this.proximityComps)
    }

    override fun updateNetLinked() {
      super<DistNetworkCoreComp>.updateNetLinked()
      this.netLinked.addAll(this.proximityComps)
    }

    override fun priority(priority: Int) {
      this.matrixGrid().priority = priority
      this.distributor.network.priorityModified(this)
    }

    override fun networkValided() {
      this.matrixGrid().clear()
    }

    override fun status(): BlockStatus? {
      return if (this.distCore!!.requestTasks.isEmpty()) BlockStatus.noInput else super.status()
    }



    override fun create(block: Block, team: Team): Building {

      super.create(block, team)
      this.initBuffers()
      this.items = (this.getBuffer(DistBufferType.itemBuffer) as ItemsBuffer).generateBindModule()
      this.liquids = (this.getBuffer(DistBufferType.liquidBuffer) as LiquidsBuffer).generateBindModule()
      this.priority(-65536)
      return this
    }

    override fun drawSelect() {
      super.drawSelect()
      Lines.stroke(1.0f, Pal.accent)
      val outline = Cons { b: Building? ->
        for (i in 0..3) {
          val p = Geometry.d8edge[i]
          val offset = (-max(b!!.block.size - 1, 0)).toFloat() / 2.0f * 8.0f
          Draw.rect("block-select", b.x + offset * p.x.toFloat(), b.y + offset * p.y.toFloat(), (i * 90).toFloat())
        }
      }
      outline.get(this)
      this.proximityComps.each(outline)
    }

    override fun matrixEnergyConsume(): Float {
      return this@DistNetCore.matrixEnergyUse + this@DistNetCore.requestEnergyCost * this.distCore!!.lastProcessed.toFloat()
    }

    override fun ioPointConfigBackEntry(ioPoint: IOPointComp) {
    }

    override fun tileValid(tile: Tile): Boolean {
      return true
    }

    override fun drawValidRange() {
    }

    fun distCore(): DistCoreModule {
      return this.distCore!!
    }

    fun distCore(value: DistCoreModule) {
      this.distCore = value
    }

    fun tempFactories(): ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>> {
      return this.tempFactories
    }

    fun requests(): OrderedSet<DistRequestBase> {
      return this.requests
    }

    fun matrixGrid(): MatrixGrid {
      return this.matrixGrid
    }

    fun configs(): TreeSeq<TargetConfigure> {
      return this.configs
    }

    fun buffers(): OrderedMap<DistBufferType<*>, BaseBuffer<*, *, *>> {
      return this.buffers
    }

    fun requestHandlerMap(): ObjectMap<DistRequestBase, RequestHandlers.RequestHandler<*>> {
      return this.requestHandlerMap
    }

    fun ioPoints(): ObjectSet<IOPointComp> {
      return this.ioPoints
    }

    override fun update() {
      super.update()
      this.updateGrid()
    }

    override fun updateTile() {
      super.updateTile()
      this.updateDistNetwork()
    }
  }
}