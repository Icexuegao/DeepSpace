package ice.library.content.blocks.abstractBlocks

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import ice.library.IFiles
import ice.library.draw.drawer.DrawMulti
import ice.library.scene.tex.IceColor
import ice.ui.BaseBundle
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault

open class IceBlock(name: String) : Block(name) {
    companion object {
        fun <T : Block> T.requirements(category: Category, vararg items: Any) {
            requirements(category, ItemStack.with(*items))
        }

        fun <T : UnlockableContent> T.desc(bundle: BaseBundle, name: String, desc: String = "", deta: String = "") {
            bundle.runBun.add {
                localizedName = name
                description = desc
                details = deta
            }
        }
    }

    var drawers = DrawMulti(DrawDefault())
    var blockColor = IceColor.b4

    init {
        var variants = 0
        while (IFiles.hasPng("$name${variants + 1}")) {
            variants++
        }
        this.variants = variants
        if (IFiles.hasPng("$name-shadow1")) customShadow = true
    }

    override fun icons(): Array<TextureRegion> {
        return if (Core.atlas.has("$name-preview")) arrayOf(Core.atlas.find("$name-preview")) else drawers.icons(this)
    }

    override fun load() {
        drawers.load(this)
        super.load()
    }

    fun setDrawMulti(vararg drawers: DrawBlock) {
        this.drawers = DrawMulti(*drawers)
    }

    open inner class IceBuild : Building() {
        override fun draw() {
            drawers.draw(this)
        }

        override fun drawLight() {
            super.drawLight()
            drawers.drawLight(this)
        }

        override fun drawConfigure() {
            Draw.color(blockColor)
            Lines.stroke(1.0f)
            Lines.square(x, y, block.size * 8f / 2.0f + 1.0f)
            Draw.reset()
        }

        override fun drawStatus() {
            if (block.enableDrawStatus) {
                val multiplier = if (block.size > 1) 1f else 0.64f
                val brcx = x + (block.size * Vars.tilesize / 2.0f) - (Vars.tilesize * multiplier / 2.0f)
                val brcy = y - (block.size * Vars.tilesize / 2.0f) + (Vars.tilesize * multiplier / 2.0f)
                Draw.z(Layer.power + 1)
                Draw.color(Pal.gray)
                Fill.square(brcx, brcy, 2.5f * multiplier, 45f)
                Draw.color(status().color)
                Fill.square(brcx, brcy, 1.5f * multiplier, 45f)
                Draw.color()
            }
        }
    }
}