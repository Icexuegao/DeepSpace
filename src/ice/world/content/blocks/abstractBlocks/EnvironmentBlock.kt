package ice.world.content.blocks.abstractBlocks

import arc.Events
import arc.graphics.g2d.TextureRegion
import ice.core.IFiles
import mindustry.world.Block

open class EnvironmentBlock(name: String) :Block(name) {
  init {
    Events.on(mindustry.game.EventType.AtlasPackEvent::class.java) {
      Variants.setBlockVariants(this)
    }
  }

  override fun icons(): Array<TextureRegion> {
    return arrayOf(IFiles.findPng("${name}1"))
  }
}