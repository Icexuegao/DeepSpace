package ice.library.pathfindAlgorithm;

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.struct.Seq
import ice.library.sortAlgorithm.InsertionSorting.sort
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.world.Tile
import mindustry.world.blocks.storage.CoreBlock
import java.util.*
import kotlin.math.abs

class PFPathFind(var begin: Tile, var target: Tile) {
    var timer: Int = 0

    fun draw() {
        Draw.color(Color.red)
        Draw.alpha(0.4f)
        visited.forEach { p: Point2 ->
            Fill.crect(
                p.x * Vars.tilesize - Vars.tilesize.toFloat() / 2,
                p.y * Vars.tilesize - Vars.tilesize.toFloat() / 2,
                Vars.tilesize.toFloat(),
                Vars.tilesize.toFloat()
            )
        }
    }

    var b: Boolean = false

    fun update() {
        if (b) return
        timer++
        if (timer >= 1) {
            timer = 0
            traversal()
        }
    }

    fun forecastCost(currentTile: Point2): Int {
        return (abs((currentTile.x - target.x).toDouble()) + abs((currentTile.y - target.y).toDouble())).toInt()
    }

    var visited: Seq<Point2> = Seq()

    fun lowP(map: IdentityHashMap<String?, Point2?>): Array<Any?>? {
        if (map.isEmpty()) return null
        val seq = Seq(map.keys.toTypedArray<String?>())
        val array = seq.toArray()
        val indices = IntArray(array.size)
        for (i in indices.indices) {
            indices[i] = array[i]!!.toInt()
        }
        val sort = sort(indices)
        val i = sort[0]
        val i1 = seq.indexOf(i.toString())
        val s = seq[i1]
        return arrayOf(map[s], s)
    }

    var low: Point2?
    var costMap: IdentityHashMap<String?, Point2?> = IdentityHashMap()

    init {
        low = Point2(begin.x.toInt(), begin.y.toInt())
    }

    fun traversal() {
        if (low!!.equals(target.x.toInt(), target.y.toInt())) {
            b = true
            return
        }
        for (i in 0..3) {
            val point = Point2(Geometry.d4(i).x + low!!.x, Geometry.d4(i).y + low!!.y)
            val tile1 = Vars.world.tile(point.x, point.y)
            if (tile1 == null || visited.contains(point)) {
                continue
            }

            // if (tile1.build != null) continue;
            if (tile1.block() === Blocks.air || tile1.block() is CoreBlock) {
            } else {
                continue
            }

            val i1 = forecastCost(point)

            val s = i1.toString()
            costMap[s] = point
            visited.add(point)
        }
        val objects = lowP(costMap) ?: return
        low = objects[0] as Point2?
        costMap.remove(objects[1] as String?)
    }
}
