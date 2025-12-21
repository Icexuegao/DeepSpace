package ice.world.content.blocks.environment

import arc.audio.Sound
import arc.graphics.g2d.Draw
import arc.math.Mathf
import ice.world.content.blocks.abstractBlocks.EnvironmentBlock
import mindustry.content.Fx
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.world.Tile
import kotlin.math.max

open class Prop(name: String, breakSounds: Sound = Sounds.rockBreak) : EnvironmentBlock(name) {
    var layer: Float = Layer.blockProp

    init {
        breakable = true
        breakSound = breakSounds
        breakEffect = Fx.breakProp
        alwaysReplace = true
        instantDeconstruct = true
        unitMoveBreakable = true
    }

    override fun drawBase(tile: Tile) {
        Draw.z(layer)
        Draw.rect(if (variants > 0) variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
            max(0, variantRegions.size - 1))] else region, tile.worldx(), tile.worldy())
    }
}