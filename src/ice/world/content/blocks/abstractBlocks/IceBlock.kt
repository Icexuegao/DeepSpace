package ice.world.content.blocks.abstractBlocks

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import ice.graphics.IceColor
import ice.library.IFiles
import ice.world.draw.DrawMulti
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault

open class IceBlock(name: String) : Block(name) {
    companion object {
        fun <T : Block> T.requirements(category: Category, vararg items: Any) {
            requirements(category, ItemStack.with(*items))
        }

        fun <T : Block> T.consumeItems(vararg items: Any): ConsumeItems {
            return consume(ConsumeItems(ItemStack.with(*items)))
        }

        fun <T : Block> T.consumeLiquids(vararg liquids: Any): ConsumeLiquids {
            return consume(ConsumeLiquids(LiquidStack.with(*liquids)))
        }
    }

    var drawers = DrawMulti(DrawDefault())
    var blockColor = IceColor.b4
    var healAmount = 0f
    var damageReduction = 0f

    init {
        var variants = 0
        while (IFiles.sprites["$name${variants + 1}.png_"]!=null) {
            variants++
        }
        this.variants = variants
        if (IFiles.sprites["$name-shadow1.png_"]!=null) customShadow = true
    }

    override fun setStats() {
        super.setStats()
        if (damageReduction > 0f) stats.add(IceStats.伤害减免, "${damageReduction * 100}%")
        if (healAmount > 0f) stats.add(IceStats.生命值恢复, "$healAmount/秒")
    }

    override fun icons(): Array<TextureRegion> {
        return if (Core.atlas.has("$name-preview")) arrayOf(Core.atlas.find("$name-preview")) else drawers.icons(this)
    }

    override fun load() {
        super.load()
        drawers.load(this)
    }

    fun setDrawMulti(vararg drawers: DrawBlock) {
        this.drawers = DrawMulti(*drawers)
    }

    open inner class IceBuild : Building() {
        override fun draw() {
            try {
                drawers.draw(this)
            } catch (e: Exception) {
                throw Exception("${block.localizedName} draws", e)
            }
        }

        override fun update() {
            super.update()
            if (healthf() < 1 && healAmount > 0f) heal(healAmount / 60f)
        }

        override fun handleDamage(amount: Float): Float {
            return maxOf(0f, amount * (1 - damageReduction.coerceIn(0f, 1f)))
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