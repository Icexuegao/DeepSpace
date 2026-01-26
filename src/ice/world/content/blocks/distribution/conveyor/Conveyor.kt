package ice.world.content.blocks.distribution.conveyor

import arc.func.Boolf
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Nullable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.core.Placement
import ice.graphics.TextureRegionArrArrDelegate
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.content.blocks.distribution.Junction
import ice.world.content.blocks.distribution.conveyor.Autotiler.SliceMode
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.entities.TargetPriority
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.gen.Teamc
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.world.Block
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.blocks.distribution.ChainedBuilding
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class Conveyor(name: String) : IceBlock(name), Autotiler {
  companion object {
    private const val itemSpace = 0.4f
    private const val capacity = 3
  }

  var regions: Array<Array<TextureRegion>> by TextureRegionArrArrDelegate(this.name, 5, 4)
  var speed: Float = 0f
  var pushUnits: Boolean = true
  lateinit var junctionReplacement: Junction
  lateinit var bridgeReplacement: TransferNode

  init {
    rotate = true
    update = true
    group = BlockGroup.transportation
    hasItems = true
    itemCapacity = capacity
    priority = TargetPriority.transport
    conveyorPlacement = true
    underBullets = true
    ambientSound = Sounds.loopConveyor
    ambientSoundVolume = 0.0022f
    unloadable = false
    noUpdateDisabled = false
    buildType = Prov(::ConveyorBuild)
  }

  override fun icons(): Array<TextureRegion> {
    return arrayOf(regions[0][0])
  }

  override fun setStats() {
    super.setStats()
    //speed值直接对应每秒运输的物品数量
    stats.add(Stat.itemsMoved, speed, StatUnit.itemsSecond)
  }

  override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
    val bits = getTiling(plan, list) ?: return
    val region = regions[bits[0]][0]
    Draw.rect(region, plan.drawx(), plan.drawy(), region.width * bits[1] * region.scl(), region.height * bits[2] * region.scl(), (plan.rotation * 90).toFloat())
  }

  override fun blends(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
    return (otherblock.outputsItems() || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems)) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock)
  }

  //stack conveyors should be bridged over, not replaced
  override fun canReplace(other: Block?): Boolean {
    return super.canReplace(other) && other !is StackConveyor
  }

  override fun handlePlacementLine(plans: Seq<BuildPlan>) {

    Placement.calculateBridges(plans, bridgeReplacement, true) { b: Block -> b is Conveyor }
    //  if (bridgeReplacement is ItemBridge) Placement.calculateBridges(plans, bridgeReplacement as ItemBridge?, hasJuntionReplacement) { b: Block? -> b is Conveyor }
  }

  override fun isAccessible(): Boolean {
    return true
  }

  override fun getReplacement(req: BuildPlan, plans: Seq<BuildPlan?>): Block? {
    val cont = Boolf { p: Point2? -> plans.contains { o: BuildPlan? -> o!!.x == req.x + p!!.x && o.y == req.y + p.y && (req.block is Conveyor || req.block is Junction) } }
    return if (cont.get(Geometry.d4(req.rotation)) && cont.get(Geometry.d4(req.rotation - 2)) && req.tile() != null && req.tile().block() is Conveyor && Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1) junctionReplacement else this
  }

  open inner class ConveyorBuild : Building(), ChainedBuilding {
    //并行数组数据
    var ids: Array<Item?> = arrayOfNulls(capacity)
    var xs: FloatArray = FloatArray(capacity)
    var ys: FloatArray = FloatArray(capacity)

    //项目数量，始终<容量
    var len: Int = 0

    //next entity
    @Nullable
    var next: Building? = null

    @Nullable
    var nextc: ConveyorBuild? = null

    //whether the next conveyor's rotation == tile rotation
    var aligned: Boolean = false
    var lastInserted: Int = 0
    var mid: Int = 0
    var minitem: Float = 1f
    var blendbits: Int = 0
    var blending: Int = 0
    var blendsclx: Int = 1
    var blendscly: Int = 1
    var clogHeat: Float = 0f

    override fun draw() {
      // speed值直接对应每秒运输的物品数量
      // Time.time也是60倍放大的，所以需要除以60f
      val frame = if (enabled && clogHeat <= 0.5f) (((Time.time * speed * 8f * itemSpace * timeScale * efficiency / 60f)) % 4).toInt() else 0
      //draw extra conveyors facing this one for non-square tiling purposes
      Draw.z(Layer.blockUnder)
      for (i in 0..3) {
        if ((blending and (1 shl i)) != 0) {
          val dir = rotation - i
          val rot = (if (i == 0) rotation * 90 else (dir) * 90).toFloat()

          Draw.rect(sliced(regions[0][frame], if (i != 0) SliceMode.bottom else SliceMode.top), x + Geometry.d4x(dir) * Vars.tilesize * 0.75f, y + Geometry.d4y(dir) * Vars.tilesize * 0.75f, rot)
        }
      }

      Draw.z(Layer.block - 0.2f)

      Draw.rect(regions[blendbits][frame], x, y, (Vars.tilesize * blendsclx).toFloat(), (Vars.tilesize * blendscly).toFloat(), (rotation * 90).toFloat())

      Draw.z(Layer.block - 0.1f)
      val layer = Layer.block - 0.1f
      val wwidth = Vars.world.unitWidth().toFloat()
      val wheight = Vars.world.unitHeight().toFloat()
      val scaling = 0.01f

      for (i in 0..<len) {
        val item = ids[i] ?: continue
        Tmp.v1.trns((rotation * 90).toFloat(), Vars.tilesize.toFloat(), 0f)
        Tmp.v2.trns((rotation * 90).toFloat(), -Vars.tilesize / 2f, xs[i] * Vars.tilesize / 2f)
        val ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x)
        val iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y)
        //keep draw position deterministic.
        Draw.z(layer + (ix / wwidth + iy / wheight) * scaling)
        Draw.rect(item.fullIcon, ix, iy, Vars.itemSize, Vars.itemSize)
      }
    }

    override fun payloadDraw() {
      Draw.rect(block.fullIcon, x, y)
    }

    override fun drawCracks() {
      Draw.z(Layer.block - 0.15f)
      super.drawCracks()
    }

    override fun overwrote(builds: Seq<Building>) {
      val build = builds.first()
      if (build is ConveyorBuild) {
        ids = build.ids.clone()
        xs = build.xs.clone()
        ys = build.ys.clone()
        len = build.len
        clogHeat = build.clogHeat
        lastInserted = build.lastInserted
        mid = build.mid
        minitem = build.minitem
        items.add(build.items)
      }
    }

    override fun shouldAmbientSound(): Boolean {
      return clogHeat <= 0.5f
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      val bits = buildBlending(tile, rotation, null, true)
      blendbits = bits[0]
      blendsclx = bits[1]
      blendscly = bits[2]
      blending = bits[4]

      next = front()
      nextc = if (next is ConveyorBuild && next!!.team === team) next as ConveyorBuild else null
      aligned = nextc != null && rotation == next!!.rotation
    }

    override fun unitOn(unit: Unit) {
      if (!pushUnits || clogHeat > 0.5f || !enabled) return

      noSleep()
      // speed值直接对应每秒运输的物品数量
      // 单位移动速度 = speed * Vars.tilesize / 60f * itemSpace
      val mspeed = speed * Vars.tilesize / 60f * itemSpace
      val centerSpeed = 0.1f
      val centerDstScl = 3f
      val tx = Geometry.d4x(rotation).toFloat()
      val ty = Geometry.d4y(rotation).toFloat()
      var centerx = 0f
      var centery = 0f

      if (abs(tx) > abs(ty)) {
        centery = Mathf.clamp((y - unit.y()) / centerDstScl, -centerSpeed, centerSpeed)
        if (abs(y - unit.y()) < 1f) centery = 0f
      } else {
        centerx = Mathf.clamp((x - unit.x()) / centerDstScl, -centerSpeed, centerSpeed)
        if (abs(x - unit.x()) < 1f) centerx = 0f
      }

      if (len * itemSpace < 0.9f) {
        unit.impulse((tx * mspeed + centerx) * delta(), (ty * mspeed + centery) * delta())
      }
    }

    override fun updateTile() {
      minitem = 1f
      mid = 0
      //如果可能，请跳过更新
      if (len == 0 && Mathf.equal(timeScale, 1f)) {
        clogHeat = 0f
        sleep()
        return
      }
      val nextMax = if (aligned) 1f - max(itemSpace - nextc!!.minitem, 0f) else 1f
      // speed值直接对应每秒运输的物品数量
      // 物品从位置0移动到位置1需要1/speed秒
      // 每帧移动的距离 = speed * edelta() / 60f * itemSpace
      val moved = speed * edelta() / 60f * itemSpace

      for (i in len - 1 downTo 0) {
        val nextpos: Float = (if (i == len - 1) 100f else ys[i + 1]) - itemSpace
        val maxmove = Mathf.clamp(nextpos - ys[i], 0f, moved)

        ys[i] += maxmove

        if (ys[i] > nextMax) ys[i] = nextMax
        if (ys[i] > 0.5 && i > 0) mid = i - 1
        xs[i] = Mathf.approach(xs[i], 0f, moved * 2)

        if (ys[i] >= 1f && pass(ids[i])) {
          //如果向前传递，则对齐 X 位置
          if (aligned) {
            nextc!!.xs[nextc!!.lastInserted] = xs[i]
          }
          //删除最后一项
          items.remove(ids[i], len - i)
          len = min(i, len)
        } else if (ys[i] < minitem) {
          minitem = ys[i]
        }
      }

      clogHeat = if (minitem < itemSpace + (if (blendbits == 1) 0.3f else 0f)) {
        Mathf.approachDelta(clogHeat, 1f, 1f / 60f)
      } else {
        0f
      }

      noSleep()
    }

    fun pass(item: Item?): Boolean {
      if (item != null && next != null && next!!.team === team && next!!.acceptItem(this, item)) {
        next!!.handleItem(this, item)
        return true
      }
      return false
    }

    override fun removeStack(item: Item?, amount: Int): Int {
      noSleep()
      var removed = 0

      repeat(amount) {
        for (i in 0..<len) {
          if (ids[i] === item) {
            remove(i)
            removed++
            break
          }
        }
      }

      items.remove(item, removed)
      return removed
    }

    override fun getStackOffset(item: Item?, trns: Vec2) {
      trns.trns(rotdeg() + 180f, Vars.tilesize / 2f)
    }

    override fun acceptStack(item: Item?, amount: Int, source: Teamc?): Int {
      return min((minitem / itemSpace).toInt(), amount)
    }

    override fun handleStack(item: Item?, amount: Int, source: Teamc?) {
      var amount = amount
      amount = min(amount, capacity - len)

      for (i in amount - 1 downTo 0) {
        add(0)
        xs[0] = 0f
        ys[0] = i * itemSpace
        ids[0] = item!!
        items.add(item, 1)
      }

      noSleep()
    }

    override fun acceptItem(source: Building, item: Item?): Boolean {
      if (len >= capacity) return false
      val facing = Edges.getFacingEdge(source.tile, tile) ?: return false
      val direction = abs(facing.relativeTo(tile.x.toInt(), tile.y.toInt()) - rotation)
      return (((direction == 0) && minitem >= itemSpace) || ((direction % 2 == 1) && minitem > 0.7f)) && !(source.block.rotate && next === source)
    }

    override fun handleItem(source: Building, item: Item?) {
      if (len >= capacity) return
      val r = rotation
      val facing = Edges.getFacingEdge(source.tile, tile)
      val ang = ((facing.relativeTo(tile.x.toInt(), tile.y.toInt()) - r))
      val x = (if (ang == -1 || ang == 3) 1 else if (ang == 1 || ang == -3) -1 else 0).toFloat()

      noSleep()
      items.add(item, 1)

      if (abs(facing.relativeTo(tile.x.toInt(), tile.y.toInt()) - r) == 0) { //idx = 0
        add(0)
        xs[0] = x
        ys[0] = 0f
        ids[0] = item!!
      } else { //idx = mid
        add(mid)
        xs[mid] = x
        ys[mid] = 0.5f
        ids[mid] = item!!
      }
    }

    override fun version(): Byte {
      return 1
    }

    override fun write(write: Writes) {
      super.write(write)
      write.i(len)

      for (i in 0..<len) {
        write.s(ids[i]?.id?.toInt() ?: 0)
        write.b((xs[i] * 127).toInt().toByte().toInt())
        write.b((ys[i] * 255 - 128).toInt().toByte().toInt())
      }
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      val amount = read.i()
      len = min(amount, capacity)

      for (i in 0..<amount) {
        val id: Short
        val x: Float
        val y: Float

        if (revision.toInt() == 0) {
          val `val` = read.i()
          id = (((`val` shr 24).toByte()).toInt() and 0xff).toShort()
          x = ((`val` shr 16).toByte()).toFloat() / 127f
          y = (((`val` shr 8).toByte()).toFloat() + 128f) / 255f
        } else {
          id = read.s()
          x = read.b().toFloat() / 127f
          y = (read.b().toFloat() + 128f) / 255f
        }

        if (i < capacity) {
          ids[i] = Vars.content.item(id.toInt())
          xs[i] = x
          ys[i] = y
        }
      }
      //this updates some state
      updateTile()
    }

    override fun sense(sensor: LAccess?): Double {
      if (sensor == LAccess.progress) {
        if (len == 0) return 0.0
        return ys[len - 1].toDouble()
      }
      return super.sense(sensor)
    }

    override fun senseObject(sensor: LAccess?): Any? {
      if (sensor == LAccess.firstItem && len > 0) return ids[len - 1]
      return super.senseObject(sensor)
    }

    override fun setProp(content: UnlockableContent?, value: Double) {
      if (content is Item && items != null) {
        val amount = min(value.toInt(), capacity)
        if (items.get(content) != amount) {
          if (items.get(content) < amount) {
            handleStack(content, amount - items.get(content), null)
          } else if (amount >= 0) {
            removeStack(content, items.get(content) - amount)
          }
        }
      } else super.setProp(content, value)
    }

    fun add(o: Int) {
      for (i in max(o + 1, len) downTo o + 1) {
        ids[i] = ids[i - 1]
        xs[i] = xs[i - 1]
        ys[i] = ys[i - 1]
      }

      len++
    }

    fun remove(o: Int) {
      for (i in o..<len - 1) {
        ids[i] = ids[i + 1]
        xs[i] = xs[i + 1]
        ys[i] = ys[i + 1]
      }

      len--
    }

    @Nullable
    override fun next(): Building? {
      return nextc
    }
  }
}