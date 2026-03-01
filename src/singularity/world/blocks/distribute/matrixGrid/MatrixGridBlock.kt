package singularity.world.blocks.distribute.matrixGrid

import arc.Core
import arc.func.Boolp
import arc.func.Cons
import arc.func.Prov
import arc.math.geom.Point2
import arc.scene.ui.layout.Table
import arc.struct.*
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import mindustry.ctype.ContentType
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.Tile
import singularity.Sgl
import singularity.ui.tables.DistTargetConfigTable
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.blocks.distribute.GenericIOPoint.GenericIOPPointBuild
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.components.distnet.DistMatrixUnitComp
import singularity.world.components.distnet.IOPointComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import singularity.world.distribution.MatrixGrid
import singularity.world.distribution.buffers.BaseBuffer
import singularity.world.distribution.buffers.ItemsBuffer
import singularity.world.distribution.buffers.LiquidsBuffer
import singularity.world.distribution.request.DistRequestBase
import singularity.world.meta.SglStat
import universecore.UncCore
import universecore.components.blockcomp.SecondableConfigBuildComp
import universecore.util.DataPackable
import universecore.util.NumberStrify
import universecore.util.colletion.TreeSeq

open class MatrixGridBlock(name: String) : DistNetBlock(name), DistMatrixUnitComp {
  override var bufferCapacity: Int = 256

  init {
    this.displayFlow = false
    this.hasLiquids = true
    this.hasItems = true
    this.outputsLiquid = false
    this.outputItems = false
    this.configurable = true
    this.independenceInventory = false
    this.independenceLiquidTank = false
    this.displayLiquid = false
    buildType = Prov(::MatrixGridBuild)
  }

  override fun init() {
    super.init()
    this.itemCapacity = this.bufferCapacity / 8
    this.liquidCapacity = this.bufferCapacity.toFloat() / 4.0f
    if (this.size < 3) {
      throw RuntimeException("matrix grid core size must >= 3, curr: " + this.size)
    }
  }

  override fun setStats() {
    super.setStats()
    this.stats.add(SglStat.bufferSize) { t: Table? ->
      t!!.defaults().left().fillX().padBottom(5.0f).padLeft(10.0f)
      t.row()
      t.add(Core.bundle.get("content.item.name") + ": " + NumberStrify.toByteFix(256.0, 2))
      t.row()
      t.add(Core.bundle.get("content.liquid.name") + ": " + NumberStrify.toByteFix(256.0, 2))
    }
  }

  override fun parseConfigObjects(e: SglBuilding?, obj: Any?) {
    val entity = e as MatrixGridBuild
    if (obj is TargetConfigure) {
      val c = obj
      val t = e.nearby(Point2.x(c.offsetPos).toInt(), Point2.y(c.offsetPos).toInt())
      if (t == null) {
        return
      }

      if (c.isClear) {
        if (t is IOPointComp) {
          val io = t as IOPointComp
          io.config = (null)
          io.parentMat = (null)
          entity.ioPoints.remove(io)
        }

        val oldCfg = entity.configMap.remove(c.offsetPos)
        if (oldCfg != null) {
          entity.configs().remove(oldCfg)
        }

        entity.matrixGrid().remove(t)
      } else {
        if (t is IOPointComp) {
          val io = t as IOPointComp
          io.config = (c)
          entity.ioPointConfigBackEntry(io)
        }

        val old = entity.configMap.put(c.offsetPos, c)
        if (old != null) {
          entity.configs().remove(old)
        }

        entity.configs().add(c)
        entity.matrixGrid().remove(t)
        entity.matrixGrid().addConfig(c)
      }

      entity.shouldUpdateTask = true
    } else if (obj is PosCfgPair) {
      val pair = obj
      entity.matrixGrid().clear()
      entity.ioPoints.clear()
      entity.configs().clear()
      entity.configMap.clear()

      for (cfg in pair.configs.values()) {
        val b: Building?
        if ((e.nearby(Point2.x(cfg.offsetPos).toInt(), Point2.y(cfg.offsetPos).toInt()).also { b = it }) != null && b!!.pos() == Point2.pack(e.tileX() + Point2.x(cfg.offsetPos), e.tileY() + Point2.y(cfg.offsetPos))) {
          entity.configMap.put(cfg.offsetPos, cfg)
          entity.configs().add(cfg)
          entity.matrixGrid().addConfig(cfg)
        }
      }

      entity.shouldUpdateTask = true
      Pools.free(pair)
    }
  }

  override fun pointConfig(config: Any?, transformer: Cons<Point2>): Any? {
    if (config is ByteArray) {
      val b = config
      val var5 = DataPackable.readObject<DataPackable?>(b, *arrayOfNulls<Any>(0))
      if (var5 is PosCfgPair) {
        val cfg = var5
        cfg.handleConfig(transformer)
        return cfg.pack()
      }
    }

    return config
  }

  fun bufferCapacity(): Int {
    return this.bufferCapacity
  }

  open inner class MatrixGridBuild : DistNetBuild(), DistMatrixUnitBuildComp, SecondableConfigBuildComp {
    override var ioPoints = ObjectSet<IOPointComp>()
    override var requestHandlerMap: ObjectMap<DistRequestBase, RequestHandlers.RequestHandler<*>> = ObjectMap<DistRequestBase, RequestHandlers.RequestHandler<*>>()
    override var buffers: OrderedMap<DistBufferType<*>, BaseBuffer<*, *, *>> = OrderedMap<DistBufferType<*>, BaseBuffer<*, *, *>>()
    override var configs = TreeSeq<TargetConfigure>(Comparator { a, b -> b.priority - a.priority })
    override var matrixGrid: MatrixGrid = MatrixGrid(this)
    override var requests: OrderedSet<DistRequestBase> = OrderedSet<DistRequestBase>()
    override var tempFactories: ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>> = ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>>()
    var configMap: IntMap<TargetConfigure> = IntMap<TargetConfigure>()
    var shouldUpdateTask: Boolean = true
    private var added = false
    override var priority: Int = 0

    override fun create(block: Block, team: Team): Building {
      super.create(block, team)
      this.initBuffers()
      this.items = (this.getBuffer(DistBufferType.itemBuffer) as ItemsBuffer).generateBindModule()
      this.liquids = (this.getBuffer(DistBufferType.liquidBuffer) as LiquidsBuffer).generateBindModule()
      return this
    }

    override fun networkValided() {
      this.shouldUpdateTask = true
    }

    override fun gridValid(): Boolean {

      return this.added && this.distributor.network.netValid()
    }

    override fun ioPointConfigBackEntry(ioPoint: IOPointComp) {
      ioPoint.parentMat = (this)
      this.ioPoints.add(ioPoint)
      this.configMap.put(ioPoint.config!!.offsetPos, ioPoint.config)
      this.configs().add(ioPoint.config)
      this.matrixGrid().addConfig(ioPoint.config!!)
      this.shouldUpdateTask = true
    }

    override fun buildSecondaryConfig(table: Table, target: Building) {
      val config: Array<GridChildType?>? = if (target is IOPointComp) target.configTypes() else arrayOf<GridChildType?>(GridChildType.container)
      val off = Point2.pack(target.tileX() - tileX(), target.tileY() - tileY())
      table.add().width(45f)
      table.table(Tex.pane) { t ->
        t.add(
          DistTargetConfigTable(
          off, configMap.get(off), config, if (target is IOPointComp) target.configContentTypes()
          else getAcceptType(target.block), target is GenericIOPPointBuild, { c: TargetConfigure? -> configure(c!!.pack()) }, { UncCore.secConfig.hideConfig() }))
      }
      table.top().button(Icon.info, Styles.grayi, 32f, Runnable {
        // Sgl.ui.document.showDocument("", MarkdownStyles.defaultMD, Singularity.getDocument("matrix_grid_config_help.md"))
      }).size(45f).top()
    }

    private fun getAcceptType(block: Block?): Array<ContentType?>? {
      val res: Seq<ContentType> = Seq<ContentType>()
      val var3: ObjectMap.Entries<*, *> = Sgl.matrixContainers.getContainer(block).capacities.iterator()

      while (var3.hasNext()) {
        val entry: ObjectMap.Entry<DistBufferType<*>, Float> = var3.next() as ObjectMap.Entry<DistBufferType<*>, Float>
        if (entry.value > 0.0f) {
          res.add((entry.key as DistBufferType<*>).targetType())
        }
      }

      return res.toArray<Any?>(ContentType::class.java) as Array<ContentType?>?
    }

    override fun drawConfigure() {
      this.drawValidRange()
    }

    override fun tileValid(tile: Tile): Boolean {
      return false
    }

    override fun drawValidRange() {
    }

    override fun addIO(io: IOPointComp) {
      if (this.isAdded()) {
        this.ioPointConfigBackEntry(io)
      }
    }

    override fun removeIO(io: IOPointComp) {
      if (this.isAdded()) {
        this.ioPoints.remove(io)
        this.matrixGrid().remove(io.building)
        val cfg = this.configMap.remove(Point2.pack(io.tile!!.x - this.tileX(), io.tile!!.y - this.tileY()))
        if (cfg != null) {
          this.configs().remove(cfg)
        }

        this.shouldUpdateTask = true
      }
    }

    override fun onProximityAdded() {
      super.onProximityAdded()
      this.added = true

      for (config in this.configMap.values()) {
        val other = this.nearby(Point2.x(config.offsetPos).toInt(), Point2.y(config.offsetPos).toInt())
        if (other != null && Point2.pack(Point2.x(other.pos()) - this.tileX(), Point2.y(other.pos()) - this.tileY()) == config.offsetPos) {
          if (other is IOPointComp) {
            val io = other as IOPointComp
            io.config = (config)
            this.ioPointConfigBackEntry(io)
          } else {
            this.matrixGrid().addConfig(config)
            this.configs().add(config)
          }
        } else {
          this.configMap.remove(config.offsetPos)
        }
      }
    }

    override fun updateTile() {
      var var1: MutableIterator<*> = this.buffers().values().iterator()

      while (var1.hasNext()) {
        val buffer = var1.next() as BaseBuffer<*, *, *>
        buffer.update()
      }

      if (this.gridValid()) {
        for (value in GridChildType.entries) {
          for (entry in this.matrixGrid().get<Any?>(value) { bx: Any?, cx: TargetConfigure? -> true }) {
            val b = this.nearby(Point2.x(entry.config.offsetPos).toInt(), Point2.y(entry.config.offsetPos).toInt())
            if (b == null || b !== entry.entity) {
              if (b is IOPointComp) {
                val io = b as IOPointComp
                if (!b.isAdded()) {
                  this.removeIO(io)
                }
              } else {
                val c = this.configMap.remove(entry.config.offsetPos)
                if (c != null) {
                  this.configs().remove(c)
                }

                this.matrixGrid().remove(entry.entity as Building?)
                this.shouldUpdateTask = true
              }
            }
          }
        }

        if (this.shouldUpdateTask) {
          this.releaseRequest()
          this.shouldUpdateTask = false
        }

        for (request in requests()) {
          val handler = requestHandlerMap().get(request)
          request.update({ t: Boolp -> (handler as RequestHandlers.RequestHandler<DistRequestBase>).preCallBack(this, request as DistRequestBase, t) }, { t: Boolp -> (handler as RequestHandlers.RequestHandler<DistRequestBase>).callBack(this, request as DistRequestBase, t) }, { t: Boolp -> (handler as RequestHandlers.RequestHandler<DistRequestBase>).afterCallBack(this, request as DistRequestBase, t) })
        }
      }

      super.updateTile()
    }

    override fun onConfigureBuildTapped(other: Building): Boolean {

      if (this.tileValid(other.tile) && this.gridValid()) {

        if (this.configValid(other)) {
          UncCore.secConfig.showOn(other)
        }

        return false
      } else {
        return true
      }
    }

    override fun config(): ByteArray {
      val pair = PosCfgPair()
      pair.configs.clear()

      for (entry in this.configMap) {
        val build = this.nearby(Point2.x(entry.key).toInt(), Point2.y(entry.key).toInt())
        if (build != null) {
          if (build is IOPointComp) {
            val io = build as IOPointComp
            if (!this.ioPoints.contains(io)) {
              continue
            }
          }

          pair.configs.put(entry.key, entry.value)
        }
      }

      return pair.pack()
    }

    override fun acceptItem(source: Building, item: Item?): Boolean {
      val var10000: Boolean
      if (source is IOPointComp) {
        val io = source as IOPointComp
        if (this.ioPoints.contains(io)) {
          var10000 = true
          return var10000
        }
      }

      var10000 = false
      return var10000
    }

    override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      val var10000: Boolean
      if (source is IOPointComp) {
        val io = source as IOPointComp
        if (this.ioPoints.contains(io)) {
          var10000 = true
          return var10000
        }
      }

      var10000 = false
      return var10000
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      val pair = PosCfgPair()
      val len = read.i()
      val bytes = read.b(len)
      pair.read(bytes)
      this.configMap = pair.configs
      Pools.free(pair)
    }

    override fun write(write: Writes) {
      super.write(write)
      val pair = PosCfgPair()
      pair.configs.clear()

      for (entry in this.configMap) {
        val build = this.nearby(Point2.x(entry.key).toInt(), Point2.y(entry.key).toInt())
        if (build != null) {
          if (build is IOPointComp) {
            val io = build as IOPointComp
            if (!this.ioPoints.contains(io)) {
              continue
            }
          }

          pair.configs.put(entry.key, entry.value)
        }
      }

      val bytes = pair.pack()
      write.i(bytes.size)
      write.b(bytes)
      Pools.free(pair)
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


    override fun update() {
      super.update()
      this.updateGrid()
    }
  }

  open class PosCfgPair : DataPackable, Poolable {
    var configs: IntMap<TargetConfigure> = IntMap<TargetConfigure>()

    override fun typeID(): Long {
      return 1679658234266591164L
    }

    override fun write(write: Writes) {
      write.i(this.configs.size)

      for (cfg in this.configs.values()) {
        val bytes = cfg.pack()
        write.i(bytes.size)
        write.b(bytes)
      }
    }

    override fun read(read: Reads) {
      val length = read.i()
      this.configs.clear()

      for (i in 0..<length) {
        val cfg = TargetConfigure()
        val len = read.i()
        cfg.read(read.b(len))
        this.configs.put(cfg.offsetPos, cfg)
      }
    }

    override fun reset() {
      this.configs.clear()
    }

    open fun handleConfig(handler: Cons<Point2>) {
      val c: IntMap<TargetConfigure> = IntMap<TargetConfigure>()

      for (entry in this.configs) {
        (entry.value as TargetConfigure).configHandle(handler)
        c.put((entry.value as TargetConfigure).offsetPos, entry.value as TargetConfigure)
      }

      this.configs = c
    }
  }

  companion object {
    const val typeID: Long = 1679658234266591164L
  }
}