package singularity.world.blocks.distribute.matrixGrid

import arc.math.geom.Polygon
import arc.struct.ObjectSet
import arc.struct.Seq
import mindustry.world.Tile
import singularity.world.components.EdgeLinkerBuildComp
import universecore.util.Empties

class EdgeContainer {
  var all: Seq<EdgeLinkerBuildComp> = Seq<EdgeLinkerBuildComp>()
  var poly: Polygon? = null
    private set
  var isClosure: Boolean = false
    private set

  fun inLerp(tile: Tile): Boolean {
    return this.isClosure && this.poly != null && this.poly!!.contains(tile.drawx(), tile.drawy())
  }

  fun add(other: EdgeLinkerBuildComp) {
    this.all.add(other)
    other.edges = this
  }

  @JvmOverloads
  fun flow(source: EdgeLinkerBuildComp?, exclude: Seq<EdgeLinkerBuildComp?> = Empties.nilSeq<EdgeLinkerBuildComp?>()) {
    flowed.clear()
    var curr = source

    val temp: Seq<EdgeLinkerBuildComp?>?
    temp = Seq<EdgeLinkerBuildComp?>()
    while (curr != null && flowed.add(curr) && !exclude.contains(curr)) {
      temp.add(curr)
      curr = curr.perEdge
    }

    for (i in temp.size - 1 downTo 0) {
      this.add(temp.get(i)!!)
    }

    if (curr != null && !exclude.contains(curr)) {
      this.isClosure = true
      this.updatePoly()

      for (edge in this.all) {
        edge.edgeUpdated()
      }
    } else {
      var var7 = source
      while (var7 != null && flowed.add(var7) && !exclude.contains(var7)) {
        this.add(var7)
        var7 = var7.nextEdge
      }

      this.poly = null
      this.isClosure = false

      for (edge in this.all) {
        edge.edgeUpdated()
      }
    }
  }

  private fun updatePoly() {
    val vertexArr = FloatArray(this.all.size * 2)

    for (i in 0..<this.all.size) {
      vertexArr[i * 2] = (this.all.get(i) as EdgeLinkerBuildComp).building.x()
      vertexArr[i * 2 + 1] = (this.all.get(i) as EdgeLinkerBuildComp).building.y()
    }

    this.poly = Polygon(vertexArr)
  }

  fun remove(remove: EdgeLinkerBuildComp) {
    if (remove.nextEdge != null) {
      (EdgeContainer()).flow(remove.nextEdge, Seq.with<EdgeLinkerBuildComp?>(*arrayOf<EdgeLinkerBuildComp>(remove)))
    }

    if (remove.perEdge != null && remove.edges == this) {
      (EdgeContainer()).flow(remove.perEdge, Seq.with<EdgeLinkerBuildComp?>(*arrayOf<EdgeLinkerBuildComp>(remove)))
    }
  }

  companion object {
    private val flowed: ObjectSet<EdgeLinkerBuildComp?> = ObjectSet<EdgeLinkerBuildComp?>()
  }
}