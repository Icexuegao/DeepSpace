package ice.library.content.blocks.production

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.struct.EnumSet
import arc.struct.Seq
import ice.library.scene.tex.IceColor
import mindustry.Vars
import mindustry.Vars.tilesize
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.BlockFlag

class MinerTower(name: String) : Block(name) {
    var minerTime = 60
    var minerRange = 50

    init {
        size = 2
        solid = true
        health = 30
        update = true
        hasItems = true
        itemCapacity = 50
        buildType = Prov(::MinerTowerBuild)
        flags = EnumSet.of(BlockFlag.drill)
        requirements(Category.production, ItemStack.with(Items.copper, 20))
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)
        Drawf.circles((x * tilesize+ offset), (y * tilesize+offset), (minerRange * 8).toFloat(), IceColor.b4)
    }

    inner class MinerTowerBuild : Building() {
        private val minerSeq = Seq<Tile>()
        private var time = 0f

        override fun updateTile() {
            time+=timeScale
            drawTime--
            if (time >= minerTime) {
                time = 0f
                minerSeq.clear()
                findMiner()
                miner()

            }
            dump()
        }

        private fun miner() {
            val randTile = minerSeq.random() ?: return
            val itemDrop = randTile.overlay().itemDrop
            if (acceptItem(this, itemDrop)) {
                handleStack(itemDrop, 1, this)
                setEffect(x, y, randTile, minerRange / 2)
            }
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return source==this&&items[item] < this.getMaximumAccepted(item)
        }

        override fun drawSelect() {
            super.drawSelect()
            Drawf.circles(x, y, (minerRange * 8).toFloat(), IceColor.b4)
        }

        private var drawTile: Tile? = null
        private var drawTime=0f
        override fun draw() {
            if (drawTile != null) {
                val t = drawTile!!
                Draw.color(Color.red)
                Draw.alpha(drawTime/10)
                Fill.rect(t.worldx(), t.worldy(), 8f, 8f)
            }
            Draw.color(Color.blue)
            super.draw()
        }

        private fun findMiner() {
            val range = minerRange * 2
            for (x in 0..range) {
                for (y in 0..range) {
                    val dx = x - minerRange
                    val dy = y - minerRange
                    val tile1 = Vars.world.tile(dx + tileX(), dy + tileY()) ?: continue
                    if (tile1.overlay().itemDrop != null && tile1.within(this.x, this.y, minerRange.toFloat() * 8)) {
                        minerSeq.addUnique(tile1)
                    }
                }
            }
        }

        private fun setEffect(dx: Float, dy: Float, tile1: Tile, size: Int) {
            drawTile = tile1
            drawTime=10f
            val lines = Seq<Vec2>()
            var x = dx
            var y = dy
            lines.add(Vec2(x, y))
            val length = tile.dst(tile1)
            val angle = tile.angleTo(tile1)
            val fl = length / size

val range=16
            //Log.info(trnsx)
            for (i in 1..size) {
                x += Angles.trnsx(angle, fl)
                y += Angles.trnsy(angle, fl)
                lines.add(Vec2(x + Mathf.range(range), y + Mathf.range(range)))
            }/*
            var rota =0f
            var x=dx
           var y=dy

            lines.add(Vec2(x, y))
            for (i in 0..4) {
                lines.add(Vec2(x + Mathf.range(3f), y + Mathf.range(3f)))
                x += Angles.trnsx(0f, dst / 2f)
                y += Angles.trnsy(rota, dst / 2f)
                rota += Angles.angle(x, y, tile.worldx(), tile.worldy())
            }*/

            Fx.lightning.at(x, y, angle, IceColor.b1, lines)
        }
    }
}

