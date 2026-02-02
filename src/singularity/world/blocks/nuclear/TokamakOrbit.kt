package singularity.world.blocks.nuclear

import arc.Core
import arc.func.Floatf
import arc.func.Intf
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.IntSet
import arc.struct.Seq
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.entities.units.BuildPlan
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.draw.DrawBlock
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.SglBlock
import singularity.world.blocks.nuclear.TokamakCore.TokamakCoreBuild
import singularity.world.draw.DrawDirSpliceBlock
import singularity.world.draw.DrawMultiSgl
import singularity.world.draw.DrawRegionDynamic
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.components.blockcomp.SpliceBlockComp
import universecore.components.blockcomp.SpliceBuildComp
import universecore.world.blocks.modules.ChainsModule
import universecore.world.meta.UncStat

open class TokamakOrbit(name: String) : SglBlock(name), SpliceBlockComp {
    var efficiencyPow: Float = 1f
    var flueMulti: Float = 1f

    init {
        rotate = true

        draw = DrawMultiSgl(
            object : DrawRegionDynamic<TokamakOrbitBuild?>("_corner_bottom") {
                init {
                    alpha = Floatf { e: TokamakOrbitBuild? -> if (e!!.isCorner) 1f else 0f }
                    rotation = Floatf { e: TokamakOrbitBuild? -> (e!!.rotation * 90).toFloat() }
                    makeIcon = false
                }
            },
            object : DrawRegionDynamic<TokamakOrbitBuild>("_bottom") {
                init {
                    alpha = Floatf { e: TokamakOrbitBuild? -> if (e!!.isCorner) 0f else 1f }
                    rotation = Floatf { e: TokamakOrbitBuild? -> e!!.rotation * 90f }
                    makeIcon = true
                    drawPlan = true
                }
            },
            object : DrawBlock() {
                var light: TextureRegion? = null
                var cornerLight: TextureRegion? = null

                override fun load(block: Block) {
                    light = Core.atlas.find(block.name + "_light")
                    cornerLight = Core.atlas.find(block.name + "_corner_light")
                }

                override fun draw(build: Building?) {
                    SglDraw.drawBloomUnderBlock<TokamakOrbitBuild?>(build as TokamakOrbitBuild?) { b: TokamakOrbitBuild? ->
                        Draw.color(SglDrawConst.matrixNet, Pal.reactorPurple, Mathf.absin(18f, 1f))
                        Draw.alpha((0.75f + Mathf.absin(3f, 0.25f)) * b!!.warmup())
                        if (b.isCorner) {
                            if (b.facingThis.size == 1) {
                                val rel = b.relativeTo(b.facingThis.get(0)) - b.rotation
                                if (rel == 1 || rel == -3) {
                                    Draw.scl(1f, 1f)
                                    Draw.rect(cornerLight, b.x, b.y, (b.rotation * 90).toFloat())
                                } else if (rel == -1 || rel == 3) {
                                    Draw.scl(1f, -1f)
                                    Draw.rect(cornerLight, b.x, b.y, (b.rotation * 90).toFloat())
                                }
                            }
                        } else {
                            Draw.rect(light, b.x, b.y, (b.rotation * 90).toFloat())
                        }
                        Draw.reset()
                    }
                }
            },
            object : DrawBlock() {
                var conduit: TextureRegion? = null
                var arrow: TextureRegion? = null

                override fun draw(build: Building?) {
                    Draw.z(Layer.blockOver + 1)
                    if (build is TokamakOrbitBuild && !build.isCorner) {
                        Draw.scl(1f, (if (build.rotation == 1 || build.rotation == 2) -1 else 1).toFloat())
                        Draw.rect(conduit, build.x, build.y, (build.rotation * 90).toFloat())
                        Draw.rect(arrow, build.x, build.y, (build.rotation * 90).toFloat())
                        Draw.reset()
                    }
                }

                override fun drawPlan(block: Block?, plan: BuildPlan, list: Eachable<BuildPlan?>?) {
                    Draw.scl(1f, (if (plan.rotation == 1 || plan.rotation == 2) -1 else 1).toFloat())
                    Draw.rect(conduit, plan.drawx(), plan.drawy(), (plan.rotation * 90).toFloat())
                    Draw.rect(arrow, plan.drawx(), plan.drawy(), (plan.rotation * 90).toFloat())
                    Draw.reset()
                }

                override fun load(block: Block) {
                    conduit = Core.atlas.find(block.name)
                    arrow = Core.atlas.find(block.name + "_arrow")
                }

                override fun icons(block: Block): Array<TextureRegion?> {
                    return arrayOf(conduit, arrow)
                }
            },
            object : DrawRegionDynamic<TokamakOrbitBuild?>("_corner") {
                init {
                    alpha = Floatf { e: TokamakOrbitBuild? -> if (e!!.isCorner) 1f else 0f }
                    makeIcon = false
                }
            },
            object : DrawDirSpliceBlock<TokamakOrbitBuild?>() {
                init {
                    suffix = "_corner_splicer"
                    simpleSpliceRegion = true
                    layerRec = false

                    spliceBits = Intf { e: TokamakOrbitBuild? ->
                        var res = 0
                        if (e!!.isCorner) {
                            if (e.facingNext != null && e.facingNext !is TokamakCoreBuild) {
                                res = res or (1 shl e.relativeTo(e.facingNext).toInt())
                            }
                            for (fac in e.facingThis) {
                                res = res or (1 shl e.relativeTo(fac).toInt())
                            }
                        }
                        res
                    }
                }
            },
            object : DrawDirSpliceBlock<TokamakOrbitBuild?>() {
                init {
                    suffix = "_cap"
                    simpleSpliceRegion = true
                    spliceBits = Intf { e: TokamakOrbitBuild? ->
                        var res = 0
                        if (!e!!.isCorner) {
                            if (e.facingNext == null || e.facingNext is TokamakCoreBuild) {
                                res = res or (1 shl e.rotation)
                            }

                            if (e.facingThis.size == 0) {
                                res = res or (1 shl (e.rotation + 2) % 4)
                            } else if (e.facingThis.size == 1) {
                                if (e.facingThis.get(0) is TokamakCoreBuild) {
                                    res = res or (1 shl e.relativeTo(e.facingThis.get(0) as TokamakCoreBuild ).toInt())
                                }
                            }
                        }
                        res
                    }
                }
            }
        )
        buildType= Prov(::TokamakOrbitBuild)
    }

    override fun setStats() {
        super.setStats()
        stats.remove(UncStat.maxStructureSize)
        setChainsStats(stats)
    }
    override val maxChainsWidth=0
    override var maxChainsHeight=0
    override var interCorner=false
    override var negativeSplice=false

    override fun chainable(other: ChainsBlockComp): Boolean {
        return other === this || other is TokamakCore
    }



    inner class TokamakOrbitBuild : SglBuilding(), SpliceBuildComp {
        var owner: TokamakCoreBuild? = null
        var facingNext: Building? = null
        var facingThis: Seq<Building?> = Seq<Building?>()
        override var loadingInvalidPos= IntSet()
        override var chains: ChainsModule = ChainsModule(this)
        var isCorner: Boolean = false
        override var splice: Int=0

        override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            super.init(tile, team, shouldAdd, rotation)
            chains.newContainer()
            return this
        }


        override fun updateTile() {
            chains.container.update()
        }

        override fun canChain(other: ChainsBuildComp): Boolean {
            return chainable(other.chainsBlock) && ((other is TokamakCoreBuild && (other.relativeTo(this).toInt() == rotation || relativeTo(other).toInt() == rotation))
                    || (other.tileX() == tileX() || other.tileY() == tileY()) && (relativeTo(other.building).toInt() == rotation || other.building.relativeTo(this).toInt() == other.building.rotation)
                    )
        }

        override fun write(write: Writes) {
            super.write(write)
            writeChains(write)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            readChains(read)
        }
        override fun onProximityRemoved() {
            super.onProximityRemoved()
            onChainsRemoved()
        }

        override fun onProximityAdded() {
            super.onProximityAdded()
            onChainsAdded()
        }
        override fun onProximityUpdate() {
            super.onProximityUpdate()

            facingNext = null
            facingThis.clear()
            for (comp in chainBuilds()) {
                if ((comp is TokamakOrbitBuild || comp is TokamakCoreBuild)) {
                    if (relativeTo(comp.building).toInt() == rotation) {
                        facingNext = comp.building
                    } else if ((comp is TokamakOrbitBuild && comp.relativeTo(this).toInt() == comp.rotation)
                        || (comp is TokamakCoreBuild && comp.relativeTo(this).toInt() == rotation)
                    ) {
                        facingThis.add(comp.building)
                    }
                }
            }

            isCorner = facingThis.size > 1 || facingThis.size == 1 && (facingThis.get(0) !is TokamakCoreBuild) && facingThis.get(0)!!.relativeTo(this).toInt() != rotation
            updateRegionBit()
        }

        override fun warmup(): Float {
            return if (owner == null || !owner!!.structValid()) 0f else owner!!.warmup() * owner!!.warmup() * owner!!.warmup()
        }

        override fun onChainsUpdated() {
            owner = null
            for (comp in chains.container.all) {
                if (comp is TokamakCoreBuild) {
                    if (owner == null) {
                        owner = comp
                    } else {
                        owner = null
                        break
                    }
                }
            }
        }


    }
}