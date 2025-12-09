package ice.world.content.blocks.effect

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.util.Time
import arc.util.Timer
import ice.world.content.blocks.IceBlockComponents.timesex
import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import ice.library.struct.isNotEmpty
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.type.Item
import mindustry.world.draw.DrawDefault
import kotlin.math.sin

class ItemExtractor(name: String) : LinksBlock(name) {

    init {
        hasItems = true
        drawers = DrawMulti(DrawDefault(), DrawBuild<Building> {
            Draw.z(Layer.effect)
            Draw.color(blockColor)
            Lines.stroke(3f)
            Lines.circle(x, y, 2f + sin(Time.time / 20) / 2)
        })
        buildType = Prov(::ItemExtractorBuildEnd)
    }

    inner class ItemExtractorBuildEnd : LinksBlockBuildEnd() {
        override fun acceptItem(source: Building, item: Item?): Boolean {
            return item != null && items[item] < getMaximumAccepted(item)
        }

        override fun updateTile() {
            if (timesex(0, 300f) && builds.isNotEmpty()) {
                Timer.schedule({
                    IceEffects.wave.at(this)
                }, 0f, 0.5f, 2)
            }
            dump()
            for (b in builds) {
                b ?: continue
                Vars.content.items().forEach {
                    if (b.items.has(it) && acceptItem(b, it)) {
                        b.items.remove(it, 1)
                        handleItem(b, it)
                    }
                }
            }
        }
    }
}
