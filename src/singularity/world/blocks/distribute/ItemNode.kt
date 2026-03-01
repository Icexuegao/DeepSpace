package singularity.world.blocks.distribute

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.scene.Action
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.core.Renderer
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.input.Placement
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.ui.tables.DistTargetConfigTable
import singularity.world.blocks.SglBlock
import singularity.world.distribution.GridChildType
import universecore.components.blockcomp.Takeable
import universecore.components.blockcomp.Takeable.Heaps
import universecore.util.DataPackable
import kotlin.math.abs
import kotlin.math.max

open class ItemNode(name: String) : SglBlock(name) {
  val timerCheckMoved: Int = this.timers++
  var range: Int = 0
  var transportTime: Float = 2.0f
  var endRegion: TextureRegion? = null
  var bridgeRegion: TextureRegion? = null
  var arrowRegion: TextureRegion? = null
  var fadeIn: Boolean = true
  var pulse: Boolean = false
  var arrowSpacing: Float = 4.0f
  var arrowOffset: Float = 2.0f
  var arrowPeriod: Float = 0.4f
  var arrowTimeScl: Float = 6.2f
  var lastBuild: ItemNodeBuild? = null
  var maxItemCapacity: Int = 40
  var siphon: Boolean = false

  init {
    this.update = true
    this.solid = true
    this.underBullets = true
    this.hasPower = true
    this.conductivePower = false
    this.itemCapacity = 10
    this.outputItems = true
    this.configurable = true
    this.hasItems = true
    this.unloadable = false
    this.allowConfigInventory = false
    this.group = BlockGroup.transportation
    this.noUpdateDisabled = true
    this.copyConfig = false
    this.priority = -1.0f
    this.config(Int::class.javaObjectType) { tile: ItemNodeBuild, i: Int -> tile.link = i }
    buildType = Prov(::ItemNodeBuild)
  }

  override fun parseConfigObjects(b: SglBuilding?, obj: Any?) {
    super.parseConfigObjects(b, obj)
    val e = b as ItemNodeBuild
    if (obj is TargetConfigure) {
      e.config = if (obj.isClear) null else obj
      e.link = if (obj.offsetPos != 0) Point2.unpack(obj.offsetPos).add(e.tileX(), e.tileY()).pack() else e.link
    }
  }

  override fun pointConfig(config: Any?, transformer: Cons<Point2>): Any? {
    if (config is ByteArray) {
      val var5 = DataPackable.readObject<DataPackable?>(config, *arrayOfNulls<Any>(0))
      if (var5 is TargetConfigure) {
        var5.configHandle(transformer)
        return var5.pack()
      }
    }

    return config
  }

  override fun drawPlanConfigTop(plan: BuildPlan, list: Eachable<BuildPlan?>) {
    otherReq = null
    list.each(Cons { other: BuildPlan? ->
      if (other!!.block === this && plan !== other) {
        val any = plan.config
        if (any is Point2) {
          if (any.equals(other.x - plan.x, other.y - plan.y)) {
            otherReq = other
          }
        }
      }
    })
    if (otherReq != null) {
      this.drawBridge(plan, otherReq!!.drawx(), otherReq!!.drawy(), 0.0f)
    }
  }

  override fun setStats() {
    super.setStats()
    this.stats.remove(Stat.itemCapacity)
    this.stats.add(Stat.linkRange, this.range.toFloat(), StatUnit.blocks)
    this.stats.add(Stat.itemCapacity, Core.bundle.format("infos.mixedItemCapacity", *arrayOf<Any>(this.itemCapacity, this.maxItemCapacity)), *arrayOfNulls<Any>(0))
    this.stats.add(Stat.itemsMoved, 60.0f / this.transportTime, StatUnit.itemsSecond)
  }

  override fun load() {
    super.load()
    this.endRegion = Core.atlas.find(this.name + "_end")
    this.bridgeRegion = Core.atlas.find(this.name + "_bridge")
    this.arrowRegion = Core.atlas.find(this.name + "_arrow")
  }

  fun drawBridge(req: BuildPlan, ox: Float, oy: Float, flip: Float) {
    if (!Mathf.zero(Renderer.bridgeOpacity)) {
      Draw.alpha(Renderer.bridgeOpacity)
      Lines.stroke(8.0f)
      Tmp.v1.set(ox, oy).sub(req.drawx(), req.drawy()).setLength(4.0f)
      Lines.line(this.bridgeRegion, req.drawx() + Tmp.v1.x, req.drawy() + Tmp.v1.y, ox - Tmp.v1.x, oy - Tmp.v1.y, false)
      Draw.rect(this.arrowRegion, (req.drawx() + ox) / 2.0f, (req.drawy() + oy) / 2.0f, Angles.angle(req.drawx(), req.drawy(), ox, oy) + flip)
      Draw.reset()
    }
  }

  override fun setBars() {
    super.setBars()
    this.removeBar("items")
    this.addBar("items") { entity: Building -> Bar({ Core.bundle.format("bar.items", *arrayOf<Any>(entity.items.total())) }, { Pal.items }, { entity.items.total().toFloat() / this.maxItemCapacity.toFloat() }) }
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    val link = this.findLink(x, y)

    for (i in 0..3) {
      Drawf.dashLine(Pal.placing, (x * 8).toFloat() + Geometry.d4[i].x.toFloat() * 6.0f, (y * 8).toFloat() + Geometry.d4[i].y.toFloat() * 6.0f, (x * 8 + Geometry.d4[i].x * this.range * 8).toFloat(), (y * 8 + Geometry.d4[i].y * this.range * 8).toFloat())
    }

    Draw.reset()
    Draw.color(Pal.placing)
    Lines.stroke(1.0f)
    if (link != null && abs(link.x - x) + abs(link.y - y) > 1) {
      val rot = link.absoluteRelativeTo(x, y).toInt()
      val w = (if (link.x.toInt() == x) 8 else abs(link.x - x) * 8 - 8).toFloat()
      val h = (if (link.y.toInt() == y) 8 else abs(link.y - y) * 8 - 8).toFloat()
      Lines.rect((x + link.x).toFloat() / 2.0f * 8.0f - w / 2.0f, (y + link.y).toFloat() / 2.0f * 8.0f - h / 2.0f, w, h)
      Draw.rect("bridge-arrow", (link.x * 8 + Geometry.d4(rot).x * 8).toFloat(), (link.y * 8 + Geometry.d4(rot).y * 8).toFloat(), (link.absoluteRelativeTo(x, y) * 90).toFloat())
    }

    Draw.reset()
  }

  @JvmOverloads
  fun linkValid(tile: Tile?, other: Tile?, checkDouble: Boolean = true): Boolean {
    return if (other != null && tile != null && this.positionsValid(tile.x.toInt(), tile.y.toInt(), other.x.toInt(), other.y.toInt())) {
      (other.block() === tile.block() && tile.block() === this || tile.block() !is ItemNode && other.block() === this) && (other.team() === tile.team() || tile.block() !== this) && (!checkDouble || (other.build as ItemNodeBuild).link != tile.pos())
    } else {
      false
    }
  }

  fun positionsValid(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
    return if (x1 == x2) {
      abs(y1 - y2) <= this.range
    } else if (y1 == y2) {
      abs(x1 - x2) <= this.range
    } else {
      false
    }
  }

  fun findLink(x: Int, y: Int): Tile? {
    val tile = Vars.world.tile(x, y)
    return if (tile != null && this.lastBuild != null && this.linkValid(tile, this.lastBuild!!.tile) && this.lastBuild!!.tile !== tile && this.lastBuild!!.link == -1) this.lastBuild!!.tile else null
  }

  override fun init() {
    super.init()
    this.updateClipRadius((this.range.toFloat() + 0.5f) * 8.0f)
  }

  override fun handlePlacementLine(plans: Seq<BuildPlan?>) {
    for (i in 0..<plans.size - 1) {
      val cur = plans.get(i) as BuildPlan
      val next = plans.get(i + 1) as BuildPlan
      if (this.positionsValid(cur.x, cur.y, next.x, next.y)) {
        cur.config = Point2(next.x - cur.x, next.y - cur.y)
      }
    }
  }

  override fun changePlacementPath(points: Seq<Point2?>, rotation: Int) {
    Placement.calculateNodes(points, this, rotation) { point: Point2?, other: Point2? -> max(abs(point!!.x - other!!.x), abs(point.y - other.y)) <= this.range }
  }

  open inner class ItemNodeBuild : SglBuilding(), Takeable {
    override var heaps: ObjectMap<String, Heaps<*>> = ObjectMap<String, Heaps<*>>()
    var config: TargetConfigure? = null
    var link: Int = -1
    var incoming: IntSeq = IntSeq(false, 4)
    var warmup: Float = 0f
    var time: Float = -8.0f
    var timeSpeed: Float = 0f
    var wasMoved: Boolean = false
    var moved: Boolean = false
    var transportCounter: Float = 0f
    var itemTakeCursor: Int = 0
    var show: Runnable = Runnable {}
    var close: Runnable = Runnable {}
    var showing: Boolean = false

    override fun pickedUp() {
      this.link = -1
    }

    override fun playerPlaced(config: Any?) {
      super.playerPlaced(config)
      val link = this@ItemNode.findLink(this.tile.x.toInt(), this.tile.y.toInt())
      if (this@ItemNode.linkValid(this.tile, link) && this.link != link!!.pos() && !this.proximity.contains(link.build)) {
        link.build.configure(this.tile.pos())
      }

      this@ItemNode.lastBuild = this
    }

    override fun drawSelect() {
      if (this@ItemNode.linkValid(this.tile, Vars.world.tile(this.link))) {
        this.drawInput(Vars.world.tile(this.link))
      }

      this.incoming.each { pos: Int -> this.drawInput(Vars.world.tile(pos)) }
      Draw.reset()
    }

    private fun drawInput(other: Tile) {
      if (this@ItemNode.linkValid(this.tile, other, false)) {
        val linked = other.pos() == this.link
        Tmp.v2.trns(this.tile.angleTo(other), 2.0f)
        val tx = this.tile.drawx()
        val ty = this.tile.drawy()
        val ox = other.drawx()
        val oy = other.drawy()
        val alpha = abs((if (linked) 100 else 0).toFloat() - Time.time * 2.0f % 100.0f) / 100.0f
        val x = Mathf.lerp(ox, tx, alpha)
        val y = Mathf.lerp(oy, ty, alpha)
        val otherLink = if (linked) other else this.tile
        val rel = (if (linked) this.tile else other).absoluteRelativeTo(otherLink.x.toInt(), otherLink.y.toInt()).toInt()
        Draw.color(Pal.gray)
        Lines.stroke(2.5f)
        Lines.square(ox, oy, 2.0f, 45.0f)
        Lines.stroke(2.5f)
        Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y)
        Draw.color(if (linked) Pal.place else Pal.accent)
        Lines.stroke(1.0f)
        Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y)
        Lines.square(ox, oy, 2.0f, 45.0f)
        Draw.mixcol(Draw.getColor(), 1.0f)
        Draw.color()
        Draw.rect(this@ItemNode.arrowRegion, x, y, (rel * 90).toFloat())
        Draw.mixcol()
      }
    }

    override fun drawConfigure() {
      Drawf.select(this.x, this.y, (this.tile.block().size * 8).toFloat() / 2.0f + 2.0f, Pal.accent)

      for (i in 1..this@ItemNode.range) {
        for (j in 0..3) {
          val other = this.tile.nearby(Geometry.d4[j].x * i, Geometry.d4[j].y * i)
          if (this@ItemNode.linkValid(this.tile, other)) {
            val linked = other.pos() == this.link
            Drawf.select(other.drawx(), other.drawy(), (other.block().size * 8).toFloat() / 2.0f + 2.0f + (if (linked) 0.0f else Mathf.absin(Time.time, 4.0f, 1.0f)), if (linked) Pal.place else Pal.breakInvalid)
          }
        }
      }
    }

    override fun onConfigureBuildTapped(other: Building): Boolean {
      if (other === this) {
        if (!this.showing) {
          this.show.run()
          this.showing = true
        } else {
          this.close.run()
          this.showing = false
        }

        return false
      } else {
        if (other is ItemNodeBuild) {
          if (other.link == this.pos()) {
            this.configure(other.pos())
            other.configure(-1)
            return true
          }
        }

        if (this@ItemNode.linkValid(this.tile, other.tile)) {
          if (this.link == other.pos()) {
            this.configure(-1)
          } else {
            this.configure(other.pos())
          }

          return false
        } else {
          return true
        }
      }
    }

    fun checkIncoming() {
      var idx = 0
      while (idx < this.incoming.size) {
        val i = this.incoming.items[idx]
        val other = Vars.world.tile(i)
        if (!this@ItemNode.linkValid(this.tile, other, false) || (other.build as ItemNodeBuild).link != this.tile.pos()) {
          this.incoming.removeIndex(idx)
          --idx
        }
        ++idx
      }
    }

    fun updateTransport(other: Building) {
      this.transportCounter += this.consEfficiency() * this.delta()

      while (this.transportCounter >= this@ItemNode.transportTime) {
        val items = Vars.content.items()
        var any = false

        var i = 0
        while (this.transportCounter >= this@ItemNode.transportTime && i < items.size) {
          this.itemTakeCursor = (this.itemTakeCursor + 1) % items.size
          val id = this.itemTakeCursor
          if (this.items.get(id) > 0) {
            val item = items.get(id)
            if (other.acceptItem(this, item)) {
              this.items.remove(item, 1)
              other.handleItem(this, item)
              this.transportCounter -= this@ItemNode.transportTime
              this.moved = true
              any = true
            }
          }
          ++i
        }

        if (!any) {
          this.transportCounter %= this@ItemNode.transportTime
        }
      }
    }

    override fun draw() {
      super.draw()
      Draw.z(70.0f)
      val other = Vars.world.tile(this.link)
      if (this@ItemNode.linkValid(this.tile, other)) {
        if (!Mathf.zero(Renderer.bridgeOpacity)) {
          val i = this.relativeTo(other.x.toInt(), other.y.toInt()).toInt()
          if (this@ItemNode.pulse) {
            Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6.0f, 0.07f))
          }

          val warmup = if (this@ItemNode.hasPower) this.warmup else 1.0f
          Draw.alpha((if (this@ItemNode.fadeIn) max(warmup, 0.25f) else 1.0f) * Renderer.bridgeOpacity)
          Draw.rect(this@ItemNode.endRegion, this.x, this.y, (i * 90 + 90).toFloat())
          Draw.rect(this@ItemNode.endRegion, other.drawx(), other.drawy(), (i * 90 + 270).toFloat())
          Lines.stroke(8.0f)
          Tmp.v1.set(this.x, this.y).sub(other.worldx(), other.worldy()).setLength(4.0f).scl(-1.0f)
          Lines.line(this@ItemNode.bridgeRegion, this.x + Tmp.v1.x, this.y + Tmp.v1.y, other.worldx() - Tmp.v1.x, other.worldy() - Tmp.v1.y, false)
          val dist = max(abs(other.x - this.tile.x), abs(other.y - this.tile.y)) - 1
          Draw.color()
          val arrows = ((dist * 8).toFloat() / this@ItemNode.arrowSpacing).toInt()
          val dx = Geometry.d4x(i)
          val dy = Geometry.d4y(i)

          for (a in 0..<arrows) {
            Draw.alpha(Mathf.absin(a.toFloat() - this.time / this@ItemNode.arrowTimeScl, this@ItemNode.arrowPeriod, 1.0f) * warmup * Renderer.bridgeOpacity)
            Draw.rect(this@ItemNode.arrowRegion, this.x + dx.toFloat() * (4.0f + a.toFloat() * this@ItemNode.arrowSpacing + this@ItemNode.arrowOffset), this.y + dy.toFloat() * (4.0f + a.toFloat() * this@ItemNode.arrowSpacing + this@ItemNode.arrowOffset), i.toFloat() * 90.0f)
          }

          Draw.reset()
        }
      }
    }

    override fun canDumpLiquid(to: Building, liquid: Liquid?): Boolean {
      return this.checkDump(to)
    }

    override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      return this@ItemNode.hasLiquids && this.team === source.team && (this.liquids.current() === liquid || this.liquids.get(this.liquids.current()) < 0.2f) && this.checkAccept(source, Vars.world.tile(this.link))
    }

    protected fun checkAccept(source: Building, other: Tile?): Boolean {
      if (this.tile != null && !this.linked(source)) {
        if (this@ItemNode.linkValid(this.tile, other)) {
          val rel = this.relativeTo(other).toInt()
          val rel2 = this.relativeTo(Edges.getFacingEdge(source, this)).toInt()
          return rel != rel2
        } else {
          return false
        }
      } else {
        return true
      }
    }

    protected fun linked(source: Building?): Boolean {
      return source is ItemNodeBuild && this@ItemNode.linkValid(source.tile, this.tile) && source.link == this.pos()
    }

    override fun canDump(to: Building, item: Item?): Boolean {
      return this.checkDump(to)
    }

    protected fun checkDump(to: Building): Boolean {
      val other = Vars.world.tile(this.link)
      if (!this@ItemNode.linkValid(this.tile, other)) {
        val edge = Edges.getFacingEdge(to.tile, this.tile)
        val i = this.relativeTo(edge.x.toInt(), edge.y.toInt()).toInt()

        for (j in 0..<this.incoming.size) {
          val v = this.incoming.items[j]
          if (this.relativeTo(Point2.x(v).toInt(), Point2.y(v).toInt()).toInt() == i) {
            return false
          }
        }

        return true
      } else {
        val rel = this.relativeTo(other.x.toInt(), other.y.toInt()).toInt()
        val rel2 = this.relativeTo(to.tileX(), to.tileY()).toInt()
        return rel != rel2
      }
    }

    override fun shouldConsume(): Boolean {
      return this@ItemNode.linkValid(this.tile, Vars.world.tile(this.link)) && this.enabled
    }

    override fun buildConfiguration(table: Table) {
      this.showing = false
      table.table { t: Table? ->
        t!!.visible = false
        t.setOrigin(1)
        t.add().width(45.0f)
        (t.center().table(Tex.pane).get() as Table).add(DistTargetConfigTable(0, this.config, if (this@ItemNode.siphon) arrayOf(GridChildType.output, GridChildType.acceptor, GridChildType.input) else arrayOf(GridChildType.output, GridChildType.acceptor), arrayOf(ContentType.item), true, { c: TargetConfigure? ->
          c!!.offsetPos = 0
          this.configure(c.pack())
        }, { Vars.control.input.config.hideConfig() })).fill().center()
        t.top().button(Icon.info, Styles.grayi, 32.0f) {
          //  Sgl.ui.document.showDocument("", MarkdownStyles.defaultMD, arrayOf<String?>(Singularity.getDocument("matrix_grid_config_help.md")))
        }.size(45.0f).top()
        this.show = Runnable {
          t.visible = true
          t.pack()
          t.isTransform = true
          t.actions(*arrayOf<Action?>(Actions.scaleTo(0.0f, 1.0f), Actions.visible(true), Actions.scaleTo(1.0f, 1.0f, 0.07f, Interp.pow3Out)))
        }
        this.close = Runnable { t.actions(*arrayOf<Action?>(Actions.scaleTo(1.0f, 1.0f), Actions.scaleTo(0.0f, 1.0f, 0.07f, Interp.pow3Out), Actions.visible(false))) }
      }.fillY()
    }

    override fun updateTile() {
      if (this.timer(this@ItemNode.timerCheckMoved, 30.0f)) {
        this.wasMoved = this.moved
        this.moved = false
      }

      this.timeSpeed = Mathf.approachDelta(this.timeSpeed, if (this.wasMoved) 1.0f else 0.0f, 0.016666668f)
      this.time += this.timeSpeed * this.delta()
      this.checkIncoming()
      if (this.config != null && this.config!!.priority > 0) {
        this.doDump()
        if (this@ItemNode.siphon) {
          this.doSiphon()
        }
      }

      val other = Vars.world.tile(this.link)
      if (!this@ItemNode.linkValid(this.tile, other)) {
        this.warmup = 0.0f
      } else {
        val inc = (other.build as ItemNodeBuild).incoming
        val pos = this.tile.pos()
        if (!inc.contains(pos)) {
          inc.add(pos)
        }

        this.warmup = Mathf.approachDelta(this.warmup, this.efficiency, 0.033333335f)
        this.updateTransport(other.build)
      }

      if ((this.config != null || !this@ItemNode.linkValid(this.tile, other)) && (this.config == null || this.config!!.priority <= 0)) {
        if (this@ItemNode.siphon) {
          this.doSiphon()
        }

        this.doDump()
      }
    }

    fun doSiphon() {
      if (this.config != null) {
        val var1 = this.config!!.get(GridChildType.input, ContentType.item)!!.iterator()

        while (var1.hasNext()) {
          val con = var1.next() as UnlockableContent?
          val item = con as Item?
          val other = this.getNext("siphonItem") { e: Building? -> e!!.interactable(this.team) && e.block.hasItems && e.items.has(item) && this.config!!.directValid(GridChildType.input, item, this.getDirectBit(e)) }
          if (other == null || !this@ItemNode.hasItems || this.items.get(item) >= this@ItemNode.itemCapacity || this.items.total() >= this@ItemNode.maxItemCapacity) {
            return
          }

          other.removeStack(item, 1)
          this.handleItem(other, item)
        }
      }
    }

    fun doDump() {
      if (this.config != null && !this.config!!.isClear) {
        val var1 = this.config!!.get(GridChildType.output, ContentType.item)!!.iterator()

        while (var1.hasNext()) {
          val content = var1.next() as UnlockableContent?
          val i = content as Item?
          if (this.items.get(i) > 0) {
            val next = this.getNext("items") { e: Building? -> e!!.interactable(this.team) && this.config!!.directValid(GridChildType.output, i, this.getDirectBit(e)) && e.acceptItem(this, i) }
            if (next != null) {
              this.items.remove(i, 1)
              next.handleItem(this, i)
            }
          }
        }
      } else {
        this.dumpAccumulate()
      }
    }

    override fun acceptItem(source: Building, item: Item?): Boolean {
      return this@ItemNode.hasItems && this.team === source.team && this.items.get(item) < this@ItemNode.itemCapacity && this.items.total() < this@ItemNode.maxItemCapacity && this.checkAccept(source, Vars.world.tile(this.link), item)
    }

    protected fun checkAccept(source: Building, other: Tile?, content: UnlockableContent?): Boolean {
      return if (this.tile != null && !this.linked(source) && this.config != null) {
        if (this@ItemNode.linkValid(this.tile, other)) this.config!!.directValid(GridChildType.acceptor, content, this.getDirectBit(source)) else false
      } else {
        true
      }
    }

    protected fun getDirectBit(e: Building): Byte {
      val dir = this.relativeTo(Edges.getFacingEdge(e, this))
      return (if (dir.toInt() == 0) 1 else (if (dir.toInt() == 1) 2 else (if (dir.toInt() == 2) 4 else (if (dir.toInt() == 3) 8 else 0)))).toByte()
    }

    override fun config(): Any? {
      val res = if (this.config == null) TargetConfigure() else this.config!!.clone()
      res.offsetPos = Point2.unpack(this.link).sub(this.tile.x.toInt(), this.tile.y.toInt()).pack()
      return res.pack()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.i(this.link)
      write.f(this.warmup)
      write.i(this.itemTakeCursor)
      write.b(this.incoming.size)

      for (i in 0..<this.incoming.size) {
        write.i(this.incoming.items[i])
      }

      write.bool(this.wasMoved || this.moved)
      val b = if (this.config == null) EMP else this.config!!.pack()
      write.i(b.size)
      if (b.size > 0) {
        write.b(b)
      }
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.link = read.i()
      this.warmup = read.f()
      this.itemTakeCursor = read.i()
      val links = read.b()

      for (i in 0..<links) {
        this.incoming.add(read.i())
      }

      this.moved = read.bool()
      this.wasMoved = this.moved
      val len = read.i()
      if (len != 0) {
        this.config = TargetConfigure()
        this.config!!.read(read.b(len))
      }
    }

    fun heaps(): ObjectMap<String, Heaps<*>> {
      return this.heaps
    }
  }

  companion object {
    val EMP: ByteArray = ByteArray(0)
    private var otherReq: BuildPlan? = null
  }
}