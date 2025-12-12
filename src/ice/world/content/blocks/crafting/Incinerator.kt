package ice.world.content.blocks.crafting

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.util.Time
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.meta.BlockStatus

class Incinerator(name: String) : IceBlock(name) {
    var effect: Effect = Fx.fuelburn
    var flameColor: Color = Color.valueOf("ffad9d")

    init {
        conductivePower=true
        hasPower = true
        hasLiquids = true
        hasItems = true
        update = true
        solid = true
        buildType = Prov(::IncineratorBuild)
    }

    inner class IncineratorBuild : IceBuild() {
        var heat: Float = 0f

        override fun updateTile() {
            heat = Mathf.approachDelta(heat, efficiency, 0.04f)
        }

        override fun status(): BlockStatus {
            return if (!enabled) BlockStatus.logicDisable else if (heat > 0.5f) BlockStatus.active else BlockStatus.noInput
        }

        override fun draw() {
            super.draw()

            if (heat > 0f) {
                val g = 0.3f
                val r = 0.06f

                Draw.alpha(((1f - g) + Mathf.absin(Time.time, 8f, g) + Mathf.random(r) - r) * heat)

                Draw.tint(flameColor)
                Fill.circle(x, y, 2f)
                Draw.color(1f, 1f, 1f, heat)
                Fill.circle(x, y, 1f)

                Draw.color()
            }
        }

        override fun acceptItem(source: Building?, item: Item?): Boolean {
            return hasItems && heat > 0.5f
        }

        override fun acceptLiquid(source: Building?, liquid: Liquid): Boolean {
            return hasLiquids && heat > 0.5f && liquid.incinerable
        }

        override fun handleItem(source: Building?, item: Item?) {
            if (Mathf.chance(0.3)) {
                effect.at(x, y)
            }
        }

        override fun handleLiquid(source: Building?, liquid: Liquid?, amount: Float) {
            if (Mathf.chance(0.02)) {
                effect.at(x, y)
            }
        }
    }
}
