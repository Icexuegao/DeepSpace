package ice.Alon.library.PathfindAlgorithm

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.struct.ObjectMap
import arc.struct.Queue
import arc.struct.Seq
import ice.Alon.library.drawUpdate.DrawUpdates
import ice.Alon.library.drawUpdate.DrawUpdates.Companion.updateSeq
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Items


class Pathfind() : DrawUpdates.DrawUpdate() {
    var draw = true

    /**
     * 存储他和他的上一个po2 用于递归
     */
    private val map = ObjectMap<Point2, Point2?>()

    /**
     * 唯一的po2
     */
    private val visited = Seq<Point2>()

    /**
     * 不断循环寻找新的po2
     */
    private val queue = Queue<Point2>()

    /**
     * path路径数组有序
     */
    private val path = Seq<Point2>()

    /**
     * 完成遍历
     */
    var remove = false

    /**
     * 最后的po2,也是索引目标
     */
    private var end: Point2? = null

    /**
     * 初始遍历po2
     */
    private lateinit var begin:Point2

    /**
     * path数组是否获取过了
     */
    var pathFirst = false

    /**
     * 绘画判断 路径或者广度
     */
    private var drawto = false

    /**
     * 构造函数
     */
   constructor(begin: Point2) : this() {
        this.begin = begin
        queue.add(begin)
        map.put(begin, null)
        updateSeq.add(this)
   }

    /**
     * 返回path数组
     */
    fun getPath(): Seq<Point2> {
        return path
    }

    /**
     * 递归获取path po2并且添加到path
     */
    private fun path(end: Point2?) {
        if (end != null) {
            path.add(end)
            path(map[end])
        }
    }

    override var overall=false


    /**
     * 绘制遍历过的区块
     */
    override fun draw() {
        if (draw) {
            Draw.color(Color.red)
            Draw.alpha(0.4f)
            if (drawto) {
                path.each { p: Point2 ->
                    Fill.crect(
                        p.x * Vars.tilesize - Vars.tilesize.toFloat() / 2,
                        p.y * Vars.tilesize - Vars.tilesize.toFloat() / 2,
                        Vars.tilesize.toFloat(),
                        Vars.tilesize.toFloat()
                    )
                }
                Draw.reset()
            } else {
                for (e in visited) {
                    Fill.crect(
                        e.x * Vars.tilesize - Vars.tilesize.toFloat() / 2,
                        e.y * Vars.tilesize - Vars.tilesize.toFloat() / 2,
                        Vars.tilesize.toFloat(),
                        Vars.tilesize.toFloat()
                    )
                }
            }
            Draw.reset()
        }
    }
    override fun update() {
        /**只完整遍历一次,这意味着每一个Pathfind只能使用一次 */
        if (!remove) traversal()
        /**是否找到目标 找到就递归添加进path数组 */
        if (end != null) {
            if (!pathFirst) {
                path(end)
                pathFirst = true
                /**处理draw */
                drawto = true
            }
        }
        /**如果begin的位置不存在block则说明block被删除了 this也理应删除 否则虚空绘制 */
        if (Vars.world.tile(begin.x, begin.y).block() === Blocks.air) kill()
    }

    /**
     * 开始遍历queue数值 运行一次 只会向queue添加一次周围的po2
     */
    private fun traversal() {
        val size = queue.size
        val iterator = queue.iterator()
        val tmp = arrayOfNulls<Point2>(size * 4)
        var idx = 0
        for (i1 in 0 until size) {
            val e = iterator.next()
            iterator.remove()
            for (i in 0..3) {
                val point = Point2(Geometry.d4(i).x + e.x, Geometry.d4(i).y + e.y)
                val tile1 = Vars.world.tile(point.x, point.y)
                if (tile1 == null || visited.contains(point)) continue
                if (tile1.build != null) continue
                if (tile1.block() !== Blocks.air) continue
                if (tile1.overlay().itemDrop != null && tile1.overlay().itemDrop === Items.copper) {
                    remove = true
                    end = point
                }
                map.put(point, e)
                visited.add(point)
                tmp[idx++] = point
            }
        }
        var i = 0
        while (i < size * 4 && tmp[i] != null) {
            queue.add(tmp[i])
            i++
        }
    }

    /**
     * 删除建筑时调用否则会虚空绘制
     */
    override fun kill() {
        map.clear()
        visited.clear()
        queue.clear()
        path.clear()
        super.kill()
    }
}