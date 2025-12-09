package ice.graphics.lightnings

import arc.func.Cons
import arc.func.Cons2
import arc.math.Interp
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Time
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import ice.graphics.lightnings.generator.LightningGenerator
import kotlin.math.max

/**闪电容器，使用一个闪电生成器产生闪电，由容器进行处理和绘制，通常用于一类闪电用同一个容器存储
 *
 * @since 1.5
 * @author EBwilson
 * */
open class LightningContainer : Iterable<Lightning?> {
    /**闪电从产生到完全出现需要的时间，这会平摊给每一段闪电，fps为当前帧率
     * 但如果这个值为0,那么闪电会立即出现 */
    var time: Float = 0f

    /**闪电的扩散速度，小于或等于0时默认使用time提供的路径扩散计算方式，否则使用给出的速度来处理闪电的扩散（单位：/tick）
     */
    @Deprecated("规范化，此API不再可用")
    var speed: Float = 0f

    /**闪电的存在时间 */
    var lifeTime: Float = 30f

    /**闪电消逝的过渡时间，若不设置则消失过度时间等于闪电的存在时间 */
    var fadeTime: Float = -1f

    /**闪电整体的宽度是否随闪电的持续时间淡出 */
    var fade: Boolean = true

    /**闪电是否随淡出过程从起点开始消失 */
    var backFade: Boolean = false

    /**闪电每一段宽度的随机区间 */
    var minWidth: Float = 2.5f
    var maxWidth: Float = 4.5f

    /**闪电的衰减变换器，传入的数值为闪电的存在时间进度 */
    var lerp: Interp = Interp.pow2Out

    /**闪电分支创建时调用的回调函数，一般用于定义闪电的分支子容器属性 */
    var branchCreated: Cons<Lightning?>? = null

    /**闪电顶点触发器，当一个闪电节点已到达后触发，传入前一个顶点和这一个顶点 */
    var trigger: Cons2<LightningVertex?, LightningVertex?>? = null
    var headClose: Boolean = false
    var endClose: Boolean = false

    protected var clipSize: Float = 0f

    protected val lightnings: Seq<Lightning> = Seq<Lightning>()

    /**使用给出的闪电生成器在容器中创建一道新的闪电 */
    fun create(generator: LightningGenerator) {
        generator.branched(branchCreated)
        val lightning = Lightning.create(
            generator, Mathf.random(minWidth, maxWidth), lifeTime, if (fadeTime > 0) fadeTime else lifeTime, lerp, time, fade, backFade, trigger
        )
        lightning.headClose = headClose
        lightning.endClose = endClose
        lightnings.add(lightning)
    }

    override fun iterator(): MutableIterator<Lightning?> {
        return lightnings.iterator()
    }

    /**更新一次当前容器中所有子闪电的状态 */
    fun update() {
        val itr = lightnings.iterator()
        while (itr.hasNext()) {
            val lightning = itr.next()
            clipSize = max(clipSize, lightning.clipSize)

            val progress = (Time.time - lightning.startTime) / lifeTime
            if (progress > 1) {
                itr.remove()
                Pools.free(lightning)
                clipSize = 0f
                continue
            }

            lightning.update()
        }
    }

    /**绘制容器，这会将容器中保存的所有闪电进行绘制
     *
     * @param x 绘制闪电的原点x坐标
     * @param y 绘制闪电的原点y坐标
     */
    fun draw(x: Float, y: Float) {
        for (lightning in lightnings) {
            lightning.draw(x, y)
        }
    }

    fun clipSize(): Float {
        return clipSize
    }

    /**闪电分支容器，用于绘制分支闪电，会递归绘制所有的子分支 */
    class PoolLightningContainer : LightningContainer(), Poolable {
        override fun reset() {
            time = 0f
            lifeTime = 0f
            clipSize = 0f
            maxWidth = 0f
            minWidth = 0f
            lerp = Interp { f: Float -> 1 - f }
            branchCreated = null
            trigger = null

            for (lightning in lightnings) {
                Pools.free(lightning)
            }
            lightnings.clear()
        }

        companion object {
            fun create(lifeTime: Float, minWidth: Float, maxWidth: Float): PoolLightningContainer {
                val result = Pools.obtain(PoolLightningContainer::class.java) { PoolLightningContainer() }
                result.lifeTime = lifeTime
                result.minWidth = minWidth
                result.maxWidth = maxWidth

                return result
            }
        }
    }
}

