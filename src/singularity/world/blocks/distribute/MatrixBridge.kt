package singularity.world.blocks.distribute

import arc.Core
import arc.func.Cons
import arc.func.Cons2
import arc.func.Intc2
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Geometry
import arc.math.geom.Intersector
import arc.math.geom.Point2
import arc.struct.IntSeq
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import mindustry.Vars
import mindustry.content.Liquids
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Tile
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.modules.LiquidModule.LiquidConsumer
import singularity.Sgl
import singularity.Singularity
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.components.distnet.DistElementBlockComp
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.components.distnet.DistNetworkCoreComp
import singularity.world.meta.SglStat
import singularity.world.modules.SglLiquidModule
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class MatrixBridge(name: String) : DistNetBlock(name) {
    var effectColor: Color? = SglDrawConst.matrixNet
    var linkRegion: TextureRegion? = null
    var topRegion: TextureRegion? = null
    var linkRange: Int = 16
    var maxLinks: Int = 8
    var transportItemTime: Float = 1f
    var linkStoke: Float = 8f
    var crossLinking: Boolean = false
    var maxLiquidCapacity: Float = -1f

    init {
        configurable = true
        topologyUse = 0
        hasItems = true
        outputsLiquid = true
        hasLiquids = outputsLiquid
        isNetLinker = true
    }

    public override fun appliedConfig() {
        super.appliedConfig()

        config<IntSeq?, MatrixBridgeBuild?>(IntSeq::class.java, Cons2 { e: MatrixBridgeBuild?, seq: IntSeq? ->
            if (seq!!.get(0) != -1) {
                val p = Point2.unpack(seq.get(0))
                e!!.linkElementPos = p.set(e.tile.x + p.x, e.tile.y + p.y).pack()
            }
            if (seq.get(1) != -1) {
                val p = Point2.unpack(seq.get(1))
                e!!.linkNextPos = p.set(e.tile.x + p.x, e.tile.y + p.y).pack()
            }
        })

        config<Point2?, MatrixBridgeBuild?>(Point2::class.java, Cons2 { e: MatrixBridgeBuild?, p: Point2? ->
            val pos = p!!.pack()
            val target = Vars.world.build(pos)

            if (target is DistNetworkCoreComp) {
                e!!.linkNextPos = -1
            }
            if (target is MatrixBridgeBuild) {
                if (e!!.linkElement is DistNetworkCoreComp) {
                    e.linkElementPos = -1
                }

                if (target.linkNext === e) {
                    target.linkNextPos = -1
                    target.deLink(e)
                    target.linkNext = null
                } else e.linkNextLerp = 0f
                e.linkNextPos = if (e.linkNextPos == pos) -1 else target.pos()
            } else if (target is DistElementBuildComp) {
                e!!.linkElementPos = if (e.linkElementPos == pos) -1 else target.pos()
                e.linkElementLerp = 0f
            }
        })
    }

    public override fun init() {
        super.init()
        clipSize = max(clipSize, (linkRange * Vars.tilesize * 2).toFloat())
        if (maxLiquidCapacity == -1f) maxLiquidCapacity = liquidCapacity * 4
    }

    public override fun setStats() {
        super.setStats()
        stats.add(Stat.linkRange, linkRange.toFloat(), StatUnit.blocks)
        stats.add(SglStat.maxMatrixLinks, maxLinks.toFloat())
    }

    public override fun load() {
        super.load()
        linkRegion = Core.atlas.find(name + "_link", Singularity.getModAtlas("matrix_link_laser"))
        topRegion = Core.atlas.find(name + "_top", Singularity.getModAtlas("matrix_link_light"))
    }

    fun linkInlerp(origin: Tile?, other: Tile?, range: Float): Boolean {
        if (origin == null || other == null) return false
        if (crossLinking) {
            val xDistance = abs(origin.x - other.x)
            val yDistance = abs(origin.y - other.y)
            val linkLength = min(linkRange, if (other.block() is MatrixBridge) (other.block() as MatrixBridge).linkRange else linkRange)

            return (yDistance < linkLength + size / 2f + offset && origin.x == other.x && origin.y != other.y)
                    || (xDistance < linkLength + size / 2f + offset && origin.x != other.x && origin.y == other.y)
        }
        return Intersector.overlaps(Tmp.cr1.set(origin.drawx(), origin.drawy(), range), other.getHitbox(Tmp.r1))
    }

    override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
        val seq = req.config as IntSeq?
        if (seq == null) return
        val p = arrayOf<Point2?>(
            if (seq.get(0) == -1) null else Point2.unpack(seq.get(0)),
            if (seq.get(1) == -1) null else Point2.unpack(seq.get(1))
        )
        val links = arrayOfNulls<BuildPlan>(2)
        list.each(Cons { plan: BuildPlan? ->
            if (p[1] != null && plan!!.block is MatrixBridge) {
                if (p[1]!!.cpy().set(req.x + p[1]!!.x, req.y + p[1]!!.y).pack() == Point2.pack(plan.x, plan.y)) {
                    links[0] = plan
                }
            } else if (p[0] != null && plan!!.block is DistElementBlockComp) {
                if (p[0]!!.cpy().set(req.x + p[0]!!.x, req.y + p[0]!!.y).pack() == Point2.pack(plan.x, plan.y)) {
                    links[1] = plan
                }
            }
        })

        Draw.rect(topRegion, req.drawx(), req.drawy())

        for (plan in links) {
            if (plan != null) {
                SglDraw.drawLaser(
                    req.drawx(), req.drawy(),
                    plan.drawx(), plan.drawy(),
                    linkRegion,
                    null,
                    linkStoke
                )
            }
        }
    }

    fun doCanLink(origin: Tile, range: Float, cons: Cons<MatrixBridgeBuild?>) {
        Geometry.circle(origin.x.toInt(), origin.y.toInt(), (range * Vars.tilesize).toInt(), Intc2 { x: Int, y: Int ->
            val e = Vars.world.build(x, y)
            if (e is MatrixBridgeBuild && temps.add(e)) {
                cons.get(e)
            }
        })
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)

        if (crossLinking) {
            Tmp.v1.set(1f, 0f)
            for (i in 0..3) {
                val dx = x * Vars.tilesize + offset + Geometry.d4x(i) * size * Vars.tilesize / 2f
                val dy = y * Vars.tilesize + offset + Geometry.d4y(i) * size * Vars.tilesize / 2f

                Drawf.dashLine(
                    Pal.accent,
                    dx,
                    dy,
                    dx + Geometry.d4x(i) * linkRange * Vars.tilesize,
                    dy + Geometry.d4y(i) * linkRange * Vars.tilesize
                )

                for (d in 1..linkRange) {
                    Tmp.v1.setLength(d.toFloat())
                    val t = Vars.world.build(x + Tmp.v1.x.toInt(), y + Tmp.v1.y.toInt())

                    if (t != null && linkInlerp(Vars.world.tile(x, y), t.tile, (linkRange * Vars.tilesize).toFloat()) && (t.block is MatrixBridge)) {
                        Drawf.select(t.x, t.y, t.block.size * Vars.tilesize / 2f + 2f + Mathf.absin(Time.time, 4f, 1f), Pal.breakInvalid)
                    }
                }
                Tmp.v1.rotate90(1)
            }
        } else {
            Lines.stroke(1f)
            Draw.color(Pal.placing)
            Drawf.circles(x * Vars.tilesize + offset, y * Vars.tilesize + offset, (linkRange * Vars.tilesize).toFloat())
        }
    }

    inner class MatrixBridgeBuild : DistNetBuild() {
        var rand: Rand = Rand()
        var transportCounter: Float = 0f
        var linkNextPos: Int = -1
        var linkElementPos: Int = -1
        var linkNext: MatrixBridgeBuild? = null
        var linkElement: DistElementBuildComp? = null
        var linkNextLerp: Float = 0f
        var linkElementLerp: Float = 0f
        var drawEffs: Seq<EffTask> = Seq<EffTask>()
        var netEfficiency: Float = 0f

        override fun status(): BlockStatus? {
            return if (!enabled) BlockStatus.logicDisable else if (distributor!!.network.netValid()) BlockStatus.active else if (distributor!!.network.netStructValid()) if (distributor!!.network.topologyUsed <= distributor!!.network.totalTopologyCapacity) BlockStatus.noInput else BlockStatus.noOutput else consumer!!.status()
        }

        fun canLink(other: Building): Boolean {
            if (crossLinking) {
                if (other !is MatrixBridgeBuild) return false
                val linkLength = min(linkRange, (other.block as MatrixBridge).linkRange).toFloat()
                if ((other.block as MatrixBridge).crossLinking) {
                    val xDistance = abs(tileX() - other.tileX())
                    val yDistance = abs(tileY() - other.tileY())

                    if (!((yDistance < linkLength + block.size / 2f + block.offset && tileX() == other.tileX() && tileY() != other.tileY())
                                || (xDistance < linkLength + block.size / 2f + block.offset && tileX() != other.tileX() && tileY() == other.tileY()))
                    ) return false
                } else {
                    if (!Intersector.overlaps(Tmp.cr1.set(x, y, linkLength * Vars.tilesize), other.tile.getHitbox(Tmp.r1))) return false
                }
            } else {
                if (!this@MatrixBridge.linkInlerp(tile, other.tile, (linkRange * Vars.tilesize).toFloat())) return false
            }
            if (other is MatrixBridgeBuild && other.block === block) return true
            return other is DistElementBuildComp && linkable(other) && other.linkable(this)
        }

        override fun onProximityAdded() {
            super.onProximityAdded()
            updateNetLinked()
        }

        public override fun updateTile() {
            super.updateTile()
            if (linkNextPos != -1 && (linkNext == null || !linkNext!!.isAdded() || linkNextPos != linkNext!!.pos())) {
                if (linkNext != null) {
                    if (!linkNext!!.isAdded()) {
                        linkNextPos = -1
                    }
                    deLink(linkNext!!)
                    linkNext = null
                }
                val build = if (linkNextPos == -1) null else Vars.world.build(linkNextPos)
                if (build is MatrixBridgeBuild) {
                    linkNext = build
                    linkNextPos = linkNext!!.pos() //对齐偏移距离
                    link(linkNext!!)
                }
            } else if (linkNextPos == -1) {
                if (linkNext != null) {
                    deLink(linkNext!!)
                    linkNext = null
                }
                linkNextLerp = 0f
            }

            netEfficiency = Mathf.lerpDelta(netEfficiency, drawEff(), 0.02f)

            if (linkElementPos != -1 && (linkElement == null || !linkElement!!.building.isAdded() || linkElementPos != linkElement!!.building.pos())) {
                if (linkElement != null) {
                    if (!linkElement!!.building.isAdded()) {
                        linkElementPos = -1
                    }
                    deLink(linkElement!!)
                    linkElement = null
                }
                val build = if (linkElementPos == -1) null else Vars.world.build(linkElementPos)
                if (build is DistElementBuildComp) {
                    linkElement = build as DistElementBuildComp
                    linkElementPos = linkElement!!.building.pos() //对齐偏移距离
                    link(linkElement!!)
                }
            } else if (linkElementPos == -1) {
                if (linkElement != null) {
                    deLink(linkElement!!)
                    linkElement = null
                }
                linkElementLerp = 0f
            }

            if (linkElementPos != -1 && linkElement != null && linkElementLerp < 0.99f) {
                linkElementLerp = Mathf.lerpDelta(linkElementLerp, 1f, 0.04f)
            }

            if (linkNextPos != -1 && linkNext != null && linkNextLerp < 0.99f) {
                linkNextLerp = Mathf.lerpDelta(linkNextLerp, 1f, 0.04f)
            }

            if (linkNext == null) {
                doDump()
            } else if (!distributor!!.network.netValid() && consumeValid()) {
                doTransport(linkNext!!)
            }

            updateEff()
        }

        override fun pickedUp() {
            linkElementPos = -1
            linkNextPos = -1
            updateTile()
        }

        fun updateEff() {
            val itr = drawEffs.iterator()
            while (itr.hasNext()) {
                val eff = itr.next()
                if (eff.progress >= 1) {
                    itr.remove()
                    Pools.free(eff)
                }
                eff.update()
            }
            val scl = (Vars.renderer.getScale() - Vars.renderer.minZoom) / (Vars.renderer.maxZoom - Vars.renderer.minZoom)


            if (linkNext != null) {
                if (rand.random(1f) <= 0.05f * linkNextLerp * netEfficiency * Time.delta * scl) {
                    makeEff(x, y, linkNext!!.x, linkNext!!.y)
                }
            }

            if (linkElement != null) {
                if (rand.random(1f) <= 0.05f * linkElementLerp * netEfficiency * Time.delta * scl) {
                    if (linkElement is DistNetworkCoreComp) {
                        makeEff(x, y, linkElement!!.building.x, linkElement!!.building.y)
                    } else makeEff(linkElement!!.building.x, linkElement!!.building.y, x, y)
                }
            }
        }

        fun makeEff(fromX: Float, fromY: Float, toX: Float, toY: Float) {
            Tmp.v1.setAngle(rand.random(0f, 360f))
            Tmp.v1.setLength(rand.random(2f, 5f))

            drawEffs.add(
                EffTask.Companion.make(
                    fromX + Tmp.v1.x, fromY + Tmp.v1.y,
                    toX + Tmp.v1.x, toY + Tmp.v1.y,
                    rand.random(0.3f, 1.2f),
                    rand.random(0.125f, 0.4f),
                    rand.random(180).toFloat(),
                    rand.random(-0.6f, 0.6f),
                    effectColor
                )
            )
        }

        fun doDump() {
            dumpAccumulate()
            dumpLiquid()
        }

        fun doTransport(next: MatrixBridgeBuild) {
            transportCounter += delta() * consEfficiency()
            while (transportCounter >= transportItemTime) {
                val item = items.take()
                if (item != null) {
                    if (next.acceptItem(this, item)) {
                        next.handleItem(this, item)
                    } else {
                        items.add(item, 1)
                        items.undoFlow(item)
                    }
                }

                transportCounter -= transportItemTime
            }

            liquids.each(LiquidConsumer { l: Liquid?, a: Float ->
                moveLiquid(next, l)
            })
        }

        override fun onConfigureBuildTapped(other: Building?): Boolean {
            if (other == null) return true

            if (other === this) {
                if (linkNext != null) configure(Point2.unpack(linkNext!!.pos()))
                if (linkElement != null) configure(Point2.unpack(linkElement!!.building.pos()))
                return true
            }

            if (canLink(other)) {
                if (distributor!!.distNetLinks.size >= maxLinks
                    || other is MatrixBridgeBuild && other.distributor!!.distNetLinks.size >= (other.block as MatrixBridge).maxLinks
                ) return false

                if (other is DistElementBuildComp) {
                    configure(Point2.unpack(other.pos()))
                }
                return false
            }

            return true
        }

        override fun drawConfigure() {
            if (crossLinking) {
                Drawf.square(x, y, (size * Vars.tilesize).toFloat(), Pal.accent)

                if (linkNext != null) {
                    Tmp.v2.set(
                        Tmp.v1.set(linkNext!!.building.x, linkNext!!.building.y)
                            .sub(x, y)
                            .setLength(size * Vars.tilesize / 2f)
                    )
                        .setLength(linkNext!!.block.size * Vars.tilesize / 2f)

                    Drawf.square(
                        linkNext!!.building.x,
                        linkNext!!.building.y,
                        (linkNext!!.block.size * Vars.tilesize).toFloat(),
                        45f,
                        Pal.accent
                    )
                    Lines.stroke(3f, Pal.gray)
                    Lines.line(
                        x + Tmp.v1.x,
                        y + Tmp.v1.y,
                        linkNext!!.building.x - Tmp.v2.x,
                        linkNext!!.building.y - Tmp.v2.y
                    )
                    Lines.stroke(1f, Pal.accent)
                    Lines.line(
                        x + Tmp.v1.x,
                        y + Tmp.v1.y,
                        linkNext!!.building.x - Tmp.v2.x,
                        linkNext!!.building.y - Tmp.v2.y
                    )
                }
            } else {
                Drawf.circles(x, y, tile.block().size * Vars.tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f))
                Drawf.circles(x, y, (linkRange * Vars.tilesize).toFloat())
            }
            var last: Building? = null
            for (x in tile.x - linkRange - 2..tile.x + linkRange + 2) {
                for (y in tile.y - linkRange - 2..tile.y + linkRange + 2) {
                    val link = Vars.world.build(x, y)
                    if (last === link) continue
                    last = link
                    if (link != null && link !== this && canLink(link)) {
                        if (linkNext === link || linkElement === link) {
                            val radius = link.block.size * Vars.tilesize / 2f + 1f
                            Drawf.square(link.x, link.y, radius, Pal.place)
                            Tmp.v1.set(link.x, link.y).sub(this.x, this.y).setLength(radius)
                            Drawf.dashLine(
                                Pal.accent,
                                this.x + Tmp.v1.x, this.y + Tmp.v1.y,
                                link.x - Tmp.v1.x, link.y - Tmp.v1.y
                            )
                        } else {
                            if (link is MatrixBridgeBuild && canLink(link)) {
                                drawLinkable(link)
                            }
                        }
                    }
                }
            }

            Draw.reset()
        }

        private fun drawLinkable(link: MatrixBridgeBuild) {
            if (link.linkNext !== this) {
                val radius = link.block.size * Vars.tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f)
                Tmp.v1.set(0f, 1f).setLength(radius + 4).setAngle(Time.time)
                Drawf.circles(link.x, link.y, radius)
                for (i in 0..3) {
                    Draw.color(Pal.gray)
                    Fill.poly(link.x + Tmp.v1.x, link.y + Tmp.v1.y, 3, 3.5f, Time.time + i * 90 + 60)
                    Draw.color(Pal.accent)
                    Fill.poly(link.x + Tmp.v1.x, link.y + Tmp.v1.y, 3, 1.5f, Time.time + i * 90 + 60)
                    Tmp.v1.rotate(90f)
                }
            } else {
                Drawf.select(link.x, link.y, link.block.size * Vars.tilesize / 2f + 2f + Mathf.absin(Time.time, 4f, 1f), Pal.breakInvalid)
            }
        }

        public override fun draw() {
            Draw.rect(region, x, y)

            Drawf.light(x, y, 16f, Liquids.cryofluid.color, linkNextLerp * 0.5f)
            val alp = 0.3f + 0.7f * netEfficiency
            Draw.alpha(alp * max(linkElementLerp, linkNextLerp))
            Draw.z(if (netEfficiency > 0.3f) Layer.effect else Layer.blockBuilding + 5f)
            Draw.rect(topRegion, x, y)
            if (linkNext != null) {
                Draw.alpha(alp * linkNextLerp)
                Draw.rect(topRegion, linkNext!!.building.x, linkNext!!.building.y)
                Draw.z(Layer.power)
                SglDraw.drawLaser(
                    x, y,
                    linkNext!!.building.x, linkNext!!.building.y,
                    linkRegion,
                    null,
                    linkStoke * linkNextLerp
                )
            }
            if (linkElement != null) {
                Draw.alpha(alp * linkElementLerp)
                Draw.z(if (netEfficiency > 0.3f) Layer.effect else Layer.blockBuilding + 5f)
                Draw.rect(topRegion, linkElement!!.building.x, linkElement!!.building.y)
                Draw.z(Layer.power)
                SglDraw.drawLaser(
                    x, y,
                    linkElement!!.building.x, linkElement!!.building.y,
                    linkRegion,
                    null,
                    linkStoke * linkElementLerp
                )
            }

            drawEffect()
        }

        fun drawEff(): Float {
            return if (distributor!!.network.netStructValid()) distributor!!.network.netEfficiency() else consEfficiency()
        }

        fun drawEffect() {

            Draw.z(Layer.effect)
            for (eff in drawEffs) {
                eff.draw()
            }
        }

        public override fun acceptItem(source: Building, item: Item?): Boolean {
            return source.interactable(team) && items.get(item) < itemCapacity && (source.block === block || linkNext != null)
        }

        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            return source.interactable(team) && liquids.get(liquid) < liquidCapacity && (liquids as SglLiquidModule).total() < maxLiquidCapacity && (source.block === block || linkNext != null)
        }

        public override fun config(): IntSeq? {
            val t = if (linkElementPos == -1) null else Point2.unpack(linkElementPos)
            val m = if (linkNextPos == -1) null else Point2.unpack(linkNextPos)
            return IntSeq.with(
                if (t == null) -1 else t.set(t.x - tile.x, t.y - tile.y).pack(),
                if (m == null) -1 else m.set(m.x - tile.x, m.y - tile.y).pack()
            )
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            linkNextLerp = read.f()
            linkElementLerp = read.f()
            transportCounter = read.f()
            linkNextPos = read.i()
            linkElementPos = read.i()
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(linkNextLerp)
            write.f(linkElementLerp)
            write.f(transportCounter)
            write.i(linkNextPos)
            write.i(linkElementPos)
        }
    }

    class EffTask : Poolable {
        var fromX: Float = 0f
        var fromY: Float = 0f
        var toX: Float = 0f
        var toY: Float = 0f
        var length: Float = 0f
        var radius: Float = 0f
        var speed: Float = 0f
        var angleSpeed: Float = 0f
        var rotate: Float = 0f
        var progress: Float = 0f
        var color: Color? = null

        fun update() {
            progress += if (length == 0f) 0f else speed / length * Time.delta
            rotate += angleSpeed * Time.delta
        }

        fun draw() {
            Tmp.v1.set(toX - fromX, toY - fromY)
            Tmp.v1.scl(progress)

            Draw.color(color)
            Draw.alpha(Mathf.clamp((if (progress > 0.5) (1 - progress) else progress) / 0.15f))
            Fill.square(fromX + Tmp.v1.x, fromY + Tmp.v1.y, radius, rotate)
        }

        override fun reset() {
            toY = 0f
            toX = toY
            fromY = toX
            fromX = fromY
            length = 0f
            radius = 0f
            speed = 0f
            angleSpeed = 0f

            progress = 0f
            rotate = progress

            color = null
        }

        companion object {
            fun make(fromX: Float, fromY: Float, toX: Float, toY: Float, radius: Float, speed: Float, defAngle: Float, angleSpeed: Float, color: Color?): EffTask {
                val res = Pools.obtain<EffTask?>(EffTask::class.java, Prov { EffTask() })
                res.fromX = fromX
                res.fromY = fromY
                res.toX = toX
                res.toY = toY
                res.length = Mathf.len(toX - fromX, toY - fromY)
                res.radius = radius
                res.speed = speed
                res.rotate = defAngle
                res.angleSpeed = angleSpeed
                res.color = color

                return res
            }
        }
    }

    companion object {
        private val temps = ObjectSet<MatrixBridgeBuild?>()
    }
}