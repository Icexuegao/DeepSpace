package singularity.world.components

import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.struct.ObjectSet
import arc.util.Time
import arc.util.Tmp
import mindustry.Vars
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import kotlin.math.abs
import kotlin.math.min

interface EdgeLinkerComp {
  //@Annotations.BindField("linkLength")
  var linkLength: Int

  // @Annotations.BindField("linkOffset")
  var linkOffset: Float

  // @Annotations.BindField("linkRegion")
  var linkRegion: TextureRegion

  //  @Annotations.BindField("linkCapRegion")
  var linkCapRegion: TextureRegion

  //  @Annotations.MethodEntry(entryMethod = "drawPlace", paramTypes = {"int -> x", "int -> y", "int -> rotation", "boolean -> valid"})
  fun drawPlacing(x: Int, y: Int, rotation: Int, valid: Boolean) {
    Tmp.v1.set(1f, 0f)
    for (i in 0..3) {
      val dx: Float = x * Vars.tilesize + this.block.offset + Geometry.d4x(i) * this.block.size * Vars.tilesize / 2f
      val dy: Float = y * Vars.tilesize + this.block.offset + Geometry.d4y(i) * this.block.size * Vars.tilesize / 2f

      Drawf.dashLine(
        Pal.accent, dx, dy, dx + Geometry.d4x(i) * linkLength * Vars.tilesize, dy + Geometry.d4y(i) * linkLength * Vars.tilesize
      )

      for (d in 1..linkLength) {
        Tmp.v1.setLength(d.toFloat())
        val t = Vars.world.build(x + Tmp.v1.x.toInt(), y + Tmp.v1.y.toInt())
        if (t is EdgeLinkerBuildComp && canLink(Vars.world.tile(x, y), this, t.tile, t.edgeBlock!!)) {
          Drawf.select(t.x, t.y, t.block.size * Vars.tilesize / 2f + 2f + Mathf.absin(Time.time, 4f, 1f), Pal.breakInvalid)
        }
      }
      Tmp.v1.rotate90(1)
    }
  }

  // @Annotations.MethodEntry(entryMethod = "setStats", context = {"stats -> stats"})
  fun setEdgeLinkerStats(stats: Stats) {
    stats.add(Stat.linkRange, linkLength.toFloat(), StatUnit.blocks)
  }

  fun drawConfiguring(origin: EdgeLinkerBuildComp) {
    Drawf.square(origin.building.x, origin.building.y, (origin.block.size * Vars.tilesize).toFloat(), Pal.accent)
    if (origin.perEdge != null) {
      Tmp.v2.set(
        Tmp.v1.set(origin.building.x, origin.building.y).sub(origin.perEdge!!.building.x, origin.perEdge!!.building.y).setLength(origin.perEdge!!.block.size * Vars.tilesize / 2f)
      ).setLength(origin.block.size * Vars.tilesize / 2f)

      Drawf.square(
        origin.perEdge!!.building.x, origin.perEdge!!.building.y, (origin.perEdge!!.block.size * Vars.tilesize).toFloat(), 45f, Pal.place
      )
      Drawf.dashLine(
        Pal.accent, origin.perEdge!!.building.x + Tmp.v1.x, origin.perEdge!!.building.y + Tmp.v1.y, origin.building.x - Tmp.v2.x, origin.building.y - Tmp.v2.y
      )
    }

    if (origin.nextEdge != null) {
      Tmp.v2.set(
        Tmp.v1.set(origin.nextEdge!!.building.x, origin.nextEdge!!.building.y).sub(origin.building.x, origin.building.y).setLength(origin.block.size * Vars.tilesize / 2f)
      ).setLength(origin.nextEdge!!.block.size * Vars.tilesize / 2f)

      Drawf.square(
        origin.nextEdge!!.building.x, origin.nextEdge!!.building.y, (origin.nextEdge!!.block.size * Vars.tilesize).toFloat(), 45f, Pal.accent
      )
      Lines.stroke(3f, Pal.gray)
      Lines.line(
        origin.building.x + Tmp.v1.x, origin.building.y + Tmp.v1.y, origin.nextEdge!!.building.x - Tmp.v2.x, origin.nextEdge!!.building.y - Tmp.v2.y
      )
      Lines.stroke(1f, Pal.accent)
      Lines.line(
        origin.building.x + Tmp.v1.x, origin.building.y + Tmp.v1.y, origin.nextEdge!!.building.x - Tmp.v2.x, origin.nextEdge!!.building.y - Tmp.v2.y
      )
    }

    for (other in getLinkable(origin)) {
      if (other === origin.nextEdge || other === origin.perEdge) continue
      Drawf.select(
        other.building.x, other.building.y, other.block.size * Vars.tilesize / 2f + 2f + Mathf.absin(Time.time, 4f, 1f), Pal.breakInvalid
      )
    }
  }

  fun getLinkable(origin: EdgeLinkerBuildComp): ObjectSet<EdgeLinkerBuildComp> {
    tmpSeq.clear()
    for (i in 0..3) {
      val dx = Geometry.d4x(i)
      val dy = Geometry.d4y(i)
      for (l in this.block.size / 2..<linkLength + this.block.size / 2) {
        val tile = origin.tile()!!.nearby(dx * l, dy * l) ?: continue
        val build = tile.build
        if (build is EdgeLinkerBuildComp && canLink(origin, build as EdgeLinkerBuildComp)) tmpSeq.add(build as EdgeLinkerBuildComp)
      }
    }

    return tmpSeq
  }

  val block: Block
    get() = this as Block

  fun link(entity: EdgeLinkerBuildComp, pos: Int) {
    val build = Vars.world.build(pos)

    if (build is EdgeLinkerBuildComp) {
      if (entity.nextEdge === build) {
        entity.delink(build as EdgeLinkerBuildComp)
      } else entity.link(build as EdgeLinkerBuildComp)
    }
  }

  fun canLink(origin: EdgeLinkerBuildComp, other: EdgeLinkerBuildComp): Boolean {
    return canLink(origin.tile()!!, origin.edgeBlock!!, other.tile()!!, other.edgeBlock!!)
  }

  fun canLink(origin: Tile, originBlock: EdgeLinkerComp, other: Tile, otherBlock: EdgeLinkerComp): Boolean {
    if (!originBlock.linkable(otherBlock) || !otherBlock.linkable(this)) return false

    val xDistance = abs(origin.x - other.x)
    val yDistance = abs(origin.y - other.y)

    val linkLength = min(originBlock.linkLength, otherBlock.linkLength)

    return (yDistance < linkLength + this.block.size / 2f + this.block.offset && origin.x == other.x && origin.y != other.y) || (xDistance < linkLength + this.block.size / 2f + this.block.offset && origin.x != other.x && origin.y == other.y)
  }

  fun linkable(other: EdgeLinkerComp?): Boolean

  companion object {
    val tmpSeq: ObjectSet<EdgeLinkerBuildComp> = ObjectSet<EdgeLinkerBuildComp>()
  }
}