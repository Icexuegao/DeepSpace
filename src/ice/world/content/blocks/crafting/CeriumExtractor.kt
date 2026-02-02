package ice.world.content.blocks.crafting

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import ice.content.IStatus
import mindustry.entities.Damage
import mindustry.graphics.Layer
import singularity.world.blocks.product.NormalCrafter
import kotlin.math.min

class CeriumExtractor(name: String) : NormalCrafter(name) {
    init {
        buildType = Prov(::CeriumExtractorBuild)
    }

    inner class CeriumExtractorBuild : NormalCrafterBuild() {
        var size = 0f
        fun range(): Float {
            return block.size * 8 * 1.5f
        }

        override fun draw() {
            super.draw()
            Draw.z(Layer.shields)
            Draw.color(Color.valueOf("F9A3C7"))
            Draw.alpha(0.4f + Mathf.absin(3.14f * 5f, 0.4f) * efficiency)
            Fill.poly(this.x, this.y, 16, this.range() * this.warmup * size)
        }

        override fun updateTile() {
            super.updateTile()
            size = (if (timeScale > 1) min((timeScale - 1f) / 2f + 1f, 2.5f) else timeScale) + Mathf.absin(3.14f * 3f,
                0.1f) * this.efficiency
            Damage.status(null, this.x, this.y, range() * this.warmup * size, IStatus.辐射, 300f, true, true)
        }
    }
}