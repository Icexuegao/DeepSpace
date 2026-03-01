package singularity.world.blocks.distribute

import arc.Core
import arc.func.Cons
import arc.func.Intc2
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Geometry
import arc.math.geom.Intersector
import arc.math.geom.Point2
import arc.struct.IntSeq
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import mindustry.Vars
import mindustry.content.Liquids
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Tile
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.modules.LiquidModule.LiquidConsumer
import singularity.Singularity
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.components.distnet.DistElementBlockComp
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.components.distnet.DistNetworkCoreComp
import singularity.world.meta.SglStat
import singularity.world.modules.SglLiquidModule
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class MatrixBridge(name: String) : DistNetBlock(name) {
  var effectColor: Color?
  var linkRegion: TextureRegion? = null
  var topRegion: TextureRegion? = null
  var linkRange: Int = 16
  var maxLinks: Int = 8
  var transportItemTime: Float = 1.0f
  var linkStoke: Float = 8.0f
  var crossLinking: Boolean = false
  var maxLiquidCapacity: Float

  init {
    this.effectColor = SglDrawConst.matrixNet
    this.maxLiquidCapacity = -1.0f
    this.configurable = true
    this.topologyUse = 0
    this.hasItems = true
    this.outputsLiquid = true
    this.hasLiquids = this.outputsLiquid
    this.isNetLinker = true
    buildType= Prov(::MatrixBridgeBuild)
  }

  public override fun appliedConfig() {
    super.appliedConfig()
    this.config(IntSeq::class.java) { e: MatrixBridgeBuild, seq: IntSeq? ->
      if (seq!!.get(0) != -1) {
        val p = Point2.unpack(seq.get(0))
        e.linkElementPos = p.set(e!!.tile.x + p.x, e.tile.y + p.y).pack()
      }
      if (seq.get(1) != -1) {
        val p = Point2.unpack(seq.get(1))
        e.linkNextPos = p.set(e!!.tile.x + p.x, e.tile.y + p.y).pack()
      }
    }
    this.config(Point2::class.java) { e: MatrixBridgeBuild, p: Point2? ->
      val pos = p!!.pack()
      val target = Vars.world.build(pos)
      if (target is DistNetworkCoreComp) {
        e.linkNextPos = -1
      }
      if (target is MatrixBridgeBuild) {
        if (e.linkElement is DistNetworkCoreComp) {
          e.linkElementPos = -1
        }

        if (target.linkNext === e) {
          target.linkNextPos = -1
          target.deLink(e)
          target.linkNext = null
        } else {
          e.linkNextLerp = 0.0f
        }

        e.linkNextPos = if (e.linkNextPos === pos) -1 else target.pos()
      } else if (target is DistElementBuildComp) {
        e.linkElementPos = if (e.linkElementPos === pos) -1 else target.pos()
        e.linkElementLerp = 0.0f
      }
    }
  }

  public override fun init() {
    super.init()
    this.clipSize = max(this.clipSize, (this.linkRange * 8 * 2).toFloat())
    if (this.maxLiquidCapacity == -1.0f) {
      this.maxLiquidCapacity = this.liquidCapacity * 4.0f
    }
  }

  override fun setStats() {
    super.setStats()
    this.stats.add(Stat.linkRange, this.linkRange.toFloat(), StatUnit.blocks)
    this.stats.add(SglStat.maxMatrixLinks, this.maxLinks.toFloat())
  }

  public override fun load() {
    super.load()
    this.linkRegion = Core.atlas.find(this.name + "_link", Singularity.getModAtlas("matrix_link_laser"))
    this.topRegion = Core.atlas.find(this.name + "_top", Singularity.getModAtlas("matrix_link_light"))
  }

  fun linkInlerp(origin: Tile?, other: Tile?, range: Float): Boolean {
    if (origin != null && other != null) {
      if (this.crossLinking) {
        val xDistance = abs(origin.x - other.x)
        val yDistance = abs(origin.y - other.y)
        val var10000 = this.linkRange
        val var8 = other.block()
        val var10001: Int
        if (var8 is MatrixBridge) {
          val m = var8
          var10001 = m.linkRange
        } else {
          var10001 = this.linkRange
        }

        val linkLength = min(var10000, var10001)
        return yDistance.toFloat() < linkLength.toFloat() + this.size.toFloat() / 2.0f + this.offset && origin.x == other.x && origin.y != other.y || xDistance.toFloat() < linkLength.toFloat() + this.size.toFloat() / 2.0f + this.offset && origin.x != other.x && origin.y == other.y
      } else {
        return Intersector.overlaps(Tmp.cr1.set(origin.drawx(), origin.drawy(), range), other.getHitbox(Tmp.r1))
      }
    } else {
      return false
    }
  }

  override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
    val seq = req.config as IntSeq?
    if (seq != null) {
      val p = arrayOf<Point2?>(if (seq.get(0) == -1) null else Point2.unpack(seq.get(0)), if (seq.get(1) == -1) null else Point2.unpack(seq.get(1)))
      val links = arrayOfNulls<BuildPlan>(2)
      list.each(Cons { planx: BuildPlan? ->
        if (p[1] != null && planx!!.block is MatrixBridge) {
          if (p[1]!!.cpy().set(req.x + p[1]!!.x, req.y + p[1]!!.y).pack() == Point2.pack(planx.x, planx.y)) {
            links[0] = planx
          }
        } else if (p[0] != null && planx!!.block is DistElementBlockComp && p[0]!!.cpy().set(req.x + p[0]!!.x, req.y + p[0]!!.y).pack() == Point2.pack(planx.x, planx.y)) {
          links[1] = planx
        }
      })
      Draw.rect(this.topRegion, req.drawx(), req.drawy())

      for (plan in links) {
        if (plan != null) {
          SglDraw.drawLaser(req.drawx(), req.drawy(), plan.drawx(), plan.drawy(), this.linkRegion, null as TextureRegion?, this.linkStoke)
        }
      }
    }
  }

  fun doCanLink(origin: Tile, range: Float, cons: Cons<MatrixBridgeBuild?>) {
    Geometry.circle(origin.x.toInt(), origin.y.toInt(), (range * 8.0f).toInt(), Intc2 { x: Int, y: Int ->
      val e = Vars.world.build(x, y)
      if (e is MatrixBridgeBuild && temps.add(e)) {
        cons.get(e)
      }
    })
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    if (this.crossLinking) {
      Tmp.v1.set(1.0f, 0.0f)

      for (i in 0..3) {
        val dx = (x * 8).toFloat() + this.offset + (Geometry.d4x(i) * this.size * 8).toFloat() / 2.0f
        val dy = (y * 8).toFloat() + this.offset + (Geometry.d4y(i) * this.size * 8).toFloat() / 2.0f
        Drawf.dashLine(Pal.accent, dx, dy, dx + (Geometry.d4x(i) * this.linkRange * 8).toFloat(), dy + (Geometry.d4y(i) * this.linkRange * 8).toFloat())

        for (d in 1..this.linkRange) {
          Tmp.v1.setLength(d.toFloat())
          val t = Vars.world.build(x + Tmp.v1.x.toInt(), y + Tmp.v1.y.toInt())
          if (t != null && this.linkInlerp(Vars.world.tile(x, y), t.tile, (this.linkRange * 8).toFloat()) && t.block is MatrixBridge) {
            Drawf.select(t.x, t.y, (t.block.size * 8).toFloat() / 2.0f + 2.0f + Mathf.absin(Time.time, 4.0f, 1.0f), Pal.breakInvalid)
          }
        }

        Tmp.v1.rotate90(1)
      }
    } else {
      Lines.stroke(1.0f)
      Draw.color(Pal.placing)
      Drawf.circles((x * 8).toFloat() + this.offset, (y * 8).toFloat() + this.offset, (this.linkRange * 8).toFloat())
    }
  }

  inner class MatrixBridgeBuild : DistNetBuild() {
    var rand: Rand = Rand()
    var transportCounter: Float = 0f
    var linkNextPos: Int = -1
    var linkElementPos: Int = -1
    var linkNext: MatrixBridgeBuild? = null
    var linkElement: DistElementBuildComp? = null
    var linkNextLerp: Float = 0f
    var linkElementLerp: Float = 0f
    var drawEffs  = Seq<EffTask>()
    var netEfficiency: Float = 0f

    override fun status(): BlockStatus? {
      return if (!this.enabled) BlockStatus.logicDisable else (if (this.distributor.network.netValid()) BlockStatus.active else (if (this.distributor.network.netStructValid()) (if (this.distributor.network.topologyUsed <= this.distributor.network.totalTopologyCapacity) BlockStatus.noInput else BlockStatus.noOutput) else this.consumer.status()))
    }

    fun canLink(other: Building): Boolean {
      if (this@MatrixBridge.crossLinking) {
        if (other !is MatrixBridgeBuild) {
          return false
        }

        val m = other
        val linkLength = min(this@MatrixBridge.linkRange, (m.block as MatrixBridge).linkRange).toFloat()
        if ((m.block as MatrixBridge).crossLinking) {
          val xDistance = abs(this.tileX() - other.tileX())
          val yDistance = abs(this.tileY() - other.tileY())
          if ((!(yDistance.toFloat() < linkLength + this.block.size.toFloat() / 2.0f + this.block.offset) || this.tileX() != other.tileX() || this.tileY() == other.tileY()) && (!(xDistance.toFloat() < linkLength + this.block.size.toFloat() / 2.0f + this.block.offset) || this.tileX() == other.tileX() || this.tileY() != other.tileY())) {
            return false
          }
        } else if (!Intersector.overlaps(Tmp.cr1.set(this.x, this.y, linkLength * 8.0f), other.tile.getHitbox(Tmp.r1))) {
          return false
        }
      } else if (!this@MatrixBridge.linkInlerp(this.tile, other.tile, (this@MatrixBridge.linkRange * 8).toFloat())) {
        return false
      }

      if (other is MatrixBridgeBuild && other.block === this.block) {
        return true
      } else {
        val var10000: Boolean
        if (other is DistElementBuildComp) {
          val comp = other as DistElementBuildComp
          if (this.linkable(comp) && comp.linkable(this)) {
            var10000 = true
            return var10000
          }
        }

        var10000 = false
        return var10000
      }
    }

    public override fun onProximityAdded() {
      super.onProximityAdded()
      this.updateNetLinked()
    }

    public override fun updateTile() {
      super.updateTile()
      if (this.linkNextPos != -1 && (this.linkNext == null || !this.linkNext!!.isAdded() || this.linkNextPos != this.linkNext!!.pos())) {
        if (this.linkNext != null) {
          if (!this.linkNext!!.isAdded()) {
            this.linkNextPos = -1
          }

          this.deLink(this.linkNext!!)
          this.linkNext = null
        }

        val build = if (this.linkNextPos == -1) null else Vars.world.build(this.linkNextPos)
        if (build is MatrixBridgeBuild) {
          this.linkNext = build
          this.linkNextPos = this.linkNext!!.pos()
          this.link(this.linkNext!!)
        }
      } else if (this.linkNextPos == -1) {
        if (this.linkNext != null) {
          this.deLink(this.linkNext!!)
          this.linkNext = null
        }

        this.linkNextLerp = 0.0f
      }

      this.netEfficiency = Mathf.lerpDelta(this.netEfficiency, this.drawEff(), 0.02f)
      if (this.linkElementPos != -1 && (this.linkElement == null || !this.linkElement!!.building.isAdded() || this.linkElementPos != this.linkElement!!.building.pos())) {
        if (this.linkElement != null) {
          if (!this.linkElement!!.building.isAdded()) {
            this.linkElementPos = -1
          }

          this.deLink(this.linkElement!!)
          this.linkElement = null
        }

        val build = if (this.linkElementPos == -1) null else Vars.world.build(this.linkElementPos)
        if (build is DistElementBuildComp) {
          this.linkElement = build as DistElementBuildComp
          this.linkElementPos = this.linkElement!!.building.pos()
          this.link(this.linkElement!!)
        }
      } else if (this.linkElementPos == -1) {
        if (this.linkElement != null) {
          this.deLink(this.linkElement!!)
          this.linkElement = null
        }

        this.linkElementLerp = 0.0f
      }

      if (this.linkElementPos != -1 && this.linkElement != null && this.linkElementLerp < 0.99f) {
        this.linkElementLerp = Mathf.lerpDelta(this.linkElementLerp, 1.0f, 0.04f)
      }

      if (this.linkNextPos != -1 && this.linkNext != null && this.linkNextLerp < 0.99f) {
        this.linkNextLerp = Mathf.lerpDelta(this.linkNextLerp, 1.0f, 0.04f)
      }

      if (this.linkNext == null) {
        this.doDump()
      } else if (!this.distributor.network.netValid() && this.consumeValid()) {
        this.doTransport(this.linkNext!!)
      }

      this.updateEff()
    }

    override fun pickedUp() {
      this.linkElementPos = -1
      this.linkNextPos = -1
      this.updateTile()
    }

    fun updateEff() {
      var eff: EffTask
      val itr = this.drawEffs.iterator()
      while (itr.hasNext()) {
        eff = itr.next() as EffTask
        if (eff.progress >= 1.0f) {
          itr.remove()
          Pools.free(eff)
        }
        eff.update()
      }

      val scl = (Vars.renderer.getScale() - Vars.renderer.minZoom) / (Vars.renderer.maxZoom - Vars.renderer.minZoom)

        if (this.linkNext != null && this.rand.random(1.0f) <= 0.05f * this.linkNextLerp * this.netEfficiency * Time.delta * scl) {
          this.makeEff(this.x, this.y, this.linkNext!!.x, this.linkNext!!.y)
        }

        if (this.linkElement != null && this.rand.random(1.0f) <= 0.05f * this.linkElementLerp * this.netEfficiency * Time.delta * scl) {
          if (this.linkElement is DistNetworkCoreComp) {
            this.makeEff(this.x, this.y, this.linkElement!!.building.x, this.linkElement!!.building.y)
          } else {
            this.makeEff(this.linkElement!!.building.x, this.linkElement!!.building.y, this.x, this.y)
          }
        }

    }

    fun makeEff(fromX: Float, fromY: Float, toX: Float, toY: Float) {
      Tmp.v1.setAngle(this.rand.random(0.0f, 360.0f))
      Tmp.v1.setLength(this.rand.random(2.0f, 5.0f))
      this.drawEffs.add(EffTask.Companion.make(fromX + Tmp.v1.x, fromY + Tmp.v1.y, toX + Tmp.v1.x, toY + Tmp.v1.y, this.rand.random(0.3f, 1.2f), this.rand.random(0.125f, 0.4f), this.rand.random(180).toFloat(), this.rand.random(-0.6f, 0.6f), this@MatrixBridge.effectColor))
    }

    fun doDump() {
      this.dumpAccumulate()
      this.dumpLiquid()
    }

    fun doTransport(next: MatrixBridgeBuild) {
      this.transportCounter += this.delta() * this.consEfficiency()
      while (this.transportCounter >= this@MatrixBridge.transportItemTime) {
        val item = this.items.take()
        if (item != null) {
          if (next.acceptItem(this, item)) {
            next.handleItem(this, item)
          } else {
            this.items.add(item, 1)
            this.items.undoFlow(item)
          }
        }
        this.transportCounter -= this@MatrixBridge.transportItemTime
      }

      this.liquids.each(LiquidConsumer { l: Liquid?, a: Float -> this.moveLiquid(next, l) })
    }

    override fun onConfigureBuildTapped(other: Building?): Boolean {
      if (other == null) {
        return true
      } else if (other === this) {
        if (this.linkNext != null) {
          this.configure(Point2.unpack(this.linkNext!!.pos()))
        }

        if (this.linkElement != null) {
          this.configure(Point2.unpack(this.linkElement!!.building.pos()))
        }

        return true
      } else if (!this.canLink(other)) {
        return true
      } else if (this.distributor.distNetLinks.size < this@MatrixBridge.maxLinks) {
        if (other is MatrixBridgeBuild) {
          val o = other
          if (o.distributor.distNetLinks.size >= (o.block as MatrixBridge).maxLinks) {
            return false
          }
        }

        if (other is DistElementBuildComp) {
          this.configure(Point2.unpack(other.pos()))
        }

        return false
      } else {
        return false
      }
    }

    override fun drawConfigure() {
      if (this@MatrixBridge.crossLinking) {
        Drawf.square(this.x, this.y, (this@MatrixBridge.size * 8).toFloat(), Pal.accent)
        if (this.linkNext != null) {
          Tmp.v2.set(Tmp.v1.set(this.linkNext!!.building.x, this.linkNext!!.building.y).sub(this.x, this.y).setLength((this@MatrixBridge.size * 8).toFloat() / 2.0f)).setLength((this.linkNext!!.block.size * 8).toFloat() / 2.0f)
          Drawf.square(this.linkNext!!.building.x, this.linkNext!!.building.y, (this.linkNext!!.block.size * 8).toFloat(), 45.0f, Pal.accent)
          Lines.stroke(3.0f, Pal.gray)
          Lines.line(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.linkNext!!.building.x - Tmp.v2.x, this.linkNext!!.building.y - Tmp.v2.y)
          Lines.stroke(1.0f, Pal.accent)
          Lines.line(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.linkNext!!.building.x - Tmp.v2.x, this.linkNext!!.building.y - Tmp.v2.y)
        }
      } else {
        Drawf.circles(this.x, this.y, (this.tile.block().size * 8).toFloat() / 2.0f + 1.0f + Mathf.absin(Time.time, 4.0f, 1.0f))
        Drawf.circles(this.x, this.y, (this@MatrixBridge.linkRange * 8).toFloat())
      }

      var last: Building? = null

      for (x in this.tile.x - this@MatrixBridge.linkRange - 2..this.tile.x + this@MatrixBridge.linkRange + 2) {
        for (y in this.tile.y - this@MatrixBridge.linkRange - 2..this.tile.y + this@MatrixBridge.linkRange + 2) {
          val link = Vars.world.build(x, y)
          if (last !== link) {
            last = link
            if (link != null && link !== this && this.canLink(link)) {
              if (this.linkNext !== link && this.linkElement !== link) {
                if (link is MatrixBridgeBuild) {
                  val l = link
                  if (this.canLink(link)) {
                    this.drawLinkable(l)
                  }
                }
              } else {
                val radius = (link.block.size * 8).toFloat() / 2.0f + 1.0f
                Drawf.square(link.x, link.y, radius, Pal.place)
                Tmp.v1.set(link.x, link.y).sub(this.x, this.y).setLength(radius)
                Drawf.dashLine(Pal.accent, this.x + Tmp.v1.x, this.y + Tmp.v1.y, link.x - Tmp.v1.x, link.y - Tmp.v1.y)
              }
            }
          }
        }
      }

      Draw.reset()
    }

    private fun drawLinkable(link: MatrixBridgeBuild) {
      if (link.linkNext !== this) {
        val radius = (link.block.size * 8).toFloat() / 2.0f + 1.0f + Mathf.absin(Time.time, 4.0f, 1.0f)
        Tmp.v1.set(0.0f, 1.0f).setLength(radius + 4.0f).setAngle(Time.time)
        Drawf.circles(link.x, link.y, radius)

        for (i in 0..3) {
          Draw.color(Pal.gray)
          Fill.poly(link.x + Tmp.v1.x, link.y + Tmp.v1.y, 3, 3.5f, Time.time + (i * 90).toFloat() + 60.0f)
          Draw.color(Pal.accent)
          Fill.poly(link.x + Tmp.v1.x, link.y + Tmp.v1.y, 3, 1.5f, Time.time + (i * 90).toFloat() + 60.0f)
          Tmp.v1.rotate(90.0f)
        }
      } else {
        Drawf.select(link.x, link.y, (link.block.size * 8).toFloat() / 2.0f + 2.0f + Mathf.absin(Time.time, 4.0f, 1.0f), Pal.breakInvalid)
      }
    }

    public override fun draw() {
      Draw.rect(this@MatrixBridge.region, this.x, this.y)
      Drawf.light(this.x, this.y, 16.0f, Liquids.cryofluid.color, this.linkNextLerp * 0.5f)
      val alp = 0.3f + 0.7f * this.netEfficiency
      Draw.alpha(alp * max(this.linkElementLerp, this.linkNextLerp))
      Draw.z(if (this.netEfficiency > 0.3f) 110.0f else 45.0f)
      Draw.rect(this@MatrixBridge.topRegion, this.x, this.y)
      if (this.linkNext != null) {
        Draw.alpha(alp * this.linkNextLerp)
        Draw.rect(this@MatrixBridge.topRegion, this.linkNext!!.building.x, this.linkNext!!.building.y)
        Draw.z(70.0f)
        val var10006 = this@MatrixBridge.linkStoke * this.linkNextLerp
        SglDraw.drawLaser(this.x, this.y, this.linkNext!!.building.x, this.linkNext!!.building.y, this@MatrixBridge.linkRegion, null as TextureRegion?, var10006)
      }

      if (this.linkElement != null) {
        Draw.alpha(alp * this.linkElementLerp)
        Draw.z(if (this.netEfficiency > 0.3f) 110.0f else 45.0f)
        Draw.rect(this@MatrixBridge.topRegion, this.linkElement!!.building.x, this.linkElement!!.building.y)
        Draw.z(70.0f)
        val var2 = this@MatrixBridge.linkStoke * this.linkElementLerp
        SglDraw.drawLaser(this.x, this.y, this.linkElement!!.building.x, this.linkElement!!.building.y, this@MatrixBridge.linkRegion, null as TextureRegion?, var2)
      }

      this.drawEffect()
    }

    fun drawEff(): Float {
      return if (this.distributor.network.netStructValid()) this.distributor.network.netEfficiency() else this.consEfficiency()
    }

    fun drawEffect() {
        Draw.z(110.0f)
        for (eff in this.drawEffs) {
          eff.draw()
        }
    }

    public override fun acceptItem(source: Building, item: Item?): Boolean {
      return source.interactable(this.team) && this.items.get(item) < this@MatrixBridge.itemCapacity && (source.block === this.block || this.linkNext != null)
    }

    public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      return source.interactable(this.team) && this.liquids.get(liquid) < this@MatrixBridge.liquidCapacity && (this.liquids as SglLiquidModule).total() < this@MatrixBridge.maxLiquidCapacity && (source.block === this.block || this.linkNext != null)
    }

    public override fun config(): IntSeq? {
      val t = if (this.linkElementPos == -1) null else Point2.unpack(this.linkElementPos)
      val m = if (this.linkNextPos == -1) null else Point2.unpack(this.linkNextPos)
      return IntSeq.with(*intArrayOf(if (t == null) -1 else t.set(t.x - this.tile.x, t.y - this.tile.y).pack(), if (m == null) -1 else m.set(m.x - this.tile.x, m.y - this.tile.y).pack()))
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.linkNextLerp = read.f()
      this.linkElementLerp = read.f()
      this.transportCounter = read.f()
      this.linkNextPos = read.i()
      this.linkElementPos = read.i()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.f(this.linkNextLerp)
      write.f(this.linkElementLerp)
      write.f(this.transportCounter)
      write.i(this.linkNextPos)
      write.i(this.linkElementPos)
    }
  }

  class EffTask : Poolable {
    var fromX: Float = 0f
    var fromY: Float = 0f
    var toX: Float = 0f
    var toY: Float = 0f
    var length: Float = 0f
    var radius: Float = 0f
    var speed: Float = 0f
    var angleSpeed: Float = 0f
    var rotate: Float = 0f
    var progress: Float = 0f
    var color: Color? = null

    fun update() {
      this.progress += if (this.length == 0.0f) 0.0f else this.speed / this.length * Time.delta
      this.rotate += this.angleSpeed * Time.delta
    }

    fun draw() {
      Tmp.v1.set(this.toX - this.fromX, this.toY - this.fromY)
      Tmp.v1.scl(this.progress)
      Draw.color(this.color)
      Draw.alpha(Mathf.clamp((if (this.progress.toDouble() > 0.5) 1.0f - this.progress else this.progress) / 0.15f))
      Fill.square(this.fromX + Tmp.v1.x, this.fromY + Tmp.v1.y, this.radius, this.rotate)
    }

    override fun reset() {
      this.toY = 0.0f
      this.toX = this.toY
      this.fromY = this.toX
      this.fromX = this.fromY
      this.length = 0.0f
      this.radius = 0.0f
      this.speed = 0.0f
      this.angleSpeed = 0.0f
      this.progress = 0.0f
      this.rotate = this.progress
      this.color = null
    }

    companion object {
      fun make(fromX: Float, fromY: Float, toX: Float, toY: Float, radius: Float, speed: Float, defAngle: Float, angleSpeed: Float, color: Color?): EffTask {
        val res = Pools.obtain<EffTask?>(EffTask::class.java, Prov { EffTask() }) as EffTask
        res.fromX = fromX
        res.fromY = fromY
        res.toX = toX
        res.toY = toY
        res.length = Mathf.len(toX - fromX, toY - fromY)
        res.radius = radius
        res.speed = speed
        res.rotate = defAngle
        res.angleSpeed = angleSpeed
        res.color = color
        return res
      }
    }
  }

  companion object {
    private val temps  = ObjectSet<MatrixBridgeBuild>()
  }
}