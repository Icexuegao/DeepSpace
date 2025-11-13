package ice.library.content.blocks.effect

import arc.Core
import arc.func.Prov
import mindustry.world.blocks.storage.CoreBlock

open class CoreBlock(name: String) : CoreBlock(name) {
    init {
        buildType = Prov(::CoreBuild)
    }

    inner class CoreBuild : CoreBlock.CoreBuild() {
        override fun updateLaunch() {
           // super.updateLaunch()
        }
        override fun drawLaunch() {

        }

        override fun zoomLaunch(): Float {
            Core.camera.position.set(this)
            return 4f
        }
    }
}