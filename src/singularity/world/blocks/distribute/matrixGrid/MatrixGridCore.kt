package singularity.world.blocks.distribute.matrixGrid

import arc.Core
import arc.func.Cons
import arc.func.Cons2
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Point2
import arc.math.geom.Polygon
import arc.math.geom.Vec2
import arc.struct.FloatSeq
import arc.struct.IntMap
import arc.struct.IntSet
import arc.struct.ObjectMap
import arc.util.Eachable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.world.Tile
import singularity.Singularity
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.components.EdgeLinkerBuildComp
import singularity.world.components.EdgeLinkerComp
import singularity.world.components.distnet.IOPointComp
import singularity.world.distribution.GridChildType
import singularity.world.meta.SglStat
import universecore.UncCore
import universecore.util.DataPackable
import kotlin.math.max

open class MatrixGridCore(name: String) : MatrixGridBlock(name), EdgeLinkerComp {

    companion object {
        var pair: LinkPair = LinkPair()
        private val TMP_EXI = IntSet()
        const val typeID: Long = 5463757638164667648L
    }
    var linkLength: Int = 16
    var maxEdges: Int = 8
    var linkRegion: TextureRegion? = null
    var linkCapRegion: TextureRegion? = null
    var linkLightRegion: TextureRegion? = null
    var linkLightCapRegion: TextureRegion? = null
    var linkOffset: Float = 0f
    var childLinkRegion: TextureRegion? = null
    var linkColors: ObjectMap<GridChildType?, Color?> = ObjectMap<GridChildType?, Color?>()

    override fun parseConfigObjects(e: SglBuilding?, obj: Any?) {
        val entity = e as MatrixGridCoreBuild
        if (obj is LinkPair) {
            val p = obj.linking
            entity.nextPos = Point2.pack(e.tileX() + p!!.x, e.tileY() + p.y)
        }
        super.parseConfigObjects(e, obj)
    }

    override fun setStats() {
        super.setStats()
        stats.add(SglStat.maxChildrenNodes, maxEdges.toFloat())
    }

    public override fun load() {
        super.load()
        linkRegion = Core.atlas.find(name + "_link", Singularity.getModAtlas("matrix_grid_edge"))
        linkCapRegion = Core.atlas.find(name + "_cap", Singularity.getModAtlas("matrix_grid_cap"))
        linkLightRegion = Core.atlas.find(name + "_light_link", Singularity.getModAtlas("matrix_grid_light_edge"))
        linkLightCapRegion = Core.atlas.find(name + "_light_cap", Singularity.getModAtlas("matrix_grid_light_cap"))
        childLinkRegion = Core.atlas.find(name + "_child_linker", Singularity.getModAtlas("matrix_grid_child_linker"))
    }

    public override fun pointConfig(config: Any?, transformer: Cons<Point2?>): Any? {
        if (config is ByteArray && DataPackable.readObject<DataPackable>(config) is LinkPair) {
          var cfg=  DataPackable.readObject<DataPackable>(config) as  LinkPair
            cfg.handleConfig(transformer)
            return cfg.pack()
        }
        return config
    }

    init {
        config(Int::class.java, Cons2 { entity: EdgeLinkerBuildComp, pos: Int? -> this.link(entity , pos) })
    }

    override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
        val bytes = req.config as ByteArray?
        if (bytes == null) return

        pair.read(bytes)
        val p = pair.linking
        list.each(Cons { plan: BuildPlan? ->
            if (Point2.pack(req.x + p!!.x, req.y + p.y) == Point2.pack(plan!!.x, plan.y)) {
                if (plan.block is EdgeLinkerComp) {
                    SglDraw.drawLink(
                        req.drawx(), req.drawy(), linkOffset,
                        plan.drawx(), plan.drawy(), linkOffset(),
                        linkRegion, null, 1f
                    )
                }
            }
        })

        pair.reset()
    }

    override fun link(entity: EdgeLinkerBuildComp, pos: Int?) {
        super<EdgeLinkerComp>.link(entity, pos)
        entity.linkLerp(0f)
    }

    override fun linkable(other: EdgeLinkerComp?): Boolean {
        return other is MatrixEdgeBlock
    }

    override fun init() {
        super.init()
        clipSize = max(clipSize, (linkLength * Vars.tilesize * 2).toFloat())
        initColor()
    }

    fun initColor() {
        linkColors.put(GridChildType.input, Pal.heal)
        linkColors.put(GridChildType.output, Pal.accent)
        linkColors.put(GridChildType.acceptor, SglDrawConst.matrixNet)
        linkColors.put(GridChildType.container, SglDrawConst.matrixNet)
    }

    inner class MatrixGridCoreBuild : MatrixGridBuild(), EdgeLinkerBuildComp {
        override var edges: EdgeContainer = EdgeContainer()
        protected var lastPoly: Polygon? = null
        protected var vertices: Array<Vec2?>? = null
        protected var verticesSeq: FloatSeq? = null
        var nextPos: Int = -1
        protected var loaded: Boolean = false
        protected var childLinkWarmup: IntMap<FloatArray?> = IntMap<FloatArray?>()

        override fun updateTile() {
            if (lastPoly !== edges.getPoly()) {
                lastPoly = edges.getPoly()
                if (lastPoly != null) {
                    val poly = edges.getPoly()
                    val vert = poly.getVertices()
                    verticesSeq = FloatSeq.with(*vert)
                    vertices = arrayOfNulls<Vec2>(vert.size / 2)
                    for (i in vertices!!.indices) {
                        vertices!![i] = Vec2(vert[i * 2], vert[i * 2 + 1])
                    }
                }
            }

            for (a in childLinkWarmup.values()) {
                a!![0] = Mathf.lerpDelta(a[0], 1f, 0.02f)
            }

            super.updateTile()
        }

        override fun releaseRequest() {
            super.releaseRequest()

            TMP_EXI.clear()
            for (config in configs()!!) {
                var warmup = childLinkWarmup.get(config.offsetPos)
                if (warmup == null) {
                    warmup = FloatArray(1)
                    childLinkWarmup.put(config.offsetPos, warmup)
                }
                TMP_EXI.add(config.offsetPos)
            }

            for (entry in childLinkWarmup) {
                if (!TMP_EXI.contains(entry.key)) childLinkWarmup.remove(entry.key)
            }
        }

        override fun linked(next: EdgeLinkerBuildComp?) {
            if (loaded) linkLerp(0f)
        }

        override fun delinked(next: EdgeLinkerBuildComp?) {
            if (loaded) linkLerp(0f)
        }

        override fun edgeUpdated() {}

        public override fun draw() {
            super.draw()
            drawLink()
            for (entry in childLinkWarmup) {
                val cfg = configMap.get(entry.key)
                if (cfg == null || entry.value!![0] <= 0.01f) continue
                val t = Vars.world.tile(tileX() + Point2.x(entry.key), tileY() + Point2.y(entry.key))
                if (t == null) return

                Draw.alpha(0.7f * entry.value!![0])
                val map = cfg.get()
                var c: Color? = null
                for (mapEntry in map) {
                    var bool = false
                    for (setEntry in mapEntry.value!!) {
                        if (!setEntry.value!!.isEmpty()) {
                            bool = true
                            break
                        }
                    }

                    if (bool) {
                        if (c == null) {
                            c = linkColors.get(mapEntry.key, Color.white)
                        } else {
                            c = Color.white
                            break
                        }
                    }
                }
                Draw.color(c)
                Draw.z(Layer.bullet - 5)
                SglDraw.drawLaser(x, y, t.drawx(), t.drawy(), childLinkRegion, null, 8 * entry.value!![0])
                Draw.z(Layer.effect)
                Fill.circle(t.drawx(), t.drawy(), 1.5f * entry.value!![0])
            }
            Draw.z(Layer.blockBuilding)
            Draw.reset()
        }

        override fun drawLink() {
            super<EdgeLinkerBuildComp>.drawLink()
            if (nextEdge() != null) {
                val l = Draw.z()
                Draw.z(Layer.effect)
                Draw.alpha(0.65f)
                SglDraw.drawLink(
                    x, y, linkOffset,
                    nextEdge()!!.tile()!!.drawx(), nextEdge()!!.tile()!!.drawy(), nextEdge()!!.edgeBlock!!.linkOffset(),
                    linkLightRegion, linkLightCapRegion, linkLerp()
                )
                Draw.z(l)
            }
        }

        override fun updateLinking() {
            super<EdgeLinkerBuildComp>.updateLinking()
            loaded = true
        }

        public override fun onConfigureBuildTapped(other: Building): Boolean {
            val result = super.onConfigureBuildTapped(other)
            if (other is EdgeLinkerBuildComp && canLink(this, other as EdgeLinkerBuildComp)) {
                configure(other.pos())
                return false
            }
            return result
        }

        public override fun gridValid(): Boolean {
            return super.gridValid() && edges.isClosure() && edges.all.size <= maxEdges
        }

        public override fun drawConfigure() {
            super.drawConfigure()
            for (io in ioPoints()!!) {
                if (UncCore.secConfig.getConfiguring() === io) continue
                val radius = io!!.block!!.size * Vars.tilesize / 2f + 1f
                val building = io.building
                Drawf.square(building.x, building.y, radius, Pal.accent)

                Tmp.v1.set(-1f, 1f).setLength(radius + 1).scl((Time.time % 60) / 60 * 1.41421f)
                Tmp.v2.set(1f, 0f).setLength(radius + 1).add(Tmp.v1)
                for (i in 0..3) {
                    Draw.color(Pal.gray)
                    Fill.square(building.x + Tmp.v2.x, building.y + Tmp.v2.y, 2f, 45f)
                    Draw.color(Pal.place)
                    Fill.square(building.x + Tmp.v2.x, building.y + Tmp.v2.y, 1.25f, 45f)
                    Tmp.v2.rotate(90f)
                }
            }
        }

        public override fun tileValid(tile: Tile?): Boolean {
            return !edges.isClosure() || edges.inLerp(tile)
        }

        public override fun drawValidRange() {
            //TODO: draw it
        }

        override fun config(): ByteArray {
            val pair = LinkPair()
            pair.configs.clear()
            for (entry in configMap) {
                val build = nearby(Point2.x(entry.key).toInt(), Point2.y(entry.key).toInt())
                if (build != null && !(build is IOPointComp && !ioPoints()!!.contains(build))) {
                    pair.configs.put(entry.key, entry.value)
                }
            }
            pair.linking = Point2(Point2.x(nextPos) - tileX(), Point2.y(nextPos) - tileY())

            return pair.pack()
        }

    }

    class LinkPair : PosCfgPair() {
        companion object{
            const val typeID: Long = 5463757638164667648L
        }
        public override fun typeID(): Long {
            return typeID
        }

        var linking: Point2? = null

        public override fun read(read: Reads) {
            linking = Point2.unpack(read.i())
            super.read(read)
        }

        public override fun write(write: Writes) {
            write.i(linking!!.pack())
            super.write(write)
        }

        public override fun reset() {
            super.reset()
            linking = null
        }

        public override fun handleConfig(handler: Cons<Point2?>) {
            super.handleConfig(handler)
            handler.get(linking)
        }

    }
}