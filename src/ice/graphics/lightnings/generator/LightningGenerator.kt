package ice.graphics.lightnings.generator

import arc.func.Cons
import arc.func.Func2
import arc.math.Angles
import arc.math.Mathf
import arc.math.Rand
import arc.util.pooling.Pool
import arc.util.pooling.Pools
import ice.graphics.lightnings.Lightning
import ice.graphics.lightnings.LightningVertex

/**闪电生成器基类，同时实现了Iterator和Iterable接口，可以使用for-each循环形式逐个产生顶点，每一次获取迭代器都将返回生成器自身，并重置迭代状态
 * 注意，任何在迭代器运作外的时机变更生成器属性，都会直接影响迭代产生的顶点分布情况，而生成器是可复用的，每次迭代都会产生互不相关的一组顶点
 * <p>警告：这个方法不是线程安全的，任何时候要避免同时迭代此对象
 *
 * @since 1.5
 * @author EBwilson
 *  移动到图形模块中*/
abstract class LightningGenerator : Iterable<LightningVertex>, Iterator<LightningVertex> {
    var seed: Rand = Rand()

    /**顶点基准间距最小值 */
    var minInterval: Float = 6f

    /**顶点基准位置最大值 */
    var maxInterval: Float = 18f

    /**闪电顶点离散程度，越高则顶点偏移越远 */
    var maxSpread: Float = 12.25f

    /**产生分支的几率（每一个顶点） */
    var branchChance: Float = 0f

    /**最小分支强度 */
    var minBranchStrength: Float = 0.3f

    /**最大分支强度 */
    var maxBranchStrength: Float = 0.8f

    /**分支创建器，传入分支所在的顶点以及分支的强度，需要返回一个闪电生成器，注意，任何生成器对象都可以被传入，请不要new创建生成器 */
    var branchMaker: Func2<LightningVertex?, Float?, LightningGenerator>? = null

    var branchCreated: Cons<Lightning?>? = null
    var blockNow: Floatp2<LightningVertex?, LightningVertex?>? = null

    private var curr: Lightning? = null

    protected var last: LightningVertex? = null
    protected var isEnding: Boolean = false

    private var offsetX = 0f
    private var offsetY = 0f

    fun setCurrentGen(curr: Lightning) {
        this.curr = curr
    }

    fun branched(branchCreated: Cons<Lightning?>?) {
        this.branchCreated = branchCreated
    }

    /**使用当前的分支生成器对顶点创建一条分支闪电 */
    fun createBranch(vertex: LightningVertex) {
        val strength = Mathf.clamp(Mathf.random(minBranchStrength, maxBranchStrength))
        val gen = branchMaker!!.get(vertex, strength)
        gen.setOffset(vertex.x, vertex.y)
        val old = gen.blockNow
        gen.blockNow = Floatp2 { l: LightningVertex?, v: LightningVertex? -> old?.get(l, v) ?: if (blockNow != null) blockNow!!.get(l, v) else -1f }
        vertex.branchOther = Lightning.create(
            gen, curr!!.width * strength, curr!!.lifeTime, curr!!.fadeTime, curr!!.lerp, curr!!.time, curr!!.fade, curr!!.backFade, curr!!.trigger
        )
        gen.blockNow = old
        gen.resetOffset()

        vertex.branchOther?.vertices?.first()?.let { it.isStart = false }

        if (branchCreated != null) branchCreated!!.get(vertex.branchOther)
    }

    /**此类同时实现了可迭代和迭代器接口，即可以进行for-each循环来逐个产生顶点，这个方法不是线程安全的 */
    @Synchronized
    override fun iterator(): Iterator<LightningVertex> {
        reset()
        return this
    }

    open fun reset() {
        last = null
        isEnding = false
    }

    /**迭代器通过这个方法获取下一个顶点 */
    override fun next(): LightningVertex {
        val vertex = Pools.obtain(LightningVertex::class.java, null)
        handleVertex(vertex)
        offsetVertex(vertex)
        afterHandle(vertex)

        var blockLen = 0f
        if (blockNow != null && last != null && (blockNow!!.get(last, vertex).also { blockLen = it }) > 0) {
            isEnding = true
            vertex.isEnd = true
            val angle = Mathf.angle(vertex.x - last!!.x, vertex.y - last!!.y)
            vertex.x = last!!.x + Angles.trnsx(angle, blockLen)
            vertex.y = last!!.y + Angles.trnsy(angle, blockLen)
            offsetVertex(vertex)
            afterHandle(vertex)
            return vertex
        }

        if (!vertex.isStart && !vertex.isEnd && branchChance > 0 && Mathf.chance(branchChance.toDouble())) {
            createBranch(vertex)
        }
        last = vertex
        return vertex
    }

    override fun hasNext(): Boolean {
        return !isEnding
    }

    /**在顶点处理之后调用 */
    fun afterHandle(vertex: LightningVertex) {
        if (last == null) return
        vertex.angle = Mathf.angle(vertex.x - last!!.x, vertex.y - last!!.y)
    }

    fun offsetVertex(vertex: LightningVertex) {
        vertex.x += offsetX
        vertex.y += offsetY
    }

    fun setOffset(dx: Float, dy: Float) {
        offsetX = dx
        offsetY = dy
    }

    fun resetOffset() {
        offsetX = 0f
        offsetY = offsetX
    }

    fun isEnclosed(): Boolean {
        return false
    }

    /**顶点处理，实现以为顶点分配属性，如坐标等 */
    protected abstract fun handleVertex(vertex: LightningVertex)

    /**返回当前闪电的裁剪大小，此大小应当能够完整绘制闪电 */
    abstract fun clipSize(): Float

    companion object {
        val vertexPool: Pool<LightningVertex?>

        init {
            Pools.set<LightningVertex?>(LightningVertex::class.java, object : Pool<LightningVertex?>(8192, 65536) {
                override fun newObject(): LightningVertex {
                    return LightningVertex()
                }
            }.also { vertexPool = it })
        }
    }
}
