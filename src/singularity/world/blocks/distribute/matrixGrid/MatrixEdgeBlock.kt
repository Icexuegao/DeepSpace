package singularity.world.blocks.distribute.matrixGrid

import arc.Core
import arc.func.Cons
import arc.func.Cons2
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Point2
import arc.util.Eachable
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.world.Block
import singularity.Singularity
import singularity.graphic.SglDraw
import singularity.world.components.EdgeLinkerBuildComp
import singularity.world.components.EdgeLinkerComp
import kotlin.math.max

//@Annotations.ImplEntries
open class MatrixEdgeBlock(name: String?) : Block(name), EdgeLinkerComp {
    var linkLength: Int = 16
    var linkOffset: Float = 0f
    var linkRegion: TextureRegion? = null
    var linkCapRegion: TextureRegion? = null
    var linkLightRegion: TextureRegion? = null
    var linkLightCapRegion: TextureRegion? = null

    init {
        update = true
        configurable = true

        config<Point2?, MatrixEdgeBuild?>(Point2::class.java, Cons2 { e: MatrixEdgeBuild?, p: Point2? -> e!!.nextPos = Point2.pack(e.tile.x + p!!.x, e.tile.y + p.y) })
        config<Int?, MatrixEdgeBuild?>(Int::class.java, Cons2 { entity: MatrixEdgeBuild?, pos: Int? -> this.link(entity!!, pos) })
    }

    override fun link(entity: EdgeLinkerBuildComp, pos: Int?) {
        super<EdgeLinkerComp>.link(entity, pos)
        entity.linkLerp(0f)
    }

    override fun linkable(other: EdgeLinkerComp?): Boolean {
        return other is MatrixEdgeBlock || other is MatrixGridCore
    }

    override fun init() {
        super.init()
        clipSize = max(clipSize, (linkLength * Vars.tilesize * 2).toFloat())
    }

    override fun load() {
        super.load()
        linkRegion = Core.atlas.find(name + "_link", Singularity.getModAtlas("matrix_grid_edge"))
        linkCapRegion = Core.atlas.find(name + "_cap", Singularity.getModAtlas("matrix_grid_cap"))
        linkLightRegion = Core.atlas.find(name + "_light_link", Singularity.getModAtlas("matrix_grid_light_edge"))
        linkLightCapRegion = Core.atlas.find(name + "_light_cap", Singularity.getModAtlas("matrix_grid_light_cap"))
    }

    override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
        val pos = req.config as Point2?
        if (pos == null) return

        list.each(Cons { plan: BuildPlan? ->
            if (Point2.pack(req.x + pos.x, req.y + pos.y) == Point2.pack(plan!!.x, plan.y)) {
                if (plan.block is EdgeLinkerComp) {
                    SglDraw.drawLink(
                        req.drawx(), req.drawy(), linkOffset,
                        plan.drawx(), plan.drawy(), linkOffset(),
                        linkRegion, null, 1f
                    )
                }
            }
        })
    }

    //@Annotations.ImplEntries
    inner class MatrixEdgeBuild : Building(), EdgeLinkerBuildComp {
        var loaded: Boolean = false
        var nextPos: Int = -1

        override fun linked(next: EdgeLinkerBuildComp?) {
            if (loaded) linkLerp(0f)
        }

        override fun delinked(next: EdgeLinkerBuildComp?) {
            if (loaded) linkLerp(0f)
        }

        override fun edgeUpdated() {}

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

        override var edges: EdgeContainer= EdgeContainer()

        override fun updateLinking() {
            super<EdgeLinkerBuildComp>.updateLinking()
            loaded = true
        }

        override fun onConfigureBuildTapped(other: Building?): Boolean {
            if (other is EdgeLinkerBuildComp && canLink(this, other as EdgeLinkerBuildComp)) {
                configure(other.pos())
                return false
            }
            return true
        }

        override fun config(): Point2? {
            val p = Point2.unpack(nextPos)
            return p.set(p.x - tile.x, p.y - tile.y)
        }
    }
}