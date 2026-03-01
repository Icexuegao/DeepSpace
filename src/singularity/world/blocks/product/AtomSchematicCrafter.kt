package singularity.world.blocks.product

import singularity.type.SglContents

open class AtomSchematicCrafter(name: String) : MediumCrafter(name) {
  override fun init() {
    for (atomSchematic in SglContents.atomSchematics()) {
      consumers.add(atomSchematic.request)
      super.newProduce()
      produce!!.item(atomSchematic.item, 1)
    }

    super.init()
  }
}