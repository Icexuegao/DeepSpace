package ice.world.blocks.distribution.digitalStorage

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.util.Eachable
import ice.library.IFiles
import mindustry.content.Items
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block

class DigitalConduit(name: String) : Block(name) {
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
        requirements(Category.distribution, ItemStack.with(Items.copper, 2))
    }

    override fun load() {
        super.load()
        region = texture[0]
        uiIcon = texture[0]
        fullIcon = texture[0]
    }

    override fun drawPlan(plan: BuildPlan?, list: Eachable<BuildPlan>?, valid: Boolean) {
        super.drawPlan(plan, list, valid)
    }

    inner class DigitalConduitBuild : Building() {
        var building: DigitalStorage.DigitalStorageBuild? = null
        var color: Color = Color.valueOf("ff4949")
        override fun draw() {
            if (isblock(0) && isblock(1) && isblock(2) && isblock(3)) {
                Draw.rect(texture[10], x, y)
            } else if (isblock(1) && isblock(2) && isblock(3)) {
                Draw.rect(texture[9], x, y)
            } else if (isblock(0) && isblock(1) && isblock(2)) {
                Draw.rect(texture[8], x, y)
            } else if (isblock(0) && isblock(1) && isblock(3)) {
                Draw.rect(texture[7], x, y)
            } else if (isblock(0) && isblock(2) && isblock(3)) {
                Draw.rect(texture[6], x, y)
            } else if (isblock(3) && isblock(0)) {
                Draw.rect(texture[5], x, y)
            } else if (isblock(1) && isblock(0)) {
                Draw.rect(texture[4], x, y)
            } else if (isblock(3) && isblock(2)) {
                Draw.rect(texture[3], x, y)
            } else if (isblock(2) && isblock(1)) {
                Draw.rect(texture[2], x, y)
            } else if (isblock(1) || isblock(3)) {
                Draw.rect(texture[1], x, y)
            } else if (isblock(0) || isblock(2)) {
                Draw.rect(texture[0], x, y)
            } else {
                Draw.rect(texture[0], x, y)
            }
        }

        private fun isblock(pos: Int): Boolean {
            return nearby(pos) is DigitalConduitBuild
        }
    }
}