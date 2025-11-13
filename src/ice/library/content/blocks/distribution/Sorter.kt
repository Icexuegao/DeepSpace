package ice.library.content.blocks.distribution

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.content.blocks.abstractBlocks.IceBlock
import ice.library.draw.drawer.DrawBuild
import ice.library.draw.drawer.DrawMulti
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IceColor
import ice.ui.ItemSelection
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.BlockGroup

class Sorter(name: String) : IceBlock(name) {
    val top = DrawRegion("-top")

    init {
        update = false
        unloadable = false
        saveConfig = true
        destructible = true
        underBullets = true
        configurable = true
        instantTransfer = true
        clearOnDoubleTap = true
        buildType = Prov(::IceSorterBuild)
        group = BlockGroup.transportation
        drawers = DrawMulti(DrawDefault(), DrawBuild<IceSorterBuild> {
            sortItem?.run {
                Draw.color(color)
            }
        }, top)
        config(
            Item::class.java
        ) { tile: IceSorterBuild, item: Item? -> tile.sortItem = item }
        config(
            String::class.java
        ) { tile: IceSorterBuild, s: String ->
            val split = s.split("|")
            tile.invert = split[1].toBoolean()
            if (split[0] == "null") {
                tile.configure(null)
            }
            tile.sortItem = Vars.content.item(split[0])
        }
        configClear { tile: IceSorterBuild -> tile.sortItem = null }
    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan?>?) {
        if (plan.config is String) {
            val split = (plan.config as String).split("|")
            if (split[0] != "null") {
                Draw.color(Vars.content.item(split[0]).color)
            }
        }
        Draw.rect(top.region, plan.drawx(), plan.drawy())
    }

    override fun outputsItems(): Boolean {
        return true
    }

    inner class IceSorterBuild : IceBuild() {
        var invert: Boolean = false
        var sortItem: Item? = null
        override fun drawSelect() {
            super.drawSelect()
            drawItemSelection(sortItem)
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            val to = getTileTarget(item, source, false)

            return to != null && to.acceptItem(this, item) && to.team === team
        }

        override fun handleItem(source: Building, item: Item) {
            getTileTarget(item, source, true)!!.handleItem(this, item)
        }

        fun isSame(other: Building?): Boolean {
            return other != null && other.block.instantTransfer
        }

        fun getTileTarget(item: Item, source: Building, flip: Boolean): Building? {
            val dir = source.relativeTo(tile.x.toInt(), tile.y.toInt()).toInt()
            if (dir == -1) return null
            val to: Building?

            if (((item === sortItem) != invert) == enabled) {
                //prevent 3-chains
                if (isSame(source) && isSame(nearby(dir))) {
                    return null
                }
                to = nearby(dir)
            } else {
                val a = nearby(Mathf.mod(dir - 1, 4))
                val b = nearby(Mathf.mod(dir + 1, 4))
                val ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) &&
                        a.acceptItem(this, item)
                val bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) &&
                        b.acceptItem(this, item)

                if (ac && !bc) {
                    to = a
                } else if (bc && !ac) {
                    to = b
                } else if (!bc) {
                    return null
                } else {
                    to = if ((rotation and (1 shl dir)) == 0) a else b
                    if (flip) rotation = rotation xor (1 shl dir)
                }
            }

            return to
        }

        override fun buildConfiguration(table: Table) {
            table.button({
                it.update {
                    it.isChecked = invert
                }
                Label("").apply {
                    setColor(IceColor.b4)
                    setText {
                        if (invert) {
                            "分类状态: 反"
                        } else {
                            "分类状态: 正"
                        }
                    }
                }.also(it::add)
            }, IStyles.backgroundButton) {
                invert = !invert
            }.margin(12f).growX().height(60f)
            table.row()

            ItemSelection.buildTable(
                this@Sorter, table, Vars.content.items(),
                ::sortItem,
                ::configure
                ,true
            )
        }

        override fun config(): String {
            var s = ""
            s += if (sortItem == null) {
                "null"
            } else {
                sortItem!!.name
            }
            return "$s|$invert"
        }

        override fun write(write: Writes) {
            super.write(write)
            write.bool(invert)
            write.s((if (sortItem == null) -1 else sortItem!!.id).toInt())
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            invert = read.bool()
            sortItem = Vars.content.item(read.s().toInt())
        }
    }
}