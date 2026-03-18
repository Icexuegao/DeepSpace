package singularity.world.blocks.product

import ice.content.AtomSchematics
import singularity.type.SglContents

open class AtomSchematicCrafter(name: String) : MediumCrafter(name) {
  override fun init() {
    for (atomSchematic in AtomSchematics.AtomSchematic.all) {
      consumers.add(atomSchematic.request)
      super.newProduce()
      produce!!.item(atomSchematic.item, 1)
    }

    super.init()
  }
}