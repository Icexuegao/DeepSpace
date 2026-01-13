package singularity.world.blocks.drills

import arc.Core
import arc.func.*
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Point2
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.OrderedSet
import arc.struct.Sort
import arc.util.Strings
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.Tile
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.blocks.drills.MatrixMinerPlugin.MatrixMinerPluginBuild
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.buffers.ItemsBuffer
import singularity.world.distribution.request.PutItemsRequest
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class MatrixMiner(name: String) : DistNetBlock(name) {
    var baseRange: Int = 0

    init {
        configurable = true
        hasItems = true
        itemCapacity = 64
        topologyUse = 2
    }

    public override fun appliedConfig() {
        config<Int?, MatrixMinerBuild?>(Int::class.java, Cons2 { b: MatrixMinerBuild?, i: Int? ->
            val item = Vars.content.item(i!!)
            if (!b!!.drillItem.remove(item)) b.drillItem.add(item)
        })

        config<Float?, MatrixMinerBuild?>(Float::class.java, Cons2 { b: MatrixMinerBuild?, f: Float? -> b!!.drillRange = f!!.toInt() })
    }

    public override fun setBars() {
        super.setBars()
        addBar<MatrixMinerBuild?>("efficiency", Func { m: MatrixMinerBuild? ->
            Bar(
                Prov { Core.bundle.format("bar.efficiency", Mathf.round(m!!.consEfficiency() * 100)) },
                Prov { Pal.lighterOrange },
                Floatp { m!!.consEfficiency() }
            )
        })
        addBar<MatrixMinerBuild?>("energyUse", Func { m: MatrixMinerBuild? ->
            Bar(
                Prov {
                    Core.bundle.format(
                        "bar.energyCons",
                        Strings.autoFixed(m!!.matrixEnergyConsume() * 60, 1),
                        Strings.autoFixed(m.energyConsMultiplier, 1)
                    )
                },
                Prov { SglDrawConst.matrixNet },
                Floatp { if (m!!.matrixEnergyConsume() > 0.01f) 1f else 0f }
            )
        })
    }

    override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
        return Vars.indexer.findTile(team, tile.worldx(), tile.worldy(), baseRange * 1.44f, Boolf { b: Building? ->
            if (abs(b!!.tileX() - tile.x) > baseRange / 2 || abs(b.tileY() - tile.y) > baseRange / 2) return@Boolf false
            b.block === this
        }) == null
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)
        val l = baseRange * Vars.tilesize / 2f
        Drawf.dashLine(Pal.accent, x - l, y - l, x - l, y + l)
        Drawf.dashLine(Pal.accent, x - l, y - l, x + l, y - l)
        Drawf.dashLine(Pal.accent, x + l, y + l, x - l, y + l)
        Drawf.dashLine(Pal.accent, x + l, y + l, x + l, y - l)
    }

    inner class MatrixMinerBuild : DistNetBuild() {
        var plugins: OrderedSet<MatrixMinerPluginBuild> = OrderedSet<MatrixMinerPluginBuild>()
        var drillItem: ObjectSet<Item> = ObjectSet<Item>()
        var allOre: OrderedSet<Item> = OrderedSet<Item>()
        var energyConsMultiplier: Float = 1f
        var drillRange: Int = 0
        var maxRange: Int = 0
        var lastRadius: Int = 0
        var boost: Float = 0f
        var drillMoveMulti: Float = 0f
        var drillSize: Int = 0
        var itemBuffer: ItemsBuffer = DistBufferType.itemBuffer.get(itemCapacity * DistBufferType.itemBuffer.unit())
        var ores: ObjectMap<Int?, Item?> = ObjectMap<Int?, Item?>()
        var orePosArr: Array<Int?>? = NIL
        var pierce: Boolean = false
        var putReq: PutItemsRequest? = null

        override fun create(block: Block?, team: Team?): Building? {
            super.create(block, team)
            items = itemBuffer.generateBindModule()
            return this
        }

        override fun buildConfiguration(table: Table) {
            super.buildConfiguration(table)

            table.table(Tex.pane, Cons { t: Table? ->
                t!!.defaults().left()
                t.add(Core.bundle.get("fragment.buttons.selectMine"))
                t.row()
                t.table(Cons { items: Table? ->
                    var counter = 0
                    for (item in allOre) {
                        if (item.unlockedNow()) {
                            val button = items!!.button(Tex.whiteui, Styles.selecti, 30f, Runnable {
                                configure(item.id.toInt())
                            }).size(40f).get()
                            button.getStyle().imageUp = TextureRegionDrawable(item.uiIcon)
                            button.update(Runnable { button.setChecked(drillItem.contains(item)) })

                            if (counter++ != 0 && counter % 5 == 0) items.row()
                        }
                    }
                })
                t.row()
                t.add("").update(Cons { l: Label? -> l!!.setText(Core.bundle.format("infos.drillRange", drillRange)) })
                t.row()
                t.slider(0f, maxRange.toFloat(), 1f, drillRange.toFloat(), Floatc { value: Float -> this.configure(value) }).growX().height(45f)
            })
        }

        override fun drawConfigure() {
            super.drawConfigure()
            val l = drillRange * Vars.tilesize / 2f
            Drawf.dashLine(Pal.accent, x - l, y - l, x - l, y + l)
            Drawf.dashLine(Pal.accent, x - l, y - l, x + l, y - l)
            Drawf.dashLine(Pal.accent, x + l, y + l, x - l, y + l)
            Drawf.dashLine(Pal.accent, x + l, y + l, x + l, y - l)

            for (plugin in plugins) {
                plugin.drawConfigure()
            }
        }

        public override fun updateTile() {
            super.updateTile()

            energyConsMultiplier = 1f
            boost = 1f
            drillMoveMulti = 1f
            drillSize = 0
            maxRange = baseRange
            pierce = false

            itemBuffer.update()

            for (plugin in plugins) {
                if (!plugin.enabled) continue

                energyConsMultiplier *= plugin.energyMultiplier()
                boost *= plugin.boost()
                drillMoveMulti *= plugin.drillMoveMulti()
                maxRange += plugin.range()
                drillSize = max(drillSize, plugin.drillSize())
                pierce = pierce or plugin.pierceBuild()
            }

            if (lastRadius != maxRange) {
                drillRange = min(drillRange, maxRange)

                ores.clear()
                val off = (maxRange + 1) % 2
                val ox = tileX() - maxRange / 2 + off
                val oy = tileY() - maxRange / 2 + off

                for (rx in 0..<maxRange) {
                    for (ry in 0..<maxRange) {
                        val t = Vars.world.tile(ox + rx, oy + ry)
                        if (t == null) continue

                        if (t.drop() != null) {
                            ores.put(t.pos(), t.drop())
                            allOre.add(t.drop())
                        }
                    }
                }

                orePosArr = ores.keys().toSeq().toArray<Int?>(Int::class.java)
                Sort.instance().sort<Int?>(orePosArr, Comparator { a: Int?, b: Int? ->
                    val x1 = Point2.x(a!!) - tileX()
                    val y1 = Point2.y(a) - tileY()
                    val x2 = Point2.x(b!!) - tileX()
                    val y2 = Point2.y(b) - tileY()
                    val r1 = max(abs(x1), abs(y1)).toFloat()
                    val rot1 = Angles.angle(x1.toFloat(), y1.toFloat())
                    val r2 = max(abs(x2), abs(y2)).toFloat()
                    val rot2 = Angles.angle(x2.toFloat(), y2.toFloat())
                    (r1 * 1000 + rot1).compareTo(r2 * 1000 + rot2)
                })
            }

            if (putReq != null && distributor!!.network.netValid()) putReq!!.update()
        }

        override fun networkValided() {
            super.networkValided()

            if (putReq != null) putReq!!.kill()
            putReq = PutItemsRequest(this, itemBuffer)
            distributor!!.assign(putReq)
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            plugins.clear()

            for (building in proximity) {
                if (building is MatrixMinerPluginBuild && building.interactable(team)
                    && (building.tileX() == tileX() || building.tileY() == tileY())
                ) {
                    plugins.add(building)
                    building.owner=this
                }
            }
        }

        public override fun updateValid(): Boolean {
            return itemBuffer.remainingCapacity() > 0
        }

        fun angleValid(angle: kotlin.Float): Boolean {
            for (plugin in plugins) {
                if (plugin.angleValid(angle)) return true
            }
            return false
        }

        public override fun config(): Any? {
            return null
        }

        public override fun consEfficiency(): kotlin.Float {
            return super.consEfficiency() * distributor!!.network.netEfficiency()
        }

        override fun matrixEnergyConsume(): kotlin.Float {
            return super.matrixEnergyConsume() * energyConsMultiplier
        }

        fun isChild(build: MatrixMinerPluginBuild?): Boolean {
            return plugins.contains(build)
        }

        public override fun draw() {
            super.draw()
            Draw.z(Layer.effect)
            Lines.stroke(1.6f * consEfficiency() * boost, SglDrawConst.matrixNet)
            SglDraw.dashCircle(x, y, 12f, 6, 180f, Time.time)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(drillRange)
            write.i(drillItem.size)
            for (item in drillItem) {
                write.i(item.id.toInt())
            }
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            drillRange = read.i()
            val l = read.i()
            for (i in 0..<l) {
                drillItem.add(Vars.content.item(read.i()))
            }
        }
    }

    companion object {
        val NIL: Array<Int?> = arrayOfNulls<Int>(0)
    }
}