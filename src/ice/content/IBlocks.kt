package ice.content

import ice.content.block.*
import ice.library.world.ContentLoad

object IBlocks : ContentLoad {
    init {
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