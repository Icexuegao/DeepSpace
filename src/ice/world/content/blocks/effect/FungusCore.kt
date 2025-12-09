package ice.world.content.blocks.effect

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Rand
import arc.struct.Seq
import ice.library.IFiles
import mindustry.content.Blocks
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.world.Tile
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.meta.Env

class FungusCore(name: String) : CoreBlock(name) {
    init {
        size = 2
        buildType = Prov(::FungusCoreBuild)
        envEnabled = Env.any
    }

    inner class FungusCoreBuild : CoreBuild() {
        val tiles = Seq<Tile>()
        val floort= Array<TextureRegion>(2){
            IFiles.findPng("fungusTower-floor-$it")
        }
        val blockt= Array<TextureRegion>(3){
            IFiles.findPng("fungusTower-block-$it")
        }
        val rand= Rand()
        override fun draw() {
            super.draw()
            tiles.forEach {
                rand.setSeed(it.pos().toLong())
               if (it.block()== Blocks.air){
                   Draw.rect(floort[rand.random(1)],it.drawx(),it.drawy())
               }
                if (it.block()!= Blocks.air&&it.build==null){
                    Draw.rect(blockt[rand.random(2)],it.drawx(),it.drawy())
                }
            }
            /*  queue.forEach {
                  Draw.color(arc.graphics.Color.red)
                  Fill.rect(it.drawx(), it.drawy(), 8f, 8f)
              }*/

        }

        override fun init(tile: Tile, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            tiles.add(tile.nearby(2))
            return super.init(tile, team, shouldAdd, rotation)
        }

        override fun updateTile() {
            super.updateTile()
            val timer1 = timer(timerDump, 30f)
            if (timer1) {
                df()
            }
        }
        fun df(){
            val nearby = tiles.random().nearby((0..3).random())
            if (!tiles.contains(nearby)&&nearby.build==null)tiles.add(nearby) else df()
        }
    }
}