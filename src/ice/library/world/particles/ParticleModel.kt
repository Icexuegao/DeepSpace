package ice.library.world.particles


import arc.graphics.Color
import arc.util.pooling.Pools
import ice.library.world.particles.Particle.Cloud
import mindustry.graphics.Layer


open class ParticleModel {

    /**使用该模型创建一个粒子的实例
     *
     * @param x 粒子创建时的x坐标
     * @param y 粒子创建时的y坐标
     * @param color 粒子的颜色
     * @param sx 粒子初始运动速度的x分量
     * @param sy 粒子初始运动速度的y分量
     * @param size 粒子的尺寸
     */
    fun create(x: Float, y: Float, color: Color?, sx: Float, sy: Float, size: Float): Particle {
        return create(x, y, color, sx, sy, size, Layer.effect)
    }

    /**使用该模型创建一个粒子的实例
     *
     * @param parent 粒子所属的父级粒子
     * @param x 粒子创建时的x坐标
     * @param y 粒子创建时的y坐标
     * @param color 粒子的颜色
     * @param sx 粒子初始运动速度的x分量
     * @param sy 粒子初始运动速度的y分量
     * @param size 粒子的尺寸
     */
    fun create(parent: Particle?, x: Float, y: Float, color: Color?, sx: Float, sy: Float, size: Float): Particle {
        return create(parent, x, y, color, sx, sy, size, Layer.effect)
    }

    /**使用该模型创建一个粒子的实例
     *
     * @param x 粒子创建时的x坐标
     * @param y 粒子创建时的y坐标
     * @param color 粒子的颜色
     * @param sx 粒子初始运动速度的x分量
     * @param sy 粒子初始运动速度的y分量
     * @param size 粒子的尺寸
     * @param layer 粒子所在的层，这只在绘制流程中使用
     */
    fun create(x: Float, y: Float, color: Color?, sx: Float, sy: Float, size: Float, layer: Float): Particle {
        return create(null, x, y, color, sx, sy, size, layer)
    }

    /**使用该模型创建一个粒子的实例
     *
     * @param parent 粒子所属的父级粒子
     * @param x 粒子创建时的x坐标
     * @param y 粒子创建时的y坐标
     * @param color 粒子的颜色
     * @param sx 粒子初始运动速度的x分量
     * @param sy 粒子初始运动速度的y分量
     * @param size 粒子的尺寸
     * @param layer 粒子所在的层，这只在绘制流程中使用
     */
    fun create(parent: Particle?, x: Float, y: Float, color: Color?, sx: Float, sy: Float, size: Float, layer: Float): Particle {
        val ent = Pools.obtain(Particle::class.java) { Particle() }
        ent.parent = parent
        ent.x = x
        ent.y = y
        ent.color.set(color)
        ent.layer = layer
        ent.startPos.set(x, y)
        ent.speed.set(sx, sy)
        ent.defSpeed = ent.speed.len()
        ent.defSize = size
        ent.size = currSize(ent)

        ent.model = this
        ent.add()

        return ent
    }

    open fun draw(p: Particle) {
    }

    open fun updateTrail(p: Particle, c: Cloud) {
    }

    open fun update(p: Particle) {
    }

    open fun deflect(p: Particle) {
    }

    open fun drawTrail(c: Particle) {
    }

    open fun init(particle: Particle) {
    }

    open fun isFinal(p: Particle): Boolean {
        return false
    }

    open fun trailColor(p: Particle): Color? {
        return null
    }

    open fun currSize(p: Particle): Float {
        return p.defSize
    }

    open fun isFaded(p: Particle, cloud: Cloud): Boolean {
        return false
    }
}