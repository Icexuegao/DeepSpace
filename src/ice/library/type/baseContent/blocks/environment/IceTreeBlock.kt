package ice.library.type.baseContent.blocks.environment

import arc.graphics.g2d.Draw
import ice.content.IceStatus
import ice.library.scene.texs.Colors
import ice.library.type.components.block.BlockDrawSelect
import ice.library.type.components.block.BlockUpdate
import mindustry.Vars
import mindustry.entities.Units
import mindustry.graphics.Drawf
import mindustry.world.Tile
import mindustry.world.blocks.environment.TreeBlock

class IceTreeBlock(name: String) : TreeBlock(name), BlockUpdate, BlockDrawSelect {
    var range = 40f

    override fun update(tile: Tile) {
        Units.nearby(Vars.player.team(), tile.worldx(), tile.worldy(), range) {
            it.apply(IceStatus.寄生, 3 * 60f)
        }
    }
    override fun draw(tile: Tile) {
        Draw.color(Colors.b4)
        Drawf.circles(tile.drawx(), tile.drawy(), range)
    }
}