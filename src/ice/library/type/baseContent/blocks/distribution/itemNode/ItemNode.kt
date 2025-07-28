package ice.library.type.baseContent.blocks.distribution.itemNode

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.math.geom.Vec2
import arc.struct.IntSeq
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.IFiles.findPng
import ice.library.type.baseContent.blocks.abstractBlocks.IceBlock
import ice.library.scene.texs.Colors.b4
import mindustry.Vars
import mindustry.core.Renderer
import mindustry.entities.TargetPriority
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.input.Placement
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Tile
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.BuildVisibility
import mindustry.world.meta.Env
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class ItemNode(name: String) : IceBlock(name) {
    companion object {
        private var otherReq: BuildPlan? = null
    }

    val timerCheckMoved: Int = timers++
    var rangeb = 10
    var transportTime = 2f
    val endRegion: TextureRegion=   findPng("$name-end")
    var bridgeRegion: TextureRegion = findPng("$name-bridge")
    var arrowRegion: TextureRegion = findPng("$name-arrow")
    var topRegion: TextureRegion = findPng("$name-top")
    var bottomRegion: TextureRegion = findPng("$name-bottom")
    var fadeIn = true
    var pulse = false
    var arrowSpacing = 4f
    var arrowOffset = 2f
    var arrowPeriod = 0.4f
    var arrowTimeScl = 6.2f
    var bridgeWidth = 6.5f

    //for autolink
    var lastBuild: ItemNodeBuild? = null

    init {
        hasItems = false
        hasLiquids = true
        outputsLiquid = true
        canOverdrive = false
        group = BlockGroup.liquids
        envEnabled = Env.any
        size = 1
        buildVisibility = BuildVisibility.shown
        update = true
        solid = true
        underBullets = true
        hasPower = true
        hasLiquids = true
        liquidCapacity = 10f
        itemCapacity = 10
        configurable = true
        hasItems = true
        unloadable = false
        noUpdateDisabled = true
        copyConfig = false
        //disabled as to not be annoying
        allowConfigInventory = false
        priority = TargetPriority.transport
        //point2 config is relative
        buildType = Prov(::ItemNodeBuild)
        config(Point2::class.java) { tile: ItemNodeBuild, i: Point2 ->
            tile.link = Point2.pack(i.x + tile.tileX(), i.y + tile.tileY())
        }
        //integer is not
        config(Int::class.javaObjectType) { tile: ItemNodeBuild, i: Int -> tile.link = i }
    }

    override fun drawPlanConfigTop(plan: BuildPlan, list: Eachable<BuildPlan>) {
        otherReq = null
        list.each { other: BuildPlan ->
            if (other.block === this && plan !== other && plan.config is Point2 && (plan.config as Point2).equals(
                    other.x - plan.x, other.y - plan.y)
            ) {
                otherReq = other
            }
        }

        if (otherReq != null) {
            drawBridge(plan, otherReq!!.drawx(), otherReq!!.drawy(), 0f)
        }
    }

    private fun drawBridge(req: BuildPlan, ox: Float, oy: Float, flip: Float) {
        if (Mathf.zero(Renderer.bridgeOpacity)) return
        Draw.alpha(Renderer.bridgeOpacity)

        Lines.stroke(bridgeWidth)

        Tmp.v1.set(ox, oy).sub(req.drawx(), req.drawy()).setLength(Vars.tilesize / 2f)

        Lines.line(bridgeRegion, req.drawx() + Tmp.v1.x, req.drawy() + Tmp.v1.y, ox - Tmp.v1.x, oy - Tmp.v1.y, false)

        Draw.rect(arrowRegion, (req.drawx() + ox) / 2f, (req.drawy() + oy) / 2f,
            Angles.angle(req.drawx(), req.drawy(), ox, oy) + flip)

        Draw.reset()
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)
        val link: Tile? = findLinklastBuild(x, y)

        for (i in 0..3) {
            Drawf.dashLine(Pal.placing, x * Vars.tilesize + Geometry.d4[i].x * (Vars.tilesize / 2f + 2),
                y * Vars.tilesize + Geometry.d4[i].y * (Vars.tilesize / 2f + 2),
                (x * Vars.tilesize + Geometry.d4[i].x * (rangeb) * Vars.tilesize).toFloat(),
                (y * Vars.tilesize + Geometry.d4[i].y * (rangeb) * Vars.tilesize).toFloat())
        }

        Draw.reset()
        Draw.color(Pal.placing)
        Lines.stroke(1f)
        if (link != null && abs(link.x - x) + abs(link.y - y) > 1) {
            val w: Float = (if (link.x.toInt() == x) Vars.tilesize else abs(
                (link.x - x).toDouble()) * Vars.tilesize - Vars.tilesize).toFloat()
            val h: Float = (if (link.y.toInt() == y) Vars.tilesize else abs(
                (link.y - y).toDouble()) * Vars.tilesize - Vars.tilesize).toFloat()
            Lines.rect((x + link.x) / 2f * Vars.tilesize - w / 2f, (y + link.y) / 2f * Vars.tilesize - h / 2f, w, h)
            val angle: Float = Tmp.v1.set((x * 8).toFloat(), (y * 8).toFloat()).sub(link.drawx(), link.drawy()).angle()
            val rotate1: Vec2 = Tmp.v2.set(Vars.tilesize.toFloat(), 0f).setAngle(angle)

            Draw.color(b4)
            Draw.rect("bridge-arrow", link.drawx() + rotate1.x, link.drawy() + rotate1.y, angle)
            val angle1: Float = Tmp.v1.set(link.drawx(), link.drawy()).sub(x * 8f, y * 8f).angle()
            val rotate2: Vec2 = Tmp.v2.set(Vars.tilesize.toFloat(), 0f).setAngle(angle1)
            Draw.rect("bridge-arrow", x * 8 + rotate2.x, y * 8 + rotate2.y, angle - 180)
        }
        Draw.reset()
    }

    fun linkValid(tile: Tile?, other: Tile?, checkDouble: Boolean = true): Boolean {
        if (other == null) return false
        if (tile == null) return false
        if (!positionsValid(tile.x.toInt(), tile.y.toInt(), other.x.toInt(), other.y.toInt())) return false
        val block =
            (other.block() === tile.block() && tile.block() === this) || (tile.block() !is ItemNode && other.block() === this)
        val team = (other.team() == tile.team() || tile.block() != this)
        val build = fun(): Boolean {
            val build = other.build
            if (build is ItemNodeBuild) {
                return (!checkDouble || build.link != tile.pos())
            }
            return false
        }
        return block && team && build()
    }

    private fun positionsValid(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return abs(y1 - y2) <= rangeb && abs(x1 - x2) <= rangeb
    }

    fun findLinklastBuild(x: Int, y: Int): Tile? {
        val tile: Tile? = Vars.world.tile(x, y)

        if (tile != null && lastBuild != null) {
            val lastBuild1 = lastBuild!!

            if (linkValid(tile, lastBuild1.tile) && lastBuild1.tile !== tile && lastBuild1.link == -1) {
                return lastBuild1.tile
            }
        }

        return null
    }

    override fun init() {
        super.init()
        updateClipRadius((rangeb + 0.5f) * Vars.tilesize)
    }

    override fun handlePlacementLine(plans: Seq<BuildPlan>) {
        for (i in 0 until plans.size - 1) {
            val cur: BuildPlan = plans.get(i)
            val next: BuildPlan = plans.get(i + 1)
            if (positionsValid(cur.x, cur.y, next.x, next.y)) {
                cur.config = Point2(next.x - cur.x, next.y - cur.y)
            }
        }
    }

    override fun changePlacementPath(points: Seq<Point2>, rotation: Int) {
        Placement.calculateNodes(points, this, rotation) { point: Point2, other: Point2 ->
            max(abs((point.x - other.x).toDouble()), abs((point.y - other.y).toDouble())) <= rangeb
        }
    }

    open inner class ItemNodeBuild : IceBuild() {
        var link = -1
        private var incoming = IntSeq(false, 4)
        private var warmup = 0f
        var time = -8f
        private var timeSpeed = 0f
        private var wasMoved = false
        private var moved = false
        private var transportCounter: Float = 0f
        override fun pickedUp() {
            link = -1
        }

        override fun playerPlaced(config: Any?) {
            val linkTile: Tile? = findLinklastBuild(tile.x.toInt(), tile.y.toInt())
            if (linkValid(tile, linkTile) && this.link != linkTile!!.pos() /*&& !proximity.contains(linkTile.build)*/) {
                linkTile.build.configure(tile.pos())
            }

            lastBuild = this
        }

        override fun drawSelect() {
            if (linkValid(tile, Vars.world.tile(link))) {
                drawInput(Vars.world.tile(link))
            }

            incoming.each { pos: Int -> drawInput(Vars.world.tile(pos)) }

            Draw.reset()
        }

        private fun drawInput(other: Tile) {
            if (!linkValid(tile, other, false)) return
            val linked: Boolean = other.pos() == link

            Tmp.v2.trns(tile.angleTo(other), 2f)
            val tx: Float = tile.drawx()
            val ty: Float = tile.drawy()
            val ox: Float = other.drawx()
            val oy: Float = other.drawy()
            val alpha: Float = (abs(((if (linked) 100 else 0) - (Time.time * 2f) % 100f).toDouble()) / 100f).toFloat()
            val x: Float = Mathf.lerp(ox, tx, alpha)
            val y: Float = Mathf.lerp(oy, ty, alpha)
            val otherLink: Tile = if (linked) other else tile
            val rel: Int =
                (if (linked) tile else other).absoluteRelativeTo(otherLink.x.toInt(), otherLink.y.toInt()).toInt()
            //draw "background"
            Draw.color(Pal.gray)
            Lines.stroke(2.5f)
            Lines.square(ox, oy, 2f, 45f)
            Lines.stroke(2.5f)
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y)
            //绘制前景色
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
            for (i in 0..(rangeb * 2)) {
                for (j in 0..(rangeb * 2)) {
                    val other: Tile? = Vars.world.tile(tile.x + i - rangeb, tile.y + j - rangeb)
                    if (linkValid(tile, other)) {
                        val linked: Boolean = other!!.pos() == link
                        Drawf.select(other.drawx(), other.drawy(),
                            other.block().size * Vars.tilesize / 2f + 2f + (if (linked) 0f else Mathf.absin(Time.time,
                                4f, 1f)), if (linked) Pal.place else Pal.breakInvalid)
                    }
                }
            }
        }

        override fun onConfigureBuildTapped(other: Building): Boolean {
            //反向连接
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

        private fun checkIncoming() {
            var idx = 0
            while (idx < incoming.size) {
                val i: Int = incoming.items[idx]
                val other: Tile = Vars.world.tile(i)
                if (!linkValid(tile, other, false) || (other.build as ItemNodeBuild).link != tile.pos()) {
                    incoming.removeIndex(idx)
                    idx--
                }
                idx++
            }
        }

        override fun updateTile() {
            if (timer(timerCheckMoved, 30f)) {
                wasMoved = moved
                moved = false
            }
            //平滑动画，使其不会立即停止/启动
            timeSpeed = Mathf.approachDelta(timeSpeed, if (wasMoved) 1f else 0f, 1f / 60f)

            time += timeSpeed * delta()

            checkIncoming()
            val other: Tile? = Vars.world.tile(link)
            if (!linkValid(tile, other)) {
                doDump()
                warmup = 0f
            } else {
                val inc: IntSeq = (other!!.build as ItemNodeBuild).incoming
                val pos: Int = tile.pos()
                if (!inc.contains(pos)) {
                    inc.add(pos)
                }

                warmup = Mathf.approachDelta(warmup, efficiency, 1f / 30f)
                updateTransport(other.build)
            }
        }

        private fun doDump() {
            dumpAccumulate()
            dumpLiquid(liquids.current(), 1f)
        }

        private fun updateTransport(other: Building) {
            transportCounter += edelta()
            while (transportCounter >= transportTime) {
                val item: Item? = items.take()
                if (item != null && other.acceptItem(this, item)) {
                    other.handleItem(this, item)
                    moved = true
                } else if (item != null) {
                    items.add(item, 1)
                    items.undoFlow(item)
                }
                transportCounter -= transportTime
            }
            if (warmup >= 0.25f) {
                moved = moved or (moveLiquid(other, liquids.current()) > 0.05f)
            }
        }

        override fun draw() {
            Draw.rect(bottomRegion, x, y)
            Draw.z(Layer.power)
            val other: Tile? = Vars.world.tile(link)
            if (!(!linkValid(tile, other) || Mathf.zero(Renderer.bridgeOpacity))) {
                if (pulse) {
                    Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6f, 0.07f))
                }
                val warmup: Float = if (hasPower) this.warmup else 1f

                Draw.alpha((if (fadeIn) max(warmup, 0.25f) else 1f) * Renderer.bridgeOpacity)
                val angle: Float = Vec2(x, y).sub(other!!.drawx(), other.drawy()).angle()

                Draw.rect(endRegion, x, y, angle - 90)
                Draw.rect(endRegion, other.drawx(), other.drawy(), angle - 270)
                /* Draw.rect(endRegion, x, y, i * 90 + 90);
            Draw.rect(endRegion, other.drawx(), other.drawy(), i * 90 + 270);*/
                Lines.stroke(bridgeWidth)
                Tmp.v1.set(x, y).sub(other.worldx(), other.worldy()).setLength(1f).scl(-1f)

                Lines.line(bridgeRegion, x + Tmp.v1.x, y + Tmp.v1.y, other.worldx() - Tmp.v1.x,
                    other.worldy() - Tmp.v1.y, false)
                val dist = ((other.x - tile.x) * (other.x - tile.x) + (other.y - tile.y) * (other.y - tile.y)).toFloat()
                val dis: Int = sqrt(dist).toInt() - 1
                Draw.color()
                val arrows: Float = (dis * Vars.tilesize / arrowSpacing).toInt().toFloat()
                val dx: Float = (other.worldx() - x) / arrows / arrowSpacing
                val dy: Float = (other.worldy() - y) / arrows / arrowSpacing
                var a = 0
                while (a < arrows - 2) {
                    Draw.alpha(Mathf.absin(a - time / arrowTimeScl, arrowPeriod, 1f) * warmup * Renderer.bridgeOpacity)
                    Draw.rect(arrowRegion, x + dx * (Vars.tilesize / 2f + a * arrowSpacing + arrowOffset),
                        y + dy * (Vars.tilesize / 2f + a * arrowSpacing + arrowOffset), angle)
                    a++
                }
                Draw.reset()
            }

            Draw.alpha(1f)
            Draw.z(Layer.power + 1)
            Draw.rect(topRegion, x, y)
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return hasItems && team === source.team && items.total() < itemCapacity && checkAccept(source,
                Vars.world.tile(link))
        }

        override fun canDumpLiquid(to: Building, liquid: Liquid): Boolean {
            return checkDump()
        }

        override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
            return hasLiquids && team === source.team && (liquids.current() === liquid || liquids.get(
                liquids.current()) < 0.2f) && checkAccept(source, Vars.world.tile(link))
        }

        private fun checkAccept(source: Building, other: Tile?): Boolean {
            if (tile == null || linked(source)) return true
            if (linkValid(tile, other)) {
                return true
            }
            return false
        }

        private fun linked(source: Building): Boolean {
            return source is ItemNodeBuild && linkValid(source.tile, tile) && source.link == pos()
        }

        override fun canDump(to: Building, item: Item): Boolean {
            return checkDump()
        }

        private fun checkDump(): Boolean {
            val other: Tile? = Vars.world.tile(link)
            return !linkValid(tile, other)
        }

        override fun shouldConsume(): Boolean {
            return linkValid(tile, Vars.world.tile(link)) && enabled
        }

        override fun config(): Point2 {
            return Point2.unpack(link).sub(tile.x.toInt(), tile.y.toInt())
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(link)
            write.f(warmup)
            write.b(incoming.size)
            for (i in 0 until incoming.size) {
                write.i(incoming.items[i])
            }
            write.bool(wasMoved || moved)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            link = read.i()
            warmup = read.f()
            val links: Byte = read.b()
            for (i in 0 until links) {
                incoming.add(read.i())
            }
            moved = read.bool()
            wasMoved = moved
        }

    }
}

