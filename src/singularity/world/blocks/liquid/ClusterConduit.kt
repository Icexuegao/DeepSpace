package singularity.world.blocks.liquid

import arc.Core
import arc.func.Boolf
import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Nullable
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.input.Placement
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.blocks.distribution.ItemBridge
import mindustry.world.blocks.liquid.LiquidJunction
import mindustry.world.meta.BlockGroup
import mindustry.world.modules.LiquidModule

open class ClusterConduit(name: String?) : MultLiquidBlock(name) {
    val timerFlow: Int = timers++
    var botColor: Color = Color.valueOf("565656")
    var cornerRegion: TextureRegion? = null
    var botRegions: Array<TextureRegion?> = arrayOfNulls(conduitAmount)
    var capRegion: TextureRegion? = null
    var arrow: TextureRegion? = null

    @Nullable
    var junctionReplacement: Block? = null

    @Nullable
    var bridgeReplacement: Block? = null

    init {
        rotate = true
        solid = false
        floating = true
        conveyorPlacement = true
        noUpdateDisabled = true
        canOverdrive = false
        group = BlockGroup.liquids
    }

    override fun load() {
        super.load()
        for (i in 0..<conduitAmount) {
            botRegions[i] = Core.atlas.find(name + "_bottom_" + i)
        }
        capRegion = Core.atlas.find(name + "_cap")
        arrow = Core.atlas.find(name + "_arrow")
        cornerRegion = Core.atlas.find(name + "_corner")
    }

    override fun init() {
        super.init()

        if (junctionReplacement == null) junctionReplacement = Blocks.liquidJunction
        if (bridgeReplacement == null || bridgeReplacement !is ItemBridge) bridgeReplacement = Blocks.bridgeConduit
    }

    override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
        val corner = cornerIng(req, list)

        if (corner) {
            Draw.rect(cornerRegion, req.drawx(), req.drawy())
            Draw.rect(arrow, req.drawx(), req.drawy(), (req.rotation * 90).toFloat())
        } else {
            Draw.color(botColor)
            Draw.alpha(0.5f)
            for (i in 0..<conduitAmount) {
                Draw.rect(botRegions[i], req.drawx(), req.drawy(), (req.rotation * 90).toFloat())
            }
            Draw.color()
            Draw.rect(region, req.drawx(), req.drawy(), (req.rotation * 90).toFloat())
        }
    }

    protected fun cornerIng(req: BuildPlan, list: Eachable<BuildPlan?>): Boolean {
        if (req.tile() == null) return false
        val result = booleanArrayOf(false)
        list.each(Cons { other: BuildPlan? ->
            if (other!!.breaking || other === req) return@Cons
            for (point in Geometry.d4) {
                val x = req.x + point.x
                val y = req.y + point.y
                if (x >= other.x - (other.block.size - 1) / 2 && x <= other.x + (other.block.size / 2) && y >= other.y - (other.block.size - 1) / 2 && y <= other.y + (other.block.size / 2)) {
                    if (Vars.world.tile(other.x + Geometry.d4(other.rotation).x, other.y + Geometry.d4(other.rotation).y) === req.tile()) {
                        result[0] = result[0] or (other.rotation != req.rotation && Vars.world.tile(req.x + Geometry.d4(req.rotation).x, req.y + Geometry.d4(req.rotation).y) !== other.tile())
                    }
                }
            }
        })
        return result[0]
    }

    override fun getReplacement(req: BuildPlan, requests: Seq<BuildPlan?>): Block? {
        if (junctionReplacement == null) return this
        val cont = Boolf { p: Point2? -> requests.contains { o: BuildPlan? -> o!!.x == req.x + p!!.x && o.y == req.y + p.y && o.rotation == req.rotation && (req.block is ClusterConduit || req.block is LiquidJunction) } }
        return if (cont.get(Geometry.d4(req.rotation)) &&
            cont.get(Geometry.d4(req.rotation - 2)) && req.tile() != null &&
            req.tile().block() is ClusterConduit && Mathf.mod(req.build().rotation - req.rotation, 2) == 1
        ) junctionReplacement else this
    }

    override fun handlePlacementLine(plans: Seq<BuildPlan?>) {
        if (bridgeReplacement == null) return

        Placement.calculateBridges(plans, bridgeReplacement as ItemBridge)
    }

    override fun icons(): Array<TextureRegion?> {
        return arrayOf(Core.atlas.find("conduit-bottom"), region)
    }

    open inner class ClusterConduitBuild : MultLiquidBuild() {
        var capped: Boolean = false
        var isCorner: Boolean = false

        override fun draw() {
            if (isCorner) {
                Draw.rect(cornerRegion, x, y)
                Draw.rect(arrow, x, y, (rotation * 90).toFloat())
            } else {
                for (i in 0..<conduitAmount) {
                    Draw.color(botColor)
                    Draw.rect(botRegions[i], x, y, (rotation * 90).toFloat())
                    Drawf.liquid(botRegions[i], x, y, liquidsBuffer[i].smoothCurrent / liquidCapacity, liquidsBuffer[i].current().color, (rotation * 90).toFloat())
                }

                Draw.rect(region, x, y, (rotation * 90).toFloat())
                if (capped && capRegion!!.found()) Draw.rect(capRegion, x, y, rotdeg())
            }
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            val next = front()
            capped = next == null || next.team !== team || !next.block.hasLiquids
            for (other in proximity) {
                isCorner = isCorner or (other.block is ClusterConduit && other.nearby(other.rotation) === this && other.rotation != rotation && nearby(rotation) !== other)
            }
        }

        override fun updateTile() {
            super.updateTile()

            if (anyLiquid() && timer(timerFlow, 1f)) {
                moveLiquidForward(false, null)

                noSleep()
            } else {
                sleep()
            }
        }

        override fun display(table: Table?) {
            super.display(table)
        }

        override fun conduitAccept(source: MultLiquidBuild, index: Int, liquid: Liquid): Boolean {
            return super.conduitAccept(source, index, liquid) && (source.block is ClusterConduit || source.tile.absoluteRelativeTo(tile.x.toInt(), tile.y.toInt()).toInt() == rotation)
        }

        override fun moveLiquidForward(leaks: Boolean, liquid: Liquid?): Float {
            val next = tile.nearby(rotation)
            if (next == null || next.build == null) return 0f
            val dest = next.build.getLiquidDestination(this, liquid)
            var flow = 0f
            for (i in liquidsBuffer.indices) {
                val liquids: LiquidModule = liquidsBuffer[i]
                if (dest is MultLiquidBuild && dest.shouldClusterMove(this)) {
                    flow += moveLiquid(dest, i, liquids.current())
                } else if (dest != null) {
                    this.liquids = liquids
                    flow += moveLiquid(dest, liquids.current())
                    this.liquids = cacheLiquids
                }
            }

            return flow
        }

        override fun moveLiquid(next: Building, liquid: Liquid?): Float {
            var next = next
            next = next.getLiquidDestination(this, liquid)
            if (next is MultLiquidBuild && next.shouldClusterMove(this)) {
                var f = 0f
                for (index in liquidsBuffer.indices) {
                    if (liquidsBuffer[index] == liquids && next.conduitAccept(this, index, liquidsBuffer[index].current())) {
                        f += moveLiquid(next, index, liquidsBuffer[index].current())
                    }
                }
                if (f > 0) return f
            }
            return super.moveLiquid(next, liquid)
        }

        override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            return super.acceptLiquid(source, liquid) && (tile == null || source.tile.absoluteRelativeTo(tile.x.toInt(), tile.y.toInt()).toInt() == rotation)
        }
    }
}