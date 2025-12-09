package ice.world.content.blocks.environment

import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.struct.Seq
import ice.library.IFiles
import mindustry.type.Item
import mindustry.world.Tile
import mindustry.world.blocks.environment.OreBlock
import kotlin.math.max

class IceOreBlock(name: String, ore: Item) : OreBlock("${name}Ore", ore) {
    companion object{
        var ores= Seq<IceOreBlock>()
    }
    var display=false
    init {
        ores.add(this)
        useColor = true
        mapColor = itemDrop.color
        var variants = 0
        while (IFiles.hasPng("${name}Ore${variants + 1}")) {
            variants++
        }
        this.variants = variants
    }
    override fun drawBase(tile: Tile) {
        if (Mathf.randomSeed(tile.pos().toLong(),1,20)==1||display){
            Draw.rect(
                variantRegions[Mathf.randomSeed(
                    tile.pos().toLong(), 0, max(0, (variantRegions.size - 1))
                )], tile.worldx(), tile.worldy()
            )
        }
    }
}