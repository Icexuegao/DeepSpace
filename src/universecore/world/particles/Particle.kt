package universecore.world.particles

import arc.func.Boolf
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.geom.Vec2
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Interval
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pool
import arc.util.pooling.Pools
import mindustry.entities.EntityGroup
import mindustry.gen.Bullet
import mindustry.gen.Decal
import mindustry.gen.Groups
import kotlin.math.max

class Particle : Decal(), Iterable<Particle.Cloud> {
  companion object {
    private var counter = 0

    /**粒子的最大共存数量,总量大于此数目时,创建新的粒子会清除最先产生的粒子 */
    var maxAmount: Int = 1024

     val all: ObjectSet<Particle?> = ObjectSet<Particle?>()
     val temp = Seq<Particle>()

    fun count(): Int {
      return all.size
    }

    fun get(filter: Boolf<Particle>): Seq<Particle> {
      temp.clear()
      for (particle in all) {
        if (filter.get(particle)) temp.add(particle)
      }
      return temp
    }
  }
  var dest: Vec2 = Vec2()
  var eff: Float = 0f

  var timer: Interval = Interval(6)
  var owner: Bullet? = null
  var bullet: Bullet? = null

  var strength: Float = 1f
  var deflectAngle: Float = 45f

   var startPos: Vec2 = Vec2()
   var clipSize: Float = 0f

  var currentCloud: Cloud? = null
  var firstCloud: Cloud? = null
  var cloudCount: Int = 0

  var maxCloudCounts: Int = -1

  var parent: Particle? = null

  /**粒子的速,矢量 */
  var speed: Vec2 = Vec2()

  /**粒子当前的尺寸 */
  var size: Float = 0f

  var defSpeed: Float = 0f
  var defSize: Float = 0f

  /**粒子模型,决定了该粒子的行为 */
  var model: ParticleModel? = null
  var layer: Float = 0f

  fun cloudCount(): Float {
    return cloudCount.toFloat()
  }

  override fun add() {
    index__all = Groups.all.addIndex(this)
    index__draw = Groups.draw.addIndex(this)

    all.add(this)

    counter++

    currentCloud = Pools.get<Cloud?>(Cloud::class.java,::Cloud, 65536).obtain()
    currentCloud!!.x = x
    currentCloud!!.y = y
    currentCloud!!.size = 0f
    currentCloud!!.color.set(model!!.trailColor(this))

    firstCloud = currentCloud

    added = true

    model!!.init(this)

    if (counter >= maxAmount) {
      remove()
    }
  }

  override fun draw() {
    val l = Draw.z()
    Draw.z(layer)

    if (parent != null) {
      x += parent!!.x
      y += parent!!.y
    }

    model!!.draw(this)

    if (currentCloud != null) {
      model!!.drawTrail(this)
    }

    if (parent != null) {
      x -= parent!!.x
      y -= parent!!.y
    }

    Draw.z(l)
    Draw.reset()
  }

  override fun update() {
    model!!.deflect(this)
    x += speed.x * Time.delta
    y += speed.y * Time.delta
    size = model!!.currSize(this)
    model!!.update(this)
    val c = Pools.get(Cloud::class.java,::Cloud, 65536).obtain()
    c.x = if (parent == null) x else x + parent!!.x
    c.y = if (parent == null) y else y + parent!!.y
    c.size = size
    c.color.set(model!!.trailColor(this))

    c.perCloud = currentCloud
    currentCloud!!.nextCloud = c

    currentCloud = c

    cloudCount++

    for (cloud in currentCloud!!) {
      model!!.updateTrail(this, cloud!!)
    }

    var mark = false
    while (firstCloud!!.nextCloud != null) {
      if (maxCloudCounts in 1..<cloudCount || model!!.isFaded(this, firstCloud!!)) {
        mark = !(maxCloudCounts > 0 && cloudCount > maxCloudCounts)
        popFirst()
      } else break
    }

    if (!mark && (parent != null && !parent!!.isAdded || model!!.isFinal(this))) {
      popFirst()
      if (cloudCount > 4) popFirst()
    }

    if (cloudCount <= 4 && model!!.isFinal(this)) remove()
  }

  private fun popFirst() {
    val n = firstCloud!!.nextCloud
    n!!.perCloud = null
    Pools.free(firstCloud)
    firstCloud = n
    cloudCount--
  }

  override fun remove() {
    if (added) {
      Groups.all.removeIndex(this, this.index__all)
      index__all = -1
      Groups.draw.removeIndex(this, this.index__draw)
      index__draw = -1
      Groups.queueFree(this)

      all.remove(this)
      counter--
      added = false
    }
  }

  override fun classId(): Int {
    return 102
  }

  override fun clipSize(): Float {
    return max(Tmp.v1.set(x, y).sub(startPos).len(), clipSize).also { clipSize = it }
  }

  override fun reset() {
    added = false
    parent = null
    id = EntityGroup.nextId()
    lifetime = 0f
    region = null
    rotation = 0f
    time = 0f
    x = 0f
    y = 0f

    maxCloudCounts = -1

    speed.setZero()
    startPos.setZero()

    layer = 0f
    clipSize = 0f

    while (firstCloud!!.nextCloud != null) {
      popFirst()
    }
    Pools.free(firstCloud)

    currentCloud = null
    firstCloud = null

    cloudCount = 0
    size = 0f

    //extra().clear();
    model = null

    color.set(Color.white)
  }

  override fun iterator(): Iterator<Cloud> {
    return currentCloud!!.iterator()
  }

  class Cloud : Pool.Poolable, Iterable<Cloud> {
    val color: Color = Color()

    var x: Float = 0f
    var y: Float = 0f
    var size: Float = 0f
    var perCloud: Cloud? = null
    var nextCloud: Cloud? = null

    var itr =Itr()

    @JvmOverloads
    fun draw(modulate: Float = 1f, modulateNext: Float = 1f) {
      Draw.color(color)

      if (perCloud != null && nextCloud != null) {
        var angle = Angles.angle(x - perCloud!!.x, y - perCloud!!.y)
        val dx1 = Angles.trnsx(angle + 90, size * modulate)
        val dy1 = Angles.trnsy(angle + 90, size * modulate)
        angle = Angles.angle(nextCloud!!.x - x, nextCloud!!.y - y)
        val dx2 = Angles.trnsx(angle + 90, nextCloud!!.size * modulateNext)
        val dy2 = Angles.trnsy(angle + 90, nextCloud!!.size * modulateNext)

        Fill.quad(
          x + dx1,
          y + dy1,
          x - dx1,
          y - dy1,
          nextCloud!!.x - dx2,
          nextCloud!!.y - dy2,
          nextCloud!!.x + dx2,
          nextCloud!!.y + dy2
        )
      } else if (perCloud == null && nextCloud != null) {
        val angle = Angles.angle(nextCloud!!.x - x, nextCloud!!.y - y)
        val dx2 = Angles.trnsx(angle + 90, nextCloud!!.size * modulate)
        val dy2 = Angles.trnsy(angle + 90, nextCloud!!.size * modulate)

        Fill.quad(x, y, x, y, nextCloud!!.x - dx2, nextCloud!!.y - dy2, nextCloud!!.x + dx2, nextCloud!!.y + dy2)
      }
    }

    override fun reset() {
      x = 0f
      y = 0f
      size = 0f
      color.set(Color.clear)

      perCloud = null
      nextCloud = null
    }

    override fun iterator(): Iterator<Cloud> {
      itr.reset()
      return itr
    }

     inner class Itr : Iterator<Cloud> {
      var curr: Cloud? = this@Cloud

      fun reset() {
        curr = this@Cloud
      }

      override fun hasNext(): Boolean {
        return curr!!.perCloud != null
      }

      override fun next(): Cloud {
        return curr!!.perCloud.also { curr = it }!!
      }
    }
  }


}