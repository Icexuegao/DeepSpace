package ice.world.content.blocks.power

import arc.Core
import arc.func.Boolf
import arc.func.Cons
import arc.func.Func
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Intersector
import arc.math.geom.Point2
import arc.struct.IntSeq
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.*
import ice.graphics.IceColor
import ice.graphics.TextureRegionDelegate
import mindustry.Vars
import mindustry.core.Renderer
import mindustry.core.UI
import mindustry.core.World
import mindustry.entities.units.BuildPlan
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.input.Placement
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.blocks.power.PowerGraph
import mindustry.world.meta.Env
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.max

open class PowerNode(name: String) : PowerBlock(name) {
    companion object {
        protected var otherReq: BuildPlan? = null
        protected var returnInt: Int = 0
        protected val graphs: ObjectSet<PowerGraph?> = ObjectSet<PowerGraph?>()

        /** The maximum range of all power nodes on the map  */
        protected var maxRange: Float = 0f

        fun makePowerBalance(): Func<Building, Bar> {
            return Func { entity ->
                Bar({
                    Core.bundle.format("bar.powerbalance",
                        ((if (entity.power.graph.powerBalance >= 0) "+" else "") + UI.formatAmount((entity.power.graph.powerBalance * 60).toLong())))
                },
                    { Pal.powerBar },
                    { Mathf.clamp(entity.power.graph.lastPowerProduced / entity.power.graph.lastPowerNeeded) }
                )
            }
        }

        fun makeBatteryBalance(): Func<Building, Bar> {
            return Func { entity ->
                Bar({
                    Core.bundle.format("bar.powerstored",
                        (UI.formatAmount(entity.power.graph.lastPowerStored.toLong())), UI.formatAmount(entity.power.graph.lastCapacity.toLong()))
                },
                    { Pal.powerBar },
                    { Mathf.clamp(entity.power.graph.lastPowerStored / entity.power.graph.lastCapacity) }
                )
            }
        }
        //TODO code duplication w/ method above?
        /** Iterates through linked nodes of a block at a tile. All returned buildings are power nodes.  */
        fun getNodeLinks(tile: Tile, block: Block, team: Team, others: Cons<Building>) {
            val valid = Boolf { other: Building? ->
                other != null && other.tile !== tile && other.block is PowerNode &&
                        (block as PowerNode).autolink && other.power.links.size < block.maxNodes &&
                        block.overlaps(other.x, other.y, tile, block, block.laserRange * Vars.tilesize) && other.team === team && !graphs.contains(other.power.graph) && !insulated(tile, other.tile) && !Structs.contains(Edges.getEdges(block.size)) { p: Point2? ->  //do not link to adjacent buildings
                    val t = Vars.world.tile(tile.x + p!!.x, tile.y + p.y)
                    t != null && t.build === other
                }
            }

            tempBuilds.clear()
            graphs.clear()
            //add conducting graphs to prevent double link
            for (p in Edges.getEdges(block.size)) {
                val other = tile.nearby(p)
                if (other != null && other.team() === team && other.build != null && other.build.power != null && !(block.consumesPower && other.block().consumesPower && !block.outputsPower && !other.block().outputsPower)) {
                    graphs.add(other.build.power.graph)
                }
            }

            if (tile.build != null && tile.build.power != null) {
                graphs.add(tile.build.power.graph)
            }
            val rangeWorld: Float = maxRange * Vars.tilesize
            val tree = team.data().buildingTree
            tree?.intersect(tile.worldx() - rangeWorld, tile.worldy() - rangeWorld, rangeWorld * 2, rangeWorld * 2, Cons { build: Building? ->
                if (valid.get(build) && !tempBuilds.contains(build)) {
                    tempBuilds.add(build)
                }
            })

            tempBuilds.sort(Comparator { a: Building, b: Building ->
                val type = -java.lang.Boolean.compare(a.block is PowerNode, b.block is PowerNode)
                if (type != 0) return@Comparator type
                a.dst2(tile).compareTo(b.dst2(tile))
            })

            tempBuilds.each(valid) { t: Building? ->
                graphs.add(t!!.power.graph)
                others.get(t)
            }
        }

        fun insulated(tile: Tile, other: Tile): Boolean {
            return insulated(tile.x.toInt(), tile.y.toInt(), other.x.toInt(), other.y.toInt())
        }

        fun insulated(tile: Building, other: Building): Boolean {
            return insulated(tile.tileX(), tile.tileY(), other.tileX(), other.tileY())
        }

        fun insulated(x: Int, y: Int, x2: Int, y2: Int): Boolean {
            return World.raycast(x, y, x2, y2) { wx: Int, wy: Int ->
                val tile = Vars.world.build(wx, wy)
                tile != null && tile.isInsulated
            }
        }
    }

    var laser: TextureRegion by TextureRegionDelegate("${this.name}-laser", "laser")
    var laserEnd: TextureRegion by TextureRegionDelegate("${this.name}-laser-end", "laser-end")
    var laserRange: Float = 6f
    var maxNodes: Int = 3
    var autolink: Boolean = true
    var drawRange: Boolean = true
    var sameBlockConnection: Boolean = false
    var laserScale: Float = 0.25f
    var powerLayer: Float = Layer.power
    var laserColor1: Color = Color.white
    var laserColor2: Color? = IceColor.b4

    init {
        configurable = true
        consumesPower = false
        outputsPower = false
        canOverdrive = false
        swapDiagonalPlacement = true
        schematicPriority = -10
        drawDisabled = false
        envEnabled = envEnabled or Env.space
        destructible = true
        //nodes do not even need to update
        update = false

        buildType = Prov(::PowerNodeBuild)
        config(Int::class.javaObjectType) { entity: Building?, value: Int? ->
            val power = entity!!.power
            val other = Vars.world.build(value!!)
            val contains = power.links.contains(value)
            val valid = other != null && other.power != null
            if (contains) {
                //unlink
                power.links.removeValue(value)
                if (valid) other.power.links.removeValue(entity.pos())
                val newgraph = PowerGraph()
                //reflow from this point, covering all tiles on this side
                newgraph.reflow(entity)

                if (valid && other.power.graph !== newgraph) {
                    //create new graph for other end
                    val og = PowerGraph()
                    //reflow from other end
                    og.reflow(other)
                }
            } else if (linkValid(entity, other) && valid && power.links.size < maxNodes) {
                power.links.addUnique(other.pos())

                if (other.team === entity.team) {
                    other.power.links.addUnique(entity.pos())
                }

                power.graph.addGraph(other.power.graph)
            }
        }

        config(Array<Point2>::class.java) { tile: Building?, value: Array<Point2>? ->
            val old = IntSeq(tile!!.power.links)
            //clear old
            for (i in 0..<old.size) {
                configurations.get(Int::class.javaObjectType).get(tile, old.get(i))
            }
            //set new
            for (p in value!!) {
                configurations.get(Int::class.javaObjectType).get(tile, Point2.pack(p.x + tile.tileX(), p.y + tile.tileY()))
            }
        }
    }

    override fun setBars() {
        super.setBars()
        addBar("power", makePowerBalance())
        addBar("batteries", makeBatteryBalance())

        addBar("connections") { entity: Building? ->
            Bar({ Core.bundle.format("bar.powerlines", entity!!.power.links.size, maxNodes) },
                { Pal.items },
                { entity!!.power.links.size.toFloat() / maxNodes.toFloat() }
            )
        }
    }

    override fun setStats() {
        super.setStats()

        stats.add(Stat.powerRange, laserRange, StatUnit.blocks)
        stats.add(Stat.powerConnections, maxNodes.toFloat(), StatUnit.none)
    }

    override fun init() {
        super.init()

        clipSize = max(clipSize, laserRange * Vars.tilesize)
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        val tile = Vars.world.tile(x, y)

        if (tile == null || !autolink) return

        Lines.stroke(1f)
        Draw.color(Pal.placing)
        Drawf.circles(x * Vars.tilesize + offset, y * Vars.tilesize + offset, laserRange * Vars.tilesize)

        getPotentialLinks(tile, Vars.player.team()) { other: Building? ->
            Draw.color(laserColor1, Renderer.laserOpacity * 0.5f)
            drawLaser(x * Vars.tilesize + offset, y * Vars.tilesize + offset, other!!.x, other.y, size, other.block.size)
            Drawf.square(other.x, other.y, other.block.size * Vars.tilesize / 2f + 2f, Pal.place)
        }

        Draw.reset()
    }

    override fun changePlacementPath(points: Seq<Point2?>, rotation: Int) {
        Placement.calculateNodes(points, this, rotation) { point: Point2?, other: Point2? -> overlaps(Vars.world.tile(point!!.x, point.y), Vars.world.tile(other!!.x, other.y)) }
    }

    protected fun setupColor(satisfaction: Float) {
        Draw.color(Tmp.c1.set(laserColor1).lerp(laserColor2, (1f - satisfaction) * 0.86f + Mathf.absin(3f, 0.1f)).a(Renderer.laserOpacity))
    }

    fun drawLaser(x1: Float, y1: Float, x2: Float, y2: Float, size1: Int, size2: Int) {
        val angle1 = Angles.angle(x1, y1, x2, y2)
        val vx = Mathf.cosDeg(angle1)
        val vy = Mathf.sinDeg(angle1)
        val len1 = size1 * Vars.tilesize / 2f - 1.5f
        val len2 = size2 * Vars.tilesize / 2f - 1.5f

        Drawf.laser(laser, laserEnd, x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2, laserScale)
    }

    protected fun overlaps(srcx: Float, srcy: Float, other: Tile, otherBlock: Block, range: Float): Boolean {
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), Tmp.r1.setCentered(other.worldx() + otherBlock.offset, other.worldy() + otherBlock.offset,
            (otherBlock.size * Vars.tilesize).toFloat(), (otherBlock.size * Vars.tilesize).toFloat()))
    }

    protected fun overlaps(srcx: Float, srcy: Float, other: Tile, range: Float): Boolean {
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), other.getHitbox(Tmp.r1))
    }

    protected fun overlaps(src: Building, other: Building, range: Float): Boolean {
        return overlaps(src.x, src.y, other.tile, range)
    }

    protected fun overlaps(src: Tile, other: Tile, range: Float): Boolean {
        return overlaps(src.drawx(), src.drawy(), other, range)
    }

    fun overlaps(@Nullable src: Tile?, @Nullable other: Tile?): Boolean {
        if (src == null || other == null) return true
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, laserRange * Vars.tilesize), Tmp.r1.setSize((size * Vars.tilesize).toFloat()).setCenter(other.worldx() + offset, other.worldy() + offset))
    }

    protected fun getPotentialLinks(tile: Tile, team: Team, others: Cons<Building?>) {
        if (!autolink) return
        val valid = Boolf { other: Building? ->
            other != null && other.tile !== tile && other.block.connectedPower && other.power != null &&
                    (other.block.outputsPower || other.block.consumesPower || other.block is PowerNode) &&
                    overlaps(tile.x * Vars.tilesize + offset, tile.y * Vars.tilesize + offset, other.tile, laserRange * Vars.tilesize) && other.team === team && !graphs.contains(other.power.graph) && !insulated(tile, other.tile) && !(other is PowerNodeBuild && other.power.links.size >= (other.block as PowerNode).maxNodes) && !Structs.contains(Edges.getEdges(size)) { p: Point2? ->  //do not link to adjacent buildings
                val t = Vars.world.tile(tile.x + p!!.x, tile.y + p.y)
                t != null && t.build === other
            }
        }

        tempBuilds.clear()
        graphs.clear()
        //add conducting graphs to prevent double link
        for (p in Edges.getEdges(size)) {
            val other = tile.nearby(p)
            if (other != null && other.team() === team && other.build != null && other.build.power != null) {
                graphs.add(other.build.power.graph)
            }
        }

        if (tile.build != null && tile.build.power != null) {
            graphs.add(tile.build.power.graph)
        }
        val worldRange = laserRange * Vars.tilesize
        val tree = team.data().buildingTree
        tree?.intersect(tile.worldx() - worldRange, tile.worldy() - worldRange, worldRange * 2, worldRange * 2, Cons { build: Building? ->
            if (valid.get(build) && !tempBuilds.contains(build)) {
                tempBuilds.add(build)
            }
        })

        tempBuilds.sort(Comparator { a: Building?, b: Building? ->
            val type = -java.lang.Boolean.compare(a!!.block is PowerNode, b!!.block is PowerNode)
            if (type != 0) return@Comparator type
            a.dst2(tile).compareTo(b.dst2(tile))
        })

        returnInt = 0

        tempBuilds.each(valid) { t: Building? ->
            if (returnInt++ < maxNodes) {
                graphs.add(t!!.power.graph)
                others.get(t)
            }
        }
    }

    override fun drawPlanConfigTop(plan: BuildPlan, list: Eachable<BuildPlan?>) {
        val config = plan.config
        if (config is Array<*> && config.isArrayOf<Point2>()) {
            config as Array<Point2>
            setupColor(1f)
            for (point in config) {
                val px: Int = plan.x + point.x
                val py: Int = plan.y + point.y
                otherReq = null
                list.each(Cons { other: BuildPlan? ->
                    if (other!!.block != null && (px >= other.x - ((other.block.size - 1) / 2) && py >= other.y - ((other.block.size - 1) / 2) && px <= other.x + other.block.size / 2 && py <= other.y + other.block.size / 2)
                        && other !== plan && other.block.hasPower
                    ) {
                        otherReq = other
                    }
                })
                //uncomment for debugging connection translation issues in schematics
                //Draw.color(Color.red);
                //Lines.line(plan.drawx(), plan.drawy(), px * tilesize, py * tilesize);
                //Draw.color();
                if (otherReq == null || otherReq!!.block == null) continue
                otherReq?.let {
                    drawLaser(plan.drawx(), plan.drawy(), it.drawx(), it.drawy(), size, it.block.size)
                }
            }
            Draw.color()
        }
    }

    @JvmOverloads
    fun linkValid(tile: Building, link: Building?, checkMaxNodes: Boolean = true): Boolean {
        if (tile === link || link == null || !link.block.hasPower || !link.block.connectedPower || tile.team !== link.team || (sameBlockConnection && tile.block !== link.block)) return false

        if (overlaps(tile, link, laserRange * Vars.tilesize) || (link.block is PowerNode && overlaps(link, tile, (link.block as PowerNode).laserRange * Vars.tilesize))) {
            val block = link.block
            if (checkMaxNodes && block is PowerNode) {
                return link.power.links.size < block.maxNodes || link.power.links.contains(tile.pos())
            }
            return true
        }
        return false
    }

    open inner class PowerNodeBuild : Building() {
        override fun created() { // Called when one is placed/loaded in the world
            if (autolink && laserRange > maxRange) maxRange = laserRange

            super.created()
        }

        override fun placed() {
            if (Vars.net.client() || power.links.size > 0) return

            getPotentialLinks(tile, team) { other: Building? ->
                if (!power.links.contains(other!!.pos())) {
                    configureAny(other.pos())
                }
            }

            super.placed()
        }

        override fun dropped() {
            power.links.clear()
            updatePowerGraph()
        }

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if (linkValid(this, other)) {
                configure(other.pos())
                return false
            }

            if (this === other) { //double tapped
                if (other.power.links.size == 0) { //find links
                    val points = Seq<Point2?>()
                    getPotentialLinks(tile, team, Cons { link: Building ->
                        if (!insulated(this, link) && points.size < maxNodes) {
                            points.add(Point2(link.tileX() - tile.x, link.tileY() - tile.y))
                        }
                    })
                    configure(points.toArray<Any?>(Point2::class.java))
                } else { //clear links
                    configure(arrayOfNulls<Point2>(0))
                }
                deselect()
                return false
            }

            return true
        }

        override fun drawSelect() {
            super.drawSelect()

            if (!drawRange) return

            Lines.stroke(1f)

            Draw.color(IceColor.b4)
            Drawf.circles(x, y, laserRange * Vars.tilesize)
            Draw.reset()
        }

        override fun drawConfigure() {
            Drawf.circles(x, y, tile.block().size * Vars.tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f))

            if (drawRange) {
                Drawf.circles(x, y, laserRange * Vars.tilesize)

                run {
                    var x = (tile.x - laserRange - 2).toInt()
                    while (x <= tile.x + laserRange + 2) {
                        run {
                            var y = (tile.y - laserRange - 2).toInt()
                            while (y <= tile.y + laserRange + 2) {
                                val link = Vars.world.build(x, y)

                                if (link !== this && linkValid(this, link, false)) {
                                    val linked = linked(link)

                                    if (linked) {
                                        Drawf.square(link.x, link.y, link.block.size * Vars.tilesize / 2f + 1f, Pal.place)
                                    }
                                }
                                y++
                            }
                        }
                        x++
                    }
                }

                Draw.reset()
            } else {
                power.links.each { i: Int ->
                    val link = Vars.world.build(i)
                    if (link != null && linkValid(this, link, false)) {
                        Drawf.square(link.x, link.y, link.block.size * Vars.tilesize / 2f + 1f, Pal.place)
                    }
                }
            }
        }

        override fun draw() {
            super.draw()

            if (Mathf.zero(Renderer.laserOpacity) || isPayload || team === Team.derelict) return

            Draw.z(powerLayer)
            setupColor(power.graph.getSatisfaction())

            for (i in 0..<power.links.size) {
                val link = Vars.world.build(power.links.get(i))

                if (!linkValid(this, link)) continue

                if (link.block is PowerNode && link.id >= id) continue

                drawLaser(x, y, link.x, link.y, size, link.block.size)
            }

            Draw.reset()
        }

        protected fun linked(other: Building): Boolean {
            return power.links.contains(other.pos())
        }

        override fun config(): Array<Point2?> {
            val out = arrayOfNulls<Point2>(power.links.size)
            for (i in out.indices) {
                out[i] = Point2.unpack(power.links.get(i)).sub(tile.x.toInt(), tile.y.toInt())
            }
            return out
        }
    }
}