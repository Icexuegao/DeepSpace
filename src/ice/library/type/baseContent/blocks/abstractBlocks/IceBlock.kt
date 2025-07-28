package ice.library.type.baseContent.blocks.abstractBlocks

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import ice.content.IceItems
import ice.library.scene.texs.Colors
import ice.library.type.draw.IceDrawMulti
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawDefault

open class IceBlock(name: String) : Block(name) {
    var drawers = IceDrawMulti(DrawDefault())
    var blockColor = Colors.b4

    init {
        sync = true
        solid = true
        update = true
        buildTime = 1f
        buildType = Prov(::IceBuild)
        requirements(Category.distribution, ItemStack.with(IceItems.铜锭, 10))
    }

    override fun icons(): Array<TextureRegion> {
        return drawers.icons(this)
    }

    override fun load() {
        drawers.load(this)
        super.load()
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