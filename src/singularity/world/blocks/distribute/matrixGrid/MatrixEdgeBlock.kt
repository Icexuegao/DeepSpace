package singularity.world.blocks.distribute.matrixGrid

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Point2
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.modules.ItemModule
import mindustry.world.modules.LiquidModule
import singularity.Singularity
import singularity.graphic.SglDraw
import singularity.world.components.EdgeLinkerBuildComp
import singularity.world.components.EdgeLinkerComp
import kotlin.math.max

open class MatrixEdgeBlock(name: String?) : Block(name), EdgeLinkerComp {
  override var linkLength: Int = 16
  override var linkOffset: Float = 0f
  override lateinit var linkRegion: TextureRegion
  override lateinit var linkCapRegion: TextureRegion
  var linkLightRegion: TextureRegion? = null
  var linkLightCapRegion: TextureRegion? = null

  init {
    this.update = true
    this.configurable = true
    config(Point2::class.java) { e: MatrixEdgeBuild, p: Point2 -> e.nextPos = Point2.pack(e.tile!!.x + p.x, e.tile!!.y + p.y) }
    config(Int::class.javaObjectType) { entity: MatrixEdgeBuild, pos: Int -> this.link(entity, pos) }
    buildType = Prov(::MatrixEdgeBuild)
  }

  fun link(entity: EdgeLinkerBuildComp, pos: Int?) {
    super.link(entity, pos!!)
    entity.linkLerp = (0.0f)
  }

  override fun linkable(other: EdgeLinkerComp?): Boolean {
    return other is MatrixEdgeBlock || other is MatrixGridCore
  }

  override fun init() {
    super.init()
    this.clipSize = max(this.clipSize, (this.linkLength * 8 * 2).toFloat())
  }

  override fun load() {
    super.load()
    this.linkRegion = Core.atlas.find(this.name + "_link", Singularity.getModAtlas("matrix_grid_edge"))
    this.linkCapRegion = Core.atlas.find(this.name + "_cap", Singularity.getModAtlas("matrix_grid_cap"))
    this.linkLightRegion = Core.atlas.find(this.name + "_light_link", Singularity.getModAtlas("matrix_grid_light_edge"))
    this.linkLightCapRegion = Core.atlas.find(this.name + "_light_cap", Singularity.getModAtlas("matrix_grid_light_cap"))
  }

  override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>) {
    val pos = req.config as Point2?
    if (pos != null) {
      list.each(Cons { plan: BuildPlan? ->
        if (Point2.pack(req.x + pos.x, req.y + pos.y) == Point2.pack(plan!!.x, plan.y)) {
          val `patt2435$temp` = plan.block
          if (`patt2435$temp` is EdgeLinkerComp) {
            val b = `patt2435$temp` as EdgeLinkerComp
            SglDraw.drawLink(req.drawx(), req.drawy(), this.linkOffset, plan.drawx(), plan.drawy(), b.linkOffset, this.linkRegion, null as TextureRegion?, 1.0f)
          }
        }
      })
    }
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

  override fun setStats() {
    super.setStats()
    this.setEdgeLinkerStats(this.stats)
  }

  inner class MatrixEdgeBuild : Building(), EdgeLinkerBuildComp {
    override var linkLerp: Float = 0f
    override var perEdge: EdgeLinkerBuildComp? = null
    override var nextEdge: EdgeLinkerBuildComp? = null
    override var edges: EdgeContainer = EdgeContainer()
    var loaded: Boolean = false
    override var nextPos: Int = -1

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

    override fun drawLink() {
      super.drawLink()
      if (this.nextEdge() != null) {
        val l = Draw.z()
        Draw.z(110.0f)
        Draw.alpha(0.65f)
        SglDraw.drawLink(this.x, this.y, this@MatrixEdgeBlock.linkOffset, this.nextEdge()!!.tile()!!.drawx(), this.nextEdge()!!.tile()!!.drawy(), this.nextEdge()!!.edgeBlock.linkOffset, this@MatrixEdgeBlock.linkLightRegion, this@MatrixEdgeBlock.linkLightCapRegion, this.linkLerp())
        Draw.z(l)
      }
    }

    override fun updateLinking() {
      super.updateLinking()
      this.loaded = true
    }

    override fun onConfigureBuildTapped(other: Building?): Boolean {
      if (other is EdgeLinkerBuildComp && this@MatrixEdgeBlock.canLink(this, other as EdgeLinkerBuildComp)) {
        this.configure(other.pos())
        return false
      } else {
        return true
      }
    }

    override fun config(): Point2? {
      val p = Point2.unpack(this.nextPos)
      return p.set(p.x - this.tile!!.x, p.y - this.tile!!.y)
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

    override val tile: Tile?
      get() = super<EdgeLinkerBuildComp>.tile

    override fun items(): ItemModule? {
      return super.items
    }

    override fun liquids(): LiquidModule? {
      return super.liquids
    }

    override fun onProximityRemoved() {
      super.onProximityRemoved()
      this.edgeRemoved()
    }

    override fun updateTile() {
      super.updateTile()
      this.updateLinking()
    }

    override fun draw() {
      super.draw()
      this.drawLink()
    }

    override fun pickedUp() {
      super.pickedUp()
      this.edgePickedUp()
    }

    override fun onRemoved() {
      super.onRemoved()
      this.edgeRemove()
    }

    override fun drawConfigure() {
      super.drawConfigure()
      this.drawLinkConfig()
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.readLink(read)
    }

    override fun write(write: Writes) {
      super.write(write)
      this.writeLink(write)
    }
  }
}