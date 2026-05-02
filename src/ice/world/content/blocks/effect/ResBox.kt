package ice.world.content.blocks.effect

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import ice.content.IItems
import universecore.struct.texture.LazyTextureSingleDelegate

import ice.world.content.item.IceItem
import universecore.world.draw.DrawBuild
import universecore.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.world.Tile
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BuildVisibility
import singularity.world.blocks.SglBlock

open class ResBox(name: String) : SglBlock(name) {
  var top: TextureRegion by LazyTextureSingleDelegate("${this.name}-top")

  init {
    size = 1
    health = 40
    hasItems = true
    itemCapacity = 20
    requirements(Category.effect, BuildVisibility.sandboxOnly, IItems.铜锭, 1)
    buildType = Prov(::ResBoxBuild)
    drawers = DrawMulti(DrawDefault(), DrawBuild<ResBoxBuild> {
      if (items.empty()) {
        Draw.rect(top, x, y)
      }
    })
  }

  inner class ResBoxBuild : SglBuilding() {
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


