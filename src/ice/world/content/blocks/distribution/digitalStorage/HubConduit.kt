package ice.world.content.blocks.distribution.digitalStorage

import arc.Events
import arc.func.Prov
import arc.graphics.g2d.Draw
import ice.library.EventType
import ice.library.IFiles
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.world.Tile

class HubConduit(name: String) : IceBlock(name) {
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

    override fun blockChanged(tile: Tile) {
        Events.fire(EventType.LogisticsHubFire())
    }

    inner class DigitalConduitBuild : IceBuild() {
        var building: LogisticsHub.DigitalStorageBuild? = null
        var indx = 0
        override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            Events.on(EventType.LogisticsHubFire::class.java) {
                indx = if (isblock(0) && isblock(1) && isblock(2) && isblock(3)) {
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
            }
            return super.init(tile, team, shouldAdd, rotation)
        }

        override fun draw() {
            Draw.rect(texture[indx], x, y)
        }

        private fun isblock(pos: Int): Boolean {
            return nearby(pos) is DigitalConduitBuild
        }
    }
}