package ice.library.type.baseContent.blocks.effect

import arc.func.Prov
import arc.graphics.g2d.Draw
import ice.library.IFiles
import ice.library.type.baseContent.blocks.abstractBlocks.IceBlock
import ice.library.type.baseContent.item.IceItem
import ice.library.type.draw.DrawBuild
import ice.library.type.draw.IceDrawMulti
import ice.library.type.meta.IceEffects
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.world.Tile
import mindustry.world.draw.DrawDefault

class ResBox(name: String) : IceBlock(name) {
    val top = IFiles.findPng("$name-top")

    init {
        size = 1
        health = 40
        hasItems = true
        itemCapacity = 20
        category = Category.effect
        buildType = Prov(::ResBoxBuild)
        drawers = IceDrawMulti(DrawDefault(), DrawBuild<ResBoxBuild> {
            if (items.empty()){
                Draw.rect(top, x, y)
            }
        })
    }

    inner class ResBoxBuild : IceBuild() {
        override fun interactable(team: Team): Boolean {
            return true
        }

        override fun init(tile: Tile, team: Team, shouldAdd: Boolean, rotation: Int): Building {
            super.init(tile, Team.derelict, shouldAdd, rotation)
            var item = Vars.content.items().random()
            while (item !is IceItem) {
                item = Vars.content.items().random()
            }
            items.add(item, IceEffects.rand.random(1, 20))
            return this
        }
    }
}


