package ice.library.baseContent.blocks.distribution.digitalStorage

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.util.Eachable
import ice.library.IFiles
import ice.library.baseContent.blocks.abstractBlocks.IceBlock
import mindustry.entities.units.BuildPlan

class DigitalConduit(name: String) : IceBlock(name) {
    val texture = Array(11) { IFiles.findPng("$name-$it") }

    init {
        size = 1
        health = 50
        rotate = true
        update = false
        hasPower = true
        hasItems = false
        unloadable = false
        destructible = true
        acceptsItems = false
        conductivePower = true
        buildType = Prov(::DigitalConduitBuild)
    }

    override fun load() {
        super.load()
        region
        uiIcon = texture[0]
        fullIcon = texture[0]
    }

    override fun drawPlan(plan: BuildPlan?, list: Eachable<BuildPlan>?, valid: Boolean) {
        super.drawPlan(plan, list, valid)
    }

    inner class DigitalConduitBuild : IceBuild() {
        var building: DigitalStorage.DigitalStorageBuild? = null
        var color: Color = Color.valueOf("ff4949")
        override fun draw() {
            val indx: Int = if (isblock(0) && isblock(1) && isblock(2) && isblock(3)) {
                10
            } else if (isblock(1) && isblock(2) && isblock(3)) {
                9
            } else if (isblock(0) && isblock(1) && isblock(2)) {
                8
            } else if (isblock(0) && isblock(1) && isblock(3)) {
                7
            } else if (isblock(0) && isblock(2) && isblock(3)) {
                6
            } else if (isblock(3) && isblock(0)) {
                5
            } else if (isblock(1) && isblock(0)) {
                4
            } else if (isblock(3) && isblock(2)) {
                3
            } else if (isblock(2) && isblock(1)) {
                2
            } else if (isblock(1) || isblock(3)) {
                1
            } else if (isblock(0) || isblock(2)) {
                0
            } else {
                0
            }
            Draw.rect(texture[indx], x, y)
        }

        private fun isblock(pos: Int): Boolean {
            return nearby(pos) is DigitalConduitBuild
        }
    }
}