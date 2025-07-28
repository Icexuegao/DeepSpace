package ice.library.type.baseContent.blocks.distribution

import arc.func.Prov
import arc.math.Mathf
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.type.baseContent.blocks.abstractBlocks.IceBlock
import ice.library.type.draw.IceDrawMulti
import ice.library.scene.texs.Colors
import ice.library.scene.texs.Texs
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.BlockGroup

class TransformOverflowGate(name: String) : IceBlock(name) {

    init {
        update = false
        hasItems = true
        unloadable = false
        copyConfig = true
        saveConfig = true
        lastConfig = true
        itemCapacity = 0
        underBullets = true
        destructible = true
        canOverdrive = false
        configurable = true
        instantTransfer = true
        group = BlockGroup.transportation
        buildType = Prov(::OverflowGateBuild)
        drawers = IceDrawMulti(DrawDefault(), DrawRegion("-top", 3f, true), DrawGlowRegion("-top").apply {
            color = Colors.b4
            rotate = true
            glowScale = 2f
            rotateSpeed = 3f
            alpha = 0.5f
        })
        config(Boolean::class.javaObjectType) { build: OverflowGateBuild, boof: Boolean ->
            build.invert = boof
        }
    }

    override fun outputsItems(): Boolean {
        return true
    }

    inner class OverflowGateBuild : IceBuild() {
        var invert: Boolean = false
        override fun warmup(): Float {
            return if (invert) 0f else 1f
        }

        override fun buildConfiguration(table: Table) {
            super.buildConfiguration(table)
            table.button({
                it.update {
                    it.isChecked = invert
                }
                Label("").apply {
                    setColor(Colors.b4)
                    setText {
                        if (invert) {
                            "溢流状态: 反"
                        } else {
                            "溢流状态: 正"
                        }
                    }
                }.also(it::add)
            }, Texs.backgroundButton) {
                invert = !invert
            }.margin(12f).size(200f, 60f)
        }

        override fun totalProgress(): Float {
            return if (invert) 0f else Time.time
        }

        override fun config(): Boolean {
            return invert
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            val to = getTileTarget(item, source, false)
            return to != null && to.acceptItem(this, item) && to.team === team
        }

        override fun handleItem(source: Building, item: Item) {
            val target = getTileTarget(item, source, true)
            target?.handleItem(this, item)
        }

        fun getTileTarget(item: Item?, src: Building, flip: Boolean): Building? {
            val from = relativeToEdge(src.tile).toInt()
            if (from == -1) return null
            var to = nearby((from + 2) % 4)
            val fromInst = src.block.instantTransfer
            val canForward = to != null && to.team === team && !(fromInst && to.block.instantTransfer) && to.acceptItem(
                this, item
            )
            val inv = invert == enabled

            if (!canForward || inv) {
                val a = nearby(Mathf.mod(from - 1, 4))
                val b = nearby(Mathf.mod(from + 1, 4))
                val ac = a != null && !(fromInst && a.block.instantTransfer) && a.team === team && a.acceptItem(
                    this, item
                )
                val bc = b != null && !(fromInst && b.block.instantTransfer) && b.team === team && b.acceptItem(
                    this, item
                )

                if (!ac && !bc) {
                    return if (inv && canForward) to else null
                }

                if (ac && !bc) {
                    to = a
                } else if (bc && !ac) {
                    to = b
                } else {
                    to = if ((rotation and (1 shl from)) == 0) a else b
                    if (flip) rotation = rotation xor (1 shl from)
                }
            }
            return to
        }

        override fun write(write: Writes) {
            super.write(write)
            write.bool(invert)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            invert = read.bool()
            items.clear()
        }
    }
}
