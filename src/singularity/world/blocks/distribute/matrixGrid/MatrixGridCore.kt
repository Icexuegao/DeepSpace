package singularity.world.blocks.distribute.matrixGrid

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Point2
import arc.math.geom.Polygon
import arc.math.geom.Vec2
import arc.struct.*
import arc.util.Eachable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.world.Tile
import singularity.Singularity
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.EdgeLinkerBuildComp
import singularity.world.components.EdgeLinkerComp
import singularity.world.components.distnet.IOPointComp
import singularity.world.distribution.GridChildType
import singularity.world.meta.SglStat
import universecore.UncCore
import universecore.util.DataPackable
import kotlin.math.max

open class MatrixGridCore(name: String) : MatrixGridBlock(name), EdgeLinkerComp {
  override var linkLength: Int = 16
  var maxEdges: Int = 8
  override lateinit var linkRegion: TextureRegion
  override lateinit var linkCapRegion: TextureRegion
  lateinit var linkLightRegion: TextureRegion
  lateinit var linkLightCapRegion: TextureRegion
  override var linkOffset: Float = 0f
  var childLinkRegion: TextureRegion? = null
  var linkColors = ObjectMap<GridChildType, Color>()

  init {
    this.config(Int::class.javaObjectType) { building: Building, int: Int -> this.link((building as EdgeLinkerBuildComp?)!!, int!!) }
    buildType = Prov(::MatrixGridCoreBuild)
  }

  override fun parseConfigObjects(e: SglBuilding?, obj: Any?) {
    val entity = e as MatrixGridCoreBuild
    if (obj is LinkPair) {
      val p: Point2 = obj.linking!!
      entity.nextPos = Point2.pack(e.tileX() + p.x, e.tileY() + p.y)
    }

    super.parseConfigObjects(e, obj)
  }

  override fun setStats() {
    super.setStats()
    this.stats.add(SglStat.maxChildrenNodes, this.maxEdges.toFloat())
    this.setEdgeLinkerStats(this.stats)
  }

  override fun load() {
    super.load()
    this.linkRegion = Core.atlas.find(this.name + "_link", Singularity.getModAtlas("matrix_grid_edge"))
    this.linkCapRegion = Core.atlas.find(this.name + "_cap", Singularity.getModAtlas("matrix_grid_cap"))
    this.linkLightRegion = Core.atlas.find(this.name + "_light_link", Singularity.getModAtlas("matrix_grid_light_edge"))
    this.linkLightCapRegion = Core.atlas.find(this.name + "_light_cap", Singularity.getModAtlas("matrix_grid_light_cap"))
    this.childLinkRegion = Core.atlas.find(this.name + "_child_linker", Singularity.getModAtlas("matrix_grid_child_linker"))
  }

  override fun pointConfig(config: Any?, transformer: Cons<Point2>): Any? {
    if (config is ByteArray) {
      val var5 = DataPackable.readObject<DataPackable?>(config, *arrayOfNulls<Any>(0))
      if (var5 is LinkPair) {
        var5.handleConfig(transformer)
        return var5.pack()
      }
    }

    return config
  }

  override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
    val bytes = req.config as ByteArray?
    if (bytes != null) {
      pair.read(bytes)
      val p: Point2 = pair.linking!!
      list.each(Cons { plan: BuildPlan ->
        if (Point2.pack(req.x + p.x, req.y + p.y) == Point2.pack(plan.x, plan.y)) {
          val patt = plan.block
          if (patt is EdgeLinkerComp) {
            val b = patt as EdgeLinkerComp
            SglDraw.drawLink(req.drawx(), req.drawy(), this.linkOffset, plan.drawx(), plan.drawy(), b.linkOffset, this.linkRegion, null as TextureRegion?, 1.0f)
          }
        }
      })
      pair.reset()
    }
  }

  fun link(entity: EdgeLinkerBuildComp, pos: Int?) {
    super.link(entity, pos!!)
    entity.linkLerp = (0.0f)
  }

  override fun linkable(other: EdgeLinkerComp?): Boolean {
    return other is MatrixEdgeBlock
  }

  override fun init() {
    super.init()
    this.clipSize = max(this.clipSize, (this.linkLength * 8 * 2).toFloat())
    this.initColor()
  }

  fun initColor() {
    this.linkColors.put(GridChildType.input, Pal.heal)
    this.linkColors.put(GridChildType.output, Pal.accent)
    this.linkColors.put(GridChildType.acceptor, SglDrawConst.matrixNet)
    this.linkColors.put(GridChildType.container, SglDrawConst.matrixNet)
  }

  fun linkLength(): Int {
    return this.linkLength
  }

  fun linkOffset(): Float {
    return this.linkOffset
  }

  fun linkRegion(): TextureRegion {
    return this.linkRegion
  }

  fun linkCapRegion(): TextureRegion {
    return this.linkCapRegion
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    this.drawPlacing(x, y, rotation, valid)
  }

  inner class MatrixGridCoreBuild : MatrixGridBuild(), EdgeLinkerBuildComp {
    override var linkLerp: Float = 0f
    override var perEdge: EdgeLinkerBuildComp? = null
    override var nextEdge: EdgeLinkerBuildComp? = null
    override var edges: EdgeContainer = EdgeContainer()
    var lastPoly: Polygon? = null
    lateinit var vertices: Array<Vec2?>
    var verticesSeq: FloatSeq? = null
    override var nextPos: Int = -1
    var loaded: Boolean = false
    var childLinkWarmup = IntMap<FloatArray>()

    override fun updateTile() {
      if (this.lastPoly !== this.edges.poly) {
        this.lastPoly = this.edges.poly
        if (this.lastPoly != null) {
          val poly = this.edges.poly
          val vert = poly!!.vertices
          this.verticesSeq = FloatSeq.with(*vert)
          this.vertices = arrayOfNulls(vert.size / 2)

          for (i in this.vertices.indices) {
            this.vertices[i] = Vec2(vert[i * 2], vert[i * 2 + 1])
          }
        }
      }

      for (a in this.childLinkWarmup.values()) {
        a[0] = Mathf.lerpDelta(a[0], 1.0f, 0.02f)
      }

      super.updateTile()
      this.updateLinking()
    }

    override fun releaseRequest() {
      super.releaseRequest()
      TMP_EXI.clear()

      var config: TargetConfigure
      val var1 = this.configs().iterator()
      while (var1.hasNext()) {
        config = var1.next() as TargetConfigure
        var warmup = this.childLinkWarmup.get(config.offsetPos)
        if (warmup == null) {
          warmup = FloatArray(1)
          this.childLinkWarmup.put(config.offsetPos, warmup)
        }
        TMP_EXI.add(config.offsetPos)
      }

      for (entry in this.childLinkWarmup) {
        if (!TMP_EXI.contains(entry.key)) {
          this.childLinkWarmup.remove(entry.key)
        }
      }
    }

    override fun linked(next: EdgeLinkerBuildComp?) {
      if (this.loaded) {
        this.linkLerp(0.0f)
      }
    }

    override fun delinked(next: EdgeLinkerBuildComp?) {
      if (this.loaded) {
        this.linkLerp(0.0f)
      }
    }

    override fun edgeUpdated() {
    }

    override fun draw() {
      super.draw()
      this.drawLink()

      for (entry in this.childLinkWarmup) {
        val cfg = this.configMap.get(entry.key)
        if (cfg != null && !(entry.value[0] <= 0.01f)) {
          val t = Vars.world.tile(this.tileX() + Point2.x(entry.key), this.tileY() + Point2.y(entry.key)) ?: return

          Draw.alpha(0.7f * entry.value[0])
          val map: ObjectMap<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> = cfg.get()
          var c: Color? = null
          val var7: ObjectMap.Entries<*, *> = map.iterator()

          while (var7.hasNext()) {
            val mapEntry: ObjectMap.Entry<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> = var7.next() as ObjectMap.Entry<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>>
            var bool = false
            val var10: ObjectMap.Entries<*, *> = (mapEntry.value as ObjectMap<*, *>).iterator()

            while (var10.hasNext()) {
              val setEntry: ObjectMap.Entry<ContentType, ObjectSet<UnlockableContent>> = var10.next() as ObjectMap.Entry<ContentType, ObjectSet<UnlockableContent>>
              if (!(setEntry.value as ObjectSet<*>).isEmpty) {
                bool = true
                break
              }
            }

            if (bool) {
              if (c != null) {
                c = Color.white
                break
              }

              c = this@MatrixGridCore.linkColors.get(mapEntry.key, Color.white)
            }
          }

          Draw.color(c)
          Draw.z(95.0f)
          SglDraw.drawLaser(this.x, this.y, t.drawx(), t.drawy(), this@MatrixGridCore.childLinkRegion, null as TextureRegion?, 8.0f * entry.value[0])
          Draw.z(110.0f)
          Fill.circle(t.drawx(), t.drawy(), 1.5f * entry.value[0])
        }
      }

      Draw.z(40.0f)
      Draw.reset()
    }

    override fun drawLink() {
      super.drawLink()
      if (this.nextEdge() != null) {
        val l = Draw.z()
        Draw.z(110.0f)
        Draw.alpha(0.65f)
        SglDraw.drawLink(this.x, this.y, this@MatrixGridCore.linkOffset, this.nextEdge()!!.tile()!!.drawx(), this.nextEdge()!!.tile()!!.drawy(), this.nextEdge()!!.edgeBlock.linkOffset, this@MatrixGridCore.linkLightRegion, this@MatrixGridCore.linkLightCapRegion, this.linkLerp())
        Draw.z(l)
      }
    }

    override fun updateLinking() {
      super.updateLinking()
      this.loaded = true
    }

    override fun onConfigureBuildTapped(other: Building): Boolean {
      val result = super.onConfigureBuildTapped(other)
      if (other is EdgeLinkerBuildComp && this@MatrixGridCore.canLink(this, other as EdgeLinkerBuildComp)) {
        this.configure(other.pos())
        return false
      } else {
        return result
      }
    }

    override fun gridValid(): Boolean {
      return super.gridValid() && this.edges.isClosure && this.edges.all.size <= this@MatrixGridCore.maxEdges
    }

    override fun drawConfigure() {
      super.drawConfigure()
      val var1 = this.ioPoints.iterator()

      while (var1.hasNext()) {
        val io = var1.next() as IOPointComp
        if (UncCore.secConfig.getConfiguring() !== io) {
          val radius = (io.block.size * 8).toFloat() / 2.0f + 1.0f
          val building = io.building
          Drawf.square(building.x, building.y, radius, Pal.accent)
          Tmp.v1.set(-1.0f, 1.0f).setLength(radius + 1.0f).scl(Time.time % 60.0f / 60.0f * 1.41421f)
          Tmp.v2.set(1.0f, 0.0f).setLength(radius + 1.0f).add(Tmp.v1)

          for (i in 0..3) {
            Draw.color(Pal.gray)
            Fill.square(building.x + Tmp.v2.x, building.y + Tmp.v2.y, 2.0f, 45.0f)
            Draw.color(Pal.place)
            Fill.square(building.x + Tmp.v2.x, building.y + Tmp.v2.y, 1.25f, 45.0f)
            Tmp.v2.rotate(90.0f)
          }
        }
      }

      this.drawLinkConfig()
    }

    override fun tileValid(tile: Tile): Boolean {
      return !this.edges.isClosure || this.edges.inLerp(tile)
    }

    override fun drawValidRange() {
    }

    override fun config(): ByteArray {
      val pair = LinkPair()
      pair.configs.clear()

      for (entry in this.configMap) {
        val build = this.nearby(Point2.x(entry.key).toInt(), Point2.y(entry.key).toInt())
        if (build != null) {
          if (build is IOPointComp) {
            val io = build as IOPointComp
            if (!this.ioPoints.contains(io)) {
              continue
            }
          }

          pair.configs.put(entry.key, entry.value)
        }
      }

      pair.linking = Point2(Point2.x(this.nextPos) - this.tileX(), Point2.y(this.nextPos) - this.tileY())
      return pair.pack()
    }

    fun nextEdge(): EdgeLinkerBuildComp? {
      return this.nextEdge
    }

    fun nextEdge(edge: EdgeLinkerBuildComp?) {
      this.nextEdge = edge
    }

    fun perEdge(): EdgeLinkerBuildComp? {
      return this.perEdge
    }

    fun perEdge(edge: EdgeLinkerBuildComp?) {
      this.perEdge = edge
    }

    fun nextPos(): Int {
      return this.nextPos
    }

    fun nextPos(pos: Int) {
      this.nextPos = pos
    }

    fun linkLerp(): Float {
      return this.linkLerp
    }

    fun linkLerp(lerp: Float) {
      this.linkLerp = lerp
    }

    public override fun onProximityRemoved() {
      super.onProximityRemoved()
      this.edgeRemoved()
    }

    override fun pickedUp() {
      super.pickedUp()
      this.edgePickedUp()
    }

    override fun onRemoved() {
      super.onRemoved()
      this.edgeRemove()
    }

    public override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.readLink(read)
    }

    public override fun write(write: Writes) {
      super.write(write)
      this.writeLink(write)
    }
  }

  class LinkPair : PosCfgPair() {
    var linking: Point2? = null

    override fun typeID(): Long {
      return 5463757638164667648L
    }

    override fun read(read: Reads) {
      this.linking = Point2.unpack(read.i())
      super.read(read)
    }

    override fun write(write: Writes) {
      write.i(this.linking!!.pack())
      super.write(write)
    }

    override fun reset() {
      super.reset()
      this.linking = null
    }

    override fun handleConfig(handler: Cons<Point2>) {
      super.handleConfig(handler)
      handler.get(this.linking)
    }

    companion object {
      const val typeID: Long = 5463757638164667648L
    }
  }

  companion object {
    var pair: LinkPair = LinkPair()
    private val TMP_EXI = IntSet()
  }
}