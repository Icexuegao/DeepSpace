package ice.world.content.blocks.effect

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IItems
import ice.library.IFiles.findPng
import ice.world.content.blocks.abstractBlocks.RangeBlock
import ice.world.meta.IceStats
import ice.library.scene.ui.itooltip
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.BlockFlag
import mindustry.world.meta.Env
import kotlin.math.abs

open class LinksBlock(name: String) : RangeBlock(name) {
    var arrowRegion: TextureRegion = findPng("$name-arrow")
    var buildSize = 10
    var allowLink = Seq<Block>()

    init {
        health = 390
        range = 20 * 8f
        configurable = true
        destructible = true
        canOverdrive = false
        envEnabled = Env.any
        flags = EnumSet.of(BlockFlag.storage)
        buildType = Prov(::LinksBlockBuildEnd)
        requirements(Category.effect, ItemStack.with(IItems.石英玻璃, 10))
    }

    override fun setStats() {
        super.setStats()
        stats.add(IceStats.范围, "[${range / 8}] T")
        stats.add(IceStats.最大连接, "$buildSize")
        if (!allowLink.isEmpty) stats.add(IceStats.可连接建筑) { ta ->
            allowLink.forEach { b ->
                ta.image(b.uiIcon).size(32f).itooltip(b.localizedName)
            }
        }
    }

    override fun setBars() {
        super.setBars()
        addBar("links") { b: LinksBlockBuildEnd ->
            Bar({ Core.bundle.formatString(IceStats.连接.localizedName, b.builds.size, buildSize) }, { blockColor }) {
                b.builds.size.toFloat() / buildSize
            }
        }
    }

    open inner class LinksBlockBuildEnd : RangeBlockBuild() {
        val builds = Seq<Building>()
        private var pos = Seq<Int>()
        override fun drawSelect() {
            super.drawSelect()
            builds.forEach {
                drawInput(it.tile)
                Drawf.select(it.x, it.y, it.block.size * Vars.tilesize / 2f - 1f, blockColor)
            }
        }

        override fun drawConfigure() {
            super.drawConfigure()
            builds.forEach {
                drawInput(it.tile)
                Drawf.select(it.x, it.y, it.block.size * Vars.tilesize / 2f - 1f, blockColor)
            }
        }

        override fun update() {
            super.update()
            builds.remove {it.dead() }
        }

        open fun drawInput(other: Tile) {
            Tmp.v2.trns(tile.angleTo(other), 2f)
            val tx = tile.drawx()
            val ty = tile.drawy()
            val ox = other.drawx()
            val oy = other.drawy()
            val alpha = (abs(0 - (Time.time * 2f) % 100f) / 100f)
            val x = Mathf.lerp(ox, tx, alpha)
            val y = Mathf.lerp(oy, ty, alpha)
            fun extracted() {
                Lines.square(this.x, this.y, 2f, 45f)
                Lines.square(ox, oy, 2f, 45f)
                Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y)
            }
            //draw "background"
            Draw.color(Pal.gray)
            Lines.stroke(2.5f)
            extracted()
            //绘制前景色
            Draw.color(blockColor)
            Lines.stroke(1f)

            extracted()

            Draw.mixcol(Draw.getColor(), 1f)
            Draw.color()
            Draw.rect(arrowRegion, x, y, Tmp.v1.set(other).sub(tile).angle() + 180)
            Draw.mixcol()
        }

        open fun addBuild(build: Building): Boolean {
            if (build == this) return false
            if (builds.size >= buildSize) return false
            if (!build.within(this, range)) return false
            if (!allowLink.isEmpty && !allowLink.contains(build.block)) return false
            return true
        }

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if (other == this) {
                if (builds.isEmpty) {
                    team().data().buildingTree.intersect(x - range, y - range, range * 2, range * 2, Cons {
                        if (addBuild(it)) builds.addUnique(it)
                    })
                } else {
                    builds.clear()
                }
                return false
            }

            if (builds.contains(other)) {
                builds.remove(other)
            } else {
                if (addBuild(other)) {
                    builds.addUnique(other)
                }
            }
            return false
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val size = read.i()
            for (i in 1..size) {
                pos.add(read.i())
            }
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(builds.size)
            builds.forEach {
                write.i(it.pos())
            }
        }

        override fun afterReadAll() {
            super.afterReadAll()
            pos.forEach {
                if (addBuild(Vars.world.build(it))) {
                    builds.addUnique(Vars.world.build(it))
                }
            }
        }
    }
}