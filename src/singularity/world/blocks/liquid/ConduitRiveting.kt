package singularity.world.blocks.liquid

import arc.Core
import arc.func.Boolc
import arc.func.Boolf
import arc.func.Cons
import arc.func.Cons2
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.scene.ui.Button
import arc.scene.ui.CheckBox
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.ObjectMap
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.entities.Puddles
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.ui.Styles
import mindustry.world.modules.LiquidModule
import universecore.components.blockcomp.ReplaceBuildComp
import universecore.components.blockcomp.Takeable

open class ConduitRiveting(name: String?) : ClusterConduit(name) {
    init {
        conduitAmount = 0
        configurable = true
        outputsLiquid = true

        config(IntSeq::class.java, Cons2 { e: ConduitRivetingBuild?, i: IntSeq? ->
            e!!.liquidsBuffer = arrayOfNulls<ClusterLiquidModule>(i!!.get(0)) as Array<ClusterLiquidModule>
            for (ind in e.liquidsBuffer.indices) {
                e.liquidsBuffer[ind] = ClusterLiquidModule()
            }
            e.input = BooleanArray(i.get(0))
            e.output = BooleanArray(i.get(0))
            e.blocking = BooleanArray(i.get(0))
            for (l in e.liquidsBuffer.indices) {
                e.input[l] = i.get(l * 3 + 1) == 1
                e.output[l] = i.get(l * 3 + 2) == 1
                e.blocking[l] = i.get(l * 3 + 3) == 1
            }
        })
        config<Int?, ConduitRivetingBuild?>(Int::class.java, Cons2 { e: ConduitRivetingBuild?, i: Int? ->
            e!!.currConf = i!!
        })
        config<ByteArray?, ConduitRivetingBuild?>(ByteArray::class.java, Cons2 { e: ConduitRivetingBuild?, b: ByteArray? ->
            when (b!![0].toInt()) {
                0 -> e!!.input[e.currConf] = !e.input[e.currConf]
                1 -> e!!.output[e.currConf] = !e.output[e.currConf]
                2 -> e!!.blocking[e.currConf] = !e.blocking[e.currConf]
            }
        })

        configClear<ConduitRivetingBuild?>(Cons { e: ConduitRivetingBuild? ->
            e!!.currConf = 0
            e.input = BooleanArray(e.liquidsBuffer.size)
            e.output = BooleanArray(e.liquidsBuffer.size)
        })
    }

    public override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
        Draw.rect(region, req.drawx(), req.drawy())
        Draw.rect(arrow, req.drawx(), req.drawy(), (req.rotation * 90).toFloat())
    }

    public override fun icons(): Array<TextureRegion?>? {
        return arrayOf<TextureRegion?>(
            region,
            arrow
        )
    }

    inner class ConduitRivetingBuild : ClusterConduitBuild(), Takeable {
        var   input = BooleanArray(liquidsBuffer.size)
        var   output = BooleanArray(liquidsBuffer.size)
        var   blocking = BooleanArray(liquidsBuffer.size)
        var currConf: Int = 0

        public override fun draw() {
            Draw.rect(region, x, y)
            Draw.rect(arrow, x, y, (rotation * 90).toFloat())
        }

        public override fun onReplaced(old: ReplaceBuildComp?) {
            liquidsBuffer = old!!.getBuild<ClusterConduitBuild?>()!!.liquidsBuffer
        }

        override fun buildConfiguration(table: Table) {
            table.table(Styles.black6, Cons { t: Table? ->
                for (i in liquidsBuffer.indices) {
                    val index = i
                    t!!.button(Cons { ta: Button? -> ta!!.add(Core.bundle.get("misc.conduit") + "#" + index).left().grow() }, Styles.underlineb, Runnable { configure(index) })
                        .update(Cons { b: Button? -> b!!.setChecked(index == currConf) }).size(250f, 35f).pad(0f)
                    t.row()
                }
            }).fill()
            table.table(Styles.black6, Cons { t: Table? ->
                t!!.defaults().left().top().height(45f).minWidth(170f).padRight(12f).left().growX()
                t.check(Core.bundle.get("infos.inputMode"), Boolc { b: Boolean -> configure(byteArrayOf(0)) })
                    .update(Cons { c: CheckBox? -> c!!.setChecked(input[currConf]) }).get().left()
                t.row()
                t.check(Core.bundle.get("infos.outputMode"), Boolc { b: Boolean -> configure(byteArrayOf(1)) })
                    .update(Cons { c: CheckBox? -> c!!.setChecked(output[currConf]) }).get().left()
                t.row()
                t.check(Core.bundle.get("infos.blocking"), Boolc { b: Boolean -> configure(byteArrayOf(2)) })
                    .update(Cons { c: CheckBox? -> c!!.setChecked(blocking[currConf]) })
                    .disabled(Boolf { c: CheckBox? -> input[currConf] || !output[currConf] }).get().left()
            }).top().fill().padLeft(0f)
        }

        public override fun moveLiquidForward(leaks: Boolean, liquid: Liquid?): Float {
            val next = tile.nearby(rotation)
            var flow = 0f
            for (i in liquidsBuffer.indices) {
                val liquids = liquidsBuffer[i]

                if (liquid != null && liquids.current !== liquid) continue

                if (output[i]) {
                    val i1 = i
                    val other = getNext(groupName(i), Boolf { e: Building? ->
                        if (!e!!.interactable(team)) return@Boolf false
                        val rot = relativeTo(e).toInt()
                        if (rot == (rotation + 1) % 4 || rot == (rotation + 3) % 4) {
                            if (e is MultLiquidBuild && e.shouldClusterMove(this)) {
                                if (e is ClusterConduitBuild) {
                                    if (e.tile.absoluteRelativeTo(tile.x.toInt(), tile.y.toInt()).toInt() == e.rotation) return@Boolf false
                                }
                                return@Boolf e.conduitAccept(this, i1, liquidsBuffer[i1].current())
                            } else return@Boolf (e.block.hasLiquids && e.acceptLiquid(this, liquidsBuffer[i1].current()))
                        }
                        false
                    })

                    if (other is MultLiquidBuild) {
                        flow += moveLiquid(other, i, liquidsBuffer[i].current())
                    } else if (other != null) {
                        this.liquids = liquids
                        flow += moveLiquid(other, liquidsBuffer[i].current())
                        this.liquids = cacheLiquids
                    }

                    if (blocking[i] && !input[i]) continue
                }

                if (next.build is MultLiquidBuild && (next.build as MultLiquidBuild).shouldClusterMove(this)) {
                    flow += moveLiquid(next.build as MultLiquidBuild?, i, liquids.current())
                } else if (next.build != null) {
                    this.liquids = liquids
                    flow += moveLiquid(next.build, liquids.current())
                    this.liquids = cacheLiquids
                } else if (leaks && !next.block().solid && !next.block().hasLiquids) {
                    val leakAmount = liquids.currentAmount() / 1.5f
                    Puddles.deposit(next, tile, liquids.current(), leakAmount)
                    liquids.remove(liquids.current(), leakAmount)
                }
            }

            return flow
        }

        override fun config(): Any {
            val req = IntSeq.with(liquidsBuffer.size)

            for (i in input.indices) {
                req.add(if (input[i]) 1 else 0)
                req.add(if (output[i]) 1 else 0)
                req.add(if (blocking[i]) 1 else 0)
            }
            return req
        }

        override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            noSleep()

            if (!source.interactable(team)) return false

            for (i in input.indices) {
                if (input[i]) {
                    val liq = liquidsBuffer[i]
                    if (liq.currentAmount() < 0.01f || liquid === liq.current() && liq.currentAmount() < liquidCapacity) return true
                }
            }

            return false
        }

        override fun conduitAccept(source: MultLiquidBuild, index: Int, liquid: Liquid): Boolean {
            noSleep()
            if (source.tile.absoluteRelativeTo(tile.x.toInt(), tile.y.toInt()).toInt() != rotation && !input[index]) return false
            val liquids: LiquidModule = liquidsBuffer[index]
            return source.interactable(team) && (liquids.currentAmount() < 0.01f || liquids.current() === liquid && liquids.currentAmount() < liquidCapacity)
        }

        public override fun getModuleAccept(source: Building?, liquid: Liquid?): LiquidModule? {
            for (i in liquidsBuffer.indices) {
                if (!input[i]) continue
                val liquids: LiquidModule = liquidsBuffer[i]
                if (liquids.current() === liquid && liquids.currentAmount() < liquidCapacity) return liquids
            }
            return null
        }

        public override fun write(write: Writes) {
            super.write(write)
            write.i(input.size)
            for (i in input.indices) {
                write.bool(input[i])
                write.bool(output[i])
                write.bool(blocking[i])
            }
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val len = read.i()
            input = BooleanArray(len)
            output = BooleanArray(len)
            blocking = BooleanArray(len)
            for (i in 0..<len) {
                input[i] = read.bool()
                output[i] = read.bool()
                blocking[i] = read.bool()
            }
        }

        override var heaps=ObjectMap<String, Takeable.Heaps<*>>()
    }
}