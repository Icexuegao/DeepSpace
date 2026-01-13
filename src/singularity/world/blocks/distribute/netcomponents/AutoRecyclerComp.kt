package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.func.Boolc
import arc.func.Cons
import arc.func.Cons2
import arc.func.Prov
import arc.math.geom.Point2
import arc.scene.event.Touchable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button
import arc.scene.ui.CheckBox
import arc.scene.ui.Tooltip
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.OrderedMap
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.Styles
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import universecore.util.Empties

open class AutoRecyclerComp(name: String) : DistNetBlock(name) {
    var usableRecycle: OrderedMap<DistBufferType<*>, Cons<Building>> = OrderedMap<DistBufferType<*>, Cons<Building>>()

    init {
        configurable = true

        config<IntSeq?, AutoRecyclerCompBuild?>(IntSeq::class.java, Cons2 { b: AutoRecyclerCompBuild?, c: IntSeq? ->
            val type: ContentType? = ContentType.entries[c!!.get(0)]
            val content = Vars.content.getByID<UnlockableContent?>(type, c.get(1))
            val set = b!!.list.get(DistBufferType.typeOf(type), Prov { ObjectSet() })
            if (!set.add(content)) set.remove(content)
            b.flush = true
        })
        config<Int?, AutoRecyclerCompBuild?>(Int::class.java, Cons2 { b: AutoRecyclerCompBuild?, i: Int? ->
            if (i == -1) {
                b!!.list.clear()
            } else if (i == 1) {
                b!!.isBlackList = !b.isBlackList
            }
            b!!.flush = true
        })
    }

    fun <E : Building?> setRecycle(type: DistBufferType<*>, recycle: Cons<E>) {
        usableRecycle.put(type, recycle as Cons<Building>)
    }

    open inner class AutoRecyclerCompBuild : DistNetBuild() {
        var list: ObjectMap<DistBufferType<*>?, ObjectSet<UnlockableContent?>> = ObjectMap<DistBufferType<*>?, ObjectSet<UnlockableContent?>>()
        var isBlackList: Boolean = true
        var flush: Boolean = false

        fun updateConfig() {
            if (distributor!!.network.netStructValid()) {
                val config = TargetConfigure()

                config.priority = -65536
                val coreTile = distributor!!.network.core!!.tile
                val dx = tile.x - coreTile!!.x
                val dy = tile.y - coreTile.y
                config.offsetPos = Point2.pack(dx, dy)

                if (!isBlackList) {
                    for (type in usableRecycle.orderedKeys()) {
                        for (content in Vars.content.getBy<Content?>(type.targetType())) {
                            if (content is UnlockableContent && !list.get(type, Empties.nilSetO<UnlockableContent?>()).contains(content)) {
                                config.set(GridChildType.container, content, byteArrayOf(-1))
                            }
                        }
                    }
                } else {
                    for (counts in list.values()) {
                        for (content in counts) {
                            config.set(GridChildType.container, content, byteArrayOf(-1))
                        }
                    }
                }

                distributor!!.network.core!!.matrixGrid()!!.remove(this)
                distributor!!.network.core!!.matrixGrid()!!.addConfig(config)
            }
        }

        public override fun updateTile() {
            super.updateTile()

            if (flush) {
                updateConfig()

                flush = false
            }

            for (rec in usableRecycle.values()) {
                rec.get(this)
            }
        }

        override fun networkValided() {
            flush = true
        }

        var rebuildItems: Runnable? = null
        var currType: DistBufferType<*>? = null
        override fun buildConfiguration(table: Table) {
            table.table(Tex.pane, Cons { main: Table? ->
                main!!.pane(Cons { items: Table? ->
                    rebuildItems = Runnable {
                        items!!.clearChildren()
                        val itemSeq = Vars.content.getBy<UnlockableContent?>(currType!!.targetType())
                        var counter = 0
                        for (item in itemSeq) {
                            if (item.unlockedNow()) {
                                val button = items.button(
                                    Tex.whiteui, Styles.selecti, 30f,
                                    Runnable { configure(IntSeq.with(item.getContentType().ordinal, item.id.toInt())) }).size(40f).get()
                                button.getStyle().imageUp = TextureRegionDrawable(item.uiIcon)
                                button.update(Runnable { button.setChecked(list.get(currType, Empties.nilSetO<UnlockableContent?>()).contains(item)) })

                                button.addListener(Tooltip(Cons { t: Table? -> t!!.table(Tex.paneLeft).get().add(item.localizedName) }))

                                if (counter++ != 0 && counter % 5 == 0) items.row()
                            }
                        }
                    }
                    currType = usableRecycle.orderedKeys().get(0)
                    rebuildItems!!.run()
                }).size(225f, 160f)
                main.image().color(Pal.gray).growY().width(4f).colspan(2).padLeft(3f).padRight(3f).margin(0f)
                main.table(Cons { sideBar: Table? ->
                    sideBar!!.pane(Cons { typesTable: Table? ->
                        for (type in usableRecycle.orderedKeys()) {
                            typesTable!!.button(Cons { t: Button? -> t!!.add(Core.bundle.get("content." + type.targetType().name + ".name")) }, Styles.underlineb, Runnable {
                                currType = type
                                rebuildItems!!.run()
                            }).growX().height(35f).update(Cons { b: Button? -> b!!.setChecked(currType === type) })
                                .touchable(Prov { if (currType === type) Touchable.disabled else Touchable.enabled })
                            typesTable.row()
                        }
                    }).size(120f, 100f)
                    sideBar.row()
                    sideBar.check("", isBlackList, Boolc { b: Boolean -> configure(1) }).update(Cons { c: CheckBox? -> c!!.setText(Core.bundle.get(if (isBlackList) "misc.blackListMode" else "misc.whiteListMode")) }).size(120f, 40f)
                    sideBar.row()
                    sideBar.button(Core.bundle.get("misc.reset"), Icon.cancel, Styles.cleart, Runnable { configure(-1) }).size(120f, 40f)
                }).fillX()
            }).fill()
        }

        override fun write(write: Writes) {
            super.write(write)
            write.bool(isBlackList)

            write.i(list.size)
            for (entry in list) {
                write.i(entry.key!!.id)
                write.i(entry.value.size)
                for (content in entry.value) {
                    write.i(content!!.getContentType().ordinal)
                    write.i(content.id.toInt())
                }
            }
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            isBlackList = read.bool()

            list.clear()
            val size = read.i()
            for (i in 0..<size) {
                val set = list.get(DistBufferType.all[read.i()], Prov { ObjectSet() })
                val s = read.i()
                for (l in 0..<s) {
                    set.add(Vars.content.getByID<UnlockableContent?>(ContentType.all[read.i()], read.i()))
                }
            }
        }
    }
}