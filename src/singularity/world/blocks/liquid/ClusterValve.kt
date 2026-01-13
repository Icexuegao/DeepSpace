package singularity.world.blocks.liquid

import arc.Core
import arc.func.*
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
import mindustry.Vars
import mindustry.entities.Puddles
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.ui.Styles
import mindustry.world.blocks.ItemSelection
import mindustry.world.modules.LiquidModule
import universecore.components.blockcomp.ReplaceBuildComp
import universecore.components.blockcomp.Takeable

open class ClusterValve(name: String?) : ClusterConduit(name) {
    init {
        conduitAmount = 0
        configurable = true
        outputsLiquid = true

        config<Int?, ClusterValveBuild?>(Int::class.java, Cons2 { e: ClusterValveBuild?, i: Int? ->
            e!!.currConfig = i!!
        })
        config(ByteArray::class.java) { e: ClusterValveBuild?, i: ByteArray? ->
            when (i!![0].toInt()) {
                0 -> e!!.input = !e.input
                1 -> e!!.output = !e.output
                2 -> e!!.blocking = !e.blocking
            }
        }

        config<IntSeq?, ClusterValveBuild?>(IntSeq::class.java, Cons2 { e: ClusterValveBuild?, c: IntSeq? ->
            if (c!!.get(0) == 0) {
                e!!.configured[c.get(1)] = Vars.content.liquid(c.get(2))
            } else if (c.get(0) == 1) {
                e!!.liquidsBuffer = arrayOfNulls<ClusterLiquidModule>(c.get(4)) as Array<ClusterLiquidModule>
                for (ind in e.liquidsBuffer.indices) {
                    e.liquidsBuffer[ind] = ClusterLiquidModule()
                }

                e.input = c.get(1) == 1
                e.output = c.get(2) == 1
                e.blocking = c.get(3) == 1

                e.configured = arrayOfNulls<Liquid>(c.get(4))
                for (l in e.liquidsBuffer.indices) {
                    e.configured[l] = Vars.content.liquid(c.get(l + 5))
                }
            }
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
    companion object {
        private val tmp = IntSeq()
    }

    inner class ClusterValveBuild : ClusterConduitBuild(), Takeable {
        var configured: Array<Liquid?> = arrayOfNulls<Liquid>(liquidsBuffer.size)
        var input: Boolean = false
        var output: Boolean = false
        var blocking: Boolean = false
        var currConfig: Int = 0

        public override fun onReplaced(old: ReplaceBuildComp?) {
            liquidsBuffer = old!!.getBuild<ClusterConduitBuild?>()!!.liquidsBuffer
            configured = arrayOfNulls<Liquid>(liquidsBuffer.size)
        }

        override fun buildConfiguration(table: Table) {
            table.table(Cons { ta: Table? ->
                ta!!.table(Styles.black6, Cons { conduits: Table? ->
                    for (i in liquidsBuffer.indices) {
                        val index = i
                        conduits!!.button(
                            Cons { t: Button? -> t!!.add(Core.bundle.get("misc.conduit") + "#" + index).left().grow() }, Styles.underlineb,
                            Runnable { configure(index) }).update(Cons { b: Button? -> b!!.setChecked(index == currConfig) }).size(250f, 35f).pad(0f)
                        conduits.row()
                    }
                })
            }).top()

            table.table(Cons { tb: Table? ->
                tb!!.clearChildren()
                tb.defaults().left().fill()
                ItemSelection.buildTable<Liquid?>(
                    tb,
                    Vars.content.liquids(),
                    Prov { configured[currConfig] },
                    Cons { li: Liquid? -> configure(IntSeq.with(0, currConfig, (if (li == null) -1 else li.id).toInt())) },
                    false
                )
            }).padLeft(0f).top()

            table.table(Styles.black6, Cons { t: Table? ->
                t!!.defaults().left().top().height(45f).minWidth(170f).padRight(12f).left().growX()
                t.check(Core.bundle.get("infos.inputMode"), Boolc { b: Boolean -> configure(byteArrayOf(0)) })
                    .update(Cons { c: CheckBox? -> c!!.setChecked(input) }).get().left()
                t.row()
                t.check(Core.bundle.get("infos.outputMode"), Boolc { b: Boolean -> configure(byteArrayOf(1)) })
                    .update(Cons { c: CheckBox? -> c!!.setChecked(output) }).get().left()
                t.row()
                t.check(Core.bundle.get("infos.blocking"), Boolc { b: Boolean -> configure(byteArrayOf(2)) })
                    .update(Cons { c: CheckBox? -> c!!.setChecked(blocking) })
                    .disabled(Boolf { c: CheckBox? -> input || !output }).get().left()
            }).top().fill().padLeft(0f)
        }

        override fun config(): Any {
            val req = IntSeq.with(1)

            req.add(if (input) 1 else 0)
            req.add(if (output) 1 else 0)
            req.add(if (blocking) 1 else 0)

            req.add(liquidsBuffer.size)
            for (liquid in configured) {
                req.add((if (liquid == null) -1 else liquid.id).toInt())
            }
            return req
        }

        public override fun moveLiquidForward(leaks: Boolean, liquid: Liquid?): Float {
            val next = tile.nearby(rotation)
            var flow = 0f
            for (i in liquidsBuffer.indices) {
                val liquids: LiquidModule = liquidsBuffer[i]
                if (liquid != null && liquids.current() !== liquid) continue
                val li = liquids.current()
                val index = configuredIndex(li)
                if (output && !index.isEmpty()) {
                    for (l in 0..<index.size) {
                        val `in` = index.get(l)
                        val other = getNext(groupName(i), Boolf { e: Building? ->
                            if (!e!!.interactable(team)) return@Boolf false
                            val rot = relativeTo(e)
                            if (rot.toInt() == (rotation + 1) % 4 || rot.toInt() == (rotation + 3) % 4) {
                                if (e is MultLiquidBuild && e.shouldClusterMove(this)) {
                                    return@Boolf e.conduitAccept(this, `in`, liquids.current())
                                } else {
                                    return@Boolf (e.block.hasLiquids && e.acceptLiquid(this, liquids.current()))
                                }
                            }
                            false
                        })

                        if (other is MultLiquidBuild) {
                            if (!other.conduitAccept(this, `in`, li)) continue

                            flow += moveLiquid(other, i, `in`, li)
                        } else if (other != null) {
                            this.liquids = liquids
                            flow += moveLiquid(other, liquids.current())
                            this.liquids = cacheLiquids
                        }
                    }

                    if (blocking && !input) continue
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

        override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            noSleep()
            val index = configuredIndex(liquid)
            for (i in 0..<index.size) {
                val `in` = index.get(i)
                if (input && (source.interactable(team) && liquidsBuffer[`in`].currentAmount() < 0.01f
                            || liquid === liquidsBuffer[`in`].current() && liquidsBuffer[`in`].currentAmount() < liquidCapacity)
                ) return true
            }

            return false
        }

        public override fun shouldClusterMove(source: MultLiquidBuild): Boolean {
            return super.shouldClusterMove(source) && source.tile.absoluteRelativeTo(tile.x.toInt(), tile.y.toInt()).toInt() == rotation
        }

        public override fun handleLiquid(source: Building, liquid: Liquid, amount: Float) {
            val index = configuredIndex(liquid)
            if (index.isEmpty()) {
                super.handleLiquid(source, liquid, amount)
            } else {
                val am = amount / index.size
                for (i in 0..<index.size) {
                    liquidsBuffer[index.get(i)].add(liquid, am)
                }
            }
        }

        fun configuredIndex(liquid: Liquid?): IntSeq {
            tmp.clear()
            for (i in configured.indices) {
                if (configured[i] === liquid) tmp.add(i)
            }
            return tmp
        }

        public override fun draw() {
            Draw.rect(region, x, y)
            Draw.rect(arrow, x, y, (rotation * 90).toFloat())
        }

        public override fun write(write: Writes) {
            super.write(write)
            write.bool(input)
            write.bool(output)
            write.bool(blocking)

            write.i(configured.size)
            for (liquid in configured) {
                write.i((if (liquid == null) -1 else liquid.id).toInt())
            }
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            input = read.bool()
            output = read.bool()
            blocking = read.bool()

            configured = arrayOfNulls<Liquid>(read.i())
            for (i in configured.indices) {
                configured[i] = Vars.content.liquid(read.i())
            }
        }

        override var heaps= ObjectMap<String, Takeable.Heaps<*>>()
    }
}