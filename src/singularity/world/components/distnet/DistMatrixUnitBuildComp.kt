package singularity.world.components.distnet

import arc.func.Cons2
import arc.func.Prov
import arc.math.geom.Point2
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.OrderedMap
import arc.struct.OrderedSet
import mindustry.Vars
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.world.Tile
import singularity.Sgl
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.blocks.distribute.matrixGrid.RequestHandlers
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import singularity.world.distribution.MatrixGrid
import singularity.world.distribution.buffers.BaseBuffer
import singularity.world.distribution.request.DistRequestBase
import universecore.util.Empties
import universecore.util.colletion.TreeSeq

interface DistMatrixUnitBuildComp : DistElementBuildComp {
    // @Annotations.BindField(value = "tempFactories", initialize = "new arc.struct.ObjectMap<>()")
    fun tempFactories(): ObjectMap<GridChildType?, ObjectMap<ContentType?, RequestHandlers.RequestHandler<*>?>>? {
        return null
    }

    // @Annotations.BindField(value = "requests", initialize = "new arc.struct.OrderedSet<>()")
    fun requests(): OrderedSet<DistRequestBase> {
       throw Exception("requests is null")
    }

    //  @Annotations.BindField(value = "matrixGrid", initialize = "new singularity.world.distribution.MatrixGrid(this)")
    fun matrixGrid(): MatrixGrid? {
        return null
    }

    //  @Annotations.BindField(value = "configs", initialize = "new universecore.util.colletion.TreeSeq<>((a, b) -> b.priority - a.priority)")
    fun configs(): TreeSeq<TargetConfigure>? {
        return null
    }

    /** @Annotations.BindField(value = "buffers", initialize = "new arc.struct.OrderedMap<>()")
     */
    fun buffers(): OrderedMap<DistBufferType<*>, BaseBuffer<*, *, *>>? {
        return null
    }

    // @Annotations.BindField(value = "requestHandlerMap", initialize = "new arc.struct.ObjectMap()")
    fun requestHandlerMap(): ObjectMap<DistRequestBase?, RequestHandlers.RequestHandler<*>?>? {
        return null
    }

    // @Annotations.BindField(value = "ioPoints", initialize = "new arc.struct.ObjectSet<>()")
    fun ioPoints(): ObjectSet<IOPointComp?>? {
        return null
    }

    // @Annotations.MethodEntry(entryMethod = "update")
    fun updateGrid() {
        if (gridValid()) matrixGrid()!!.update()
    }

    fun <T : BaseBuffer<*, *, *>> getBuffer(buff: DistBufferType<T>): T {
        return buffers()!!.get(buff) as T
    }

    fun initBuffers() {
        for (buffer in DistBufferType.all) {
            buffers()!!.put(buffer, buffer.get(this.matrixBlock!!.bufferCapacity()))
        }
    }

    fun gridValid(): Boolean {
        return true
    }

    override fun priority(): Int {
        return matrixGrid()!!.priority
    }

    override fun priority(priority: Int) {
        matrixGrid()!!.priority = priority
        distributor()!!.network.priorityModified(this)
    }

    fun releaseRequest() {
        for (request in requests()!!) {
            request.kill()
        }
        requests()!!.clear()

        resetFactories()

        for (config in configs()!!) {
            config.eachChildType(Cons2 { type: GridChildType?, map: ObjectMap<ContentType?, ObjectSet<UnlockableContent?>?>? ->
                for (contType in map!!.keys()) {
                    addConfig(type, contType, config)
                }
            })
        }

        requestHandlerMap()!!.clear()
        for (entry in tempFactories()!!) {
            for (e in entry.value) {
                val request = createRequest(entry.key, e.key)
                if (request == null) continue
                requests()!!.add(request)
                distributor()!!.assign(request, false)

                requestHandlerMap()!!.put(request, e.value)
            }
        }

        for (request in requests()!!) {
            request.init(distributor()!!.network)
        }
    }

    fun configValid(entity: Building): Boolean {
        if (entity is IOPointComp && (entity.gridConfig() == null || entity.parent() === this)) return true
        return Sgl.matrixContainers.getContainer(entity.block) != null
    }

    fun resetFactories() {
        for (fac in tempFactories()!!) {
            for (handler in fac.value.values()) {
                handler!!.reset(this)
            }
        }
        tempFactories()!!.clear()
    }

    fun addConfig(type: GridChildType?, contType: ContentType?, cfg: TargetConfigure) {
        val build = Vars.world.build(tile!!.x + Point2.x(cfg.offsetPos), tile!!.y + Point2.y(cfg.offsetPos))
        val factory = if (build is IOPointComp) (build as IOPointComp).iOBlock!!.requestFactories!!.get(type, Empties.nilMapO<ContentType?, RequestHandlers.RequestHandler<*>?>())!!.get(contType) else null

        if (factory != null) {
            val map = tempFactories()!!.get(type, Prov { ObjectMap() })
            if (!map.containsKey(contType)) map.put(contType, factory)
            factory.addParseConfig(cfg)
        }
    }

    fun createRequest(type: GridChildType?, contType: ContentType?): DistRequestBase? {
        val factory = tempFactories()!!.get(type, Empties.nilMapO<ContentType?, RequestHandlers.RequestHandler<*>?>()).get(contType)
        if (factory == null) return null
        val result = factory.makeRequest(this)
        factory.reset(this)
        return result
    }

    val matrixBlock: DistMatrixUnitComp?
        get() = getBlock(DistMatrixUnitComp::class.java)

    fun ioPointConfigBackEntry(ioPoint: IOPointComp)

    fun tileValid(tile: Tile?): Boolean

    fun drawValidRange()

    fun addIO(io: IOPointComp) {
        ioPoints()!!.add(io)
        matrixGrid()!!.addConfig(io.gridConfig())
    }

    fun removeIO(io: IOPointComp) {
        ioPoints()!!.remove(io)
        matrixGrid()!!.remove(io.building)
    }
}