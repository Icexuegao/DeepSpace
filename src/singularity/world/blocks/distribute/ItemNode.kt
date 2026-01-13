package singularity.world.blocks.distribute

import arc.Core
import arc.func.*
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.Seq
import arc.util.Align
import arc.util.Eachable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.core.Renderer
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.entities.TargetPriority
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.input.Placement
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.Singularity
import singularity.ui.tables.DistTargetConfigTable
import singularity.world.blocks.SglBlock
import singularity.world.distribution.GridChildType
import universecore.components.blockcomp.Takeable
import universecore.util.DataPackable
import kotlin.math.abs
import kotlin.math.max

open class ItemNode(name: String) : SglBlock(name) {
    val timerCheckMoved: Int = timers++
    var range: Int = 0
    var transportTime: Float = 2f
    var endRegion: TextureRegion? = null
    var bridgeRegion: TextureRegion? = null
    var arrowRegion: TextureRegion? = null
    var fadeIn: Boolean = true
    var pulse: Boolean = false
    var arrowSpacing: Float = 4f
    var arrowOffset: Float = 2f
    var arrowPeriod: Float = 0.4f
    var arrowTimeScl: Float = 6.2f
    var lastBuild: ItemNodeBuild? = null
    var maxItemCapacity: Int = 40
    var siphon: Boolean = false

    init {
        update = true
        solid = true
        underBullets = true
        hasPower = true
        conductivePower = false
        itemCapacity = 10
        outputItems = true
        configurable = true
        hasItems = true
        unloadable = false
        allowConfigInventory = false
        group = BlockGroup.transportation
        noUpdateDisabled = true
        copyConfig = false
        priority = TargetPriority.transport

        config<Int?, ItemNodeBuild?>(Int::class.java, Cons2 { tile: ItemNodeBuild?, i: Int? -> tile!!.link = i!! })
    }

    public override fun parseConfigObjects(b: SglBuilding?, obj: Any?) {
        super.parseConfigObjects(b, obj)
        val e = b as ItemNodeBuild
        if (obj is TargetConfigure) {
            e.config = if (obj.isClear()) null else obj
            e.link = if (obj.offsetPos != 0) Point2.unpack(obj.offsetPos).add(e.tileX(), e.tileY()).pack() else e.link
        }
    }

    override fun pointConfig(config: Any?, transformer: Cons<Point2?>): Any? {
        if (config is ByteArray && DataPackable.readObject<DataPackable?>(config) is TargetConfigure) {
            val cfg = DataPackable.readObject<DataPackable?>(config) as TargetConfigure
            cfg.configHandle(transformer)
            return cfg.pack()
        }
        return config
    }

    override fun drawPlanConfigTop(plan: BuildPlan, list: Eachable<BuildPlan?>) {
        otherReq = null
        list.each(Cons { other: BuildPlan? ->
            if (other!!.block === this && plan !== other && plan.config is Point2 && (plan.config as Point2).equals(other.x - plan.x, other.y - plan.y)) {
                otherReq = other
            }
        })

        if (otherReq != null) {
            drawBridge(plan, otherReq!!.drawx(), otherReq!!.drawy(), 0f)
        }
    }

    public override fun setStats() {
        super.setStats()
        stats.remove(Stat.itemCapacity)
        stats.add(Stat.linkRange, range.toFloat(), StatUnit.blocks)
        stats.add(Stat.itemCapacity, Core.bundle.format("infos.mixedItemCapacity", itemCapacity, maxItemCapacity))
        stats.add(Stat.itemsMoved, 60 / transportTime, StatUnit.itemsSecond)
    }

    public override fun load() {
        super.load()
        endRegion = Core.atlas.find(name + "_end")
        bridgeRegion = Core.atlas.find(name + "_bridge")
        arrowRegion = Core.atlas.find(name + "_arrow")
    }

    fun drawBridge(req: BuildPlan, ox: Float, oy: Float, flip: Float) {
        if (Mathf.zero(Renderer.bridgeOpacity)) return
        Draw.alpha(Renderer.bridgeOpacity)

        Lines.stroke(8f)

        Tmp.v1.set(ox, oy).sub(req.drawx(), req.drawy()).setLength(Vars.tilesize / 2f)

        Lines.line(
            bridgeRegion,
            req.drawx() + Tmp.v1.x,
            req.drawy() + Tmp.v1.y,
            ox - Tmp.v1.x,
            oy - Tmp.v1.y, false
        )

        Draw.rect(
            arrowRegion, (req.drawx() + ox) / 2f, (req.drawy() + oy) / 2f,
            Angles.angle(req.drawx(), req.drawy(), ox, oy) + flip
        )

        Draw.reset()
    }

    public override fun setBars() {
        super.setBars()
        removeBar("items")
        addBar<Building?>("items", Func { entity: Building? ->
            Bar(
                Prov { Core.bundle.format("bar.items", entity!!.items.total()) },
                Prov { Pal.items },
                Floatp { entity!!.items.total().toFloat() / maxItemCapacity })
        }
        )
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)
        val link = findLink(x, y)

        for (i in 0..3) {
            Drawf.dashLine(
                Pal.placing,
                x * Vars.tilesize + Geometry.d4[i].x * (Vars.tilesize / 2f + 2),
                y * Vars.tilesize + Geometry.d4[i].y * (Vars.tilesize / 2f + 2),
                (x * Vars.tilesize + Geometry.d4[i].x * (range) * Vars.tilesize).toFloat(),
                (y * Vars.tilesize + Geometry.d4[i].y * (range) * Vars.tilesize).toFloat()
            )
        }

        Draw.reset()
        Draw.color(Pal.placing)
        Lines.stroke(1f)
        if (link != null && abs(link.x - x) + abs(link.y - y) > 1) {
            val rot = link.absoluteRelativeTo(x, y).toInt()
            val w = (if (link.x.toInt() == x) Vars.tilesize else abs(link.x - x) * Vars.tilesize - Vars.tilesize).toFloat()
            val h = (if (link.y.toInt() == y) Vars.tilesize else abs(link.y - y) * Vars.tilesize - Vars.tilesize).toFloat()
            Lines.rect((x + link.x) / 2f * Vars.tilesize - w / 2f, (y + link.y) / 2f * Vars.tilesize - h / 2f, w, h)

            Draw.rect("bridge-arrow", (link.x * Vars.tilesize + Geometry.d4(rot).x * Vars.tilesize).toFloat(), (link.y * Vars.tilesize + Geometry.d4(rot).y * Vars.tilesize).toFloat(), (link.absoluteRelativeTo(x, y) * 90).toFloat())
        }
        Draw.reset()
    }

    @JvmOverloads
    fun linkValid(tile: Tile?, other: Tile?, checkDouble: Boolean = true): Boolean {
        if (other == null || tile == null || !positionsValid(tile.x.toInt(), tile.y.toInt(), other.x.toInt(), other.y.toInt())) return false

        return ((other.block() === tile.block() && tile.block() === this) || (tile.block() !is ItemNode && other.block() === this))
                && (other.team() === tile.team() || tile.block() !== this)
                && (!checkDouble || (other.build as ItemNodeBuild).link != tile.pos())
    }

    fun positionsValid(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        if (x1 == x2) {
            return abs(y1 - y2) <= range
        } else if (y1 == y2) {
            return abs(x1 - x2) <= range
        } else {
            return false
        }
    }

    fun findLink(x: Int, y: Int): Tile? {
        val tile = Vars.world.tile(x, y)
        if (tile != null && lastBuild != null && linkValid(tile, lastBuild!!.tile) && lastBuild!!.tile !== tile && lastBuild!!.link == -1) {
            return lastBuild!!.tile
        }
        return null
    }

    public override fun init() {
        super.init()
        updateClipRadius((range + 0.5f) * Vars.tilesize)
    }

    override fun handlePlacementLine(plans: Seq<BuildPlan>) {
        for (i in 0..<plans.size - 1) {
            val cur = plans.get(i)
            val next = plans.get(i + 1)
            if (positionsValid(cur.x, cur.y, next.x, next.y)) {
                cur.config = Point2(next.x - cur.x, next.y - cur.y)
            }
        }
    }

    override fun changePlacementPath(points: Seq<Point2?>, rotation: Int) {
        Placement.calculateNodes(points, this, rotation, Boolf2 { point: Point2?, other: Point2? -> max(abs(point!!.x - other!!.x), abs(point.y - other.y)) <= range })
    }

    inner class ItemNodeBuild : SglBuilding(), Takeable {
        var config: TargetConfigure? = null
        var link: Int = -1
        var incoming: IntSeq = IntSeq(false, 4)
        var warmup: Float = 0f
        var time: Float = -8f
        var timeSpeed: Float = 0f
        var wasMoved: Boolean = false
        var moved: Boolean = false
        var transportCounter: Float = 0f
        var itemTakeCursor: Int = 0
        var show: Runnable = Runnable {}
        var close: Runnable = Runnable {}
        var showing: Boolean = false

        override fun pickedUp() {
            link = -1
        }

        override fun playerPlaced(config: Any?) {
            super.playerPlaced(config)
            val link = findLink(tile.x.toInt(), tile.y.toInt())
            if (linkValid(tile, link) && this.link != link!!.pos() && !proximity.contains(link.build)) {
                link.build.configure(tile.pos())
            }

            lastBuild = this
        }

        override fun drawSelect() {
            if (linkValid(tile, Vars.world.tile(link))) {
                drawInput(Vars.world.tile(link))
            }

            incoming.each(Intc { pos: Int -> drawInput(Vars.world.tile(pos)) })

            Draw.reset()
        }

        private fun drawInput(other: Tile) {
            if (!linkValid(tile, other, false)) return
            val linked = other.pos() == link

            Tmp.v2.trns(tile.angleTo(other), 2f)
            val tx = tile.drawx()
            val ty = tile.drawy()
            val ox = other.drawx()
            val oy = other.drawy()
            val alpha = abs((if (linked) 100 else 0) - (Time.time * 2f) % 100f) / 100f
            val x = Mathf.lerp(ox, tx, alpha)
            val y = Mathf.lerp(oy, ty, alpha)
            val otherLink = if (linked) other else tile
            val rel = (if (linked) tile else other).absoluteRelativeTo(otherLink.x.toInt(), otherLink.y.toInt()).toInt()
            //draw "background"
            Draw.color(Pal.gray)
            Lines.stroke(2.5f)
            Lines.square(ox, oy, 2f, 45f)
            Lines.stroke(2.5f)
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y)
            //draw foreground colors
            Draw.color(if (linked) Pal.place else Pal.accent)
            Lines.stroke(1f)
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y)

            Lines.square(ox, oy, 2f, 45f)
            Draw.mixcol(Draw.getColor(), 1f)
            Draw.color()
            Draw.rect(arrowRegion, x, y, (rel * 90).toFloat())
            Draw.mixcol()
        }

        override fun drawConfigure() {
            Drawf.select(x, y, tile.block().size * Vars.tilesize / 2f + 2f, Pal.accent)

            for (i in 1..range) {
                for (j in 0..3) {
                    val other = tile.nearby(Geometry.d4[j].x * i, Geometry.d4[j].y * i)
                    if (linkValid(tile, other)) {
                        val linked = other.pos() == link

                        Drawf.select(
                            other.drawx(), other.drawy(),
                            other.block().size * Vars.tilesize / 2f + 2f + (if (linked) 0f else Mathf.absin(Time.time, 4f, 1f)), if (linked) Pal.place else Pal.breakInvalid
                        )
                    }
                }
            }
        }

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if (other === this) {
                if (!showing) {
                    show.run()
                    showing = true
                } else {
                    close.run()
                    showing = false
                }
                return false
            }
            //reverse connection
            if (other is ItemNodeBuild && other.link == pos()) {
                configure(other.pos())
                other.configure(-1)
                return true
            }

            if (linkValid(tile, other.tile)) {
                if (link == other.pos()) {
                    configure(-1)
                } else {
                    configure(other.pos())
                }
                return false
            }
            return true
        }

        fun checkIncoming() {
            var idx = 0
            while (idx < incoming.size) {
                val i = incoming.items[idx]
                val other = Vars.world.tile(i)
                if (!linkValid(tile, other, false) || (other.build as ItemNodeBuild).link != tile.pos()) {
                    incoming.removeIndex(idx)
                    idx--
                }
                idx++
            }
        }

        fun updateTransport(other: Building) {
            transportCounter += consEfficiency() * delta()
            while (transportCounter >= transportTime) {
                val items = Vars.content.items()
                var any = false
                var i = 0
                while (transportCounter >= transportTime && i < items.size) {
                    itemTakeCursor = (itemTakeCursor + 1) % items.size
                    val id = itemTakeCursor
                    if (this.items.get(id) <= 0) {
                        i++
                        continue
                    }
                    val item = items.get(id)
                    if (other.acceptItem(this, item)) {
                        this.items.remove(item, 1)
                        other.handleItem(this, item)
                        transportCounter -= transportTime
                        moved = true
                        any = true
                    }
                    i++
                }
                if (!any) transportCounter %= transportTime
            }
        }

        public override fun draw() {
            super.draw()

            Draw.z(Layer.power)
            val other = Vars.world.tile(link)
            if (!linkValid(tile, other)) return

            if (Mathf.zero(Renderer.bridgeOpacity)) return
            val i = relativeTo(other.x.toInt(), other.y.toInt()).toInt()

            if (pulse) {
                Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6f, 0.07f))
            }
            val warmup = if (hasPower) this.warmup else 1f

            Draw.alpha((if (fadeIn) max(warmup, 0.25f) else 1f) * Renderer.bridgeOpacity)

            Draw.rect(endRegion, x, y, (i * 90 + 90).toFloat())
            Draw.rect(endRegion, other.drawx(), other.drawy(), (i * 90 + 270).toFloat())

            Lines.stroke(8f)

            Tmp.v1.set(x, y).sub(other.worldx(), other.worldy()).setLength(Vars.tilesize / 2f).scl(-1f)

            Lines.line(
                bridgeRegion,
                x + Tmp.v1.x,
                y + Tmp.v1.y,
                other.worldx() - Tmp.v1.x,
                other.worldy() - Tmp.v1.y, false
            )
            val dist = max(abs(other.x - tile.x), abs(other.y - tile.y)) - 1

            Draw.color()
            val arrows = (dist * Vars.tilesize / arrowSpacing).toInt()
            val dx = Geometry.d4x(i)
            val dy = Geometry.d4y(i)

            for (a in 0..<arrows) {
                Draw.alpha(Mathf.absin(a - time / arrowTimeScl, arrowPeriod, 1f) * warmup * Renderer.bridgeOpacity)
                Draw.rect(
                    arrowRegion,
                    x + dx * (Vars.tilesize / 2f + a * arrowSpacing + arrowOffset),
                    y + dy * (Vars.tilesize / 2f + a * arrowSpacing + arrowOffset),
                    i * 90f
                )
            }

            Draw.reset()
        }

        override fun canDumpLiquid(to: Building, liquid: Liquid?): Boolean {
            return checkDump(to)
        }

        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            return hasLiquids && team === source.team &&
                    (liquids.current() === liquid || liquids.get(liquids.current()) < 0.2f) &&
                    checkAccept(source, Vars.world.tile(link))
        }

        protected fun checkAccept(source: Building, other: Tile?): Boolean {
            if (tile == null || linked(source)) return true

            if (linkValid(tile, other)) {
                val rel = relativeTo(other).toInt()
                val rel2 = relativeTo(Edges.getFacingEdge(source, this)).toInt()

                return rel != rel2
            }

            return false
        }

        protected fun linked(source: Building?): Boolean {
            return source is ItemNodeBuild && linkValid(source.tile, tile) && source.link == pos()
        }

        override fun canDump(to: Building, item: Item?): Boolean {
            return checkDump(to)
        }

        protected fun checkDump(to: Building): Boolean {
            val other = Vars.world.tile(link)
            if (!linkValid(tile, other)) {
                val edge = Edges.getFacingEdge(to.tile, tile)
                val i = relativeTo(edge.x.toInt(), edge.y.toInt()).toInt()

                for (j in 0..<incoming.size) {
                    val v = incoming.items[j]
                    if (relativeTo(Point2.x(v).toInt(), Point2.y(v).toInt()).toInt() == i) {
                        return false
                    }
                }
                return true
            }
            val rel = relativeTo(other.x.toInt(), other.y.toInt()).toInt()
            val rel2 = relativeTo(to.tileX(), to.tileY()).toInt()

            return rel != rel2
        }

        public override fun shouldConsume(): Boolean {
            return linkValid(tile, Vars.world.tile(link)) && enabled
        }

        override fun buildConfiguration(table: Table) {
            showing = false
            table.table(Cons { t: Table? ->
                t!!.visible = false
                t.setOrigin(Align.center)

                t.add().width(45f)
                t.center().table(Tex.pane).get().add<DistTargetConfigTable?>(
                    DistTargetConfigTable(
                        0,
                        config,
                        if (siphon) arrayOf<GridChildType>(GridChildType.output, GridChildType.acceptor, GridChildType.input) else arrayOf<GridChildType>(GridChildType.output, GridChildType.acceptor),
                        arrayOf<ContentType>(ContentType.item),
                        true,
                        { c: TargetConfigure? ->
                            c!!.offsetPos = 0
                            configure(c.pack())
                        },
                        { Vars.control.input.config.hideConfig() }
                    )).fill().center()
                t.top().button(Icon.info, Styles.grayi, 32f, Runnable {
                  //  Sgl.ui.document.showDocument("", MarkdownStyles.defaultMD, Singularity.getDocument("matrix_grid_config_help.md"))
                }).size(45f).top()

                show = Runnable {
                    t.visible = true
                    t.pack()
                    t.setTransform(true)
                    t.actions(
                        Actions.scaleTo(0f, 1f),
                        Actions.visible(true),
                        Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out)
                    )
                }
                close = Runnable {
                    t.actions(
                        Actions.scaleTo(1f, 1f),
                        Actions.scaleTo(0f, 1f, 0.07f, Interp.pow3Out),
                        Actions.visible(false)
                    )
                }
            }).fillY()
        }

        override fun updateTile() {
            if (timer(timerCheckMoved, 30f)) {
                wasMoved = moved
                moved = false
            }
            //smooth out animation, so it doesn't stop/start immediately
            timeSpeed = Mathf.approachDelta(timeSpeed, if (wasMoved) 1f else 0f, 1f / 60f)

            time += timeSpeed * delta()

            checkIncoming()

            if (config != null && config!!.priority > 0) {
                doDump()
                if (siphon) doSiphon()
            }
            val other = Vars.world.tile(link)
            if (!linkValid(tile, other)) {
                warmup = 0f
            } else {
                val inc = (other.build as ItemNodeBuild).incoming
                val pos = tile.pos()
                if (!inc.contains(pos)) {
                    inc.add(pos)
                }

                warmup = Mathf.approachDelta(warmup, efficiency, 1f / 30f)
                updateTransport(other.build)
            }

            if ((config != null || !linkValid(tile, other)) && (config == null || config!!.priority <= 0)) {
                if (siphon) doSiphon()
                doDump()
            }
        }

        fun doSiphon() {
            if (config == null) return
            for (con in config!!.get(GridChildType.input, ContentType.item)) {
                val item = con as Item?
                val other = getNext(
                    "siphonItem",
                    Boolf { e: Building? ->
                        e!!.interactable(team)
                                && e.block.hasItems
                                && e.items.has(item)
                                && config!!.directValid(GridChildType.input, item, getDirectBit(e))
                    })

                if (other == null || !(hasItems && items.get(item) < itemCapacity && items.total() < maxItemCapacity)) return
                other.removeStack(item, 1)
                handleItem(other, item)
            }
        }

        fun doDump() {
            if (config == null || config!!.isClear()) {
                dumpAccumulate()
            } else {
                for (content in config!!.get(GridChildType.output, ContentType.item)) {
                    val i = content as Item?
                    if (items.get(i) <= 0) continue
                    val next = getNext(
                        "items",
                        Boolf { e: Building? -> e!!.interactable(team) && config!!.directValid(GridChildType.output, i, getDirectBit(e)) && e.acceptItem(this, i) })
                    if (next == null) continue

                    items.remove(i, 1)
                    next.handleItem(this, i)
                }
            }
        }

        public override fun acceptItem(source: Building, item: Item?): Boolean {
            return hasItems && team === source.team && items.get(item) < itemCapacity && items.total() < maxItemCapacity && checkAccept(source, Vars.world.tile(link), item)
        }

        protected fun checkAccept(source: Building, other: Tile?, content: UnlockableContent?): Boolean {
            if (tile == null || linked(source) || config == null) return true

            if (linkValid(tile, other)) {
                return config!!.directValid(GridChildType.acceptor, content, getDirectBit(source))
            }

            return false
        }

        protected fun getDirectBit(e: Building): Byte {
            val dir = relativeTo(Edges.getFacingEdge(e, this))
            return (if (dir.toInt() == 0) 1 else if (dir.toInt() == 1) 2 else if (dir.toInt() == 2) 4 else if (dir.toInt() == 3) 8 else 0).toByte()
        }

        public override fun config(): Any? {
            val res = if (config == null) TargetConfigure() else config!!.clone()
            res.offsetPos = Point2.unpack(link).sub(tile.x.toInt(), tile.y.toInt()).pack()
            return res.pack()
        }

        public override fun write(write: Writes) {
            super.write(write)
            write.i(link)
            write.f(warmup)
            write.i(itemTakeCursor)
            write.b(incoming.size)

            for (i in 0..<incoming.size) {
                write.i(incoming.items[i])
            }

            write.bool(wasMoved || moved)
            val b = if (config == null) EMP else config!!.pack()
            write.i(b.size)
            if (b.size > 0) write.b(b)
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            link = read.i()
            warmup = read.f()
            itemTakeCursor = read.i()
            val links = read.b()
            for (i in 0..<links) {
                incoming.add(read.i())
            }

            moved = read.bool()
            wasMoved = moved
            val len = read.i()
            if (len == 0) return
            config = TargetConfigure()
            config!!.read(read.b(len))
        }
    }

    companion object {
        val EMP: ByteArray = ByteArray(0)
        private var otherReq: BuildPlan? = null
    }
}