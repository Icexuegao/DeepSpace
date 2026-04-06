package ice.content.block.liquid

import arc.Events
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Position
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IItems
import ice.graphics.IceColor
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.draw.DrawMulti
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.game.EventType
import mindustry.gen.Building
import mindustry.gen.Buildingc
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.draw.DrawLiquidTile
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.SglBlock
import kotlin.math.min

class P2PLiquidNode : SglBlock("p2pLiquidNode") {

  init {
    bundle {
      desc(zh_CN, "P2P流体节点", "分散流体交换通信方式")
    }
    size = 2
    health = 500
    hasLiquids = true
    outputsLiquid = true
    configurable = true
    liquidCapacity = 800f
    buildType = Prov(::P2PLiquidNodeBuild)
    drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(), DrawRegion("-top"))
    requirementPairs(Category.liquid, IItems.铬锭 to 30, IItems.电子元件 to 20, IItems.石英玻璃 to 10, IItems.铝锭 to 20)
    newConsume().apply {
      power(90f / 60f)
    }
  }

  override fun setStats() {
    super.setStats()
    stats.add(IceStats.连接范围, "无限")
    stats.add(IceStats.传输速度, "60/秒")
    consumers.forEach {
      it.display(stats)
    }
  }

  override fun setBars() {
    super.setBars()
    addBar("liked") {ent: P2PLiquidNodeBuild ->
      Bar({if (ent.connected == null) "未连接" else "已连接"}, {IceColor.b4}, {if (ent.connected == null) 0f else 1f})
    }
  }

  inner class P2PLiquidNodeBuild : SglBuilding() {
    var connected: Int? = null
    var cs = false

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      val i: Int = read.i()
      connected = if (i == -1) null else i
      cs = read.bool()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.i(connected ?: -1)
      write.bool(cs)
    }

    override fun transferLiquid(next: Building, amount: Float, liquid: Liquid) {
      val flow = min(min(next.block.liquidCapacity - next.liquids.get(liquid), amount), liquids.get(liquid))
      if (next.acceptLiquid(this, liquid)) {
        next.handleLiquid(this, liquid, flow)
        this.liquids.remove(liquid, flow)
      }
    }

    override fun drawSelect() {
      super.drawSelect()
      connected?.let {
        val bu: P2PLiquidNodeBuild = Vars.world.build(it) as? P2PLiquidNodeBuild ?: return
        transferArrowLineBreath(if (!cs) bu else this, if (!cs) this else bu)

        for (build in arrayOf(bu, this)) {
          dashCircleBreath(build, size * Vars.tilesize + size / 2f * 1.5f, IceColor.b4)
        }
      }
    }

    override fun onConfigureBuildTapped(other: Building): Boolean {
      if (other is P2PLiquidNodeBuild) {
        linked(other)
      }
      return false
    }

    override fun pickedUp() {
      super.pickedUp()
      connected?.let {
        val bu: P2PLiquidNodeBuild = Vars.world.build(it) as? P2PLiquidNodeBuild ?: return@let
        bu.connected = null
        bu.cs = false
      }
      connected = null
      cs = false
    }

    fun linked(other: P2PLiquidNodeBuild) {
      if (connected == null && other !== this && other.connected == null) {
        other.connected = this.pos()
        connected = other.pos()
        cs = true
      } else if (other.connected == this.pos() && connected == other.pos()) {
        other.connected = null
        other.cs = false
        cs = false
        connected = null
      }
    }

    override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
      return super.acceptLiquid(source, liquid) && cs || source is P2PLiquidNodeBuild
    }

    override fun acceptConsumeLiquid(source: Building, liquid: Liquid) = true
    override fun updateTile() {
      super.updateTile()
      var build: P2PLiquidNodeBuild? = null
      connected?.let {
        build = Vars.world.build(it) as? P2PLiquidNodeBuild ?: run {
          connected = null
          cs = false
          return
        }

      }
      build?.let {
        if (cs && consumeValid() && it.consumeValid()) {
          liquids().each {liquid, _ ->
            transferLiquid(it, 1 * delta(), liquid)
          }
        }
      }
      if (!cs) dumpLiquid()
    }
  }

  var sin = 0f
  var tan = 0f
  fun dashCircleBreath(
    build: Building,
    range: WorldXY, color: Color = Pal.power,
    alpha: Float = -1f, stroke: Float = 1f,
  ) = dashCircle(build.x, build.y, range + sin - 2, color, alpha, stroke)

  init {
    Events.run(EventType.Trigger.preDraw) {
      sin = Mathf.absin(Time.time, 6f, 1f)
      tan = Mathf.tan(Time.time, 6f, 1f)
    }
  }

  val Float.isZero: Boolean
    get() = Mathf.zero(this)

  fun Color.darken(percentage: Float): Color {
    r *= 1f - percentage
    g *= 1f - percentage
    b *= 1f - percentage
    return this
  }

  fun transferArrowLineBreath(
    startDrawX: WorldXY, startDrawY: WorldXY,
    endDrawX: WorldXY, endDrawY: WorldXY,
    arrowColor: Color = IceColor.b4,
    density: Float = 15f,
    /** unit per tick */
    speed: Float = 60f,
    alphaMultiplier: Float = 1f,
  ) {
    if (density.isZero) return
    if (alphaMultiplier <= 0f) return
    val originalZ = Draw.z()
    val t = Tmp.v2.set(endDrawX, endDrawY).sub(startDrawX, startDrawY)
    val angle = t.angle()
    val length = t.len()
    val count = (Mathf.ceil(length / density)).coerceAtLeast(1)
    val inner = Tmp.c1.set(arrowColor).a(arrowColor.a * alphaMultiplier)
    val outline = Tmp.c2.set(arrowColor).a(arrowColor.a * alphaMultiplier).darken(0.3f)
    var size = 1f + sin * 0.15f
    var outlineSize = 1f + sin * 0.15f + 0.4f
    if (Vars.mobile) {
      size *= 0.6f
      outlineSize *= 0.6f
    }
    val time = length / speed * 60f
    val moving = if (speed > 0f) Tmp.v3.set(t).setLength((length * (Time.time % time / time)) % length)
    else Tmp.v3.set(0f, 0f)
    val cur = Tmp.v4.set(
      startDrawX + moving.x, startDrawY + moving.y
    )
    val per = t.scl(1f / count)
    for (i in 0 until count) {
      val line = Tmp.v5.set(cur).sub(startDrawX, startDrawY)
      val lineLength = (line.len() % length / length).smooth * length
      line.setLength(lineLength)
      line.add(startDrawX, startDrawY)
      val fadeAlpha = when {
        lineLength <= 10f -> (lineLength / 10f).smooth
        length - lineLength <= 10f -> ((length - lineLength) / 10f).smooth
        else -> 1f
      }
      Draw.color(outline)
      Icon.right.region.DrawScale(line.x, line.y, scale = outlineSize * fadeAlpha, rotation = angle)
      Draw.color(inner)
      Icon.right.region.DrawScale(line.x, line.y, scale = size * fadeAlpha, rotation = angle)
      cur += per
    }
    Draw.z(originalZ)
    Draw.reset()
  }

  var ALPHA: Float = 1f
    set(value) {
      field = value.coerceIn(0f, 1f)
    }

  fun dashCircle(
    x: WorldXY, y: WorldXY, rad: WorldXY, color: Color = Pal.power,
    alpha: Float = -1f, stroke: Float = 1f,
  ) {
    Lines.stroke(stroke + 2f, Pal.gray)
    if (alpha >= 0f) {
      Draw.alpha(alpha)
    }
    Lines.circle(x, y, rad)

    Lines.stroke(stroke, color)
    if (alpha >= 0f) {
      Draw.alpha(alpha)
    }
    Lines.circle(x, y, rad)
    Draw.reset()
  }

  fun TextureRegion.DrawScale(
    x: Float, y: Float, scale: Float,
    rotation: Float = 0f,
  ) {
    Draw.alpha(Draw.getColor().a * ALPHA)
    Draw.rect(
      this, x, y, width * Draw.scl * Draw.xscl * scale, height * Draw.scl * Draw.yscl * scale, rotation
    )
  }

  fun transferArrowLineBreath(
    startBlock: Block,
    startBlockX: TileXYs, startBlockY: TileXYs,
    endBlock: Block,
    endBlockX: TileXYs, endBlockY: TileXYs,
    arrowColor: Color = Pal.power,
    density: Float = 15f,
    speed: Float = 60f,
    alphaMultiplier: Float = 1f,
  ) {
    transferArrowLineBreath(
      startBlock.getCenterWorldXY(startBlockX),
      startBlock.getCenterWorldXY(startBlockY),
      endBlock.getCenterWorldXY(endBlockX),
      endBlock.getCenterWorldXY(endBlockY),
      arrowColor = arrowColor,
      density = density,
      speed = speed,
      alphaMultiplier = alphaMultiplier,
    )
  }
  typealias TileXY = Int
  typealias TileXYs = Short
  typealias WorldXY = Float
  typealias Progress = Float

  operator fun Interp.invoke(x: Float) = this.apply(x)
  val Progress.smooth: Progress
    get() = Interp.smooth(this.coerceIn(0f, 1f))

  fun transferArrowLineBreath(
    start: Buildingc,
    end: Buildingc,
    arrowColor: Color = IceColor.b4,
    density: Float = 15f,
    speed: Float = 60f,
    alphaMultiplier: Float = 1f,
  ) = transferArrowLineBreath(
    start.x, start.y,
    end.x, end.y,
    arrowColor = arrowColor,
    density = density,
    speed = speed,
    alphaMultiplier = alphaMultiplier,
  )

  /**
   * Tile xy to world xy. Take block's offset into account
   */
  fun Block.getCenterWorldXY(xy: TileXYs): WorldXY = offset + xy * Vars.tilesize

  /**
   * Tile xy to world xy. Take block's offset into account
   */
  fun Block.getCenterWorldXY(xy: TileXY): WorldXY = offset + xy * Vars.tilesize

  fun Vec2.set(x: Short, y: Short): Vec2 = this.set(x.toFloat(), y.toFloat())

  fun Vec2.set(x: Int, y: Int): Vec2 = this.set(x.toFloat(), y.toFloat())

  fun Vec2.minus(x: Int, y: Int) = apply {
    this.x -= x.toFloat()
    this.y -= y.toFloat()
  }

  fun Vec2.minus(x: Short, y: Short) = apply {
    this.x -= x.toFloat()
    this.y -= y.toFloat()
  }

  fun Vec2.minus(x: Float, y: Float) = apply {
    this.x -= x
    this.y -= y
  }

  fun Vec2.minus(p: Position) = apply {
    this.x = p.x
    this.y = p.y
  }

  fun Vec2.div(b: Float) = apply {
    x /= b
    y /= b
  }

  operator fun Vec2.plusAssign(b: Float) {
    x += b
    y += b
  }

  operator fun Vec2.minusAssign(b: Float) {
    x -= b
    y -= b
  }

  operator fun Vec2.plusAssign(b: Vec2) {
    x += b.x
    y += b.y
  }

  operator fun Vec2.minusAssign(b: Vec2) {
    x -= b.x
    y -= b.y
  }

  operator fun Vec2.plusAssign(b: Position) {
    x += b.x
    y += b.y
  }

  operator fun Vec2.minusAssign(b: Position) {
    x -= b.x
    y -= b.y
  }

  operator fun Vec2.timesAssign(b: Float) {
    x *= b
    y *= b
  }

  operator fun Vec2.divAssign(b: Float) {
    x /= b
    y /= b
  }
}