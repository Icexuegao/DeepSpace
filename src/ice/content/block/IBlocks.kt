package ice.content.block

import ice.library.world.Load

object IBlocks : Load {
    override fun load() {
        Environment.load()
        Defense.load()
        Production.load()
        Distribution.load()
        Power.load()
        Liquid.load()
        Crafting.load()
        Effect.load()
        Turret.load()
    }
}