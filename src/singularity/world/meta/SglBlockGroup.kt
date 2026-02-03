package singularity.world.meta

import mindustry.world.meta.BlockGroup
import universecore.util.handler.EnumHandler

object SglBlockGroup {
  private val handler = EnumHandler(BlockGroup::class.java)

  var nuclear: BlockGroup = handler.addEnumItemTail("nuclear", true)
}