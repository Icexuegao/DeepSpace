package singularity.world.blocks.distribute.matrixGrid

import arc.Core
import arc.func.Boolf2
import arc.func.Boolp
import arc.func.Cons
import arc.math.geom.Point2
import arc.scene.ui.layout.Table
import arc.struct.IntMap
import arc.struct.Seq
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
import mindustry.world.meta.StatValue
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
import singularity.world.distribution.request.DistRequestBase.RequestTask
import singularity.world.distribution.request.PutItemsRequest
import singularity.world.distribution.request.PutLiquidsRequest
import singularity.world.distribution.request.ReadItemsRequest
import singularity.world.distribution.request.ReadLiquidsRequest
import singularity.world.meta.SglStat
import universecore.UncCore
import universecore.components.blockcomp.SecondableConfigBuildComp
import universecore.util.DataPackable
import universecore.util.NumberStrify

open class MatrixGridBlock(name: String) : DistNetBlock(name), DistMatrixUnitComp {
    var bufferCapacity: Int = 256

    init {
        displayFlow = false
        hasLiquids = true
        hasItems = hasLiquids
        outputsLiquid = false
        outputItems = outputsLiquid
        configurable = true
        independenceInventory = false
        independenceLiquidTank = false

        displayLiquid = false
    }

    public override fun init() {
        super.init()
        itemCapacity = bufferCapacity / 8
        liquidCapacity = bufferCapacity / 4f

        if (size < 3) throw RuntimeException("matrix grid core size must >= 3, curr: " + size)
    }

    public override fun setStats() {
        super.setStats()
        stats.add(SglStat.bufferSize, StatValue { t: Table? ->
            t!!.defaults().left().fillX().padBottom(5f).padLeft(10f)
            t.row()
            t.add(Core.bundle.get("content.item.name") + ": " + NumberStrify.toByteFix(256.0, 2))
            t.row()
            t.add(Core.bundle.get("content.liquid.name") + ": " + NumberStrify.toByteFix(256.0, 2))
        })
    }

    public override fun parseConfigObjects(e: SglBuilding?, obj: Any?) {
        val entity = e as MatrixGridBuild
        if (obj is TargetConfigure) {
            val t = e.nearby(Point2.x(obj.offsetPos).toInt(), Point2.y(obj.offsetPos).toInt())
            if (t == null) return

            if (obj.isClear()) {
                if (t is IOPointComp) {
                    t.gridConfig(null)
                    t.parent(null)
                    entity.ioPoints()!!.remove(t)
                }
                val oldCfg = entity.configMap.remove(obj.offsetPos)
                if (oldCfg != null) {
                    entity.configs()!!.remove(oldCfg)
                }
                entity.matrixGrid()!!.remove(t)
            } else {
                if (t is IOPointComp) {
                    t.gridConfig(obj)
                    entity.ioPointConfigBackEntry(t)
                }
                val old = entity.configMap.put(obj.offsetPos, obj)
                if (old != null) {
                    entity.configs()!!.remove(old)
                }
                entity.configs()!!.add(obj)
                entity.matrixGrid()!!.remove(t)
                entity.matrixGrid()!!.addConfig(obj)
            }

            entity.shouldUpdateTask = true
        } else if (obj is PosCfgPair) {
            entity.matrixGrid()!!.clear()
            entity.ioPoints()!!.clear()
            entity.configs()!!.clear()
            entity.configMap.clear()

            for (cfg in obj.configs.values()) {
                val b: Building?

                if ((e.nearby(Point2.x(cfg.offsetPos).toInt(), Point2.y(cfg.offsetPos).toInt()).also { b = it }) != null) {
                    if (b!!.pos() != Point2.pack(e.tileX() + Point2.x(cfg.offsetPos), e.tileY() + Point2.y(cfg.offsetPos))) continue
                    entity.configMap.put(cfg.offsetPos, cfg)
                    entity.configs()!!.add(cfg)
                    entity.matrixGrid()!!.addConfig(cfg)
                }
            }
            entity.shouldUpdateTask = true

            Pools.free(obj)
        }
    }

    override fun pointConfig(config: Any?, transformer: Cons<Point2?>): Any? {
        if (config is ByteArray && DataPackable.readObject<DataPackable?>(config) is PosCfgPair) {
            val cfg = DataPackable.readObject<DataPackable?>(config) as PosCfgPair
            cfg.handleConfig(transformer)
            return cfg.pack()
        }
        return config
    }

    companion object {
        const val typeID: Long = 1679658234266591164L
    }

    //  @Annotations.ImplEntries
    open inner class MatrixGridBuild : DistNetBuild(), DistMatrixUnitBuildComp, SecondableConfigBuildComp {
        var configMap: IntMap<TargetConfigure> = IntMap<TargetConfigure>()
        var shouldUpdateTask: Boolean = true
        private var added = false

        override fun create(block: Block?, team: Team?): Building? {
            super.create(block, team)
            initBuffers()

            items = getBuffer(DistBufferType.itemBuffer)!!.generateBindModule()
            liquids = getBuffer(DistBufferType.liquidBuffer)!!.generateBindModule()
            return this
        }

        override fun networkValided() {
            shouldUpdateTask = true
        }

        override fun gridValid(): Boolean {
            return added && distributor!!.network.netValid()
        }

        override fun ioPointConfigBackEntry(ioPoint: IOPointComp) {
            ioPoint.parent(this)
            ioPoints()!!.add(ioPoint)
            configMap.put(ioPoint.gridConfig()!!.offsetPos, ioPoint.gridConfig())
            configs()!!.add(ioPoint.gridConfig())
            matrixGrid()!!.addConfig(ioPoint.gridConfig())
            shouldUpdateTask = true
        }

        override fun buildSecondaryConfig(table: Table, target: Building) {
            val config = if (target is IOPointComp) target.configTypes() else arrayOf<GridChildType>(GridChildType.container)
            val off = Point2.pack(target.tileX() - tileX(), target.tileY() - tileY())
            table.add().width(45f)
            table.table(Tex.pane, Cons { t: Table? ->
                t!!.add<DistTargetConfigTable?>(
                    DistTargetConfigTable(
                        off,
                        configMap.get(off),
                        config,
                        if (target is IOPointComp)
                            target.configContentTypes()
                        else
                            getAcceptType(target.block),
                        target is GenericIOPPointBuild,
                        { c: TargetConfigure? -> configure(c!!.pack()) },
                        { UncCore.secConfig.hideConfig() }
                    ))
            })
            table.top().button(Icon.info, Styles.grayi, 32f, Runnable {
                //  Sgl.ui.document.showDocument("", MarkdownStyles.defaultMD, Singularity.getDocument("matrix_grid_config_help.md"))
            }).size(45f).top()
        }

        private fun getAcceptType(block: Block?): Array<ContentType?>? {
            val res = Seq<ContentType?>()
            for (entry in Sgl.matrixContainers.getContainer(block).capacities) {
                if (entry.value > 0) res.add(entry.key.targetType())
            }
            return res.toArray<ContentType?>(ContentType::class.java)
        }

        override fun drawConfigure() {
            drawValidRange()
        }

        override fun tileValid(tile: Tile?): Boolean {
            return false
        }

        override fun drawValidRange() {}

        override fun addIO(io: IOPointComp) {
            if (isAdded()) {
                ioPointConfigBackEntry(io)
            }
        }

        override fun removeIO(io: IOPointComp) {
            if (isAdded()) {
                ioPoints()!!.remove(io)
                matrixGrid()!!.remove(io.building)
                val cfg = configMap.remove(Point2.pack(io.tile!!.x - tileX(), io.tile!!.y - tileY()))
                if (cfg != null) configs()!!.remove(cfg)
                shouldUpdateTask = true
            }
        }

        override fun onProximityAdded() {
            super.onProximityAdded()
            added = true

            for (config in configMap.values()) {
                val other = nearby(Point2.x(config.offsetPos).toInt(), Point2.y(config.offsetPos).toInt())
                if (other == null || Point2.pack(Point2.x(other.pos()) - tileX(), Point2.y(other.pos()) - tileY()) != config.offsetPos) {
                    configMap.remove(config.offsetPos)
                    continue
                }

                if (other is IOPointComp) {
                    other.gridConfig(config)
                    ioPointConfigBackEntry(other)
                } else {
                    matrixGrid()!!.addConfig(config)
                    configs()!!.add(config)
                }
            }
        }

        public override fun updateTile() {
            for (buffer in buffers()!!.values()) {
                buffer.update()
            }

            if (gridValid()) {
                for (value in GridChildType.entries) {
                    for (entry in matrixGrid()!!.get<Building?>(value, Boolf2 { b: Building?, c: TargetConfigure? -> true })) {
                        val b = nearby(Point2.x(entry.config.offsetPos).toInt(), Point2.y(entry.config.offsetPos).toInt())
                        if (b == null || b !== entry.entity) {
                            if (b is IOPointComp) {
                                if (!b.isAdded()) removeIO(b)
                            } else {
                                val c = configMap.remove(entry.config.offsetPos)
                                if (c != null) {
                                    configs()!!.remove(c)
                                }
                                matrixGrid()!!.remove(entry.entity)
                                shouldUpdateTask = true
                            }
                        }
                    }
                }

                if (shouldUpdateTask) {
                    releaseRequest()
                    shouldUpdateTask = false
                }

                for (request in requests()) {
                    val handler = requestHandlerMap()!!.get(request)
                    request.update(
                        { t: Boolp ->
                            when (request) {
                                is PutItemsRequest -> (handler as RequestHandlers.RequestHandler<PutItemsRequest>).preCallBack(this, request, t)
                                is ReadItemsRequest -> (handler as RequestHandlers.RequestHandler<ReadItemsRequest>).preCallBack(this, request, t)
                                is PutLiquidsRequest -> (handler as RequestHandlers.RequestHandler<PutLiquidsRequest>).preCallBack(this, request, t)
                                is ReadLiquidsRequest -> (handler as RequestHandlers.RequestHandler<ReadLiquidsRequest>).preCallBack(this, request, t)
                                else -> false
                            }
                        } as RequestTask,
                        { t: Boolp ->
                            when (request) {
                                is PutItemsRequest -> (handler as RequestHandlers.RequestHandler<PutItemsRequest>).callBack(this, request, t)
                                is ReadItemsRequest -> (handler as RequestHandlers.RequestHandler<ReadItemsRequest>).callBack(this, request, t)
                                is PutLiquidsRequest -> (handler as RequestHandlers.RequestHandler<PutLiquidsRequest>).callBack(this, request, t)
                                is ReadLiquidsRequest -> (handler as RequestHandlers.RequestHandler<ReadLiquidsRequest>).callBack(this, request, t)
                                else -> false
                            }
                        } as RequestTask,
                        { t: Boolp ->
                            when (request) {
                                is PutItemsRequest -> (handler as RequestHandlers.RequestHandler<PutItemsRequest>).afterCallBack(this, request, t)
                                is ReadItemsRequest -> (handler as RequestHandlers.RequestHandler<ReadItemsRequest>).afterCallBack(this, request, t)
                                is PutLiquidsRequest -> (handler as RequestHandlers.RequestHandler<PutLiquidsRequest>).afterCallBack(this, request, t)
                                is ReadLiquidsRequest -> (handler as RequestHandlers.RequestHandler<ReadLiquidsRequest>).afterCallBack(this, request, t)
                                else -> false
                            }
                        } as RequestTask
                    )
                }

            }

            super.updateTile()
        }

        override fun priority(priority: Int) {
            TODO("Not yet implemented")
        }

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if (tileValid(other.tile) && gridValid()) {
                if (configValid(other)) {
                    UncCore.secConfig.showOn(other)
                }
                return false
            } else return true
        }

        public override fun config(): ByteArray {
            val pair = PosCfgPair()
            pair.configs.clear()
            for (entry in configMap) {
                val build = nearby(Point2.x(entry.key).toInt(), Point2.y(entry.key).toInt())
                if (build != null && !(build is IOPointComp && !ioPoints()!!.contains(build))) {
                    pair.configs.put(entry.key, entry.value)
                }
            }

            return pair.pack()
        }

        public override fun acceptItem(source: Building, item: Item?): Boolean {
            return source is IOPointComp && ioPoints()!!.contains(source)
        }

        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            return source is IOPointComp && ioPoints()!!.contains(source)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val pair = PosCfgPair()
            val len = read.i()
            val bytes = read.b(len)
            pair.read(bytes)

            configMap = pair.configs

            Pools.free(pair)
        }

        override fun write(write: Writes) {
            super.write(write)
            val pair = PosCfgPair()
            pair.configs.clear()
            for (entry in configMap) {
                val build = nearby(Point2.x(entry.key).toInt(), Point2.y(entry.key).toInt())
                if (build != null && !(build is IOPointComp && !ioPoints()!!.contains(build))) {
                    pair.configs.put(entry.key, entry.value)
                }
            }
            val bytes = pair.pack()
            write.i(bytes.size)
            write.b(bytes)

            Pools.free(pair)
        }


    }
    open  class PosCfgPair : DataPackable, Poolable {
        var configs: IntMap<TargetConfigure> = IntMap<TargetConfigure>()
companion object{
    const val typeID: Long = 1679658234266591164L
}
        override fun typeID(): Long {
            return typeID
        }

        override fun write(write: Writes) {
            write.i(configs.size)
            for (cfg in configs.values()) {
                val bytes = cfg.pack()
                write.i(bytes.size)
                write.b(bytes)
            }
        }

        override fun read(read: Reads) {
            val length = read.i()
            configs.clear()
            for (i in 0..<length) {
                val cfg = TargetConfigure()
                val len = read.i()
                cfg.read(read.b(len))
                configs.put(cfg.offsetPos, cfg)
            }
        }

        override fun reset() {
            configs.clear()
        }

        open fun handleConfig(handler: Cons<Point2?>) {
            val c = IntMap<TargetConfigure>()
            for (entry in configs) {
                entry.value.configHandle(handler)
                c.put(entry.value.offsetPos, entry.value)
            }

            configs = c
        }
    }
}