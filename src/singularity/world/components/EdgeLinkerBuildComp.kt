package singularity.world.components

import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.world.Tile
import singularity.graphic.SglDraw
import singularity.world.blocks.distribute.matrixGrid.EdgeContainer
import universecore.components.blockcomp.BuildCompBase

interface EdgeLinkerBuildComp : BuildCompBase {
    // @Annotations.BindField(value = "edges", initialize = "new singularity.world.blocks.distribute.matrixGrid.EdgeContainer()")
    var edges:EdgeContainer
    // @Annotations.BindField("nextEdge")
    var nextEdge: EdgeLinkerBuildComp?


    // @Annotations.BindField("perEdge")
    var perEdge: EdgeLinkerBuildComp?

    // @Annotations.MethodEntry(entryMethod = "onProximityRemoved")
    fun edgeRemoved() {
        edges.remove(this)
    }

    //  @Annotations.BindField(value = "nextPos", initialize = "-1")
    var nextPos: Int

    // @Annotations.BindField("linkLerp")
    var linkLerp: Float

    //@Annotations.MethodEntry(entryMethod = "updateTile", insert = Annotations.InsertPosition.HEAD)
    fun updateLinking() {
        if (nextPos != -1 && (nextEdge == null || !nextEdge!!.building.isAdded || nextEdge!!.tile()!!.pos() != nextPos)) {
            if (nextEdge != null && !nextEdge!!.building.isAdded) {
                nextPos=-1
            }
            val build = if (nextPos == -1) null else Vars.world.build(nextPos)
            if (build is EdgeLinkerBuildComp) {
                link(build as EdgeLinkerBuildComp)
            }
        } else if (nextPos == -1 && nextEdge != null) {
            delink(nextEdge!!)
        }

        if (nextPos != -1) {
            if (nextEdge != null && !nextEdge!!.building.isAdded) delink(nextEdge!!)
            linkLerp=(Mathf.lerpDelta(linkLerp, 1f, 0.02f))
        } else {
            linkLerp=0f
        }
    }

    fun linked(next: EdgeLinkerBuildComp?) {}

    fun delinked(next: EdgeLinkerBuildComp?) {}

    fun link(other: EdgeLinkerBuildComp) {
        if (other.nextEdge === this) {
            other.delink(this)
        }
        if (nextEdge != null) {
            delink(nextEdge!!)
        }
        if (other.perEdge != null) {
            other.perEdge!!.delink(other)
        }

        other.perEdge=(this)
        nextEdge=(other)

        nextPos=(other.tile()!!.pos())

        EdgeContainer().flow(this)
        linked(other)
    }

    fun delink(other: EdgeLinkerBuildComp) {
        other.perEdge=(null)
        nextEdge=(null)

        nextPos=(-1)

        EdgeContainer().flow(other)
        EdgeContainer().flow(this)
        delinked(other)
    }

    // @Annotations.MethodEntry(entryMethod = "draw")
    fun drawLink() {
        val l: Float
        Draw.z((Draw.z().also { l = it }) + 5f)
        if (nextEdge != null) {
            SglDraw.drawLink(
                tile()!!.drawx(), tile()!!.drawy(), this.edgeBlock!!.linkOffset,
                nextEdge!!.tile()!!.drawx(), nextEdge!!.tile()!!.drawy(), nextEdge!!.edgeBlock!!.linkOffset,
                this.edgeBlock!!.linkRegion, this.edgeBlock!!.linkCapRegion,
                linkLerp
            )
        }
        Draw.z(l)
    }

    //@Annotations.MethodEntry(entryMethod = "pickedUp")
    fun edgePickedUp() {
        if (perEdge != null) perEdge!!.delink(this)
        if (nextEdge != null) delink(nextEdge!!)
    }

    //@Annotations.MethodEntry(entryMethod = "onRemoved")
    fun edgeRemove() {
        if (perEdge != null) perEdge!!.delink(this)
        if (nextEdge != null) delink(nextEdge!!)
    }

    // @Annotations.MethodEntry(entryMethod = "drawConfigure")
    fun drawLinkConfig() {
        this.edgeBlock!!.drawConfiguring(this)
    }

    //  @Annotations.MethodEntry(entryMethod = "read", paramTypes = {"arc.util.io.Reads -> read", "byte"})
    fun readLink(read: Reads) {
        nextPos=(read.i())
        linkLerp=(read.f())
    }

    //  @Annotations.MethodEntry(entryMethod = "write", paramTypes = {"arc.util.io.Writes -> write"})
    fun writeLink(write: Writes) {
        write.i(nextPos)
        write.f(linkLerp)
    }

    fun tile(): Tile? {
        return building.tile
    }

    val edgeBlock: EdgeLinkerComp
        get() = getBlock(EdgeLinkerComp::class.java)

    /**当边缘连接结构发生变化时调用此方法 */
    fun edgeUpdated()
}