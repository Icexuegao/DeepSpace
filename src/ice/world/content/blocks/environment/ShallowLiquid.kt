package ice.world.content.blocks.environment

import arc.Core
import arc.graphics.Color
import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.PixmapRegion
import arc.graphics.g2d.TextureRegion
import ice.library.IFiles.appendModName
import mindustry.content.Liquids
import mindustry.graphics.CacheLayer
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType
import mindustry.world.Block

open class ShallowLiquid(name: String, val blockfoor: Block) : Floor(name) {
    var overName = "shallow-water"
    var dps = 0.3f

    init {
        cacheLayer = CacheLayer.water
        liquidDrop = Liquids.water
        isLiquid = true
    }


    override fun init() {
        super.init()
        wall = blockfoor.asFloor().wall
    }

    override fun createIcons(packer: MultiPacker) {
     //   super.createIcons(packer)
        (1..blockfoor.variants).forEach {
            val piX = Core.atlas.getPixmap(blockfoor.name + it).crop()
            val overlay = Core.atlas.getPixmap(overName.appendModName())
            piX.each { x, y ->
                piX.setRaw(x, y, Pixmap.blend((overlay.getRaw(x, y) and -0x100) or (dps * 255).toInt(), piX.getRaw(x, y)))
            }
            val region1 = PixmapRegion(piX)
            mapColor.set(region1.get(region1.width / 2, region1.height / 2))
            Core.atlas.addRegion(name + it, TextureRegion(Texture(piX)))
            packer.add(PageType.environment, name + it, region1)
            piX.dispose()
        }
        if (blendGroup !== this) {
            return
        }

        if (Core.atlas.has("$name-edge")) return
        val image = Core.atlas.getPixmap(icons()[0])
        val edge = Core.atlas.getPixmap(Core.atlas.find("$name-edge-stencil", "edge-stencil"))
        val result = Pixmap(edge.width, edge.height)

        for (x in 0..<edge.width) {
            for (y in 0..<edge.height) {
                result.set(x, y, Color.muli(edge.get(x, y), image.get(x % image.width, y % image.height)))
            }
        }

        packer.add(PageType.environment, "$name-edge", result)
        result.dispose()
    }
}