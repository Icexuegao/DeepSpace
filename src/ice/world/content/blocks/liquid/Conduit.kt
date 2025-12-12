package ice.world.content.blocks.liquid

import arc.Core
import arc.func.Boolf
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Tmp
import ice.world.content.blocks.liquid.base.LiquidBlock
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.entities.TargetPriority
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.input.Placement
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.Autotiler
import mindustry.world.blocks.Autotiler.SliceMode
import mindustry.world.blocks.distribution.ChainedBuilding
import mindustry.world.blocks.distribution.DirectionBridge
import mindustry.world.blocks.distribution.ItemBridge
import mindustry.world.blocks.liquid.Conduit
import mindustry.world.blocks.liquid.LiquidJunction

open class Conduit(name: String) : LiquidBlock(name), Autotiler {
    companion object {
        const val rotatePad: Float = 6f
        const val hpad: Float = rotatePad / 2f / 4f
        val rotateOffsets = arrayOf(floatArrayOf(hpad, hpad), floatArrayOf(-hpad, hpad), floatArrayOf(-hpad, -hpad), floatArrayOf(hpad, -hpad))
    }

    val timerFlow: Int = timers++
    var botColor: Color = Color.valueOf("565656")
    lateinit var topRegions: Array<TextureRegion>
    val botRegions: Array<TextureRegion> by lazy {
        val array = Array<TextureRegion>(5) {
            Core.atlas.find(this.name + "-bottom-$it")
        }
        if (array[0].found()) {
            return@lazy array
        } else {
            return@lazy Array(5) {
                Core.atlas.find("conduit-bottom-$it")
            }
        }
    }
    val capRegion: TextureRegion by lazy { Core.atlas.find(this.name + "-cap") }

    /** indices: [rotation] [fluid type] [frame]  */
    lateinit var rotateRegions: Array<Array<Array<TextureRegion?>>>

    /** If true, the liquid region is padded at corners, so it doesn't stick out.  */
    var padCorners: Boolean = true
    var leaks: Boolean = true
    var junctionReplacement: Block? = null
    var bridgeReplacement: Block? = null
    var rotBridgeReplacement: Block? = null

    init {
        rotate = true
        solid = false
        floating = true
        underBullets = true
        conveyorPlacement = true
        noUpdateDisabled = true
        canOverdrive = false
        priority = TargetPriority.transport
        buildType = Prov(::ConduitBuild)
    }

    override fun load() {
        super.load()
        topRegions = Array(5) {
            Core.atlas.find(this.name + "-top-$it")
        }
        rotateRegions = Array(4) { Array(2) { arrayOfNulls(Liquid.animationFrames) } }

        if (Vars.renderer != null) {
            val pad: Float = rotatePad
            val frames = Vars.renderer.getFluidFrames()

            for (rot in 0..3) {
                for (fluid in 0..1) {
                    for (frame in 0..<Liquid.animationFrames) {
                        val base = frames[fluid]!![frame]
                        val result = TextureRegion()
                        result.set(base)

                        when (rot) {
                            0 -> {
                                result.setX(result.x + pad)
                                result.setHeight(result.height - pad)
                            }

                            1 -> {
                                result.setWidth(result.width - pad)
                                result.setHeight(result.height - pad)
                            }

                            2 -> {
                                result.setWidth(result.width - pad)
                                result.setY(result.y + pad)
                            }

                            else -> {
                                result.setX(result.x + pad)
                                result.setY(result.y + pad)
                            }
                        }

                        rotateRegions[rot][fluid][frame] = result
                    }
                }
            }
        }
    }

    override fun init() {
        super.init()
        if (junctionReplacement == null) junctionReplacement = Blocks.liquidJunction
        if (bridgeReplacement == null || bridgeReplacement !is ItemBridge) bridgeReplacement = Blocks.bridgeConduit
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan?>?) {
        val bits = getTiling(plan, list) ?: return

        Draw.scl(bits[1].toFloat(), bits[2].toFloat())
        Draw.color(botColor)
        Draw.alpha(0.5f)
        Draw.rect(botRegions[bits[0]], plan.drawx(), plan.drawy(), (plan.rotation * 90).toFloat())
        Draw.color()
        Draw.rect(topRegions[bits[0]], plan.drawx(), plan.drawy(), (plan.rotation * 90).toFloat())
        Draw.scl()
    }

    override fun getReplacement(req: BuildPlan, plans: Seq<BuildPlan?>): Block? {
        if (junctionReplacement == null) return this
        val cont = Boolf { p: Point2? -> plans.contains { o: BuildPlan? -> o!!.x == req.x + p!!.x && o.y == req.y + p.y && (req.block is Conduit || req.block is LiquidJunction) } }
        return if (cont.get(Geometry.d4(req.rotation)) &&
            cont.get(Geometry.d4(req.rotation - 2)) && req.tile() != null &&
            req.tile().block() is Conduit && Mathf.mod(req.build().rotation - req.rotation, 2) == 1
        ) junctionReplacement else this
    }

    override fun blends(tile: Tile?, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
        return otherblock.hasLiquids && (otherblock.outputsLiquid || (lookingAt(tile, rotation, otherx, othery, otherblock))) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock)
    }

    override fun handlePlacementLine(plans: Seq<BuildPlan?>) {
        if (bridgeReplacement == null) return

        if (rotBridgeReplacement is DirectionBridge) {
            Placement.calculateBridges(plans, rotBridgeReplacement as DirectionBridge?, true) { b: Block? -> b is Conduit }
        } else {
            Placement.calculateBridges(plans, bridgeReplacement as ItemBridge, true) { b: Block? -> b is Conduit }
        }
    }

    override fun icons(): Array<TextureRegion> {
        return arrayOf(Core.atlas.find("conduit-bottom"), topRegions[0])
    }

    open inner class ConduitBuild : LiquidBuild(), ChainedBuilding {
        var smoothLiquid: Float = 0f
        var blendbits: Int = 0
        var xscl: Int = 1
        var yscl: Int = 1
        var blending: Int = 0
        var capped: Boolean = false
        var backCapped: Boolean = false

        override fun draw() {
            val r = this.rotation
            //draw extra conduits facing this one for tiling purposes
            Draw.z(Layer.blockUnder)
            for (i in 0..3) {
                if ((blending and (1 shl i)) != 0) {
                    val dir = r - i
                    drawAt(x + Geometry.d4x(dir) * Vars.tilesize * 0.75f, y + Geometry.d4y(dir) * Vars.tilesize * 0.75f, 0, if (i == 0) r else dir, if (i != 0) SliceMode.bottom else SliceMode.top)
                }
            }

            Draw.z(Layer.block)

            Draw.scl(xscl.toFloat(), yscl.toFloat())
            drawAt(x, y, blendbits, r, SliceMode.none)
            Draw.reset()

            if (capped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg())
            if (backCapped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg() + 180)
        }

        protected fun drawAt(x: Float, y: Float, bits: Int, rotation: Int, slice: SliceMode?) {
            val angle = rotation * 90f
            Draw.color(botColor)
            Draw.rect(sliced(botRegions[bits], slice), x, y, angle)
            val offset = if (yscl == -1) 3 else 0
            val frame = liquids.current().animationFrame
            val gas = if (liquids.current().gas) 1 else 0
            var ox = 0f
            var oy = 0f
            val wrapRot = (rotation + offset) % 4
            val liquidr = if (bits == 1 && padCorners) rotateRegions[wrapRot][gas][frame] else Vars.renderer.fluidFrames[gas][frame]

            if (bits == 1 && padCorners) {
                ox = rotateOffsets[wrapRot][0]
                oy = rotateOffsets[wrapRot][1]
            }
            //the drawing state machine sure was a great design choice with no downsides or hidden behavior!!!
            val xscl = Draw.xscl
            val yscl = Draw.yscl
            Draw.scl(1f, 1f)
            Drawf.liquid(sliced(liquidr, slice), x + ox, y + oy, smoothLiquid, liquids.current().color.write(Tmp.c1).a(1f))
            Draw.scl(xscl, yscl)

            Draw.rect(sliced(topRegions[bits], slice), x, y, angle)
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            val bits = buildBlending(tile, rotation, null, true)
            blendbits = bits[0]
            xscl = bits[1]
            yscl = bits[2]
            blending = bits[4]
            val next = front()
            val prev = back()
            capped = next == null || next.team !== team || !next.block.hasLiquids
            backCapped = blendbits == 0 && (prev == null || prev.team !== team || !prev.block.hasLiquids)
        }

        override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            noSleep()
            return (liquids.current() === liquid || liquids.currentAmount() < 0.2f)
                    && (tile == null || source === this || (source.relativeTo(tile.x.toInt(), tile.y.toInt()) + 2) % 4 != rotation)
        }

        override fun updateTile() {
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f)

            if (liquids.currentAmount() > 0.0001f && timer(timerFlow, 1f)) {
                moveLiquidForward(leaks, liquids.current())
                noSleep()
            } else {
                sleep()
            }
        }

        override fun next(): Building? {
            val next = tile.nearby(rotation)
            if (next != null && next.build is ConduitBuild) {
                return next.build
            }
            return null
        }
    }
}