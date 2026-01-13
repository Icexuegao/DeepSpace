package singularity.game

import arc.Core
import arc.Events
import arc.func.Boolf
import arc.func.Boolp
import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.math.geom.Vec2
import arc.struct.OrderedMap
import arc.struct.Seq
import arc.util.Structs
import arc.util.Time
import mindustry.Vars
import mindustry.game.EventType.BlockBuildEndEvent
import mindustry.game.EventType.ResetEvent
import mindustry.graphics.Pal
import mindustry.ui.fragments.HintsFragment.DefaultHint
import mindustry.ui.fragments.HintsFragment.Hint
import mindustry.world.Block
import singularity.contents.DistributeBlocks
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.SglBlock
import singularity.world.blocks.distribute.DistNetCore.DistNetCoreBuild
import singularity.world.blocks.distribute.MatrixBridge
import singularity.world.blocks.distribute.matrixGrid.MatrixEdgeBlock
import singularity.world.blocks.distribute.matrixGrid.MatrixGridCore
import singularity.world.blocks.distribute.matrixGrid.MatrixGridCore.MatrixGridCoreBuild
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistributeNetwork
import universecore.components.blockcomp.SpliceBlockComp
import kotlin.math.abs

open class SglHint internal constructor(private val name: String?, pages: Int, dependencies: Array<Hint> =EMP, shouldShow: Boolp, isComplete: Boolp = Boolp{true}, valid: Boolp= Boolp{true}) : Hint {
    companion object {
        val retentionBlocks: OrderedMap<Block?, Point2> = OrderedMap<Block?, Point2>()
      open  var all: Seq<SglHint> = Seq<SglHint>()
        val EMP: Array<Hint> = arrayOfNulls<Hint>(0) as Array<Hint>
        const val SEP_CHAR: Char = '\udfce'

        init {
            Events.on<BlockBuildEndEvent?>(BlockBuildEndEvent::class.java, Cons { e: BlockBuildEndEvent? ->
                if (!e!!.breaking && e.unit === Vars.player.unit()) {
                    retentionBlocks.put(e.tile.block(), Point2.unpack(e.tile.pos()))
                }
            })

            Events.on<ResetEvent?>(ResetEvent::class.java, Cons { e: ResetEvent? ->
                retentionBlocks.clear()
            })
        }

        val nuclearEnergyGuidance: SglHint = SglHint(
            "nuclearEnergyGuidance", 1,
            { retentionBlocks.orderedKeys().contains(Boolf { e: Block? -> e is SglBlock && e.hasEnergy }) },
            { false }
        ).setDocPath(0, Core.bundle.get("hints.doc.nuclearGuidance"), "nuclear_energy_blocks.md")
        val spliceStructure: SglHint = SglHint(
            "spliceStructure", 2,
            { retentionBlocks.orderedKeys().contains(Boolf { e: Block? -> e is SpliceBlockComp }) },
            { false }
        )

        //matrix distribute network
        val matrixCorePlaced: SglHint = object : SglHint(
            "matrixCorePlaced", 2,
            Boolp { retentionBlocks.containsKey(DistributeBlocks.Companion.matrix_core) && DistributeBlocks.Companion.matrix_bridge!!.unlockedNow() && DistributeBlocks.Companion.matrix_energy_manager!!.unlockedNow() && DistributeBlocks.Companion.matrix_power_interface!!.unlockedNow() },
            Boolp { DistributeNetwork.activityNetwork.orderedItems().contains(Boolf { obj: DistributeNetwork? -> obj!!.netValid() }) }
        ) {
            override fun draw(page: Int, color: Color) {
                val pos = retentionBlocks.get(DistributeBlocks.Companion.matrix_core)
                val build = Vars.world.build(pos.pack())
                if (build !is DistNetCoreBuild || build.pos() != pos.pack()) return
                val wx = (pos.x) * Vars.tilesize + DistributeBlocks.Companion.matrix_core!!.offset
                val wy = pos.y * Vars.tilesize + DistributeBlocks.Companion.matrix_core!!.offset

                Lines.stroke(2f, Pal.accent)
                Draw.alpha(color.a)
                Lines.square(wx, wy, (DistributeBlocks.Companion.matrix_core!!.size * Vars.tilesize).toFloat(), 45f)

                if (page >= 1) {
                    Draw.color()
                    val prog = (Time.time % 300) / 300
                    val al = 1 - Mathf.clamp((prog - 0.7f) / 0.22f)

                    Draw.alpha(color.a * 0.65f * Mathf.clamp(prog / 0.22f) * al)
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_bridge!!, pos.x + 6, pos.y)
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_bridge!!, pos.x + 10, pos.y)
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_energy_manager!!, pos.x + 14, pos.y)
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_power_interface!!, pos.x + 17, pos.y)

                    Draw.alpha(color.a * 0.65f * Mathf.clamp((prog - 0.4f) / 0.22f) * al)
                    Companion.drawLink((DistributeBlocks.Companion.matrix_bridge as MatrixBridge).linkRegion, null, DistributeBlocks.Companion.matrix_bridge!!, 0f, DistributeBlocks.Companion.matrix_bridge!!, 0f, pos.x, pos.y, pos.x + 6, pos.y, 1f)
                    Companion.drawLink((DistributeBlocks.Companion.matrix_bridge as MatrixBridge).linkRegion, null, DistributeBlocks.Companion.matrix_bridge!!, 0f, DistributeBlocks.Companion.matrix_bridge!!, 0f, pos.x + 6, pos.y, pos.x + 10, pos.y, 1f)
                    Companion.drawLink((DistributeBlocks.Companion.matrix_bridge as MatrixBridge).linkRegion, null, DistributeBlocks.Companion.matrix_bridge!!, 0f, DistributeBlocks.Companion.matrix_bridge!!, 0f, pos.x + 10, pos.y, pos.x + 14, pos.y, 1f)
                }
            }
        }
        val matrixUsageTip: SglHint = object : SglHint(
            "matrixUsageTip", 2, arrayOf<Hint>(matrixCorePlaced),
            shouldShow= Boolp {
                if (!retentionBlocks.containsKey(DistributeBlocks.Companion.matrix_core)) return@Boolp false
                val build = Vars.world.build(retentionBlocks.get(DistributeBlocks.matrix_core).pack())
                if (build !is DistNetCoreBuild) return@Boolp false
                build.distributor().network.netValid()
            }, Boolp { false }
        ) {
            override fun draw(page: Int, color: Color) {
                val pos = retentionBlocks.get(DistributeBlocks.Companion.matrix_core)
                val build = Vars.world.build(pos.pack())
                if (build !is DistNetCoreBuild || build.pos() != pos.pack()) return
                val wx = (pos.x) * Vars.tilesize + DistributeBlocks.Companion.matrix_core!!.offset
                val wy = pos.y * Vars.tilesize + DistributeBlocks.Companion.matrix_core!!.offset

                Lines.stroke(2f, Pal.accent)
                Draw.alpha(color.a)
                Lines.square(wx, wy, DistributeBlocks.Companion.matrix_core!!.size * Vars.tilesize / 2f)

                if (page >= 1) {
                    Draw.color()
                    Draw.alpha(color.a * 0.7f * (0.5f + Mathf.absin(7f, 0.5f)))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_topology_container!!, pos.x + 5, (pos.y + 2))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_topology_container!!, pos.x + 5, (pos.y - 2))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_topology_container!!, pos.x - 5, (pos.y + 2))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_topology_container!!, pos.x - 5, (pos.y - 2))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_process_unit!!, pos.x + 2, (pos.y + 5))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_process_unit!!, pos.x + 2, (pos.y - 4))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_process_unit!!, pos.x - 1, (pos.y + 5))
                    Companion.drawBlock(DistributeBlocks.Companion.matrix_process_unit!!, pos.x - 1, (pos.y - 4))
                }
            }
        }
        val matrixGridPlaced: SglHint = object : SglHint(
            "matrixGridPlaced", 2, arrayOf<Hint>(matrixUsageTip),
            Boolp {
                DistributeNetwork.activityNetwork.orderedItems()
                    .contains(Boolf { e: DistributeNetwork? ->
                        e!!.netValid() && e.allElem.orderedItems()
                            .contains(Boolf { el: DistElementBuildComp? -> el is MatrixGridCoreBuild && el.block === DistributeBlocks.Companion.matrix_controller })
                    }
                    )
                        && retentionBlocks.containsKey(DistributeBlocks.Companion.matrix_controller)
            },
            Boolp {
                val build = Vars.world.build(retentionBlocks.get(DistributeBlocks.Companion.matrix_controller).pack())
                if (build !is MatrixGridCoreBuild) return@Boolp false
                build.gridValid()
            }
        ) {
            val arr1: Array<Point2> = arrayOf<Point2>(Point2(0, 0), Point2(10, 0), Point2(10, 10), Point2(0, 10))
            val arr2: Array<Point2> = arrayOf<Point2>(Point2(0, 0), Point2(7, 0), Point2(12, 0), Point2(12, 7), Point2(12, 12), Point2(7, 12), Point2(0, 12), Point2(0, 7))
            val arr3: Array<Point2> = arrayOf<Point2>(Point2(0, 0), Point2(7, 0), Point2(14, 0), Point2(14, 7), Point2(7, 7), Point2(7, 14), Point2(0, 14), Point2(0, 7))

            private fun drawEdges(pos: Point2, arr: Array<Point2>, ab: Float, al: Float, p: Float) {
                for (i in arr.indices) {
                    val th = arr[i]
                    val to = arr[(i + 1) % arr.size]

                    Draw.alpha(ab)
                    if (th.x != 0 || th.y != 0) Companion.drawBlock(DistributeBlocks.Companion.matrix_grid_node!!, pos.x + th.x, pos.y + th.y)

                    Draw.alpha(al)
                    if (th.x == 0 && th.y == 0) {
                        Companion.drawLink(
                            (DistributeBlocks.Companion.matrix_controller as MatrixGridCore).linkRegion, (DistributeBlocks.Companion.matrix_controller as MatrixGridCore).linkCapRegion,
                            DistributeBlocks.Companion.matrix_controller!!, (DistributeBlocks.Companion.matrix_controller as MatrixGridCore).linkOffset, DistributeBlocks.Companion.matrix_grid_node!!, (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkOffset,
                            pos.x + th.x, pos.y + th.y, pos.x + to.x, pos.y + to.y, p
                        )
                    } else if (to.x == 0 && to.y == 0) {
                        Companion.drawLink(
                            (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkRegion, (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkCapRegion,
                            DistributeBlocks.Companion.matrix_grid_node!!, (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkOffset, DistributeBlocks.Companion.matrix_controller!!, (DistributeBlocks.Companion.matrix_controller as MatrixGridCore).linkOffset,
                            pos.x + th.x, pos.y + th.y, pos.x + to.x, pos.y + to.y, p
                        )
                    } else {
                        Companion.drawLink(
                            (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkRegion, (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkCapRegion,
                            DistributeBlocks.Companion.matrix_grid_node!!, (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkOffset, DistributeBlocks.Companion.matrix_grid_node!!, (DistributeBlocks.Companion.matrix_grid_node as MatrixEdgeBlock).linkOffset,
                            pos.x + th.x, pos.y + th.y, pos.x + to.x, pos.y + to.y, p
                        )
                    }
                }
            }

            override fun draw(page: Int, color: Color) {
                val pos = retentionBlocks.get(DistributeBlocks.Companion.matrix_controller)
                val build = Vars.world.build(pos.pack())
                if (build !is MatrixGridCoreBuild || build.pos() != pos.pack()) return
                val wx = (pos.x) * Vars.tilesize + DistributeBlocks.Companion.matrix_controller!!.offset
                val wy = pos.y * Vars.tilesize + DistributeBlocks.Companion.matrix_controller!!.offset

                Lines.stroke(2f, Pal.accent)
                Draw.alpha(color.a)
                Lines.square(wx, wy, DistributeBlocks.Companion.matrix_controller!!.size * Vars.tilesize / 2f)

                if (page >= 1) {
                    val prog = (Time.time % 1200) / 1200
                    Draw.color()

                    if (prog < 0.33f) {
                        val p = prog / 0.33f
                        val al = 1 - Mathf.clamp((p - 0.7f) / 0.22f)

                        drawEdges(pos, arr1, color.a * 0.65f * Mathf.clamp(p / 0.22f) * al, color.a * 0.65f * Mathf.clamp((p - 0.4f) / 0.22f) * al, Mathf.clamp(p / 0.6f))
                    } else if (prog < 0.66f) {
                        val p = (prog - 0.33f) / 0.33f
                        val al = 1 - Mathf.clamp((p - 0.7f) / 0.22f)

                        drawEdges(pos, arr2, color.a * 0.65f * Mathf.clamp(p / 0.22f) * al, color.a * 0.65f * Mathf.clamp((p - 0.4f) / 0.22f) * al, Mathf.clamp(p / 0.6f))
                    } else {
                        val p = (prog - 0.66f) / (1 - 0.66f)
                        val al = 1 - Mathf.clamp((p - 0.7f) / 0.22f)

                        drawEdges(pos, arr3, color.a * 0.65f * Mathf.clamp(p / 0.22f) * al, color.a * 0.65f * Mathf.clamp((p - 0.4f) / 0.22f) * al, Mathf.clamp(p / 0.6f))
                    }
                }
            }
        }
        val matrixGridActivating: SglHint = object : SglHint(
            "matrixGridActivating", 3, arrayOf<Hint>(matrixGridPlaced),
            Boolp {
                val p = retentionBlocks.get(DistributeBlocks.Companion.matrix_controller)
                if (p == null) return@Boolp false
                val build = Vars.world.build(p.pack())
                if (build !is MatrixGridCoreBuild || build.pos() != p.pack()) return@Boolp false
                build.gridValid() && build.distributor().network.netValid()
            },
            Boolp {
                DistributeNetwork.activityNetwork.orderedItems().contains(Boolf { e: DistributeNetwork? ->
                    if (!e!!.netValid()) return@Boolf false
                    e.allElem.orderedItems().contains(Boolf { el: DistElementBuildComp? -> el is MatrixGridCoreBuild && el.gridValid() && !el.configs()!!.isEmpty() })
                })
            }
        ) {
            val tmp: Vec2 = Vec2()

            override fun draw(page: Int, color: Color) {
                val pos = retentionBlocks.get(DistributeBlocks.Companion.matrix_controller)
                val build = Vars.world.build(pos.pack())
                if ((build !is MatrixGridCoreBuild) || build.pos() != pos.pack() || !build.gridValid()) return
                val wx = (pos.x) * Vars.tilesize + DistributeBlocks.Companion.matrix_controller!!.offset
                val wy = pos.y * Vars.tilesize + DistributeBlocks.Companion.matrix_controller!!.offset

                Lines.stroke(2f, Pal.accent)
                Draw.alpha(color.a)
                Lines.square(wx, wy, DistributeBlocks.Companion.matrix_controller!!.size * Vars.tilesize / 2f)

                if (page == 1) {
                    val prog = (Time.time % 200) / 200
                    val al = 1 - Mathf.clamp((prog - 0.8f) / 0.12f)
                    Draw.color()

                    Draw.alpha(color.a * 0.65f)
                    val vecs: FloatArray = build.edges.getPoly().getVertices()
                    val p = Geometry.polygonCentroid(vecs, 0, vecs.size, tmp)
                    val px = (p.x / Vars.tilesize).toInt()
                    val py = (p.y / Vars.tilesize).toInt()
                    Companion.drawBlock(DistributeBlocks.Companion.io_point!!, px, py)
                    val pr = Mathf.clamp(prog / 0.2f)
                    Draw.alpha(color.a * 0.65f * pr * al)
                    tmp.set(wx + 24 * (1 - pr), wy - 18 * (1 - pr)).lerp((px * Vars.tilesize).toFloat(), (py * Vars.tilesize).toFloat(), Mathf.clamp((prog - 0.32f) / 0.3f))
                    var click = if (prog < 0.3f) Mathf.clamp((prog - 0.22f) / 0.05f) else Mathf.clamp((prog - 0.65f) / 0.05f)
                    click = (0.5f - abs(click - 0.5f)) * 2
                    Draw.rect(SglDrawConst.cursor, tmp.x, tmp.y, 16 - 4 * click, 16 - 4 * click)
                }
            }
        }

        private fun drawBlock(block: Block, x: Int, y: Int) {
            Draw.rect(block.region, x * Vars.tilesize + block.offset, y * Vars.tilesize + block.offset)
        }

        private fun drawLink(linkRegion: TextureRegion?, cap: TextureRegion?, block: Block, off: Float, toBlock: Block, toOff: Float, x: Int, y: Int, toX: Int, toY: Int, l: Float) {
            SglDraw.drawLink(
                x * Vars.tilesize + block.offset, y * Vars.tilesize + block.offset, off,
                toX * Vars.tilesize + toBlock.offset, toY * Vars.tilesize + toBlock.offset, toOff,
                linkRegion, cap, l
            )
        }

        fun resetCompletedHints() {
            Vars.ui.hints.hints.removeAll(all)
            for (hint in all) {
                Core.settings.remove(hint.name() + "-hint-done")
            }
            Vars.ui.hints.hints.addAll(all)
        }

        fun resetAllCompletedHints() {
            Vars.ui.hints.hints.clear()
            val list: Seq<Hint> = Seq<Hint>(DefaultHint.entries.toTypedArray())
            list.addAll(all)
            for (hint in list) {
                Core.settings.remove(hint.name() + "-hint-done")
            }

            Vars.ui.hints.hints.addAll(list)
        }
    }

    private val localized: Array<String>
    private val dependencies: Array<Hint>
    private val shouldShow: Boolp
    private val isComplete: Boolp
    private val valid: Boolp
    private val id: Int
    val docPaths: Array<String?>

    internal constructor(text: String?, pages: Int, dependencies: Array<Hint>, shouldShow: Boolp, isComplete: Boolp) :
            this(text, pages, dependencies, shouldShow, isComplete, Boolp { true })

    internal constructor(text: String?, pages: Int, shouldShow: Boolp, isComplete: Boolp) :
            this(text, pages, EMP, shouldShow, isComplete, Boolp { true })

    internal constructor(text: String?, pages: Int, shouldShow: Boolp, isComplete: Boolp, valid: Boolp) :
            this(text, pages, EMP, shouldShow, isComplete, valid)

    init {
        if (pages == 1) localized = arrayOf<String>(Core.bundle.get("hints." + name))
        else {
            this.localized = arrayOfNulls<String>(pages) as  Array<String>
            for (i in 0..<pages) {
                localized[i] = Core.bundle.get("hints." + name + "-" + i)
            }
        }
        this.dependencies = dependencies
        this.docPaths = arrayOfNulls<String>(pages)
        this.shouldShow = shouldShow
        this.isComplete = isComplete
        this.valid = valid

        id = DefaultHint.entries.toTypedArray().size + all.size
        all.add(this)
        Vars.ui.hints.hints.add(this)
    }

    fun setDocPath(page: Int, name: String?, doc: String?): SglHint {
        docPaths[page] = name + SEP_CHAR + doc
        return this
    }

    override fun name(): String? {
        return name
    }

    override fun text(): String? {
        return localized[0]
    }

    fun text(index: Int): String? {
        return localized[index]
    }

    fun pages(): Int {
        return localized.size
    }

    override fun complete(): Boolean {
        return isComplete.get()
    }

    override fun show(): Boolean {
        return shouldShow.get() && (dependencies.size == 0 || !Structs.contains<Hint?>(dependencies, Boolf { d: Hint? -> !d!!.finished() }))
    }

    override fun order(): Int {
        return id
    }

    override fun valid(): Boolean {
        return valid.get()
    }

   open fun draw(page: Int, color: Color) {}
}