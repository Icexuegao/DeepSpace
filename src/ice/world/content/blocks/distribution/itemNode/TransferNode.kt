package ice.world.content.blocks.distribution.itemNode

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
import ice.graphics.TextureRegionDelegate
import ice.world.content.blocks.abstractBlocks.IceBlock
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
import mindustry.world.meta.Env
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class TransferNode(name: String) : IceBlock(name) {
    companion object {
        private var otherReq: BuildPlan? = null
    }

    val timerCheckMoved: Int = timers++
    var range = 10
    var transportTime = 2f
    val endRegion: TextureRegion by TextureRegionDelegate("${this.name}-end")
    var bridgeRegion: TextureRegion by TextureRegionDelegate("${this.name}-bridge")
    var arrowRegion: TextureRegion by TextureRegionDelegate("${this.name}-arrow")
    var topRegion: TextureRegion by TextureRegionDelegate("${this.name}-top")
    var bottomRegion: TextureRegion by TextureRegionDelegate("${this.name}-bottom")
    var fadeIn = true
    var pulse = false
    var arrowSpacing = 4f
    var arrowOffset = 2f
    var arrowPeriod = 0.4f
    var arrowTimeScl = 6.2f
    var bridgeWidth = 6.5f

    //for autolink
    var lastBuild: ItemNodeBuild? = null
    var directionAny = true

    init {
        squareSprite=false
        size = 1
        solid = true
        update = true
        hasPower = true
        hasItems = true
        allowDiagonal = true
        copyConfig = false
        envEnabled = Env.any
        unloadable = false
        hasLiquids = true
        itemCapacity = 10
        underBullets = true
        configurable = true
        canOverdrive = false
        outputsLiquid = true
        liquidCapacity = 10f
        noUpdateDisabled = true
        group = BlockGroup.liquids
        allowConfigInventory = false
        priority = TargetPriority.transport
        buildType = Prov(::ItemNodeBuild)
        config(Point2::class.java) { tile: ItemNodeBuild, i: Point2 ->
            tile.link = Point2.pack(i.x + tile.tileX(), i.y + tile.tileY())
        }
        config(Int::class.javaObjectType) { tile: ItemNodeBuild, i: Int ->
            tile.link = i
        }
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.range, range.toFloat(), StatUnit.blocks)
    }

    override fun drawPlanConfigTop(plan: BuildPlan, list: Eachable<BuildPlan>) {
        otherReq = null
        list.each { other ->
            if (other.block === this && plan !== other && plan.config is Point2 && (plan.config as Point2).equals(
                    other.x - plan.x, other.y - plan.y
                )
            ) {
                otherReq = other
            }
        }
        otherReq?.let {
            drawBridge(plan, it.drawx(), it.drawy())
        }
    }

    private fun drawBridge(req: BuildPlan, ox: Float, oy: Float) {
        if (Mathf.zero(Renderer.bridgeOpacity)) return
        Draw.alpha(Renderer.bridgeOpacity)

        Lines.stroke(bridgeWidth)

        Tmp.v1.set(ox, oy).sub(req.drawx(), req.drawy()).setLength(Vars.tilesize / 2f)

        Lines.line(bridgeRegion, req.drawx() + Tmp.v1.x, req.drawy() + Tmp.v1.y, ox - Tmp.v1.x, oy - Tmp.v1.y, false)

        Draw.rect(
            arrowRegion, (req.drawx() + ox) / 2f, (req.drawy() + oy) / 2f,
            Angles.angle(req.drawx(), req.drawy(), ox, oy)
        )

        Draw.reset()
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)
        val link = findLinklastBuild(x, y)?:return

      var b=false
      Geometry.d4.forEach {
        if (link.x+it.x==x && link.y+it.y==y) {b=true}
      }
      if (b)return


        val vvtf = Vars.tilesize.toFloat()
        if (directionAny) {
            Drawf.dashRect(
                blockColor, (x - range - 0.5f) * vvtf, (y - range - 0.5f) * vvtf, 2 * (range + 0.5f) * vvtf,
                2 * (range + 0.5f) * vvtf
            )
        } else {
            Geometry.d4.forEach { pos ->
                Drawf.dashLine(blockColor, (x + pos.x * 0.5f) * vvtf, (y + pos.y * 0.5f) * vvtf, (x + pos.x * range) * vvtf, (y + pos.y * range) * vvtf)
            }
        }


      link.let {
        Draw.color(Pal.gray.write(Tmp.c3).a(blockColor.a))
        Lines.stroke(3f)
        Lines.line(it.drawx(), it.drawy(), x * vvtf, y * vvtf)
        Draw.color(blockColor)
        Lines.stroke(1f)
        Lines.line(it.drawx(), it.drawy(), x * vvtf, y * vvtf)

        Drawf.square(x * vvtf, y * vvtf, size.toFloat(), 45f, blockColor)
        Drawf.square(it.drawx(), it.drawy(), size.toFloat(), 45f, blockColor)
      }
        Draw.reset()
    }

    fun linkValid(tile: Tile?, other: Tile?, checkDouble: Boolean = true): Boolean {
        if (other == null) return false
        if (tile == null) return false
        if (!positionsValid(tile.x.toInt(), tile.y.toInt(), other.x.toInt(), other.y.toInt())) return false
        val block = (other.block() === tile.block() && tile.block() === this) || (tile.block() !is TransferNode && other.block() === this)
        val team = (other.team() == tile.team() || tile.block() != this)
        val build =
            (other.build is ItemNodeBuild) && (!checkDouble || (other.build as ItemNodeBuild).link != tile.pos())


        return block && team && build
    }

    fun positionsValid(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return if (directionAny) {
            abs(y1 - y2) <= range && abs(x1 - x2) <= range
        } else {
            if (x1 == x2) {
                abs(y1 - y2) <= range
            } else if (y1 == y2) {
                abs(x1 - x2) <= range
            } else {
                false
            }
        }
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
        updateClipRadius((range + 0.5f) * Vars.tilesize)
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
            max(abs((point.x - other.x).toDouble()), abs((point.y - other.y).toDouble())) <= range
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
            if (linkValid(tile, linkTile) && this.link != linkTile!!.pos() && !proximity.contains(linkTile.build)) {
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
            val tile1 = if (linked) tile else other
            val rel: Float = Tmp.v5.set(tile1).sub(otherLink).angle()
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
            Draw.rect(arrowRegion, x, y, rel + 180f)
            Draw.mixcol()
        }

        override fun drawConfigure() {
            Drawf.select(x, y, tile.block().size * Vars.tilesize / 2f + 2f, Pal.accent)
            for (i in 0..(range * 2)) {
                for (j in 0..(range * 2)) {
                    val other: Tile? = Vars.world.tile(tile.x + i - range, tile.y + j - range)
                    if (linkValid(tile, other)) {
                        val linked: Boolean = other!!.pos() == link
                        Drawf.select(
                            other.drawx(), other.drawy(),
                            other.block().size * Vars.tilesize / 2f + 2f + (if (linked) 0f else Mathf.absin(
                                Time.time,
                                4f, 1f
                            )), if (linked) Pal.place else Pal.breakInvalid
                        )
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
            if (hasItems) dumpAccumulate()
            if (hasLiquids) dumpLiquid(liquids.current(), 1f)
        }

        private fun updateTransport(other: Building) {
            transportCounter += edelta()
            while (hasItems && transportCounter >= transportTime) {
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
            if (hasLiquids && warmup >= 0.25f) {
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
                val angle: Float = Vec2(x, y).sub(other!!.drawx(), other.drawy()).angle() + 180f

                Draw.rect(endRegion, x, y, angle + 90)
                Draw.rect(endRegion, other.drawx(), other.drawy(), angle-90)
                /* Draw.rect(endRegion, x, y, i * 90 + 90);
            Draw.rect(endRegion, other.drawx(), other.drawy(), i * 90 + 270);*/
                Lines.stroke(bridgeWidth)
                Tmp.v1.set(x, y).sub(other.worldx(), other.worldy()).setLength(1f).scl(-1f)

                Lines.line(
                    bridgeRegion, x + Tmp.v1.x, y + Tmp.v1.y, other.worldx() - Tmp.v1.x,
                    other.worldy() - Tmp.v1.y, false
                )
                val dist = ((other.x - tile.x) * (other.x - tile.x) + (other.y - tile.y) * (other.y - tile.y)).toFloat()
                val dis: Int = sqrt(dist).toInt() - 1
                Draw.color()
                val arrows: Float = (dis * Vars.tilesize / arrowSpacing).toInt().toFloat()
                val dx: Float = (other.worldx() - x) / arrows / arrowSpacing
                val dy: Float = (other.worldy() - y) / arrows / arrowSpacing
                var a = 0
                while (a < arrows - 2) {
                    Draw.alpha(Mathf.absin(a - time / arrowTimeScl, arrowPeriod, 1f) * warmup * Renderer.bridgeOpacity)
                    Draw.rect(
                        arrowRegion, x + dx * (Vars.tilesize / 2f + a * arrowSpacing + arrowOffset),
                        y + dy * (Vars.tilesize / 2f + a * arrowSpacing + arrowOffset), angle
                    )
                    a++
                }
                Draw.reset()
            }

            Draw.alpha(1f)
            Draw.z(Layer.power + 1)
            Draw.rect(topRegion, x, y)
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return hasItems && team === source.team && items.total() < itemCapacity && checkAccept(
                source,
                Vars.world.tile(link)
            )
        }

        override fun canDumpLiquid(to: Building, liquid: Liquid): Boolean {
            return checkDump()
        }

        override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
            return hasLiquids && team === source.team && (liquids.current() === liquid || liquids.get(
                liquids.current()
            ) < 0.2f) && checkAccept(source, Vars.world.tile(link))
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
            repeat(links.toInt()) {
                incoming.add(read.i())
            }
            moved = read.bool()
            wasMoved = moved
        }
    }
}

