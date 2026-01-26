package singularity.world.blocks.distribute

import arc.func.Cons
import arc.math.geom.Point2
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.OrderedSet
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.block.MatrixDistNet
import mindustry.ctype.ContentType
import mindustry.gen.Building
import mindustry.world.Edges
import singularity.world.blocks.SglBlock
import singularity.world.blocks.distribute.matrixGrid.RequestHandlers
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.components.distnet.IOPointBlockComp
import singularity.world.components.distnet.IOPointComp
import singularity.world.distribution.GridChildType
import singularity.world.meta.SglStat
import universecore.util.DataPackable

abstract class IOPoint(name: String) : SglBlock(name), IOPointBlockComp {
  override var supportContentType = OrderedSet<ContentType>()
  override var configTypes = OrderedSet<GridChildType>()
  override var requestFactories = ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>>()

  init {
    this.update = true
    this.buildCostMultiplier = 0.0f
    this.schematicPriority = -10
  }

  override fun init() {
    super.init()
    this.setupRequestFact()
  }

  override fun setStats() {
    super.setStats()
    this.stats.add(SglStat.componentBelongs) { t: Table? ->
      t!!.defaults().left()
      t.image(MatrixDistNet.网格控制器.fullIcon).size(35.0f).padRight(8.0f)
      t.add(MatrixDistNet.网格控制器.localizedName)
    }
  }

  override fun parseConfigObjects(e: SglBuilding?, obj: Any?) {
    if (obj is TargetConfigure) {
      if (e is IOPointBuild) {
        val tile = e.nearby(-Point2.x(obj.offsetPos), -Point2.y(obj.offsetPos))
        if (tile is DistMatrixUnitBuildComp) {
          val mat = tile as DistMatrixUnitBuildComp
          val offX = e.tileX() - tile.tileX()
          val offY = e.tileY() - tile.tileY()
          obj.offsetPos = Point2.pack(offX, offY)
          e.parent(mat)
          e.gridConfig(obj)
          e.parent()!!.addIO(e)
        } else {
          e.parent(null)
          e.gridConfig(null)
        }
      }
    }
  }

  override fun pointConfig(config: Any?, transformer: Cons<Point2>): Any? {
    if (config is ByteArray) {
      val var5 = DataPackable.readObject<DataPackable?>(config, *arrayOfNulls<Any>(0))
      if (var5 is TargetConfigure) {
        var5.configHandle(transformer)
        return var5.pack()
      }
    }

    return config
  }

  abstract fun setupRequestFact()

  fun requestFactories(): ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>> {
    return this.requestFactories
  }

  fun configTypes(): OrderedSet<GridChildType> {
    return this.configTypes
  }

  fun supportContentType(): OrderedSet<ContentType> {
    return this.supportContentType
  }

  abstract inner class IOPointBuild : SglBuilding(), IOPointComp {
    override var parentMat: DistMatrixUnitBuildComp? = null
    override var config: TargetConfigure? = null

    override fun onProximityAdded() {
      super.onProximityAdded()
      if (this.config != null) {
        val tile = this.nearby(-Point2.x(this.config!!.offsetPos), -Point2.y(this.config!!.offsetPos))
        if (tile is DistMatrixUnitBuildComp) {
          val mat = tile as DistMatrixUnitBuildComp
          this.parentMat = mat
          this.parentMat!!.addIO(this)
        } else {
          this.parentMat = null
          this.config = null
        }
      }
    }

    override fun updateTile() {
      if (this.parentMat != null && this.parentMat!!.building.isAdded) {
        if (this.parentMat!!.gridValid()) {
          this.resourcesDump()
          this.resourcesSiphon()
          this.transBack()
        }
      }
    }

    override fun onRemoved() {
      if (this.parentMat != null) {
        this.parentMat!!.removeIO(this)
      }

      super.onRemoved()
    }

    override fun config(): ByteArray? {
      return if (this.config == null) NULL else this.config!!.pack()
    }

    override fun write(write: Writes) {
      super.write(write)
      if (this.config == null) {
        write.i(-1)
      } else {
        write.i(1)
        this.config!!.write(write)
      }
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      if (revision >= 3 && read.i() > 0) {
        this.config = TargetConfigure()
        this.config!!.read(read)
      }
    }

    fun getDirectBit(e: Building): Byte {
      val dir = this.relativeTo(Edges.getFacingEdge(e, this))
      return (if (dir.toInt() == 0) 1 else (if (dir.toInt() == 1) 2 else (if (dir.toInt() == 2) 4 else (if (dir.toInt() == 3) 8 else 0)))).toByte()
    }

    protected abstract fun transBack()

    protected abstract fun resourcesSiphon()

    protected abstract fun resourcesDump()

    fun parent(): DistMatrixUnitBuildComp? {
      return this.parentMat
    }

    fun parent(valur: DistMatrixUnitBuildComp?) {
      this.parentMat = valur
    }

    fun gridConfig(): TargetConfigure? {
      return this.config
    }

    fun gridConfig(value: TargetConfigure?) {
      this.config = value
    }
  }

  companion object {
    val NULL: ByteArray = ByteArray(0)
  }
}