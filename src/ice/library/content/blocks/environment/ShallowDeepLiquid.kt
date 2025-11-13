package ice.library.content.blocks.environment

import mindustry.world.Block

class ShallowDeepLiquid(name: String, blockfoor: Block): ShallowLiquid(name,blockfoor){
    init {
        overName="deep-water"
        dps=0.6f
    }
}