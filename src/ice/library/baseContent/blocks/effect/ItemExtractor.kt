package ice.library.baseContent.blocks.effect

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.util.Time
import arc.util.Timer
import ice.library.baseContent.blocks.IceBlockComponents.timesex
import ice.library.draw.drawer.IceDrawMulti
import ice.library.meta.IceEffects
import ice.library.scene.tex.Colors
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.type.Item
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import kotlin.math.sin

class ItemExtractor(name: String) : LinksBlock(name) {

    init {
        hasItems = true
        drawers = IceDrawMulti(DrawDefault(), object : DrawBlock() {
            override fun draw(build: Building) {
                Draw.z(Layer.effect)
                Draw.color(Colors.b4)
                Lines.stroke(3f)
                Lines.circle(build.x, build.y, 2f + sin(Time.time / 20) / 2)
            }
        })
        buildType = Prov(::ItemExtractorBuildEnd)
    }

    inner class ItemExtractorBuildEnd : LinksBlock.LinksBlockBuildEnd() {
        override fun acceptItem(source: Building, item: Item?): Boolean {
            return item != null && items[item] < getMaximumAccepted(item)
        }

        override fun updateTile() {
            if (timesex(0, 300f)) {
                Timer.schedule({
                    IceEffects.wave.at(this)
                }, 0f, 0.5f, 2)
            }
            dump()
            for (b in builds) {
                b ?: continue
               if (b.dead()) { builds.remove(b) }
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
