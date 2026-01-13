package singularity.world.blocks.nuclear

import arc.func.*
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.Rand
import arc.math.WindowedMean
import arc.math.geom.Geometry
import arc.math.geom.Intersector
import arc.math.geom.Point2
import arc.struct.IntFloatMap
import arc.struct.IntMap
import arc.struct.IntSeq
import arc.struct.Seq
import arc.util.*
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.core.Renderer
import mindustry.entities.units.BuildPlan
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.input.Placement
import mindustry.io.TypeIO
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.meta.Env
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.components.NuclearEnergyBuildComp
import kotlin.math.max

open class NuclearNode(name: String) : NuclearBlock(name) {
    protected var otherReq: BuildPlan? = null
    var lightRadius: Float = 3.5f
    var linkLeaserStroke: Float = 2f

    /**节点的最大连接范围 */
    var linkRange: Float = 16f

    /**最大连接数 */
    var maxLinks: Int = 10
    var moveAlphaRate: Float = 8f
    var linkColor: Color = SglDrawConst.matrixNet
    var linkColor2: Color? = SglDrawConst.fexCrystal
    var linkGradientScl: Float = 20f
    private var returnInt = 0
    private val timeID = timers++

    @Nullable
    var lastPlaced: NuclearNodeBuild? = null

    init {
        drawDisabled = false
        schematicPriority = -10
        envEnabled = envEnabled or Env.space
        swapDiagonalPlacement = true
        energyCapacity = 60f

        configurable = true
        buildType= Prov(::NuclearNodeBuild)
    }

    override fun appliedConfig() {
        super.appliedConfig()
        config<Point2?, NuclearNodeBuild?>(Point2::class.java, Cons2 { entity: NuclearNodeBuild?, value: Point2? ->
            val tile = Vars.world.tile(value!!.x, value.y)
            if (tile == null || (tile.build !is NuclearEnergyBuildComp) || !(tile.build as NuclearEnergyBuildComp).hasEnergy()) return@Cons2

            value.x = tile.x.toInt()
            value.y = tile.y.toInt()
            var build= tile.build as NuclearEnergyBuildComp
            if (entity!!.linked.contains(value.pack())) {
                entity.deLink(build)
            } else {
                if (entity.linksCount() >= maxLinks) return@Cons2
                entity.link(build)
            }
        })
    }

    override fun init() {
        super.init()
        clipSize = max(clipSize, linkRange * Vars.tilesize * 2)
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.linkRange, linkRange, StatUnit.blocks)
        stats.add(Stat.maxConsecutive, maxLinks.toFloat())
    }

    override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
        if (req.config is Array<*> && (req.config as Array<*>).isArrayOf<Point2>()) {
            var config=req.config as Array<Point2>
            for (point in config) {
                val px: Int = req.x + point.x
                val py: Int = req.y + point.y
                otherReq = null
                list.each(Cons { other: BuildPlan? ->
                    if (other!!.block != null && (px >= other.x - ((other.block.size - 1) / 2) && py >= other.y - ((other.block.size - 1) / 2) && px <= other.x + other.block.size / 2 && py <= other.y + other.block.size / 2)
                        && other !== req && other.block is NuclearBlock
                    ) {
                        otherReq = other
                    }
                })

                if (otherReq == null || otherReq!!.block == null) continue

                setLaserColor()
                Fill.circle(req.drawx(), req.drawy(), lightRadius)
                drawLink(req.drawx(), req.drawy(), size, otherReq!!.drawx(), otherReq!!.drawy(), otherReq!!.block.size)
            }
            Draw.color()
        }
    }

    override fun changePlacementPath(points: Seq<Point2?>, rotation: Int, diagonalOn: Boolean) {
        Placement.calculateNodes(points, this, rotation) { point: Point2?, other: Point2? -> inRange(Vars.world.tile(point!!.x, point.y), Vars.world.tile(other!!.x, other.y), linkRange * Vars.tilesize) }
    }

    fun drawLink(entity: NuclearEnergyBuildComp, other: NuclearEnergyBuildComp) {
        drawLink(entity.building.x, entity.building.y, if (entity is NuclearNodeBuild) 0 else size, other.building.x, other.building.y, if (other is NuclearNodeBuild) 0 else other.block!!.size)
        if (other !is NuclearNodeBuild) {
            Fill.circle(Tmp.v1.x, Tmp.v1.y, lightRadius / 2)
        }
    }

    fun drawLink(x1: Float, y1: Float, size1: Int, x2: Float, y2: Float, size2: Int) {
        Draw.z(Layer.effect)
        setLaserColor()

        Tmp.v1.set(x1, y1).sub(x2, y2).setLength(size1 * Vars.tilesize / 2f - 1.5f).scl(-1f)
        Tmp.v2.set(x2, y2).sub(x1, y1).setLength(size2 * Vars.tilesize / 2f - 1.5f).scl(-1f)
        val xs = x1 + Tmp.v1.x
        val ys = y1 + Tmp.v1.y
        val xo = x2 + Tmp.v2.x
        val yo = y2 + Tmp.v2.y

        Tmp.v1.set(xo, yo)

        Lines.stroke(linkLeaserStroke * Renderer.laserOpacity)
        Lines.line(xs, ys, xo, yo, false)
        Lines.stroke(1f)
    }

    fun setLaserColor() {
        Draw.color(linkColor, linkColor2, Mathf.absin(linkGradientScl, 1f))
    }

    override fun changePlacementPath(points: Seq<Point2?>, rotation: Int) {
        Placement.calculateNodes(points, this, rotation) { point: Point2?, other: Point2? -> inRange(Vars.world.tile(point!!.x, point.y), Vars.world.tile(other!!.x, other.y), linkRange * Vars.tilesize) }
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        val tile = Vars.world.tile(x, y) ?: return

        Lines.stroke(1f)
        Draw.color(Pal.placing)
        Drawf.circles(x * Vars.tilesize + offset, y * Vars.tilesize + offset, linkRange * Vars.tilesize)

        if (lastPlaced != null && lastPlaced!!.isAdded
            && inRange(tile, lastPlaced!!.tile, linkRange * Vars.tilesize)
            && inRange(lastPlaced!!.tile, tile, (lastPlaced!!.block as NuclearNode).linkRange * Vars.tilesize)
            && lastPlaced!!.linksCount() < ((lastPlaced!!.block) as NuclearNode).maxLinks
        ) {
            setLaserColor()
            Draw.alpha(0.5f)

            Drawf.dashLine(linkColor, tile.worldx() + offset, tile.worldy() + offset, lastPlaced!!.x, lastPlaced!!.y)
            Drawf.circles(lastPlaced!!.x, lastPlaced!!.y, lastPlaced!!.block.size * Vars.tilesize / 1.8f + Mathf.absin(4f, 2f))
        }

        Draw.reset()
    }

    fun getPotentialLink(tile: Tile, team: Team?, cons: Cons<NuclearEnergyBuildComp?>) {
        val valid = Boolf { other: NuclearEnergyBuildComp? ->
            other != null && other.building.tile !== tile && other.energy() != null && other.consumeEnergy() && inRange(tile, other.building.tile, linkRange * Vars.tilesize)
                    && other.building.team === team && !Structs.contains(Edges.getEdges(size)) { p: Point2? ->  //do not link to adjacent buildings
                val t = Vars.world.tile(tile.x + p!!.x, tile.y + p.y)
                t != null && t.build === other
            }
        }

        tempNuclearEntity.clear()

        Geometry.circle(tile.x.toInt(), tile.y.toInt(), (linkRange + 2).toInt(), Intc2 { x: Int, y: Int ->
            val other = Vars.world.build(x, y)
            if (other !is NuclearEnergyBuildComp) return@Intc2
            if (valid.get(other as NuclearEnergyBuildComp) && !tempNuclearEntity.contains(other as NuclearEnergyBuildComp)) {
                tempNuclearEntity.add(other as NuclearEnergyBuildComp)
            }
        })

        tempNuclearEntity.sort(Comparator { a: NuclearEnergyBuildComp?, b: NuclearEnergyBuildComp? -> a!!.building.dst2(tile).compareTo(b!!.building.dst2(tile)) })

        returnInt = 0
        tempNuclearEntity.each(valid) { e: NuclearEnergyBuildComp? ->
            if (returnInt++ < maxLinks) {
                cons.get(e)
            }
        }
    }

    fun inRange(origin: Tile?, other: Tile?, range: Float): Boolean {
        if (origin == null || other == null) return false
        return Intersector.overlaps(Tmp.cr1.set(origin.drawx(), origin.drawy(), range), other.getHitbox(Tmp.r1))
    }

    fun inRange(origin: Building, other: Building, range: Float): Boolean {
        return inRange(origin.tile, other.tile, range)
    }

    fun inRange(origin: NuclearEnergyBuildComp, other: NuclearEnergyBuildComp, range: Float): Boolean {
        return inRange(origin.building, other.building, range)
    }

    /**判断从一个点到另一个点是否可以进行核能连接 */
    fun canLink(from: NuclearEnergyBuildComp, to: NuclearEnergyBuildComp): Boolean {
        if (from.building.team !== to.building.team || from === to || !from.hasEnergy() || !to.hasEnergy()) return false
        return inRange(from, to, linkRange * Vars.tilesize) || (to is NuclearNodeBuild && inRange(to, from, (to.block as NuclearNode).linkRange * Vars.tilesize))
    }

    open inner class NuclearNodeBuild : SglBuilding() {
        val smoothAlpha: IntFloatMap = IntFloatMap()
        val chanceFlow: IntFloatMap = IntFloatMap()
        val flowing: IntFloatMap = IntFloatMap()
        val flowMean: IntMap<WindowedMean> = IntMap<WindowedMean>()
        var linked: IntSeq = object : IntSeq() {
            init {
                ordered = false
            }
        }
        var linkThis: IntSeq = object : IntSeq() {
            init {
                ordered = false
            }
        }

        override fun update() {
            updateEnergy()
            super.update()
        }
        fun link(target: NuclearEnergyBuildComp) {
            val linkingPos = target.building.pos()

            if (linked.contains(linkingPos)) return

            if (linkThis.contains(linkingPos)) {
                linkThis.removeValue(linkingPos)
                if (target is NuclearNodeBuild) target.linked.removeValue(pos())
            }

            linked.add(linkingPos)
            if (target is NuclearNodeBuild) target.linkThis.add(pos())
        }

        fun deLink(target: NuclearEnergyBuildComp) {
            val delinkingPos = target.building.pos()

            if (!linked.contains(delinkingPos)) return

            linked.removeValue(delinkingPos)
            if (target is NuclearNodeBuild) target.linkThis.removeValue(pos())
        }

        fun linksCount(): Int {
            return linked.size + linkThis.size
        }

        override fun placed() {
            if (Vars.net.client()) return

            if (lastPlaced != null && lastPlaced!!.isAdded) {
                if (canLink(this, lastPlaced!!)) {
                    lastPlaced!!.configure(Point2.unpack(pos()))
                }
            }

            lastPlaced = this

            super.placed()
        }

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if (other !is NuclearEnergyBuildComp) return true

            if (canLink(this, other as NuclearEnergyBuildComp)) {
                configure(Point2.unpack(other.pos()))
                return false
            }

            if (other === this) {
                if (linked.size > 0) {
                    while (!linked.isEmpty) {
                        configure(Point2.unpack(linked.get(0)))
                    }
                } else {
                    getPotentialLink(tile, team) { e: NuclearEnergyBuildComp? -> configure(Point2.unpack(e!!.building.pos())) }
                }
                return false
            }

            return true
        }

        val nodeDumps: Seq<NuclearEnergyBuildComp>
            get() {
                val res = proximityNuclearBuilds().select { obj: NuclearEnergyBuildComp -> obj.consumeEnergy() }

                for (i in 0..<linked.size) {
                    val pos = linked.get(i)
                    val b = Vars.world.build(pos)

                    if (b is NuclearEnergyBuildComp) {
                        res.add(b)
                    } else linked.removeIndex(i)
                }

                return res
            }

        override fun updateTile() {
            super.updateTile()
            if (timer(timeID, 10f)) {
                flowing.clear()
                for (i in 0..<linked.size) {
                    val pos = linked.get(i)
                    val mean: WindowedMean = flowMean.get(pos) { WindowedMean(5) }
                    mean.add(chanceFlow.get(pos, 0f))
                    flowing.put(pos, mean.mean() / 10)
                    smoothAlpha.put(pos, Mathf.lerpDelta(smoothAlpha.get(pos), Mathf.clamp(flowing.get(pos) / moveAlphaRate), 0.02f))

                    chanceFlow.put(pos, 0f)
                }
            }

            dumpEnergy(nodeDumps as Seq<NuclearEnergyBuildComp?>)
        }

        override fun energyMoved(next: NuclearEnergyBuildComp?, rate: Float) {
            chanceFlow.increment(next!!.building.pos(), rate)
        }

        override fun draw() {
            super.draw()
            Draw.z(Layer.effect)

            setLaserColor()
            Fill.circle(x, y, lightRadius)
            Lines.stroke(0.3f)
            SglDraw.dashCircle(x, y, lightRadius * 1.5f, Time.time * 2)

            if (linked.isEmpty) return

            setLaserColor()
            Lines.stroke(4f)
            for (i in 0..<linked.size) {
                val pos = linked.get(i)
                val entity = Vars.world.build(pos)
                if (entity == null) continue
                drawLink(this, entity as NuclearEnergyBuildComp)
                val tx = Tmp.v1.x
                val ty = Tmp.v1.y
                val fi = i + 1
                Draw.draw(Draw.z(), Runnable {
                    val alpha = smoothAlpha.get(pos) * Renderer.laserOpacity
                    setLaserColor()
                    MathRenderer.setThreshold(0.5f, 0.8f)

                    rand.setSeed(id.toLong() * fi)
                    for (ig in 0..2) {
                        Draw.color(linkColor, linkColor2, Mathf.absin(rand.random(linkGradientScl / 2, linkGradientScl * 2), 1f))

                        MathRenderer.setDispersion(rand.random(0.22f, 0.34f) * alpha)
                        MathRenderer.drawSin(
                            x, y, tx, ty,
                            rand.random(linkLeaserStroke * 1.2f, linkLeaserStroke * 1.8f) * alpha * (0.9f + 0.1f * Mathf.sin(Time.time / rand.random(10f, 15f))),
                            rand.random(360f, 720f),
                            -rand.random(5f, 8f) * Time.time
                        )
                    }
                    Draw.reset()
                })

                if (entity !is NuclearNodeBuild) {
                    Fill.circle(tx, ty, lightRadius * smoothAlpha.get(pos))
                    Lines.stroke(0.3f)
                    SglDraw.dashCircle(tx, ty, lightRadius * 1.5f * smoothAlpha.get(pos), Time.time * 2)
                }
            }
            Draw.reset()
        }

        override fun dropped() {
            while (!linked.isEmpty) {
                configure(Point2.unpack(linked.get(0)))
            }
        }

        override fun drawConfigure() {
            Drawf.circles(x, y, tile.block().size * Vars.tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f))
            Drawf.circles(x, y, linkRange * Vars.tilesize)

            for (i in 0..<linked.size) {
                val pos = linked.get(i)
                val link = Vars.world.build(pos)
                if (link !is NuclearEnergyBuildComp) continue
                val len = Mathf.len(link.x - x, link.y - y)

                Drawf.arrow(x, y, link.x, link.y, len * (Time.time % 120) / 120, linkLeaserStroke * 2, linkColor)
                Drawf.line(linkColor, x, y, link.x, link.y)
                Drawf.square(link.x, link.y, link.block.size * Vars.tilesize / 2f + 1f, Pal.place)
            }

            for (i in 0..<linkThis.size) {
                val pos = linkThis.get(i)
                val other = Vars.world.build(pos)
                if (other !is NuclearEnergyBuildComp) continue
                val len = Mathf.len(other.x - x, other.y - y)

                Drawf.arrow(other.x, other.y, x, y, len * (Time.time % 120) / 120, linkLeaserStroke * 2, linkColor)
                Drawf.dashLine(linkColor, x, y, other.x, other.y)
                Drawf.circles(other.x, other.y, other.block.size * Vars.tilesize / 1.8f + Mathf.absin(4f, 2f), Pal.place)
            }

            Draw.reset()
        }

        override fun config(): Any {
            val lis = arrayOfNulls<Point2>(linked.size)
            for (i in 0..<linked.size) {
                lis[i] = Point2.unpack(linked.get(i))
            }

            return lis
        }

        override fun drawSelect() {
            super.drawSelect()

            Lines.stroke(1f)

            Draw.color(Pal.accent)
            Drawf.circles(x, y, linkRange * Vars.tilesize)
            Draw.reset()
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(smoothAlpha.size)

            TypeIO.writeIntSeq(write, linked)
            TypeIO.writeIntSeq(write, linkThis)
            val keys = smoothAlpha.keys()
            while (keys.hasNext()) {
                val p = keys.next()
                write.i(p)
                write.f(smoothAlpha.get(p))
                write.f(flowing.get(p))
                write.f(chanceFlow.get(p))
            }
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val size = read.i()

            if (revision >= 2) {
                linked = TypeIO.readIntSeq(read)
                linkThis = TypeIO.readIntSeq(read)
            }

            smoothAlpha.clear()
            flowing.clear()
            chanceFlow.clear()
            for (i in 0..<size) {
                val p = read.i()
                smoothAlpha.put(p, read.f())
                flowing.put(p, read.f())
                chanceFlow.put(p, read.f())
            }
        }
    }

    companion object {
        protected val tempNuclearEntity: Seq<NuclearEnergyBuildComp?> = Seq<NuclearEnergyBuildComp?>()
        protected val rand: Rand = Rand()
    }
}